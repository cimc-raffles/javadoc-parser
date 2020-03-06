
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import entity.ApiEntity;
import parser.ControllerParser;

public class ControllerParserTest {
	
	@DisplayName("Javadoc parser testcase")
	@Test
	void test() {

		String path = "g:/workspace/java/sample-boot/src/main/java";

		ControllerParser parser = new ControllerParser();
		List<ApiEntity> result = parser.parse(path);

		assertTrue(null != result && !result.isEmpty());
	}
}
