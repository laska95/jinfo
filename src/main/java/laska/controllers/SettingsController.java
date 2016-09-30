package laska.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import laska.data.Conf;
import laska.data.DBManager;
import laska.jinfo.App;
import laska.jinfo.Informator;

public class SettingsController {

	@FXML
	private TextField tf_jiraUrl;	//адреса Jira
	@FXML
	private TextField tf_user;		//ім’я користувача
	
	@FXML
	private RadioButton rb_0, rb_10, rb_30, rb_60;//період оновлення
	
	@FXML
	private CheckBox cb_infoNew;	//чи відслідковувати нові задачі
	
	/**
	 * Виконується при зміні значень rb_0, rb_10, rb_30, rb_60
	 */
	@FXML
	private void changeInfoTime(){
		Conf c = Conf.getCong();
		if (rb_10.isSelected()){
			c.setTime(10);
		} else if (rb_30.isSelected()){
			c.setTime(30);
		} else if (rb_60.isSelected()){
			c.setTime(60);
		} else c.setTime(0);
		try {
			c.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Виконується при зміні значення cb_infoNew
	 */
	@FXML
	private void changeInfoNew(){
		Conf c = Conf.getCong();
		c.setNew(cb_infoNew.isSelected());
		try {
			c.update();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ручне оновлення інформації про задачі
	 */
	@FXML
	private void updata(){
		Informator i = new Informator();
		if (!i.update()) i.notifacation("Змін не знайдено.");
		else{
			App.app.updateHistory(i.getHistory());
		}
	}
	
	/**
	 * Видаляє всю інформацію, про налаштування користувача,
	 * включаючи логін та пароль
	 */
	@FXML
	private void delete(){
		try {
			Conf.clean();
			DBManager.getDBManager().deleteDB();
			laska.jinfo.App.app.login();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@FXML
    public void initialize() {
		Conf c = Conf.getCong();
		try {
			//правильно встанолюємо значення всіх полів форми
			tf_jiraUrl.setText(c.getJiraUrl());
			tf_user.setText(c.getUserName());
			if (c.getNew()) cb_infoNew.setSelected(true);
			else cb_infoNew.setSelected(false);
			switch (c.getTime()){
				case 10:{
					rb_10.setSelected(true);
					break;
				}
				case 30:{
					rb_30.setSelected(true);
					break;
				}
				case 60:{
					rb_60.setSelected(true);
					break;
				}
				default: rb_0.setSelected(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
