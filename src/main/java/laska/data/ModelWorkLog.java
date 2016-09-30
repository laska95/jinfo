package laska.data;

public class ModelWorkLog extends DateTime implements IWorkLog{
	protected String idj = "";
	protected String key = "";
	protected String author = "";
	protected String body = "";
	
	/*==================================================================
	 * Set
	 *==================================================================*/
	
	public void setIdj(String string){
		this.idj = string;
	}
	
	public void setKey(String key){
		this.key = key.substring(0);
	}
	
	public void setAuthor(String author){
		this.author = author.substring(0);
	}
	
	public void setDescription(String body){
		if (body.length()>124){
			this.body = body.substring(0, 120) +"...";
		} else this.body = body.substring(0);
	}
	
	/*==================================================================
	 * Get
	 *==================================================================*/
	
	public String getIdj(){
		return this.idj;
	}
	
	public String getKey(){
		return this.key;
	}
	
	public String getAuthor(){
		return this.author;
	}
	
	public String getDescription(){
		return this.body;
	}
		
}
