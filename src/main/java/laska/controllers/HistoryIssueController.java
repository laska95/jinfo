package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import laska.data.IIssue;

public class HistoryIssueController extends NotifacationIssueController{
	
	private final IIssue is;
	
	public HistoryIssueController(IIssue issue){
		super(issue);
		is = issue;
	}
	
	@FXML
	private Label l_time, l_date;
		

	@FXML
    public void initialize() {
		super.initialize();
		l_time.setText(String.format("%02d:%02d", is.getHour(), is.getMinute()));
		l_date.setText(String.format("%02d.%02d.20%02d", 
				is.getDay(), is.getMonth(), is.getYear()));
	}
}
