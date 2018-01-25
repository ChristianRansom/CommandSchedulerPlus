package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.UUID;

public class CommandWithExecutor implements Serializable, Comparable<CommandWithExecutor> {

	private String commandString;
	private String executor;
	private UUID executorUUID;
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
	
	public UUID getExecutorUUID() {
		return executorUUID;
	}

	//WARNING this method must be called from outside the main thread. This is an expensive lookup
	public void saveExecutorUUID() {
		if (!this.executor.equalsIgnoreCase("CONSOLE")
				&& !this.executor.equalsIgnoreCase("ALLPLAYERS")) {
			System.out.println("Saving " + this);
			this.executorUUID = UUIDFetcher.getUUID(executor);
		}
	}
	
	@Override
	public String toString(){
		if(executor.equalsIgnoreCase("CONSOLE")){
			return "/" + commandString;
		}
		else {
			return executor + ": /" + commandString;
		}
	}

}
