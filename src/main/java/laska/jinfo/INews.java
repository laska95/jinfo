package laska.jinfo;

import java.io.IOException;
import javafx.scene.layout.AnchorPane;
import laska.data.DateTime;
import laska.data.IDateTime;

public interface INews extends Comparable<INews>{
	public AnchorPane loadNotificationView() throws IOException;
	public AnchorPane loadHistoryView() throws IOException;
	public AnchorPane loadView()throws IOException;
	
	public int getCompareValeu();
}
