package laska.jinfo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.Notification;

import org.controlsfx.control.Notifications;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import laska.data.Conf;
import laska.data.DBManager;
import laska.data.DateTime;
import laska.data.IComment;
import laska.data.IIssue;
import laska.data.INewIssue;
import laska.data.IWorkLog;
import laska.data.JiraLouder;
import laska.data.ModelIssue;
import laska.data.ModelNewIssue;
import net.rcarz.jiraclient.JiraException;

/**
 * Відповідає за відображення історії, 
 * повідомлень, та оновлення інформації
 * @author laska
 */
public class Informator {

	public static ReentrantLock lock = new ReentrantLock();
	
	private int showTime = 10000;
	
	//черга спливаючих повідомлень
	private List<INews> _news = new ArrayList<>();
	private Thread t = null;
	private static Thread autoUdate=null;
	
	public void rerun(){
		if (!autoUdate.isAlive()){
			Thread tt = new Thread(()->{
				int time = Conf.getCong().getTime();
				while(time!=0){
					try {
						Thread.sleep(time*60*1000);
						update();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
					
			});
		tt.setDaemon(true);
		autoUdate=tt; tt.start();
		}
	}
	
	/**
	 * Показує задану сцену як повідомлення
	 * @param veiv
	 */
	public void notifacation(Parent veiv){
		Platform.runLater(()->{
			Notifications n = Notifications.create();
			n.graphic(veiv);
			n.hideAfter(new Duration(showTime));
			n.show();
		});
	}

	public void notifacation(String text){
		Platform.runLater(()->{
			Notifications n = Notifications.create();
			n.text(text);
			n.hideAfter(new Duration(showTime));
			n.show();
		});
	}
	
	/**
	 * Виводить повідомлення, що стоять в черзі _news.
	 * Потрібен, щоб вони не спливали на екрані кучею,
	 * а по черзі, із затримкою в showTime/2
	 */	
	private void toLine(INews news){
		synchronized (_news) {
			_news.add(news);
		}
		//створюємо і запускаємо потік, якщо його не існує
		if (t==null){
			t = new Thread(()->{
				while(!_news.isEmpty()){
					INews n = null;
					synchronized (_news) {
						n = _news.get(0);
						_news.remove(0);
					}
					try {
						notifacation(n.loadNotificationView());
						Thread.sleep(showTime/2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			t.setDaemon(true);
			t.start();
		}
	}

	
	/**
	 * Оновлює все і інформує про зміни
	 * @return
	 */
	public boolean update(){
		boolean r = false;
		try{
			lock.lock();
			r |= updateNewIssue();
			r |= updateAllIssues();
		} catch (Exception e) {
			e.printStackTrace();
			r = false;
		} finally {
			lock.unlock();
		}
		return r;
	}
	
	/**
	 * Оновлює інформацію про всі задачі, що відслідковуються користувачем
	 * та інформує про зміни
	 * @return true - щось змінилося
	 */
	private boolean updateAllIssues(){
		boolean r = false;
		DBManager db = DBManager.getDBManager();
		try {
			//дістаємо задачі з БД
			List<ModelIssue> issdb = db.getAllIssues();
			//аналізуємо кожну задачу почерзі
			for(IIssue is:issdb){
				r |= updataIssue(new Issue(is));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * Оновлює інформацію про задачу, згідно з її налаштуваннями.
	 * @param is - задача, яку треба перевірити
	 * @return true - щось змінилося
	 */
	private boolean updataIssue(Issue is){
		boolean r = false;
		System.out.println("=====================================\n"+
		"start upddate: "+is.getKey());
		DBManager db = DBManager.getDBManager();
		//якщо треба перевірити тільки зміну статусу задачі
		if((is.getIfStatus()>0) && (is.getIfComm()+is.getIfWL()==0)){
			//перший тип JQL запиту
			r = updateOnlyStatus(is);
		}else {
			//інакше другий тип JQL запиту
			r = updateAllComponents(is);
		}
		return r;
	}

	/**
	 * Перевіряє тільки зміну статусу задачі. 
	 * Якщо так, то кидає її в чергу на нотифікацію
	 * Tут використовується більш ефективний 1 тип JQL запиту
	 * @param is
	 * @return
	 */
	private boolean updateOnlyStatus(Issue is){
		boolean r = false;
		try {
			//загружаємо інформацію з сервера jira
			net.rcarz.jiraclient.Issue jis =  JiraLouder.loadIssue1(is);
			if(jis==null) return r;
			//оновлюємо статус без лишніх перевірок
			is.setStatus(jis.getStatus().toString());
			is.setCurrentDateTime();
			DBManager.getDBManager().updataIssue(is);
			//кидаємо в чергу на нотифікацію
			toLine(is);
			r = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;	
	}
	
	/**
	 * Перевіряє чи задача змінилася. 
	 * Якщо так, то кидає її в чергу на нотифікацію
	 * Tут використовується 2 тип JQL запиту
	 * @param is
	 * @return
	 */
	private boolean updateAllComponents(Issue is){
		boolean r = false;
		if (is.getIfStatus()+is.getIfComm()+is.getIfWL()==0) return r;
		try {
			//загружаємо інформацію з сервера jira
			net.rcarz.jiraclient.Issue jis =  JiraLouder.loadIssue2(is);
			//задача ніяк не змінилася
			if (jis==null) {
				System.out.println("Ніяких змін на сервері не виявлено");
				return r;
			}
			
			//другий тип JQL запиту відслідковує будь-які зміни в задачі
			//тому треба перевірити, що саме змінилося
			
			//Загружаємо всю інф про задачу
			jis = JiraLouder.loadIssue(is.getKey());
			
			//перевіряємо, чи змінився статус
			if(is.getIfStatus()>0){
				System.out.println("Перевіряємо статус:");
				boolean r1 = is.checkStatus(jis);
				if (r1) {
					toLine(is);
					System.out.println("\t\t"+is);
				}
				r |= r1;
			}
			//перевіряємо, чи змінилися коментарі
			if(is.getIfComm()>0){
				System.out.println("Перевіряємо коментарі:");
				Comment dbcom = new Comment(DBManager.getDBManager().
						getComment(is.getKey()));
				boolean r1 = dbcom.checkComment(jis);
				if (r1) {
					toLine(dbcom);
					System.out.println("\t\t"+dbcom);
				}
				r |= r1;
			}
			//перевіряємо, чи змінилися робочі записи
			if(is.getIfWL()>0){
				System.out.println("Перевіряємо WL:");
				WorkLog dbwl= new WorkLog(DBManager.getDBManager().
						getWorkLog(is.getKey()));
				boolean r1 = dbwl.checkWorkLog(jis);
				if (r1) {
					toLine(dbwl);
					System.out.println("\t\t"+dbwl);
				}
				r |= r1;
			}
		} catch (Exception  e) {
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * Загружає нові задачі із сервера і кидає в чергу на нотифікацію
	 * @return true - якщо є нові задачі
	 */
	private boolean updateNewIssue(){
		System.out.println("Оновлюємо інформацію про нові задачі:");
		boolean r = false;
		try {
			//отримуємо список нових задач
			List<NewIssue> jniss = NewIssue.loadAllFromJira();
			//якщо нових задач нема
			if (jniss==null) return r;
			//приводимо jniss до виду List<ModelNewsIssue>
			List<ModelNewIssue> dbniss = new ArrayList<>();
			for(NewIssue nis:jniss){
				dbniss.add((ModelNewIssue) nis);
				//кидаємо новину в чергу
				toLine(nis);
			}
			//записуємо нові задачі в БД
			DBManager db = DBManager.getDBManager();
			db.removeAllNewIssues();	//видяляємо старе
			db.addNewIssues(dbniss);	//записуємо нове
			r = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}
	
	/**
	 * Читає дані з БД, і визначає, чи їх потрібно відображати в історії
	 * @return - свіжий сисок історії
	 */
	public List<INews>  getHistory(){
		List<INews> hist = new ArrayList<>();	//список новин
		DBManager db = DBManager.getDBManager();
		
		try {
			//список нових задач з БД
			List<ModelNewIssue> dbniss = db.getAllNewIssues();
			for(INewIssue nis:dbniss){
				if (nis.isNullDateTime()) continue;
				hist.add(new NewIssue(nis));
			}
		} catch (Exception e) {
			//NullPointer якщо нових задач нема
			e.printStackTrace();
		}
		
		try {
			//список задач з БД
			List<ModelIssue> dbiss = db.getAllIssues();
			for(IIssue is:dbiss){
				if(is.getIfStatus()>0 && !is.isNullDateTime()){
					hist.add(new Issue(is));
				}
				if(is.getIfComm()>0){
					IComment com = db.getComment(is.getKey());
					if (!com.isNullDateTime() && !com.getIdj().equals("")) 
						hist.add(new Comment(com));
				}
				if(is.getIfWL()>0){
					IWorkLog wl = db.getWorkLog(is.getKey());
					if (!wl.isNullDateTime() && !wl.getIdj().equals("")) 
						hist.add(new WorkLog(wl));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hist;
	}
}
