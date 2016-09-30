package laska.jinfo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import laska.controllers.LoginController;
import laska.controllers.MainFormController;
import laska.data.*;

public class App extends Application
{
	public static App app = null;
	private static final String iconImageLoc ="/icon.png";		//іконка в треї
	
	private String fxmlLogin = "/fxml/main_loginForm.fxml";		//адреса форми логінування
	private String fxmlMainForm = "/fxml/main_mainForm.fxml";	//адреса основної форми
	public static final String css = "/css/style.css";			//адреса файлу зі стилями
	private java.awt.SystemTray tray = null;
	private java.awt.TrayIcon trayIcon = null;
	protected static Stage _stage = null;
	private MainFormController _mainFormC = null;
	
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		App.app = this;
		_stage = stage;
		//перехоплюємо подію закриття програми
		_stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
				@Override
				public void handle(WindowEvent event) {
					killTray();//і видаляємо значок з трею
				}
			});
		//додаємо програму в трей
		javax.swing.SwingUtilities.invokeLater(this::addAppToTray);
		login();
	}
   
	/**
	* Кидаємо програму в трей
	*/
	private void addAppToTray() {
		try {
			java.awt.Toolkit.getDefaultToolkit();
			if (!java.awt.SystemTray.isSupported()) {
				System.out.println("Система не підтримує роботу з треєм.");
				Platform.exit();
			}

			//Вказуємо іконку для трею
			tray = java.awt.SystemTray.getSystemTray();
			java.awt.Image image = ImageIO.read(getClass().getResource(iconImageLoc));
			trayIcon = new java.awt.TrayIcon(image);

			ActionListener act = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showHideStage();
				}
			};
			// Відкриваємо/ховаємо вікно при подвійному клику на іконці в треї
			trayIcon.addActionListener(act);

			//елемент меню: показати/сховати програму
			java.awt.MenuItem openItem = new java.awt.MenuItem("Відкрити/Сховати");
			openItem.addActionListener(act);

			//елемент меню: вийти з програми
			java.awt.MenuItem exitItem = new java.awt.MenuItem("Вихйти з програми");
			exitItem.addActionListener(event -> {
				tray.remove(trayIcon);
				Platform.exit();
			});

			//додаємо налаштовані елементи меню
			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(openItem);
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);
			tray.add(trayIcon);	//показуємо іконку в треї
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
   
	/**
	 * Повністю ховає (лишає тільки в треї)/показує програму
	 */
	private void showHideStage() {
			if (_stage != null) {
				if (_stage.isShowing()){
					Platform.setImplicitExit(false);
					Platform.runLater(()->{
						_stage.hide();
					});
				}
				else{
					Platform.runLater(()->{
							_stage.show();
							_stage.toFront();
					});
				}
			}
	}
	
	/**
	 * Видаляє іконку з трею
	 */
	private void killTray(){
		try{
			tray.remove(trayIcon);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Програма видалена з трею");
	}
	
	/**
	 * Намагається авторизуватися на вказаному сервері
	 * Адреса сервера, логін та пароль беруться з файлу налаштувань
	 * @throws IOException
	 */
	public void login() throws IOException{
		Conf c = Conf.getCong();
		String un = c.getUserName();
		String jira = c.getJiraUrl();
		if (un.equals("")&&jira.equals("")){
			//якщо інформація про користувача відсутня
			showLogin();
		} else {
			//в conf збережені логін та пароль
			if (authorization(jira, un, c.getPassword())){
				showMainForm();
				System.out.println("Вхід виконано");
			}else{
				Conf.clean();
				System.out.println("Дані пошкоджено");
				login();
			}
		}
	}
	
	/**
	 * Намагається авторизуватися на jurl, з вказаними
	 * логіном та паролем
	 * @param jurl
	 * @param userName
	 * @param password
	 * @return
	 */
	public boolean authorization(String jurl, 
			String userName, String password){
		Conf c = Conf.getCong();
		c.setJiraUrl(jurl);
		c.setUserName(userName);
		c.setPassword(password);
		
		//тестовий запит до сервера
		boolean r = JiraLouder.testLogin();
		if (r){
			//авторизація пройшла успішно
			try {
				c.update();
				showMainForm();
			} catch (IOException e) {
				//проблеми при доступі до файлу conf
				e.printStackTrace();
				r = false;
			}
		}
		else {
			//проблеми при авторизації
			Conf.clean();
		}
		return r;
	}
	
	/**
	 * Показує форму для авторизації
	 */
	private void showLogin(){
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlLogin));
		loader.setController(new LoginController());
		Parent root;
		try {
			root = (Parent)loader.load();
			root.getStylesheets().setAll(
					getClass().getResource(css).toExternalForm());
			_stage.setTitle("Jira інформатор");
			_stage.setScene(new Scene(root));
			_stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Показує основне вікно програми
	 * @throws IOException
	 */
	private  void showMainForm() throws IOException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlMainForm));
		_mainFormC = new MainFormController();
		loader.setController(_mainFormC);
		Parent root = (Parent)loader.load();
		root.getStylesheets().setAll(getClass().getResource(css).toExternalForm());
		_stage.setTitle("Jira інформатор");
		_stage.setScene(new Scene(root));
		_stage.show();
	}
	
	public void updateHistory(List<INews> list){
		_mainFormC.updateHistory(list);
	}
	
	public void openInBrouser(String url){
		getHostServices().showDocument(url);
	}
}
