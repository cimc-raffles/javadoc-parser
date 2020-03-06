package visitor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedClassDeclaration;

import assistant.VisitorAssistant;
import entity.ParameterEntity;
import parser.EntityParser;

public class MethodParameterVisitor extends VoidVisitorAdapter<List<ParameterEntity>> {
	@Override
	public void visit(Parameter p, List<ParameterEntity> list) {
		super.visit(p, list);

		List<ParameterEntity> customObjectParameters = new ArrayList<>();

		ParameterEntity entity = new ParameterEntity();

		// annotation
		if( p.isAnnotationPresent(RequestParam.class))
			p.accept( new RequestParamAnnotationVisitor(), entity) ;

		// name
		entity.setName(p.getNameAsString());

		// type
		ClassOrInterfaceType type = p.getType().asClassOrInterfaceType();
		ResolvedClassDeclaration resolvedClass = type.resolve().getTypeDeclaration().asClass();

		String className = resolvedClass.getQualifiedName();

		if (VisitorAssistant.isJavaNativeObject(className))
			entity.setType(className);
		else {
			getCustomParameter(resolvedClass, customObjectParameters);
		}

		if (customObjectParameters.isEmpty())
			list.add(entity);
		else
			list.addAll(customObjectParameters);
	}
	
	private class RequestParamAnnotationVisitor extends VoidVisitorAdapter<ParameterEntity> {
		@Override
		public void visit(NormalAnnotationExpr node, ParameterEntity arg) {
			super.visit(node, arg);
			
			Map<String, Expression> map = VisitorAssistant.normalAnnotationExpr2Map(node);
			Expression required = map.get("required");
			arg.setRequired( null == required ? true : required.asBooleanLiteralExpr().getValue());
			Expression value = map.get("value");
			if(null != value)
				arg.setAlias( value.asStringLiteralExpr().getValue());
			Expression name = map.get("name");
			if(null != name)
				arg.setAlias( name.asStringLiteralExpr().getValue());
			Expression defaultValue = map.get("defaultValue");
			if(null != defaultValue)
				arg.setDefaultValue( defaultValue.asStringLiteralExpr().getValue());
		}
		
		@Override
		public void visit(SingleMemberAnnotationExpr node, ParameterEntity arg) {
			super.visit(node, arg);
			arg.setAlias( node.getMemberValue().asStringLiteralExpr().getValue());
		}
		
		@Override
		public void visit(MarkerAnnotationExpr node, ParameterEntity arg) {
			super.visit(node, arg);
			arg.setRequired(true);
		}
	}

	private void getCustomParameter(ResolvedClassDeclaration resolvedClass,List<ParameterEntity> result) {
		
		EntityParser parser = new EntityParser(resolvedClass) ;
		
		try {
			List<ParameterEntity> list = parser.parse();
			result.addAll(list);

			//TODO: Recursion
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
