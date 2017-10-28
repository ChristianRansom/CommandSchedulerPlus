package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledCommand implements Serializable, Comparable<ScheduledCommand> {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> command = new ArrayList<String>();
	private GregorianCalendar date;
	private TimeSlice repeat; // stores a millisecond value
	private boolean commandGroup;

	// Default Constructor
	public ScheduledCommand() {
		command.add("No Command Given");
		commandGroup = false;
		date = new GregorianCalendar();
		repeat = new TimeSlice(0, 0, 0, 0); // hour = minute = second = 0;
	}
	
	//copy constructor
	public ScheduledCommand(ArrayList<String> command2, GregorianCalendar date2, TimeSlice repeat2, boolean commandGroup2) {
		command = command2; // only has one command
		commandGroup = commandGroup2;
		date = date2;
		repeat = repeat2;
	}
	
	//main constructor
	public ScheduledCommand(ArrayList<String> commandStrings, GregorianCalendar Adate, TimeSlice Arepeat) {
		command = commandStrings; // only has one command
		commandGroup = false;
		date = Adate;
		repeat = Arepeat;
	}

	// Single command constructor
	public ScheduledCommand(String Acommand, GregorianCalendar Adate, TimeSlice Arepeat) {
		command.add(Acommand); // only has one command
		commandGroup = false;
		date = Adate;
		repeat = Arepeat;
	}

	public ScheduledCommand(GregorianCalendar aDate, String aCommand) {
		command.add(aCommand);
		commandGroup = false;
		date = aDate;
	}

	public boolean isCommandGroup() {
		return commandGroup;
	}

	public void setCommandGroup(boolean commandGroup) {
		this.commandGroup = commandGroup;
	}

	public GregorianCalendar getDate() {
		return date;
	}

	public void setDate(GregorianCalendar date) {
		this.date = date;
	}

	@Override
	public int compareTo(ScheduledCommand otherCommand) {
		if (this.getDate().compareTo(otherCommand.getDate()) != 0) {
			return this.getDate().compareTo(otherCommand.getDate());
		} else if (this.getRepeat().compareTo(otherCommand.getRepeat()) != 0) {
			return this.getRepeat().compareTo(otherCommand.getRepeat());
		} else if (this.getCommand().compareTo(otherCommand.getCommand()) != 0) {
			return this.getCommand().compareTo(otherCommand.getCommand());
		} else {
			return 0;
		}
	}

	public TimeSlice getRepeat() {
		return repeat;
	}

	public void setRepeat(TimeSlice repeat) {
		this.repeat = repeat;
	}

	public ScheduledCommand copy() {
		return new ScheduledCommand(this.command, this.date, this.repeat, this.commandGroup);
	}

	public String getCommand() {
		return command.get(0);
	}

	public ArrayList<String> getCommands() {
		return command;
	}

	public void setCommand(String aCommand) {
		command.set(0, aCommand);
	}

	public void setCommands(ArrayList<String> aCommand) {
		command = aCommand;
	}

	@Override
	public String toString() {
		String dateString = (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) + "/"
				+ date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);

		return "ScheduledCommand [command = " + command + ", date = " + dateString + "]";
	}

	public boolean getCommandGroup() {
		return commandGroup;
	}

}
