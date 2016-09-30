package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import laska.data.Conf;
import laska.data.IIssue;
import laska.jinfo.App;

public class NotifacationIssueController {
	
	private final IIssue is;
	
	public NotifacationIssueController(IIssue issue){
		is = issue;
	}
	
	@FXML
	private Label l_key, l_type, l_sub, l_status;
	
	@FXML
	private Hyperlink h_link;
	

	@FXML
    public void initialize() {
		l_key.setText(is.getKey());
		l_type.setText(is.getType());
		l_sub.setText(is.getSummary());
		l_status.setText(is.getStatus());
	}
	
	@FXML
	private void open(){
		String url = Conf.getCong().getJiraUrl()+"/browse/"+is.getKey();
		App.app.openInBrouser(url);
	}
}
