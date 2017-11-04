package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ScheduledCommand implements Serializable, Comparable<ScheduledCommand> {

	private static final long serialVersionUID = 1L;
	private GregorianCalendar date;
	private TimeSlice repeat; // stores a millisecond value
	private boolean commandGroup;
	private ArrayList<CommandWithExecutor> commands = new ArrayList<CommandWithExecutor>();
	
	// Default Constructor
	public ScheduledCommand() {
		commands.add(new CommandWithExecutor("say this is an example command"));
		commandGroup = false;
		date = new GregorianCalendar();
		repeat = new TimeSlice(0, 0, 0, 0); // hour = minute = second = 0;
	}
	
	//copy constructor
	public ScheduledCommand(ArrayList<CommandWithExecutor> commands2, GregorianCalendar date2, TimeSlice repeat2, boolean commandGroup2) {
		commands = commands2; // only has one command
		commandGroup = commandGroup2;
		date = date2;
		repeat = repeat2;
	}
	
	//main constructor
	public ScheduledCommand(ArrayList<CommandWithExecutor> commandStrings, GregorianCalendar Adate, TimeSlice Arepeat) {
		commands = commandStrings; // only has one command
		commandGroup = false;
		date = Adate;
		repeat = Arepeat;
	}

	// Single command constructor
	public ScheduledCommand(String Acommand, GregorianCalendar Adate, TimeSlice Arepeat) {
		commands.add(new CommandWithExecutor("Acommand")); // only has one command
		commandGroup = false;
		date = Adate;
		repeat = Arepeat;
	}

	//for non repeating commands
	public ScheduledCommand(GregorianCalendar aDate, String aCommand) {
		commands.add(new CommandWithExecutor(aCommand));
		commandGroup = false;
		date = aDate;
	}
	//TODO  change arrays.copyofrange to O.split(" ", 2) -> more efficient
	public void setCommand(String[] args, int index){
		String aCommand;
		String anExecutor = "CONSOLE";
		
		//CONSOLE running is default. The option to make it console run is left out because of that. 
		if(args[0].equalsIgnoreCase("ALLPLAYERS")){
			aCommand = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
			anExecutor = "ALLPLAYERS";
		}
		else if(args[0].equalsIgnoreCase("PLAYER")){ 
			aCommand = String.join(" ", Arrays.copyOfRange(args, 2, args.length)); //Second argument is player name eg. PLAYER spartagon123 say hi
			anExecutor = args[1];
			System.out.println("Warning: Player commands will only work if the player is online when the command runs");
		}
		else { 
			aCommand = String.join(" ", Arrays.copyOfRange(args, 0, args.length)); //Default commands that are console run
			anExecutor = "CONSOLE";
		}
		
		//If its a simple add command
		if(index == -1){
			commands.add(new CommandWithExecutor(aCommand, anExecutor));
		}
		else {
			commands.set(index, new CommandWithExecutor(aCommand, anExecutor));
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

	public void setCommand(String aCommand) {
		commands.set(0, new CommandWithExecutor(aCommand));
	}

	public void setCommands(ArrayList<CommandWithExecutor> aCommand) {
		commands = aCommand;
	}
	
	public boolean getCommandGroup() {
		return commandGroup;
	}

	@Override
	public String toString() {
		String dateString = (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) + "/"
				+ date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
		
		if(!commandGroup) {
			return "Command = " + commands + ", Date = " + dateString;
		}
		else {
			return "Commands = " + commands + ", Date = " + dateString;
		}
	}
}
