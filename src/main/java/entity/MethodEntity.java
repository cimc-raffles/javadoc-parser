package entity;

import java.util.List;

import lombok.Data;

@Data
public class MethodEntity {
	
	private String id ;
	
	private String name ;
	
	private String alias ;
	
	private String summary ;

	private String path ;
	
	private String method ;
	
	private List<ParameterEntity> request ;
	
	private List<ParameterEntity> response ;
	
}
