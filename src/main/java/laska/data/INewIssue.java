package laska.data;

public interface INewIssue extends IDateTime{
	public void setKey(String key);
	public void setSummary(String summary);
	public void setType(String type);
	public void setPriority(String priority);
	public void setDescription(String description);

	public String getKey();
	public String getSummary();
	public String getType();
	public String getPriority();
	public String getDescription();
}
