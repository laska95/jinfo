package laska.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import laska.data.Conf;
import laska.data.IWorkLog;
import laska.jinfo.App;

public class NotifacationWLController {
	
	private final IWorkLog wl;
	
	public NotifacationWLController(IWorkLog workLog){
		wl = workLog;
	}
	
	@FXML
	protected Label  l_aut, l_body, key, title;
	
	@FXML
	private Hyperlink h_link;
	

	@FXML
    public void initialize() {
		l_aut.setText(wl.getAuthor());
		l_body.setText(wl.getDescription());
		key.setText(wl.getKey());
		title.setText("Додано робочий запис");
	}
	
	@FXML
	private void open(){
		String url = Conf.getCong().getJiraUrl()+"/browse/"+wl.getKey();
		App.app.openInBrouser(url);
	}
}
