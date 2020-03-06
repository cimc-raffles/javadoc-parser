package visitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.javadoc.description.JavadocDescription;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

import assistant.VisitorAssistant;
import entity.ApiEntity;
import entity.MethodEntity;
import entity.ParameterEntity;
import parser.EntityParser;

public class MethodVisitor extends VoidVisitorAdapter<ApiEntity> {
	@Override
	public void visit(MethodDeclaration method, ApiEntity collector) {
		
		super.visit(method, collector);
		
		MethodEntity entity = new MethodEntity();
		
		//name
		entity.setName(method.getNameAsString());
		
		//summary
		Javadoc document = method.getJavadoc().orElse(new Javadoc(new JavadocDescription()));
		entity.setSummary(document.getDescription().toText());
		
		//parameter
		List<ParameterEntity> parameters = new ArrayList<>();
		entity.setRequest(parameters);
		method.accept( new MethodParameterVisitor(), parameters) ;

		List<JavadocBlockTag> tags = document.getBlockTags();
		for (JavadocBlockTag x : tags) {
			com.github.javaparser.javadoc.JavadocBlockTag.Type type = x.getType();
			if (!type.equals(JavadocBlockTag.Type.PARAM))
				continue;
			String name = x.getName().orElse(null);
			if( StringUtils.isEmpty(name))
				continue;
			String summary = x.getContent().toText();
			
			for( ParameterEntity p : parameters) {
				if( ! StringUtils.isEmpty(p.getName()) && name.equalsIgnoreCase(p.getName())) {
					p.setSummary(summary);
					break;
				}
			}
		}
		
		//return
		ResolvedReferenceType returnType = method.getType().resolve().asReferenceType();
		
		List<ParameterEntity> result = new ArrayList<>() ;
		String className = returnType.getQualifiedName() ;
		if ( VisitorAssistant.isJavaNativeObject(className)) {
			ParameterEntity parameter = new ParameterEntity() ;
			result.add(parameter);
			parameter.setType(className);
		} else {
			EntityParser parser = new EntityParser(returnType.getTypeDeclaration()) ;
			try {
				List<ParameterEntity> parsedResult = parser.parse();
				result.addAll(parsedResult);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		entity.setResponse(result);
		
		
		//method
		if( ! StringUtils.isEmpty(collector.getPath()))
			entity.setPath( VisitorAssistant.startsWithSlash(collector.getPath()));
		
		NodeList<AnnotationExpr> s = method.getAnnotations();
		s.accept( new MethodAnnotationVisitor(), entity);

		collector.getMethods().add(entity);
	}

}