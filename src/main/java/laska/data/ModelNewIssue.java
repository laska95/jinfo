package laska.data;

public class ModelNewIssue extends DateTime implements INewIssue{
	
	protected String key = "";
	protected String summary = "";
	protected String type = "";
	protected String priority = "";
	protected String description = "";
	
	/*==================================================================
	 * Set
	 *==================================================================*/
		
	public void setKey(String key){
		this.key = key.substring(0);
	}
	
	public void setSummary(String summary){
		this.summary = summary.substring(0);
	}

	public void setType(String type){
		this.type = type.substring(0);
	}
	
	public void setPriority(String priority){
		this.priority = priority.substring(0);
	}
	
	public void setDescription(String description){
		if (description.length()>124){
			this.description = description.substring(0, 120) +"...";
		} else this.description = description.substring(0);
	}

	/*==================================================================
	 * Get
	 *==================================================================*/
	
	public String getKey(){
		return this.key;
	}
	
	public String getSummary(){
		return this.summary;
	}

	public String getType(){
		return this.type;
	}
	
	public String getPriority(){
		return this.priority;
	}
	
	public String getDescription(){
		return this.description;
	}
}
