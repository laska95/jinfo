package laska.jinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import laska.controllers.HistoryWLController;
import laska.controllers.NotifacationWLController;
import laska.data.DBManager;
import laska.data.IComment;
import laska.data.IWorkLog;
import laska.data.ModelWorkLog;

public class WorkLog extends ModelWorkLog implements INews{
	
	public WorkLog(){
		super();
	}
	
	public WorkLog(IWorkLog com){
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
	 * Оновляє останній робочий запис в БД, що належать вказаному завданню.
	 * Інформує користувача про нові робочі записи.
	 * @return - true, якщо знайдено нові коментарі
	 * @throws SQLException 
	 * @throws IOException 
	 */
	public boolean checkWorkLog(net.rcarz.jiraclient.Issue jis) throws SQLException, IOException{
		
		//останній доданий wl в Jira
		List<net.rcarz.jiraclient.WorkLog> jcms = jis.getWorkLogs();
		net.rcarz.jiraclient.WorkLog lastjcom =jcms.isEmpty()?null:jcms.get(jcms.size()-1);
		
		DBManager db = DBManager.getDBManager();
		ModelWorkLog lastdbcom = this;
		
		if(lastjcom!=null){	//в jira є wl
			if (!lastdbcom.getIdj().equals(lastjcom.getId())){
				//id останнього wl змінився
				//оновлюємо значення
				updata(lastjcom);
				db.updataWorkLog(this);
				return true;
			} //else коментарі не змінилися
		} //else будь які коментарі відсутні
		
		return false;
	}
	
	private void updata(net.rcarz.jiraclient.WorkLog jcom){
		setIdj(jcom.getId());
		setAuthor(jcom.getAuthor().getName());
		setDescription(jcom.getComment());
		setCurrentDateTime();
	}
	
	public static WorkLog getLastComment(List<net.rcarz.jiraclient.WorkLog> wls){
		WorkLog wl = null;
		if (wls==null||wls.isEmpty()){
			wl =  new WorkLog();
		}else{
			wl = convertToWorkLog(wls.get(wls.size()-1));
			wl.setCurrentDateTime();
		}
		return wl;	
	}
	
	private static WorkLog convertToWorkLog(net.rcarz.jiraclient.WorkLog jcom){
		WorkLog com = new WorkLog();
		com.setIdj(jcom.getId());
		com.setAuthor(jcom.getAuthor().getName());
		com.setDescription(jcom.getComment());
		com.setCurrentDateTime();
		return com;
	}

	@Override
	public AnchorPane loadNotificationView() throws IOException {
		String fxml = "/fxml/notification_com.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new NotifacationWLController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	@Override
	public AnchorPane loadHistoryView() throws IOException {
		String fxml = "/fxml/historyModule_com.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new HistoryWLController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	@Override
	public AnchorPane loadView() throws IOException {
		//не портебує реалізації
		return null;
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
