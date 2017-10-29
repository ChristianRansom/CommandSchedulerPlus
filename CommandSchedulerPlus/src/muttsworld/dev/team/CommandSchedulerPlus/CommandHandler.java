package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;


public class CommandHandler implements CommandExecutor{
	private final AVLTree<ScheduledCommand> commands; //the list of commands
	public CommandSchedulerPlus plugin;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private CommandCreator commandCreator;

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
    public CommandHandler(AVLTree<ScheduledCommand> commands2, CommandSchedulerPlus commandSchedulerPlus) {
    	commands = commands2;
    	plugin = commandSchedulerPlus;
    	commandCreator = new CommandCreator(commands);
	}
    //Called when a command registered to the plugin in the plugin.yml is entered
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//This is longer than it needs to be so I can separate out the code into different classes...
		if(commandCreator.groupCommandEditor){ 
			return commandCreator.createCommandGroup(args);
		}
		else if(commandCreator.commandEditor){
			return commandCreator.commandEditor(args);
		}
		else if(args[0].equals("create")){
			return commandCreator.createCommand();
		}
		else if(args[0].equals("forceupdate")){
			return forceupdate();
		}
		else if(args[0].equals("forcerun")){
			return forceRun(args);
		}
		else if(args[0].equals("listcommands") || args[0].equals("commandlist") || args[0].equals("list")){
			return listCommands();
		}
		else if(args[0].equals("edit")){
			return commandCreator.editCommand(args);
		}
		else if(args[0].equals("delete")){
			return deleteCommand(args);
		}
		else if(args[0].equals("preorder")){
			commands.preOrder();
			return true;
		}
		else if(args[0].equals("updatesize")){
			commands.updateSize();
			return true;
		}
		else if(args[0].equals("time")){
			System.out.print(commandCreator.displayDate(new GregorianCalendar()));
			return true;
		}
		else {
			System.out.println("Uknown command usage");
			return true;
		}
	}
	
	private boolean forceRun(String[] args) {
		
		//System.out.println("Finding command " + args[1]);
		synchronized(commands) {
			ArrayList<String> tempCommandGroup = commands.find(Integer.parseInt(args[1])).getCommands();
			System.out.println(tempCommandGroup.size() + " commands found");
			for(String aCommand : tempCommandGroup){
				System.out.println("Running: " + aCommand);
				Bukkit.dispatchCommand(console, aCommand);
			}
		}
		return true;
	}
	private boolean deleteCommand(String[] args) {
		System.out.println("Finding command " + args[1]);
		synchronized(commands) {
			commands.delete(commands.find(Integer.parseInt(args[1])));
		}
		return true; 
	}
		
	private boolean listCommands() {
		synchronized(commands){
			commands.inOrder();
		}
		return true;
	}

	private boolean forceupdate() {
		CommandRunnerThread thread = new CommandRunnerThread(commands, plugin);
		thread.start();
		return true;
	}
}
