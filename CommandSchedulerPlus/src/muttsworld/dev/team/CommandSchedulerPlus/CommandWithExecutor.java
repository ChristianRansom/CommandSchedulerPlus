package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.spi.DirStateFactory.Result;

import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

public class CommandWithExecutor implements Serializable, Comparable<CommandWithExecutor> {

	private ArrayList<String> commandString = new ArrayList<String>();
	private String executor;
	private UUID executorUUID;
	private static final long serialVersionUID = 1L;
	 //Indexes in the command string the location of players that the user wants to protect the UUID
	private Map<Integer, UUID> UUIDProtectedNames = new HashMap<Integer, UUID>();

	//Default constructor
	public CommandWithExecutor() {
		commandString = new ArrayList<String>(Arrays.asList(new String[] 
				{"say", "this", "is", "an", "example", "command"}));
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
		this.executor = anExecutor;
		//convert array to array list
		ArrayList<String> commandArrayList = new ArrayList<String>(Arrays.asList(aCommand));
		this.commandString = commandArrayList;
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
	
	//WARNING this method must be called from outside the main thread. This is an expensive lookup
	public boolean saveExecutorUUID(CommandSender sender) {
		if (!this.executor.equalsIgnoreCase("CONSOLE")
				&& !this.executor.equalsIgnoreCase("ALLPLAYERS")) {
			try {
				UUIDProtectedNames.put(0, ProfileUtils.lookup(executor).getId()); 
			}
			catch (Exception e) {
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error 
								+ "UUID not found for " + ChatColor.YELLOW + executor);
				sender.sendMessage(PluginMessages.prefix + ChatColor.YELLOW 
						+ executor + ChatColor.WHITE + 
						" has been saved without UUID protection.");
			}
			//this.executorUUID = UUIDFetcher.getUUID(executor);
			//System.out.println("UUID is saved as " + this.executorUUID);
		}
		int i = 0;
		boolean result = true;
		for(Iterator<String> iterator = commandString.iterator(); iterator.hasNext(); ){ //read in arguments starting with --
			String value = iterator.next();
			if(!value.equals("") && value.length() < 4 && value.charAt(0) == '-' && value.charAt(1) == '-'){
				if(value.charAt(2) == 'p'){
					iterator.remove(); //removing the --p
					try {
						UUIDProtectedNames.put(i, ProfileUtils.lookup(commandString.get(i)).getId());
					}
					catch (Exception e) {
						sender.sendMessage(PluginMessages.prefix + PluginMessages.error 
										+ "UUID not found for " + ChatColor.YELLOW + commandString.get(i));
						sender.sendMessage(PluginMessages.prefix + ChatColor.YELLOW 
								+ commandString.get(i) + ChatColor.WHITE + 
								" has been saved without UUID protection.");
						result =  false;
					}
					i--;
				}
			}
			i++;
		}
		System.out.println(UUIDProtectedNames);

		return result;
	}
	
	//Uses a separate object UUIDProtectedName to store UUID and PlayerName and CommandStringIndex
	//Instead of using a 2 dimensional array. The UUID ArrayList is mapped with the playerIndex ArrayList
	public void updateUUIDCommand() {
		//Mojang API throttles profiles requests for the same profile to one request per minute.
		Iterator<Entry<Integer, UUID>> iterator = UUIDProtectedNames.entrySet().iterator();
		while(iterator.hasNext()){ 
			Entry<Integer, UUID> value = iterator.next();
			//System.out.println("Updating UUID " + value.getValue());
			String temp;
			//System.out.println("Key is " + value.getKey());
			try {
				temp = ProfileUtils.lookup(value.getValue()).getName();
				if(value.getKey() == 0) {
					if (!this.executor.equalsIgnoreCase("CONSOLE") //if its a player run command
							&& !this.executor.equalsIgnoreCase("ALLPLAYERS")) {
						this.executor = temp; 
						//System.out.println("Name from UUID is " + temp);
					}
				}
				else {
					//System.out.println("Name from UUID is " + temp);
					commandString.set(value.getKey(), "" + temp);
				}
			}
			catch (Exception e ){
				System.out.println("CSP Error looking up saved UUID. Player does not exist");
			}
		}
	}
	

	@Override
	public int compareTo(CommandWithExecutor o) {
		return String.join(" ", this.commandString).compareTo(String.join(" ", o.commandString));
	}

	public String getCommandString() {
		return String.join(" ", this.commandString);
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
