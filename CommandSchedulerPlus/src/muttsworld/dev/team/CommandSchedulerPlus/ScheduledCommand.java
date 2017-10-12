package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledCommand {

	private String command;
	private GregorianCalendar date;
	
	
	public ScheduledCommand() {
		command = "";
		date = new GregorianCalendar();
	}
	
	public ScheduledCommand(GregorianCalendar aDate, String aCommand){
		command = aCommand;
		date = aDate; 
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
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
	
	
	
}
