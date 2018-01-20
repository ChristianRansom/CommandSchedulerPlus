package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.GregorianCalendar;

import org.bukkit.ChatColor;

//Imports needed to properly register this as a CommandExecutor to receive events
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandHandler implements CommandExecutor{
	private final AVLTree<ScheduledCommand> commands; //the list of commands
	public CommandSchedulerPlus plugin;
	private CommandCreator commandCreator;
	private CommandSender sender;

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its fields given
    public CommandHandler(AVLTree<ScheduledCommand> commands2, CommandSchedulerPlus commandSchedulerPlus) {
    	commands = commands2;
    	plugin = commandSchedulerPlus;
    	commandCreator = new CommandCreator(commands, plugin);
	}
    //Called when a command registered to the plugin in the plugin.yml is entered
	@Override
    public boolean onCommand(CommandSender aSender, Command cmd, String label, String[] args) {
		sender = aSender;
		if(!sender.hasPermission("csp.admin")){ //I don't need this here. bukkit blocks it before this is even reached. 
			sender.sendMessage(plugin.prefix + "You do not have permission to perform this command!");
			return true;
		}
		if(commandCreator.groupCommandEditor){ 
			return commandCreator.createCommandGroup(args, sender);
		}
		else if(commandCreator.commandEditor){
			return commandCreator.commandEditor(args, sender);
		}
		else if(args.length < 1){
			return helpInfo();
		}	
		else if(args[0].equals("help")){
			return helpInfo();
		}
		else if(args[0].equals("interval")){
			sender.sendMessage(plugin.prefix + (long)plugin.getConfig().getDouble("CheckInterval"));
			return true;
		}
		else if(args[0].equals("create")){
			if(args.length > 1){
				return commandCreator.quickCreate(args);
			}
			else {
				return commandCreator.createCommand(sender);
			}
		}
		else if(args[0].equals("forceupdate") || args[0].equals("forcecheck")){
			return forceupdate();
		}
		else if(args[0].equals("test")){
			return forceRun(args);
		}
		else if(args[0].equals("listcommands") || args[0].equals("commandlist") || args[0].equals("list")){
			return listCommands();
		}
		else if(args[0].equals("edit")){
			return commandCreator.editCommand(args, sender);
		}
		else if(args[0].equals("delete")){
			return deleteCommand(args);
		}
		else if(args[0].equals("preorder")){ //secret debugging command
			synchronized(commands) {
				commands.preOrder(sender, plugin);
			}
			return true;
		}
		else if (args[0].equals("time")){
			sender.sendMessage(plugin.prefix + commandCreator.displayDate(new GregorianCalendar()));
			return true;
		}
		else if(args[0].equals("reload")){
			sender.sendMessage(plugin.prefix + "Reloading CommandSchedulerPlus");
			plugin.reloadMyConfig();
			return true;
		}
		else {
			sender.sendMessage(plugin.prefix + plugin.error + "Uknown command. Usage: " + plugin.command + "/csp help");
			return true;
		}
	}
	
	private boolean helpInfo() {
		sender.sendMessage(ChatColor.YELLOW + "-------- " + ChatColor.BLUE + 
				"Command Scheduler Plus " + ChatColor.YELLOW + "--------"); //TODO update this with help tips and all commands
		sender.sendMessage(plugin.prefix + plugin.command + "/csp create");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp delete <number>");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp edit <number>");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp time");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp test <number>");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp list");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp forceupdate");
		return true;
	}
	private boolean forceRun(String[] args) {
		if(args.length < 2){
			sender.sendMessage(plugin.prefix + plugin.command + "Usage: " + plugin.command +  "/csp test <number>");
			return true;
		}
		synchronized(commands) {
			plugin.runCommand(commands.find(Integer.parseInt(args[1])).getCommands());
		}
		return true;
	}
	private boolean deleteCommand(String[] args) {
		if(args.length < 2){
			sender.sendMessage(plugin.prefix + plugin.command + "Usage: " + plugin.command +  "/csp delete <number>");
			return true;
		}
		synchronized(commands) {  //TODO Remove redundant parses throughout the project
			if(Integer.parseInt(args[1]) <= commands.getSize() && Integer.parseInt(args[1]) > 0){
				ScheduledCommand deleted = commands.find(Integer.parseInt(args[1]));
				if(commands.delete(deleted)){
					sender.sendMessage(plugin.prefix + deleted + " was deleted.");
					return true;
				}
			}
			else {
				sender.sendMessage(plugin.prefix + plugin.error + "There is no command with that number. Use " 
						+ plugin.command + "/csp list " + plugin.error + "to see the commands.");
				return true;
			}
		}
		return true; 
	}
		
	private boolean listCommands() {
		sender.sendMessage(plugin.prefix + "--------- Command List ---------");
		synchronized(commands){
			if(commands.isEmpty()){	
				sender.sendMessage(plugin.prefix + plugin.error + "There are no commands");
			}
			commands.inOrder(sender, plugin);
		}
		return true;
	}

	private boolean forceupdate() {
		CommandRunnerThread thread = new CommandRunnerThread(commands, plugin);
		thread.start();
		return true;
	}
}
