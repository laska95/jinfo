package laska.data;

public class ModelComment extends DateTime implements IComment{
	
	protected String idj = "";
	protected String key = "";
	protected String author = "";
	protected String body = "";
	
	public ModelComment(){
		super();
	}
	
	/*==================================================================
	 * Set
	 *==================================================================*/
	
	public void setIdj(String string){
		this.idj = string.substring(0);
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
