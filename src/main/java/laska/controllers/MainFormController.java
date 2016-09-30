package laska.controllers;

import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import laska.data.DBManager;
import laska.data.IIssue;
import laska.data.ModelIssue;
import laska.jinfo.App;
import laska.jinfo.INews;
import laska.jinfo.Informator;
import laska.jinfo.Issue;

public class MainFormController {
		
	@FXML
	private Tab tab_set;		//вкладка з налаштуваннями
	
	@FXML
	private VBox main_panel;	//тут розміщується блок addIssue
	
	@FXML
	private VBox issues_panel;	//тут міститься список завдань 
	
	@FXML
	private VBox vb_history;	//тут виводиться історія
	
	@FXML
    public void initialize() {
		setTabSettings();
		setEddIssues();
		setCurrentIssues();
		Informator inf = new Informator();
		updateHistory(inf.getHistory());
	}
	
	/**
	 * Відображає блок для додавання нових задач
	 */
	private void setEddIssues(){
		try {
			String fxmlAddIssue = "/fxml/module_addIssue.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlAddIssue));
			loader.setController(new AddIssueController(issues_panel));
			AnchorPane ap = (AnchorPane) loader.load();
			ap.getStylesheets().setAll(
					getClass().getResource(App.css).toExternalForm());
			main_panel.getChildren().add(ap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Завантажує вкладку з налаштуваннями
	 */
	private void setTabSettings(){
		try {
			String setFxml = "/fxml/module_settings.fxml";
			FXMLLoader loader = new FXMLLoader(getClass().getResource(setFxml));
			loader.setController(new SettingsController());
			AnchorPane ap = (AnchorPane) loader.load();
			ap.getStylesheets().setAll(
					getClass().getResource(App.css).toExternalForm());
			tab_set.setContent(ap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Завантажує завдання, за якими вже слідкує користувач
	 */
	private void setCurrentIssues(){
		DBManager db = DBManager.getDBManager();
		try {
			List<ModelIssue> iss = db.getAllIssues();
			for(IIssue is:iss){
				issues_panel.getChildren().add((new Issue(is)).loadView());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateHistory(List<INews> list){
		vb_history.getChildren().clear();
		Collections.sort(list);
		try{
			for(INews n:list){
				vb_history.getChildren().add(n.loadHistoryView());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
