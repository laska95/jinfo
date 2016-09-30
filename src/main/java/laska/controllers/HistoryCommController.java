package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import laska.data.IComment;


public class HistoryCommController extends NotifacationCommController{
	
	private final IComment comm;
	
	public HistoryCommController(IComment comment){
		super(comment);
		comm = comment;
	}
	
	@FXML
	private Label  l_time, l_date, title;
		

	@FXML
    public void initialize() {
		super.initialize();
		title.setText("Додано нові коментарі");
		l_time.setText(String.format("%02d:%02d", comm.getHour(), comm.getMinute()));
		l_date.setText(String.format("%02d.%02d.20%02d", 
				comm.getDay(), comm.getMonth(), comm.getYear()));
	}
}
