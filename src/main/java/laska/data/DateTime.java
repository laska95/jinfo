package laska.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTime implements IDateTime{
	
	/**якщо задача, комент чи wl записані в БД
	 * з цією датою, то вони не відображаюьбся в історії
	 */
	private static final int NULL_DATE = 101;

	protected Integer date;
	protected Integer time;
	
	public DateTime(){
		date = 101;
		time = 0;
	}
	
	/*==================================================================
	 * Set
	 *==================================================================*/
	
	public void setDate(int yy, int mm, int dd){
		this.date = yy*10000 + mm*100 + dd;
	}
	
	public void setTime(int hh, int mm){
		this.time = hh*100+mm;
	}
	/*==================================================================
	 * Get
	 *==================================================================*/
	
	public int getYear(){
		return (int)(this.date/10000);
	}
	
	public int getMonth(){
		return (int)(this.date/100 - getYear()*100);
	}
	
	public int getDay(){
		return (int)(this.date%100);
	}

	public int getHour(){
		return this.time/100;
	}
	
	public int getMinute(){
		return this.time%100;
	}

	@Override
	public String jiraDateTime() {
		return String.format("20%02d-%02d-%02d %02d:%02d",
				getYear(), getMonth(), getDay(), getHour(), getMinute());
	}

	@Override
	public void setCurrentDateTime() {
		LocalDate ld = LocalDate.now();
		LocalTime lt = LocalTime.now();
		setDate(ld.getYear()%100, ld.getMonthValue(), ld.getDayOfMonth());
		setTime(lt.getHour(), lt.getMinute());		
	}

	/**
	 * Оптимальною є час за 12 год до поточного
	 */
	@Override
	public void setOptimalDateTime() {
		LocalDateTime t = LocalDateTime.now();
		t = t.minusHours(12);
		this.setDate(t.getYear()%100, t.getMonthValue(), t.getDayOfMonth());
		this.setTime(t.getHour(), t.getMinute());	
	}

	@Override
	public void setNullDateTime() {
		date = NULL_DATE;
		time = 0;
	}

	@Override
	public boolean isNullDateTime() {
		return date==NULL_DATE?true:false;
	}

	@Override
	public int getCompareValeu() {
		return (getYear()*10000+getMonth()*100+getDay())*10000+
				getHour()*100+getMinute();
	}}
