package entity;

import lombok.Data;

@Data
public class ParameterEntity {
	
	private String id ;
	
	private String name ;
	
	private String alias ;
	
	private String summary ;
	
	private String parent ;
	
	private String type ;
	
	private boolean required ;
	
	private String defaultValue ;
	
}
