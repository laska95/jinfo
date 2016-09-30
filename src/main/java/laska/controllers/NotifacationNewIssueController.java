package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import laska.data.Conf;
import laska.data.INewIssue;
import laska.jinfo.App;

public class NotifacationNewIssueController {
	
	private final INewIssue ni;
	
	public NotifacationNewIssueController(INewIssue newIssue){
		ni = newIssue;
	}
	
	@FXML
	private Label l_key, l_type, l_sub, l_desc;
	
	@FXML
	private Hyperlink h_link;
	

	@FXML
    public void initialize() {
		l_key.setText(ni.getKey());
		l_type.setText(ni.getType());
		l_sub.setText(ni.getSummary());
		l_desc.setText(ni.getDescription());
	}
	
	@FXML
	private void open(){
		String url = Conf.getCong().getJiraUrl()+"/browse/"+ni.getKey();
		App.app.openInBrouser(url);
	}
}
