package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.bukkit.ChatColor;

public class ScheduledCommand implements Serializable, Comparable<ScheduledCommand> {

	private static final long serialVersionUID = 1L;
	private GregorianCalendar date;
	private TimeSlice repeat; // stores a millisecond value
	private boolean commandGroup;
	private ArrayList<CommandWithExecutor> commands = new ArrayList<CommandWithExecutor>();

	// Default Constructor
	public ScheduledCommand() {
		commands.add(new CommandWithExecutor());
		commandGroup = false;
		date = new GregorianCalendar();
		repeat = new TimeSlice(0, 0, 0, 0); // hour = minute = second = 0;
	}

	// copy constructor
	public ScheduledCommand(ArrayList<CommandWithExecutor> commands2, GregorianCalendar date2, TimeSlice repeat2,
			boolean commandGroup2) {
		commands = commands2; // only has one command
		commandGroup = commandGroup2;
		date = date2;
		repeat = repeat2;
	}

	// main constructor
	public ScheduledCommand(ArrayList<CommandWithExecutor> commandStrings, GregorianCalendar Adate, TimeSlice Arepeat) {
		commands = commandStrings; // only has one command
		commandGroup = false;
		date = Adate;
		repeat = Arepeat;
	}

	// Single command constructor
	public ScheduledCommand(String[] aCommand, GregorianCalendar Adate, TimeSlice Arepeat) {
		commands.add(new CommandWithExecutor(aCommand)); // only has one command
		commandGroup = false;
		date = Adate;
		repeat = Arepeat;
	}

	// for non repeating commands
	public ScheduledCommand(GregorianCalendar aDate, String[] aCommand) {
		commands.add(new CommandWithExecutor(aCommand));
		commandGroup = false;
		date = aDate;
	}

	//For quick create commands without command specified
	public ScheduledCommand(GregorianCalendar dateEntry, TimeSlice repeatSlice) {
		commands.add(new CommandWithExecutor()); //Default constructor uses default command
		commandGroup = false;
		date = dateEntry;
		repeat = repeatSlice;
	}

	public void setCommand(String[] args, int index) {
		// If its a simple add command adds to end of list
		if (index == -1) {
			commands.add(new CommandWithExecutor(args));
		} else {
			commands.set(index, new CommandWithExecutor(args));
		}
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
		} else if (this.getCommand().compareTo(otherCommand.getCommand()) != 0) {
			return this.getCommand().compareTo(otherCommand.getCommand());
		} else if (this.getRepeat().compareTo(otherCommand.getRepeat()) != 0) {
			return this.getRepeat().compareTo(otherCommand.getRepeat());
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
		return new ScheduledCommand(this.commands, this.date, this.repeat, this.commandGroup);
	}

	public CommandWithExecutor getCommand() {
		return commands.get(0);
	}

	public ArrayList<CommandWithExecutor> getCommands() {
		return commands;
	}

	public void setCommand(String[] aCommand) {
		commands.set(0, new CommandWithExecutor(aCommand));
	}

	public void setCommands(ArrayList<CommandWithExecutor> aCommand) {
		commands = aCommand;
	}

	public boolean getCommandGroup() {
		return commandGroup;
	}

	public void saveUUIDs() {
		for (CommandWithExecutor aCommand : commands) {
			aCommand.saveExecutorUUID();
		}
	}

	@Override
	public String toString() {
		String dateString = (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) + "/"
				+ date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) + ":";
		if (date.get(Calendar.MINUTE) < 10) {
			dateString += ("0" + date.get(Calendar.MINUTE));
		} else {
			dateString += date.get(Calendar.MINUTE);
		}

		if (!commandGroup) {
			return "Command = " + ChatColor.GREEN + commands.get(0).toString() + ChatColor.WHITE + ", Date = "
					+ dateString;
		} else {
			String multiCommand = "Commands = ";
			for (CommandWithExecutor aCommand : commands) {
				multiCommand += ChatColor.GREEN + aCommand.toString() + " | ";
			}
			return multiCommand + ChatColor.WHITE + "Date = " + dateString;
		}
	}

}
