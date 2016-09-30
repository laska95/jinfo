package laska.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
/**
 * Описує методи, для роботи з файлом налаштувань.
 * В файлі міститься інформація про адресу сервера jira, 
 * логін та пароль користувача.
 * Реалізує патерн singleton
 * @author laska
 */
public class Conf {
	
	private static Conf CONF = null;
	
	private String _jiraUrl;
	private String _userName;
	private String _password;//шифроване значення паролю. таке як в conf файлі
	private Integer _time;//інтервал, з якким оновлювати інформацію
	private Boolean _new;//чи відслідковувати нові задачі
	
	private Conf(){
		try {
			File f = new File("conf");
			if (f==null||f.length()==0){
				setDefultConf();
				update();
			}
			readConf();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Оновлює інформацію в файлі
	 * @throws IOException
	 */
	public void update() throws IOException{
		Properties property = new Properties();
		property.setProperty("JIRA_URL", _jiraUrl);
		property.setProperty("USER_NAME", _userName);
		property.setProperty("PAS", _password);
		property.setProperty("TIME", _time.toString());
		property.setProperty("NEW", _new.toString());
		
		//Створюємо/перезаписуємо файл з налаштуваннями
		FileWriter fw = null;
		try {
			fw = new FileWriter("conf");
			property.store(fw, null);		
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
		finally{
			try{
				fw.close();
			} catch (Exception e){
				//потік уже закритий
			}
		}
	}
	
	/**
	 * Читає інформацію з файлу налаштувань
	 * @throws IOException
	 */
	public void readConf() throws IOException{
		Conf cf = null;
		FileReader fr = null;
		try {
			fr = new FileReader("conf");
			Properties property = new Properties();
			property.load(fr);

			_jiraUrl = property.getProperty("JIRA_URL", null);
			_userName = property.getProperty("USER_NAME", null);
			_password = property.getProperty("PAS", null);
			_time = Integer.parseInt(property.getProperty("TIME", "0"));
			_new = Boolean.parseBoolean(property.getProperty("NEW", "0"));
			CONF = cf;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} finally {
			try {
				fr.close();
			} catch (Exception e2) {
				//уже закритий
			}
		}
	}
	
	/*************************************************************************
	 * Get
	 *************************************************************************/
	
	public static Conf getCong(){
		if (CONF==null){
			clean();
		}
		return CONF;
	}
	
	public String getJiraUrl(){
		return _jiraUrl;
	}
	
	public String getUserName(){
		return _userName;
	}
	
	public String getPassword(){
		//пароль розшифровується
		return decode(_password);
	}
	
	public int getTime(){
		return _time;
	}
	
	public boolean getNew(){
		return _new;
	}
	
	/*************************************************************************
	 * Set
	 *************************************************************************/
	
	public void setJiraUrl(String jiraURL){
		_jiraUrl = jiraURL;
	}
	
	public void setUserName(String userName){
		_userName = userName;
	}
	
	public void setPassword(String password){
		//шифруємо пароль
		_password = code(password);
	}
	
	/**
	 * Встановлює періодичність в хв., з якою потрібно оновляти дані.
	 * time = 0 - інформація самостійно не оновлюється
	 * @param time
	 */
	public void setTime(int time){
		_time = time;
	}
	
	public void setNew(boolean b){
		_new = b;
	}
		
	public static void clean(){
		CONF = new Conf();
		CONF.setDefultConf();
		try {
			CONF.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefultConf(){
		_jiraUrl = "";
		_userName = "";
		_password = "";
		_time = 30;
		_new = true;
	}
	
	private String code(String str){
		String code = "";
		for(char c:str.toCharArray()){
			code+= (char)((int)c+1);
		}
		return code;
	}
	
	private String decode(String str){
		String decode = "";
		for(char c:str.toCharArray()){
			decode+= (char)((int)c-1);
		}
		return decode;
	}
}
