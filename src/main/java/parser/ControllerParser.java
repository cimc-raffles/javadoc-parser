package parser;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import com.github.javaparser.ParseResult;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import entity.ApiEntity;
import entity.MethodEntity;
import visitor.MethodVisitor;
import visitor.TypeAnnotationVisitor;

public class ControllerParser {
	
	public List<ApiEntity> parse(String path) {
		return this.parse(Paths.get(path));
	}
	
	public List<ApiEntity> parse(Path path) {
		
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(path));

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);


        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        
		SourceRoot sourceRoot = new SourceRoot(path);
		sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver);

		List<ParseResult<CompilationUnit>> list = new ArrayList<>();
		try {
			list = sourceRoot.tryToParse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(list.isEmpty())
			return null ;

		List<ApiEntity> apis = new ArrayList<>();

		for (ParseResult<CompilationUnit> x : list) {

			CompilationUnit unit = x.getResult().get();

			TypeDeclaration<?> type = unit.getPrimaryType().get();

			if (!this.hasControllerAnnotation(type))
				continue;

			ApiEntity api = new ApiEntity();
			type.accept( new TypeAnnotationVisitor(), api) ;
			
			//name
			api.setEntity(this.getEntityName(unit, type));

			//summary
			Javadoc comment = this.getJavadoc(type);
			api.setSummary(comment.getDescription().toText());
			
			//author,category ...
			Map<String, Object> commentBlocks = this.getJavadocBlocks(comment);
			try {
				BeanUtils.copyProperties(api, commentBlocks);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}

			//method
			List<MethodEntity> methods = new ArrayList<>();
			api.setMethods(methods);
			type.accept( new MethodVisitor(), api);
			
			//id
			api.setId(DigestUtils.md5Hex(api.getEntity()));
			apis.add(api);

		}
		
		return apis;
	}

	private String getPackageName(CompilationUnit unit) {
		Optional<PackageDeclaration> declaration = unit.getPackageDeclaration();
		return declaration.isPresent() ? declaration.get().getNameAsString() : StringUtils.EMPTY;
	}

	private String getEntityName(CompilationUnit unit, TypeDeclaration<?> type) {
		return String.format("%s.%s", this.getPackageName(unit), type.getNameAsString());
	}

	private boolean hasControllerAnnotation(TypeDeclaration<?> type) {
		return type.isAnnotationPresent(RestController.class) || type.isAnnotationPresent(Controller.class);
	}

	private Javadoc getJavadoc(TypeDeclaration<?> type) {
		return type.getJavadoc().orElse(new Javadoc(new JavadocDescription()));
	}

	private Map<String, Object> getJavadocBlocks(Javadoc comment) {
		Map<String, Object> map = new HashMap<>();
		List<JavadocBlockTag> tags = comment.getBlockTags();
		for (JavadocBlockTag tag : tags) {
			String key = tag.getTagName();
			String value = tag.getContent().toText();
			map.put(key, value);
		}
		return map;
	}

}
