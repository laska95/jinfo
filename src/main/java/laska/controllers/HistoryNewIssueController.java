package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import laska.data.INewIssue;

public class HistoryNewIssueController extends NotifacationNewIssueController {
	
	private final INewIssue ni;
	
	public HistoryNewIssueController(INewIssue newIssue){
		super(newIssue);
		ni = newIssue;
	}
	
	@FXML
	private Label l_time, l_date;
		

	@FXML
    public void initialize() {
		super.initialize();
		l_time.setText(String.format("%02d:%02d", ni.getHour(), ni.getMinute()));
		l_date.setText(String.format("%02d.%02d.20%02d", 
				ni.getDay(), ni.getMonth(), ni.getYear()));
	}
}
