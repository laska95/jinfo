package laska.data;

public interface IIssue extends IDateTime{

	public void setKey(String key);
	public void setSummary(String summary);
	public void setType(String type);
	public void setStatus(String status);
	public void setIfStatus(int b);
	public void setIfComm(int b);
	public void setIfWL(int b);

	public String getKey();
	public String getSummary();
	public String getType();
	public String getStatus();
	public int getIfStatus();
	public int getIfComm();
	public int getIfWL();
	
	public IDateTime getOptimalDate() throws Exception;
}
