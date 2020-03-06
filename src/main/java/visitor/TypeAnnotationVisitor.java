package visitor;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import assistant.VisitorAssistant;
import entity.ApiEntity;

public class TypeAnnotationVisitor extends VoidVisitorAdapter<ApiEntity> {
	
	private final String REQUEST_MAPPING = RequestMapping.class.getSimpleName() ;
	
//	private String[] CONTROLLER_MAPPING = { RestController.class.getSimpleName(), Controller.class.getSimpleName() } ;
	
	@Override
	public void visit(NormalAnnotationExpr node, ApiEntity arg) {
		
		super.visit(node, arg);
		
		if( REQUEST_MAPPING.equals( node.getNameAsString())) {
			Map<String,Expression> map = VisitorAssistant.normalAnnotationExpr2Map(node) ;
			Expression value = map.get("value") ;
			if( null!=value)
				arg.setPath( VisitorAssistant.startsWithSlash( value.asStringLiteralExpr().getValue()));
			Expression path = map.get("path") ;
			if( null!=path)
				arg.setPath( VisitorAssistant.startsWithSlash( path.asStringLiteralExpr().getValue()));
		}
	}
	
	@Override
	public void visit(SingleMemberAnnotationExpr node, ApiEntity arg) {
		
		super.visit(node, arg);
		
		if( REQUEST_MAPPING.equals( node.getNameAsString())) {
			Expression expression = node.getMemberValue() ;
			String value = expression.asStringLiteralExpr().getValue() ;
			arg.setPath( VisitorAssistant.startsWithSlash(value));
		}
	}
	
	@Override
	public void visit(MarkerAnnotationExpr node, ApiEntity arg) {
		super.visit(node, arg);
	}
}
