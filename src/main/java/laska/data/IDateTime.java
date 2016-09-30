package laska.data;

public interface IDateTime{

	public void setDate(int yy, int mm, int dd);
	public void setTime(int hh, int mm);
	
	public int getYear();
	public int getMonth();
	public int getDay();
	public int getHour();
	public int getMinute();
	
	/**
	 * Повертає дату в форматі "yyyy/mm/dd hh:mm"
	 * @return
	 */
	public String jiraDateTime();
	
	public void setCurrentDateTime();
	public void setOptimalDateTime();
	public void setNullDateTime();
	public boolean isNullDateTime();
	
	public int getCompareValeu();
}
