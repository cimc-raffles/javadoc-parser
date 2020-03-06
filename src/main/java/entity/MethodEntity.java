package entity;

import java.util.List;

public class MethodEntity {
	
	private String id ;
	
	private String name ;
	
	private String alias ;
	
	private String summary ;

	private String path ;
	
	private String method ;
	
	private List<ParameterEntity> request ;
	
	private List<ParameterEntity> response ;

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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<ParameterEntity> getRequest() {
		return request;
	}

	public void setRequest(List<ParameterEntity> request) {
		this.request = request;
	}

	public List<ParameterEntity> getResponse() {
		return response;
	}

	public void setResponse(List<ParameterEntity> response) {
		this.response = response;
	}
	
	
}
