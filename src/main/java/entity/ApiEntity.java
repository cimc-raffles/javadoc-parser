package entity;

import java.util.List;

import lombok.Data;

@Data
public class ApiEntity 
{
	private String Id; 
	
	private String name ;
	
	private String entity ;
	
	private String alias ;
	
	private String summary ;
	
	private String author ;
	
	private String category ;
	
	private String path ;
	
	private List<MethodEntity> methods ;
	
}
