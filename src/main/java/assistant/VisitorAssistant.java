package assistant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

public class VisitorAssistant {
	
	public static Map<String, Expression> normalAnnotationExpr2Map(NormalAnnotationExpr node) {
		Map<String, Expression> map = new HashMap<>();
		NodeList<MemberValuePair> pairs = node.getPairs();
		Iterator<MemberValuePair> iterator = pairs.iterator();
		while (iterator.hasNext()) {
			MemberValuePair pair = iterator.next();
			map.put(pair.getNameAsString(), pair.getValue());
		}
		return map;
	}
	
	public static String getTypeAsString( com.github.javaparser.ast.type.Type type) {
		String computedType = null ;
		if( type.isPrimitiveType())
			computedType = type.asPrimitiveType().getType().toString();
		else if (type.isReferenceType())
			computedType = type.resolve().asReferenceType().getQualifiedName();
				
		return computedType ;
	}
 
	public static String startsWithSlash( String text) {
		String slash = "/" ;
		text = text.startsWith(slash) ? text : slash.concat(text);
		text = text.endsWith(slash) ? text.substring( 0, text.length()-1-1) : text ;
		return text ;
	}
	
	public static boolean isPrimitiveOrWrapper(String className) {
		String[] primitiveClasses = { 
				byte.class.getName(), 
				char.class.getName(), 
				short.class.getName(),
				int.class.getName(), 
				long.class.getName(), 
				float.class.getName(), 
				double.class.getName(),
				boolean.class.getName(), 
				void.class.getName() 
		};
		String[] primitiveWrapperClasses = { 
				Boolean.class.getName(), 
				Byte.class.getName(), 
				Character.class.getName(),
				Short.class.getName(), 
				Integer.class.getName(), 
				Long.class.getName(), 
				Double.class.getName(),
				Float.class.getName(), 
				Void.class.getName(), 
		};
		
		String[] extraClasses = { String.class.getName() };
		
		return ArrayUtils.contains(primitiveClasses, className)
				|| ArrayUtils.contains(primitiveWrapperClasses, className)
				|| ArrayUtils.contains(extraClasses, className);
	}
	
	public static boolean isJavaNativeObject( String className) {
		return className.startsWith("java.") || className.startsWith("javax.") ;
	}
	
}
