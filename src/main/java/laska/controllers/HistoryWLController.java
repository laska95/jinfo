package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import laska.data.IWorkLog;

public class HistoryWLController extends NotifacationWLController{
	
	private final IWorkLog wl;
	
	public HistoryWLController(IWorkLog workLog){
		super(workLog);
		wl = workLog;
	}
	
	@FXML
	private Label  l_time, l_date;
		

	@FXML
    public void initialize() {
		super.initialize();
		l_time.setText(String.format("%02d:%02d", wl.getHour(), wl.getMinute()));
		l_date.setText(String.format("%02d.%02d.20%02d", 
				wl.getDay(), wl.getMonth(), wl.getYear()));
	}
}
