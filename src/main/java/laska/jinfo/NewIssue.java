package laska.jinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import laska.controllers.HistoryNewIssueController;
import laska.controllers.NotifacationNewIssueController;
import laska.data.DBManager;
import laska.data.INewIssue;
import laska.data.JiraLouder;
import laska.data.ModelNewIssue;
import net.rcarz.jiraclient.JiraException;

public class NewIssue extends laska.data.ModelNewIssue implements INews{
	
	public NewIssue() {
		super();
		setOptimalDateTime();
	}
	
	public NewIssue(INewIssue ni) {
		super();
		if (ni!=null){
			setKey(ni.getKey());
			setType(ni.getType());
			setSummary(ni.getSummary());
			setDescription(ni.getDescription());
			setPriority(ni.getPriority());
			setDate(ni.getYear(), ni.getMonth(), ni.getDay());
			setTime(ni.getHour(), ni.getMinute());
		}
	}
	
	@Override
	public AnchorPane loadNotificationView() throws IOException{
		String fxml = "/fxml/notification_newIssue.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new NotifacationNewIssueController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}	

	@Override
	public AnchorPane loadHistoryView() throws IOException {
		String fxml = "/fxml/historyModule_newIssue.fxml";
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(new HistoryNewIssueController(this));
		AnchorPane ap = (AnchorPane) loader.load();
		ap.getStylesheets().setAll(
				getClass().getResource(App.css).toExternalForm());
		return ap;
	}

	/**
	 * Реалізація не потрібна
	 */
	@Override
	public AnchorPane loadView() {
		return null;
	}
	
	public static List<NewIssue> loadAllFromJira() throws SQLException, JiraException{
		//дістаємо задвчі, що збережені в БД
		DBManager db = DBManager.getDBManager();
		List<ModelNewIssue> dbiss = db.getAllNewIssues();
		INewIssue dbis = dbiss.isEmpty()?new NewIssue():dbiss.get(0);
		
		//відсилаємо JQL запит і отримуємо результат
		List<net.rcarz.jiraclient.Issue> jiss = 
				JiraLouder.loadAllNewIssue(dbis);
		
		//нових завдань нема
		if (jiss==null||jiss.isEmpty()){
			System.out.println("На сервері нових задач нема.");
			return null;
		}
		else{
			//є нові задачі. перезаписуємо інф. в БД
			db.removeAllNewIssues();
			ArrayList<NewIssue> niss = new ArrayList<>();
			for(net.rcarz.jiraclient.Issue jis:jiss){
				niss.add(jiraIssueToNewIssue(jis));
			}
			System.out.println("Список нових задач: "+niss);
			return niss;
		}		
	}
	
	private static NewIssue jiraIssueToNewIssue(net.rcarz.jiraclient.Issue jis){
		NewIssue ni = new NewIssue();
		ni.setKey(jis.getKey());
		ni.setSummary(jis.getSummary());
		ni.setType(jis.getIssueType().toString());
		ni.setPriority(jis.getPriority().toString());
		ni.setDescription(jis.getDescription());
		LocalDate ld = LocalDate.now();
		LocalTime lt = LocalTime.now();
		ni.setDate(ld.getYear()%100, ld.getMonthValue(), ld.getDayOfMonth());
		ni.setTime(lt.getHour(), lt.getMinute());
		return ni;
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
