package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledCommand implements Serializable, Comparable<ScheduledCommand>{

	private static final long serialVersionUID = 1L;
	private String command;
	private GregorianCalendar date;
	private RepeatTime repeat; //stores a millisecond value
	
	public ScheduledCommand(String Acommand, GregorianCalendar Adate, RepeatTime Arepeat) {
		this.command = Acommand;
		this.date = Adate;
		this.repeat = Arepeat;
	}

	//Default Constructor
	public ScheduledCommand() {
		command = "No Command Given";
		date = new GregorianCalendar();
		//hour = minute = second = 0;
		repeat = new RepeatTime(0, 0, 0, 0);
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
	public int compareTo(ScheduledCommand otherCommand) {
		return this.getDate().compareTo(otherCommand.getDate());		
	}

	public RepeatTime getRepeat() {
		return repeat;
	}

	public void setRepeat(RepeatTime repeat) {
		this.repeat = repeat;
	}
	
	public ScheduledCommand copy(){  
		return new ScheduledCommand(this.command, this.date, this.repeat); 
	}  
	
	@Override
	public String toString() {
		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
							+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
							":" + date.get(Calendar.MINUTE);

		return "ScheduledCommand [command = " + command + ", date = " + dateString + "]";
	}

	
}
