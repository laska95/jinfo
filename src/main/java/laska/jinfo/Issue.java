package laska.jinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import laska.controllers.HistoryIssueController;
import laska.controllers.IssueController;
import laska.controllers.NotifacationIssueController;
import laska.data.DBManager;
import laska.data.IIssue;
import laska.data.JiraLouder;
import laska.data.ModelIssue;
import net.rcarz.jiraclient.JiraException;

public class Issue extends ModelIssue implements INews{
		
	public Issue(){
		super();
	}
	
	public Issue(IIssue is){
		super();
		if(is!=null){
			setKey(is.getKey());
			setSummary(is.getSummary());
			setType(is.getType());
			setStatus(is.getStatus());
			setIfStatus(is.getIfStatus());
			setIfComm(is.getIfComm());
			setIfWL(is.getIfWL());
			setDate(is.getYear(), is.getMonth(), is.getDay());
			setTime(is.getHour(), is.getMinute());
		}
	}
	
	/**
	 * Перевіряє зміну статусу задачі. Нове значення записує в БД
	 * @return - true, якщо статус змінився
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public boolean checkStatus(net.rcarz.jiraclient.Issue jis) throws SQLException, IOException{
		DBManager db = DBManager.getDBManager();
		//якщо статус не змінився
		if(this.getStatus().equals(jis.getStatus().toString())){
			return false;
		}
		//оновлюємо значення в БД
		this.setStatus(jis.getStatus().toString());
		this.setCurrentDateTime();
		db.updataIssue(this);

		return true;
	}
		
	@Override
	public AnchorPane loadNotificationView() throws IOException {
		String fxml = "/fxml/notification_issue.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new NotifacationIssueController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	@Override
	public AnchorPane loadHistoryView() throws IOException {
		String fxml = "/fxml/historyModule_issue.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new HistoryIssueController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	@Override
	public AnchorPane loadView() throws IOException {
		String fxml = "/fxml/module_issue.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new IssueController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}
	
	/*==================================================================
	 * Static
	 *==================================================================*/
	
	/**
	 * Загружає інформацію про задачу з ключем key з сервера jira
	 * і додає його в БД. Перед загрузкою перевіряє, чи така задача є в БД.
	 * @param key - ключ задачі
	 * @return null, якщо задача вже є в БД
	 * @throws JiraException 
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public static Issue loadIssueFromJira(String key) throws IOException, JiraException, SQLException{
		DBManager db = DBManager.getDBManager();
		IIssue dbis = db.getIssue(key);
		Issue is = dbis!=null?new Issue(dbis):null;
		if (is!=null) return null;
		//завантажуємо Issue з сервера
		//викидає помилку JiraException якщо задача не знайдена
		net.rcarz.jiraclient.Issue jiss =  JiraLouder.loadIssue(key);
		//добавляємо задачу в БД
		is = new Issue();
        is.setKey(jiss.getKey());
        is.setSummary(jiss.getSummary());
        is.setType(jiss.getIssueType().toString());
		is.setStatus(jiss.getStatus().toString());
		is.setCurrentDateTime();
		db.addIssue((ModelIssue) is);
		
		//додаємо пустий коментар 
		Comment com = new Comment();
		com.setKey(is.getKey());
		DBManager.getDBManager().addComment(com);
		
		//додаємо пустий worklog
		WorkLog wl = new WorkLog();
		wl.setKey(is.getKey());
		DBManager.getDBManager().addWorkLog(wl);
		
		return is;
	}

	@Override
	public int compareTo(INews n) {
		int d1 = getCompareValeu();
		int d2 = n.getCompareValeu();
		if (d1==d2) return 0;
		if (d1>d2) return -1;
		else return 1;
	}

	@Override
	public int getCompareValeu() {
		return (getYear()*10000+getMonth()*100+getDay())*10000+
				getHour()*100+getMinute();	
	}
	
}
