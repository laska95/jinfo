package laska.controllers;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import laska.data.DBManager;
import laska.data.ModelComment;
import laska.data.ModelWorkLog;
import laska.jinfo.Issue;

public class IssueController {
	
	private final Issue is;
	
	public IssueController(Issue issue) {
		this.is = issue;	
	}
	
	@FXML 
	private AnchorPane main;//кореневий вузол
	
	@FXML
	private Label l_key;	//ключ задачі
	
	@FXML
	private Button b_more;	//показати/сховати додаткові налаштування
	
	@FXML
	private VBox p_more;	//панель з додатковими налаштуваннями
	
	@FXML
	private CheckBox cb_st, cb_com, cb_wl; //ifSt, isCom, ifWl
	
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
	private void changeState(){
		try {
			DBManager db = DBManager.getDBManager();
			if(cb_st.isSelected()){
				is.setIfStatus(1);
				is.setOptimalDateTime();//з цього моменту відслідковуються зміни
			}else{
				is.setIfStatus(0);
				is.setNullDateTime();//з NulDateTime запис не показується в історії
			}
			db.updataIssueConf(is);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void changeCom(){
		try {
			DBManager db = DBManager.getDBManager();
			ModelComment com = db.getComment(is.getKey());
			if(cb_com.isSelected()){
				is.setIfComm(1);
				com.setOptimalDateTime();//з цього моменту відслідковуються зміни
			}else{
				is.setIfComm(0);
				com.setNullDateTime();//з NulDateTime запис не показується в історії	
			}
			db.updataComment(com);
			db.updataIssueConf(is);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void changeWL(){
		try {
			DBManager db = DBManager.getDBManager();
			ModelWorkLog com = db.getWorkLog(is.getKey());
			if(cb_com.isSelected()){
				is.setIfWL(1);
				com.setOptimalDateTime();//з цього моменту відслідковуються зміни	
			}else{
				is.setIfWL(0);
				com.setNullDateTime();//з NulDateTime запис не показується в історії
			}
			db.updataWorkLog(com);
			db.updataIssueConf(is);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Видаляє задачу з БД і форми.
	 * Ця задача більше не відстежується
	 */
	@FXML
	private void deleteIssue(){
		try {
			VBox p = (VBox) main.getParent();
			p.getChildren().remove(main);					  //видаляємо з форми
			DBManager.getDBManager().removeIssue(is.getKey());//видаляємо з БД
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@FXML
    public void initialize() {
		showHideMore();
		update();
		main.setId(is.getKey());
	}
	
	/**
	 * Заповнює поля форми, для вказаної задачі
	 * @param is
	 */
	public void update(){

		l_key.setText(is.getKey());
		
		if(is.getIfStatus()>0) cb_st.setSelected(true);
		else cb_st.setSelected(false);
		
		if(is.getIfComm()>0) cb_com.setSelected(true);
		else cb_com.setSelected(false);
		
		if(is.getIfWL()>0) cb_wl.setSelected(true);
		else cb_wl.setSelected(false);
	}
}
