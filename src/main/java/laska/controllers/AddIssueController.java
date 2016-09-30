package laska.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import laska.data.DBManager;
import laska.data.IComment;
import laska.data.IWorkLog;
import laska.data.ModelComment;
import laska.data.ModelWorkLog;
import laska.jinfo.App;
import laska.jinfo.Comment;
import laska.jinfo.Issue;
import laska.jinfo.WorkLog;
import net.rcarz.jiraclient.JiraException;

public class AddIssueController {
	
	private VBox issues_panel;
	
	/**
	 * @param ip - панель, куди виводити додані задачі
	 */
	public AddIssueController(VBox ip) {
		issues_panel = ip;
	} 
	
	@FXML
	private Label info;			//надпис з підказками
	
	@FXML
	private TextField tf_key;	//поле для введення ключа
	
	@FXML
	private VBox p_more;		//блок з додатковими налаштуваннями
	
	@FXML
	private CheckBox cb_st, cb_com, cb_wl; //ifSt, isCom, ifWl
	
	@FXML
    public void initialize() {
		showHideMore();
		info.setText("");
	}
	
	@FXML
	private void showHideMore(){
		if(p_more.isVisible()){
			//ховаємо додаткові налаштування
			p_more.setVisible(false);
			p_more.setMinHeight(0);
			p_more.setMaxHeight(0);
		}else{
			//показуємо додаткові налаштування
			p_more.setVisible(true);
			p_more.setMinHeight(Control.USE_COMPUTED_SIZE);
			p_more.setMaxHeight(Control.USE_COMPUTED_SIZE);
		}
	}
	
	@FXML
	private void addIssue(){
		DBManager db = DBManager.getDBManager();
		try {
			Issue is = Issue.loadIssueFromJira(tf_key.getText());
			if(is==null){
				errorDublicateIssue();
				return;
			}
			//встановлюємо парамери відстеження
			if(cb_st.isSelected()) is.setIfStatus(1);
			if(cb_com.isSelected()) {
				is.setIfComm(1);
				ModelComment com = db.getComment(is.getKey());
				com.setOptimalDateTime();
				db.updataComment(com);
			}
			if(cb_wl.isSelected()) {
				is.setIfWL(1);
				ModelWorkLog wl = db.getWorkLog(is.getKey());
				wl.setOptimalDateTime();
				db.addWorkLog(wl);
			}
			db.updataIssueConf(is);//оновлюємо налаштування в БД
			
			//відображаємо задачу в формі
			AnchorPane ap = is.loadView();
			issues_panel.getChildren().add(ap);
			
			//очишчуємо форму
			clean();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JiraException e) {
			errorNotFoundIssue();
		} 
	}
	
	private void clean(){
		tf_key.setText("");
		cb_st.setSelected(true);
		cb_com.setSelected(false);
		cb_wl.setSelected(false);
		info.setText("");
	}
	
	private void errorDublicateIssue(){
		info.setText("Задача з вказаним ключум вже є в БД");
	}
	
	private void errorNotFoundIssue(){
		info.setText("Неможливо завантажити із сервера "
				+ "інформацію про вказану задачу");
	}
}
