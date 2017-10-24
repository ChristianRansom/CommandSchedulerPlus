package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledCommand implements Serializable, Comparable<ScheduledCommand>{

	private static final long serialVersionUID = 1L;
	private String command;
	private GregorianCalendar date;
	private GregorianCalendar repeat;
	
	public ScheduledCommand(String command, GregorianCalendar date, GregorianCalendar repeat) {
		this.command = command;
		this.date = date;
		this.repeat = repeat;
	}

	//Default Constructor
	public ScheduledCommand() {
		command = "No Command Given";
		date = new GregorianCalendar();
		//month = day = year = hour = minute = 0; - default construct creates a date that matched current time
		repeat = new GregorianCalendar(0, 0, 0, 0, 0);
	}
	
	public ScheduledCommand(GregorianCalendar aDate, String aCommand){
		command = aCommand;
		date = aDate; 
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String aCommand) {
		this.command = aCommand;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	@Override
	public String toString() {
		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
							+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
							":" + date.get(Calendar.MINUTE);

		return "ScheduledCommand [command = " + command + ", date = " + dateString + "]";
	}

	@Override
	public int compareTo(ScheduledCommand otherCommand) {
		return this.getDate().compareTo(otherCommand.getDate());		
	}

	public GregorianCalendar getRepeat() {
		return repeat;
	}

	public void setRepeat(GregorianCalendar repeat) {
		this.repeat = repeat;
	}
	
	public ScheduledCommand copy(){  
		return new ScheduledCommand(this.command, this.date, this.repeat); 
	}  
	
	
	
}
