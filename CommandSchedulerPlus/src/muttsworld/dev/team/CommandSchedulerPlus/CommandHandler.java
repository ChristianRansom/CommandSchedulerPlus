package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.GregorianCalendar;

import org.bukkit.ChatColor;

//Imports needed to properly register this as a CommandExecutor to receive events
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandHandler implements CommandExecutor {
	private final AVLTree<ScheduledCommand> commands; // the list of commands
	public CommandSchedulerPlus plugin;
	private CommandCreator commandCreator;
	private CommandSender sender;

	// Main Constructor - No default Constructor since I want to ensure the
	// thread is created with its fields given
	public CommandHandler(AVLTree<ScheduledCommand> commands2, CommandSchedulerPlus commandSchedulerPlus) {
		commands = commands2;
		plugin = commandSchedulerPlus;
		commandCreator = new CommandCreator(commands);
	}

	// Called when a command registered to the plugin in the plugin.yml is
	// entered
	@Override
	public boolean onCommand(CommandSender aSender, Command cmd, String label, String[] args) {
		sender = aSender;
		if (!sender.hasPermission("csp.admin")) { // I don't need this here.
													// bukkit blocks it before
													// this is even reached.
			sender.sendMessage(PluginMessages.prefix + "You do not have permission to perform this command!");
		}
		else if (commandCreator.groupCommandEditor) {
			commandCreator.commandGroupEditor(args, sender);
		} else if (commandCreator.commandEditor) {
			commandCreator.commandEditor(args, sender);
		} else if (args.length < 1) {
			helpInfo();
		} else if (args[0].equals("help")) {
			helpInfo();
		} else if (args[0].equals("interval")) {
			sender.sendMessage(PluginMessages.prefix + (long) plugin.getConfig().getDouble("CheckInterval"));			
		} else if (args[0].equals("create")) {
			if (args.length > 1) {
				commandCreator.quickCreate(args, sender);
			} else {
				commandCreator.createCommand(sender);
			}
		} else if (args[0].equals("forceupdate") || args[0].equals("forcecheck")) {
			forceupdate();
		} else if (args[0].equals("test")) {
			forceRun(args);
		} else if (args[0].equals("listcommands") || args[0].equals("commandlist") || args[0].equals("list")) {
			listCommands();
		} else if (args[0].equals("edit")) {
			commandCreator.editCommand(args, sender);
		} else if (args[0].equals("delete")) {
			deleteCommand(args);
		} else if (args[0].equals("preorder")) { // secret debugging command
			synchronized (commands) {
				commands.preOrder(sender);
			}
		} else if (args[0].equals("time")) {
			sender.sendMessage(PluginMessages.prefix + commandCreator.displayDate(new GregorianCalendar()));
		} else if (args[0].equals("reload")) {
			sender.sendMessage(PluginMessages.prefix + "Reloading CommandSchedulerPlus");
			plugin.reloadMyConfig();
		} else {
			sender.sendMessage(PluginMessages.prefix + PluginMessages.error + "Uknown command. Usage: " + PluginMessages.command + "/csp help");
		}
		return true;
	}

	private void helpInfo() {
		sender.sendMessage(ChatColor.YELLOW + "-------- " + ChatColor.BLUE + "Command Scheduler Plus "
				+ ChatColor.YELLOW + "--------"); // TODO update this with help
													// tips and all commands
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp create");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp delete <number>");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp edit <number>");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp time");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp test <number>");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp list");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp forceupdate");
	}

	private void forceRun(String[] args) {
		if (args.length < 2) {
			sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "Usage: " + PluginMessages.command + "/csp test <number>");
			return;
		}
		synchronized (commands) {
			plugin.runCommand(commands.find(Integer.parseInt(args[1])).getCommands());
		}
	}

	private void deleteCommand(String[] args) {
		if (args.length < 2) {
			sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "Usage: " 
					+ PluginMessages.command + "/csp delete <number>");
			return;
		}
		synchronized (commands) { // TODO Remove redundant parses throughout the
									// project
			if (Integer.parseInt(args[1]) <= commands.getSize() && Integer.parseInt(args[1]) > 0) {
				ScheduledCommand deleted = commands.find(Integer.parseInt(args[1]));
				if (commands.delete(deleted)) {
					sender.sendMessage(PluginMessages.prefix + deleted + " was deleted.");
				}
			} else {
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error + 
						"There is no command with that number. Use "
						+ PluginMessages.command + "/csp list " + PluginMessages.error + "to see the commands.");
			}
		}
	}

	private void listCommands() {
		sender.sendMessage(PluginMessages.prefix + "--------- Command List ---------");
		synchronized (commands) {
			if (commands.isEmpty()) {
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error + "There are no commands");
			}
			commands.inOrder(sender);
		}
	}

	private void forceupdate() {
		CommandRunnerThread thread = new CommandRunnerThread(commands, plugin);
		thread.start();
	}
}
