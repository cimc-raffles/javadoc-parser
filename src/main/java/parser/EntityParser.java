package parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.resolution.declarations.ResolvedTypeDeclaration;

import entity.ParameterEntity;

public class EntityParser {
	
	private String path = "E:/workspace/plugin/smaple/src/main/java";
	
	private ResolvedTypeDeclaration resolvedTypeDeclaration ;
	
	public EntityParser() {
	}
	
	public EntityParser( ResolvedTypeDeclaration resolvedTypeDeclaration) {
		this.resolvedTypeDeclaration = resolvedTypeDeclaration ;
	}
	
	public List<ParameterEntity> parse() throws IOException {
		
		String qualifiedName = resolvedTypeDeclaration.getQualifiedName();
     
        Path sourcePath = Paths.get( path, this.qualifiedName2Path(qualifiedName).concat( ".java"));

        CompilationUnit unit = StaticJavaParser.parse(sourcePath) ;
        
		TypeDeclaration<?> clazz = unit.getPrimaryType().get();
		
		List<ParameterEntity> result = new ArrayList<>() ;
		clazz.accept( new FieldVisitor(), result) ;
		
		return result ;
		
	}
	
	private class FieldVisitor extends VoidVisitorAdapter<List<ParameterEntity>> {
		
		@Override
		public void visit(FieldDeclaration node, List<ParameterEntity> arg) {
			super.visit(node, arg);
			
			if( node.isAnnotationPresent(JsonIgnore.class))
				return ;
			
			ParameterEntity parameter = new ParameterEntity() ;
			arg.add(parameter);
			
			VariableDeclarator variableDeclarator = node.getVariable(0);
			parameter.setParent( resolvedTypeDeclaration.getQualifiedName());
			parameter.setName( variableDeclarator.getNameAsString());
			parameter.setType( variableDeclarator.getType().resolve().asReferenceType().getQualifiedName());
			Optional<Javadoc> comment = node.getJavadoc();
			if( comment.isPresent()) {
				Javadoc javadoc = comment.get() ;
				parameter.setSummary( javadoc.getDescription().toText());
			}
		}
	}
	
	private String qualifiedName2Path( String qualifiedName) {
		return qualifiedName.replace(".", "/") ;
	}

}
