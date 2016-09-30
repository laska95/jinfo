package laska.data;

public interface IComment extends IDateTime{
	
	public void setIdj(String string);
	public void setKey(String key);
	public void setAuthor(String author);
	public void setDescription(String body);

	public String getIdj();
	public String getKey();
	public String getAuthor();
	public String getDescription();
}
