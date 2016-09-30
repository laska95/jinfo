package laska.jinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import laska.controllers.HistoryCommController;
import laska.controllers.NotifacationCommController;
import laska.data.DBManager;
import laska.data.DateTime;
import laska.data.IComment;
import laska.data.ModelComment;

public class Comment extends ModelComment implements INews{
		
	public Comment(){
		super();
	}
	
	public Comment(IComment com){
		super();
		if (com!=null){
			setIdj(com.getIdj());
			setKey(com.getKey());
			setAuthor(com.getAuthor());
			setDescription(com.getDescription());
			setDate(com.getYear(), com.getMonth(), com.getDay());
			setTime(com.getHour(), com.getMinute());
		}
	}
	
	/**
	 * Оновляє останній коментарар в БД, що належать вказаному завданню.
	 * Інформує користувача про нові коментарі.
	 * @return - true, якщо знайдено нові коментарі
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public boolean checkComment(net.rcarz.jiraclient.Issue jis) throws SQLException, IOException{
		
		//останній доданий коментар в Jira
		List<net.rcarz.jiraclient.Comment> jcms = jis.getComments();
		net.rcarz.jiraclient.Comment lastjcom =
				jcms.isEmpty()?null:jcms.get(jcms.size()-1);
		DBManager db = DBManager.getDBManager();
		IComment lastdbcom = this;
		
		if(lastjcom!=null){	//в jira є коментарі
			if (!lastdbcom.getIdj().equals(lastjcom.getId())){
				//id останнього коментаря змінився
				//оновлюємо значення
				updata(lastjcom);
				db.updataComment(this);
				return true;
			} //else коментарі не змінилися
		} //else будь які коментарі відсутні
		
		return false;
	}
	
	private void updata(net.rcarz.jiraclient.Comment jcom){
		setIdj(jcom.getId());
		setAuthor(jcom.getAuthor().getName());
		setDescription(jcom.getBody());
		setCurrentDateTime();
	}
	
	public static Comment getLastComment(List<net.rcarz.jiraclient.Comment> coms){
		Comment com = null;
		if (coms==null||coms.isEmpty()){
			com =  new Comment();
		}else{
			com = convertToComment(coms.get(coms.size()-1));
			com.setCurrentDateTime();
		}
		return com;	
	}
	
	private static Comment convertToComment(net.rcarz.jiraclient.Comment jcom){
		Comment com = new Comment();
		com.setIdj(jcom.getId());
		com.setAuthor(jcom.getAuthor().getName());
		com.setDescription(jcom.getBody());
		com.setCurrentDateTime();
		return com;
	}

	@Override
	public AnchorPane loadNotificationView() throws IOException {
		String fxml = "/fxml/notification_com.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new NotifacationCommController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	@Override
	public AnchorPane loadHistoryView() throws IOException {
		String fxml = "/fxml/historyModule_com.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new HistoryCommController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	@Override
	public AnchorPane loadView() throws IOException {
		//не потребує реалізації
		return null;
	}

	@Override
	public int getCompareValeu() {
		return (getYear()*10000+getMonth()*100+getDay())*10000+
				getHour()*100+getMinute();
	}
	
	@Override
	public int compareTo(INews n){
		int d1 = getCompareValeu();
		int d2 = n.getCompareValeu();
		if (d1==d2) return 0;
		if (d1>d2) return -1;
		else return 1;
	}
}
