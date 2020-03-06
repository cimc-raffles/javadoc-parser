
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import entity.ApiEntity;
import parser.ControllerParser;

public class ControllerParserTest {
	
	@Test
	public void test() throws JsonProcessingException {
		
		String path = "./workspace/sample/src/main/java" ;
		
		ControllerParser parser = new ControllerParser() ;
		List<ApiEntity> result = parser.parse(path) ;
		
		assertTrue( null != result && ! result.isEmpty());
	}
}
