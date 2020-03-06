import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entity.ApiEntity;
import parser.ControllerParser;

public class ControllerParserTest {
	
	@Test
	@DisplayName("controller parser test")
	void test() {
		String path = "E:/workspace/plugin/sample/src/main/java" ;
		ControllerParser parser = new ControllerParser() ;
		List<ApiEntity> result = parser.parse(path) ;
		
		assertTrue( null != result && ! result.isEmpty());
	}
}
