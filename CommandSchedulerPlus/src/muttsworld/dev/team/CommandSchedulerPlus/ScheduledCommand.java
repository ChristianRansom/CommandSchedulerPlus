package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledCommand implements Serializable, Comparable<ScheduledCommand>{

	private static final long serialVersionUID = 1L;
	private String command;
	private GregorianCalendar date;
	
	//Default Constructor
	public ScheduledCommand() {
		command = "No Command Given";
		date = new GregorianCalendar();
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
	
	
	
}
