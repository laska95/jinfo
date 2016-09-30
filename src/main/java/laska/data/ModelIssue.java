package laska.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModelIssue extends DateTime implements IIssue{
	
	protected String key = "";
	protected String type = "";
	protected String summary = "";
	protected String status = "";
	protected int ifStatus;
	protected int ifComm;
	protected int ifWL;
	
	public ModelIssue(){
		super();
		ifStatus = 0;
		ifComm = 0;
		ifWL = 0;
	}
	
	/*==================================================================
	 * Set
	 *==================================================================*/
		
	public void setKey(String key){
		this.key = key.substring(0);
	}
	
	public void setSummary(String summary){
		this.summary = summary.substring(0);
	}

	public void setType(String type){
		this.type = type.substring(0);
	}
	
	public void setStatus(String status){
		this.status = status.substring(0);
	}
	
	public void setIfStatus(int b){
		this.ifStatus = b;
	}
	
	public void setIfComm(int b){
		this.ifComm = b;
	}
	
	public void setIfWL(int b){
		this.ifWL = b;
	}
	
	/*==================================================================
	 * Get
	 *==================================================================*/
		
	public String getKey(){
		return this.key;
	}
	
	public String getSummary(){
		return this.summary;
	}

	public String getType(){
		return this.type;
	}
	
	public String getStatus(){
		return this.status;
	}

	public int getIfStatus(){
		return this.ifStatus;
	}
	
	public int getIfComm(){
		return this.ifComm;
	}
	
	public int getIfWL(){
		return this.ifWL;
	}

	@Override
	public String toString() {
		return key;
	}

	@Override
	public IDateTime getOptimalDate() throws Exception {
		List<IDateTime> dt = new ArrayList();
		if (ifStatus>0) dt.add(this);
		DBManager db = DBManager.getDBManager();
		if (ifComm>0) dt.add(db.getComment(key));
		if (ifWL>0) dt.add(db.getWorkLog(key));
		if (dt.isEmpty()) return null;
		//сортуємо в порядку зростання
		Comparator<IDateTime> comp = new Comparator<IDateTime>() {
			@Override
			public int compare(IDateTime t1, IDateTime t2) {
				int d1 = t1.getCompareValeu();
				int d2 = t2.getCompareValeu();
				if (d1==d2) return 0;
				if (d1>d2) return 1;
				else return -1;
			}
		};
		Collections.sort(dt, comp);
		return dt.get(0);
	}
	
	
}
