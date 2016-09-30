package laska.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.TextFlow;
import laska.jinfo.App;

public class LoginController {

	@FXML
	private TextField jiraURL;
	
	@FXML
	private TextField userName;
	
	@FXML
	private TextField password;
	
	@FXML
	private Button loginb;
	
	@FXML
	private Label info;
	
	@FXML
    public void initialize() {
		hideInfo();
	}
	
	
	@FXML
	private void login() throws IOException{
		boolean r = App.app.authorization(jiraURL.getText(), 
				userName.getText(), password.getText());
		if (!r){
			invalidData();
		}
	}

	@FXML
	private void hideInfo(){
		info.setText("");
	}
	
	/**
	 * Дані введені при авторизації невірні
	 */
	private void invalidData(){
		info.setText("Дані введено невірно, неможли \n "
				+ "авторизуватися на сервері");
		password.setText("");
	}
	
}
