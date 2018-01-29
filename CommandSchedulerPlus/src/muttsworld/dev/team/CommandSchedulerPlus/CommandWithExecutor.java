package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;

public class CommandWithExecutor implements Serializable, Comparable<CommandWithExecutor> {

	private String[] commandString;
	private String executor;
	private UUID executorUUID;
	private static final long serialVersionUID = 1L;
	 //Indexes in the command string the location of players that the user wants to protect the UUID
	private LinkedList<Integer> playerIndexes;

	//Default constructor
	public CommandWithExecutor() {
		commandString = new String[] {"say", "this", "is", "an", "example", "command"};
		executor = "CONSOLE";
	}
	
	//main constructor
	public CommandWithExecutor(String[] args) {
		String[] aCommand;
		String anExecutor = "CONSOLE";
		// CONSOLE running is default. The option to make it console run is left
		// out because of that.
		if (args[0].equalsIgnoreCase("ALLPLAYERS")) {
			removeSlash(args, 1);
			aCommand =  Arrays.copyOfRange(args, 1, args.length);
			anExecutor = "ALLPLAYERS";
		} else if (args[0].equalsIgnoreCase("PLAYER")) {
			removeSlash(args, 2);
			//second argument is player name
			aCommand = Arrays.copyOfRange(args, 2, args.length); 
			anExecutor = args[1];
			// System.out.println("Warning: Player commands will only work if
			// the player is online when the command runs");
		} else {
			removeSlash(args, 0);
			//Default commands are console run
			aCommand = Arrays.copyOfRange(args, 0, args.length); 
			anExecutor = "CONSOLE";
		}
		this.commandString = aCommand;
		this.executor = anExecutor;
		this.playerIndexes = findPlayerIndexes(this.commandString);
	}
	
	public LinkedList<Integer> findPlayerIndexes(String[] args){
		LinkedList<Integer> playerList = new LinkedList<Integer>();
		for(int i = 1; i < args.length; i++){ //read in arguments starting with --
			if(!args[i].equals("") && args[i].length() < 4 && args[i].charAt(0) == '-' && args[i].charAt(1) == '-'){
				if(args[i].charAt(2) == 'p'){
					playerList.add(i + 1);
					System.out.println("Adding index " + (i + 1) + " to the playerIndexList"); 
				}
			}
		}
		return playerList;
	}
	
	@Override
	public int compareTo(CommandWithExecutor o) {
		return String.join(" ", this.commandString).compareTo(String.join(" ", o.commandString));
	}

	public String getCommandString() {
		return String.join(" ", this.commandString);
	}

	private void removeSlash(String[] args, int location) {
		if ((args[location].charAt(0)) == '/') {
			// System.out.println("The commands started with a /. Removing it
			// for storage");
			StringBuilder sb = new StringBuilder(args[location]);
			sb.deleteCharAt(0);
			String finalCommand = sb.toString();
			args[location] = finalCommand;
		}
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
			//System.out.println("Saving " + this);
			this.executorUUID = UUIDFetcher.getUUID(executor);
			//System.out.println("UUID is saved as " + this.executorUUID);
		}
	}
	
	public void updateUUIDCommand() {
		if (!this.executor.equalsIgnoreCase("CONSOLE")
				&& !this.executor.equalsIgnoreCase("ALLPLAYERS")) {
			//System.out.println("Updating UUID: " + this);
			this.executor = NameFetcher.getName(this.executorUUID);
			//System.out.println("Name updated from UUID as " + this.executorUUID);
		}
	}
	
	@Override
	public String toString(){
		if(executor.equalsIgnoreCase("CONSOLE")){
			return "/" + String.join(" ", this.commandString);
		}
		else {
			return executor + ": /" + String.join(" ", this.commandString);
		}
	}
}
