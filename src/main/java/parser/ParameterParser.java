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
import com.github.javaparser.resolution.types.ResolvedType;

import assistant.SolverAssistant;
import assistant.VisitorAssistant;
import entity.ParameterEntity;

public class ParameterParser {
	
	private ResolvedType resolvedType ;
	private ParameterEntity entity ;
	
	public ParameterParser() {
	}
	
	public ParameterParser( ResolvedType resolvedType) {
		this.resolvedType = resolvedType ;
	}
	
	public ParameterParser( ResolvedType resolvedType , ParameterEntity entity) {
		this.resolvedType = resolvedType ;
		this.entity = entity ;
	}
	
	public List<ParameterEntity> parse() {
		try {
			List<ParameterEntity> result = this.parse( this.resolvedType) ;
			return result ;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null ;
	}
	
	public List<ParameterEntity> parse(ResolvedType type) throws IOException {
		
		if(type.isPrimitive()) {
			entity.setType(type.asPrimitive().getBoxTypeQName());
			return null ;
		}
		if( type.isArray()) {
			ResolvedType componentType = type.asArrayType().getComponentType() ;
			//TODO: setType as Array
			this.parse(componentType);
			return null;
		}
		if( type.isReferenceType()) {
			
			String qualifiedName = type.asReferenceType().getQualifiedName();
			
			if( VisitorAssistant.isJavaNativeObject(qualifiedName)) {
				entity.setType(qualifiedName);
				return null ;
			}
			
			String path = SolverAssistant.getInstance().getPath().toString();
			Path sourcePath = Paths.get( path, this.qualifiedName2Path(qualifiedName).concat( ".java"));
			
			CompilationUnit unit = StaticJavaParser.parse(sourcePath) ;
			
			TypeDeclaration<?> clazz = unit.getPrimaryType().get();
			
			List<ParameterEntity> result = new ArrayList<>();
			clazz.accept( new FieldVisitor(), result) ;
			
			return result ;
		}
			
		return null ;
		
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
			parameter.setParent( resolvedType.asReferenceType().getQualifiedName());
			parameter.setName( variableDeclarator.getNameAsString());
			com.github.javaparser.ast.type.Type type = variableDeclarator.getType();
			parameter.setType( VisitorAssistant.getTypeAsString(type));
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
