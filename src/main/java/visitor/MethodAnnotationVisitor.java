package visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import assistant.VisitorAssistant;
import entity.MethodEntity;
import entity.ParameterEntity;

public class MethodAnnotationVisitor extends VoidVisitorAdapter<MethodEntity> {

	private static final String[] SIMPLE_REQUEST_METHODS = { GetMapping.class.getSimpleName(),
			PostMapping.class.getSimpleName(), DeleteMapping.class.getSimpleName(), PutMapping.class.getSimpleName(),
			PatchMapping.class.getSimpleName(), };

	private static String NORMAL_REQUEST_METHOD = RequestMapping.class.getSimpleName();

	@Override
	public void visit(NormalAnnotationExpr node, MethodEntity arg) {
		super.visit(node, arg);
		
		if( ! this.isRequestMethod(node))
			return;
		
		this.setMethod(node, arg);

		Map<String, Expression> map = VisitorAssistant.normalAnnotationExpr2Map(node);
		Expression value = map.get("value");
		if (null != value)
			this.setPath(value, arg);
		Expression path = map.get("path");
		if (null != path)
			this.setPath(path, arg);
		Expression method = map.get("method");
		if (null != method)
			arg.setMethod(method.asFieldAccessExpr().getNameAsString());
		Expression params = map.get("params");
		if (null != params)
			this.setParameter(params, arg);

	}

	@Override
	public void visit(SingleMemberAnnotationExpr node, MethodEntity arg) {
		super.visit(node, arg);
		
		if( ! this.isRequestMethod(node))
			return;
		
		Expression expression = node.getMemberValue();
		this.setPath(expression, arg);

		this.setMethod(node, arg);
	}

	@Override
	public void visit(MarkerAnnotationExpr node, MethodEntity arg) {
		super.visit(node, arg);
		
		if( ! this.isRequestMethod(node))
			return;
		
		this.setMethod(node, arg);
	}

	private void setMethod(AnnotationExpr node, MethodEntity arg) {
		String nodeName = node.getNameAsString();

		if (this.isNormalRequestMethod(node)) {
			arg.setMethod(RequestMethod.GET.name());
		}
		if (this.isSimpleREquestMethod(node))
			arg.setMethod(nodeName.toUpperCase().replace("MAPPING", ""));
	}

	private void setPath(Expression expression, MethodEntity arg) {
		String value = expression.asStringLiteralExpr().getValue();
		String originValue = arg.getPath();
		if (StringUtils.isEmpty(originValue))
			originValue = StringUtils.EMPTY;
		value = originValue + VisitorAssistant.startsWithSlash(value);
		arg.setPath(value);
	}

	private void setParameter(Expression params, MethodEntity arg) {
		List<ParameterEntity> list = arg.getRequest();
		list = null == list ? new ArrayList<ParameterEntity>() : list;

		String paramsText = params.asStringLiteralExpr().getValue();
		if (StringUtils.isEmpty(paramsText))
			return;
		String[] paramsTexts = paramsText.split(",");
		for (String x : paramsTexts) {
			if (x.startsWith("!"))
				continue;
			ParameterEntity parameter = new ParameterEntity();
			parameter.setName(x);
			parameter.setRequired(true);
			list.add(parameter);
		}
		arg.setRequest(list);
	}
	
	private boolean isRequestMethod(AnnotationExpr node) {
		return isSimpleREquestMethod(node) || isNormalRequestMethod(node) ;
	}
	
	private boolean isNormalRequestMethod(AnnotationExpr node) {
		return NORMAL_REQUEST_METHOD.equalsIgnoreCase(node.getNameAsString()) ;
	}
	
	private boolean isSimpleREquestMethod(AnnotationExpr node) {
		return ArrayUtils.contains(SIMPLE_REQUEST_METHODS, node.getNameAsString()) ;
	}
}