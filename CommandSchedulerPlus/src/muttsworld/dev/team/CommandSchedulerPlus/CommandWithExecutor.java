package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;

public class CommandWithExecutor implements Serializable, Comparable<CommandWithExecutor> {

	private String commandString;
	private String executor;
	private static final long serialVersionUID = 1L;

	public CommandWithExecutor(String aCommand) {
		commandString = aCommand;
		executor = "CONSOLE";
	}

	public CommandWithExecutor(String aCommand, String anExecutor) {
		commandString = aCommand;
		executor = anExecutor;
	}

	@Override
	public int compareTo(CommandWithExecutor o) {
		return this.commandString.compareTo(o.commandString);
	}

	public String getCommandString() {
		return commandString;
	}

	public void setCommandString(String commandString) {
		this.commandString = commandString;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}
	
	public String toString(){
		if(executor.equalsIgnoreCase("CONSOLE")){
			return commandString;
		}
		else {
			return executor + ": " + commandString;
		}
	}

}
