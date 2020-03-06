package entity;

import java.util.List;

public class ApiEntity 
{
	private String id; 
	
	private String name ;
	
	private String entity ;
	
	private String alias ;
	
	private String summary ;
	
	private String author ;
	
	private String category ;
	
	private String path ;
	
	private List<MethodEntity> methods ;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<MethodEntity> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodEntity> methods) {
		this.methods = methods;
	}

	
	
}
