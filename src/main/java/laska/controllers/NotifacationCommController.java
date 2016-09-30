package laska.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import laska.data.Conf;
import laska.data.IComment;
import laska.jinfo.App;

public class NotifacationCommController {
	
	private final IComment comm;
	
	public NotifacationCommController(IComment comment){
		comm = comment;
	}
	
	@FXML
	protected Label  l_aut, l_body, key;
	
	@FXML
	private Hyperlink h_link;
	

	@FXML
    public void initialize() {
		l_aut.setText(comm.getAuthor());
		l_body.setText(comm.getDescription());
		key.setText(comm.getKey());
	}
	
	@FXML
	private void open(){
		String url = Conf.getCong().getJiraUrl()+"/browse/"+comm.getKey();
		App.app.openInBrouser(url);
	}
	
}
