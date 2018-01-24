package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class GroupCommandEditor {

	// Group Command Creation Variables
	private static int groupCommandEditorOption = 0;
	static boolean groupCommandEditor = false; // Value used to handle command groups
												
	// package visibility
	static void commandGroupEditor(String[] args, CommandSender sender) {
		switch (groupCommandEditorOption) {
		case 0:
			if (args.length < 1 || !NumberUtils.isNumber(args[0])) {
				displayCommandGroupMenu(CommandCreator.currentCommand.getCommands(), sender);
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error + " Use " + PluginMessages.command
						+ "/csp <number>" + PluginMessages.error + " to selection an option");
				return;
			}
			groupCommandEditorOption = Integer.parseInt(args[0]);
			if (groupCommandEditorOption > 0 && groupCommandEditorOption <= 5) {
				switch (groupCommandEditorOption) { // Should move all messages
													// here for maintainability
				case 1:
					sender.sendMessage(PluginMessages.prefix + "Enter the command you want to add to the group.");
					break;
				case 2:
					CommandCreator.showCommandGroup(sender);
					sender.sendMessage(PluginMessages.prefix
							+ "Enter the number of command you want to replace followed by the commmand");
					sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp <number> <command>");
					break;
				case 3:
					sender.sendMessage(PluginMessages.prefix + "Enter the number of the command you want to delete.");
					break;
				case 4:
					sender.sendMessage(PluginMessages.prefix + "Cleared all the commands");
					CommandCreator.currentCommand.getCommands().clear();
					displayCommandGroupMenu(CommandCreator.currentCommand.getCommands(), sender);
					groupCommandEditorOption = 0;
					break;
				case 5:
					sender.sendMessage(PluginMessages.prefix + "Command Group Saved");
					if (CommandCreator.currentCommand.getCommands().isEmpty()) {
						sender.sendMessage(PluginMessages.prefix + PluginMessages.error
								+ "No commands found in the command group. Adding a default command");
						String defaultCommand[] = { "this", "is", "an", "example", "command" };
						CommandCreator.currentCommand.setCommand(defaultCommand, -1);
					} else {
						CommandCreator.currentCommand.setCommandGroup(true);
					}
					groupCommandEditorOption = 0;
					groupCommandEditor = false;
					CommandCreator.displayCommandMenu(CommandCreator.currentCommand, sender);
					break;
				}
			} else {
				CommandCreator.displayCommandMenu(CommandCreator.currentCommand, sender);
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error + "Enter an option from 1 to 5");
				groupCommandEditorOption = 0;
			}
			break; // Breaks from outer switch case
		case 1: // handle new command being added to the commmandGroup
			if (args.length < 1) {
				CommandCreator.showCommandGroup(sender);
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error
						+ "Enter the command you want to add to the group.");
				return;
			} else {
				CommandCreator.currentCommand.setCommand(args, -1);
				groupCommandEditorOption = 0;
			}
			displayCommandGroupMenu(CommandCreator.currentCommand.getCommands(), sender);
			break;
		case 2: // handle command replace
			if (args.length < 2 || !NumberUtils.isNumber(args[0])) {
				commandGroupInsertError(sender);
				return;
			}
			int replaceIndex = Integer.parseInt(args[0]);
			if (replaceIndex - 1 < CommandCreator.currentCommand.getCommands().size() && replaceIndex - 1 >= 0) {
				if ((args[1].charAt(0)) == '/') {
					StringBuilder sb = new StringBuilder(args[1]);
					sb.deleteCharAt(0);
					String finalCommand = sb.toString();
					args[1] = finalCommand;
				}
				CommandCreator.currentCommand.setCommand(Arrays.copyOfRange(args, 1, args.length),
						Integer.parseInt(args[0]) - 1);
				groupCommandEditorOption = 0;
			} else {
				commandGroupInsertError(sender);
				return;
			}
			displayCommandGroupMenu(CommandCreator.currentCommand.getCommands(), sender);
			break;
		case 3: // handle command delete
			if (args.length < 1 || !NumberUtils.isNumber(args[0])) {
				CommandCreator.showCommandGroup(sender);
				sender.sendMessage(PluginMessages.prefix + PluginMessages.error
						+ "Enter the number of the command you want to delete from the command group.");
				return;
			} 
			CommandCreator.currentCommand.getCommands().remove(Integer.parseInt(args[0]) - 1);
			groupCommandEditorOption = 0;
			displayCommandGroupMenu(CommandCreator.currentCommand.getCommands(), sender);
			break;
		}
	}

	static void displayCommandGroupMenu(ArrayList<CommandWithExecutor> arrayList, CommandSender sender) {
		groupCommandEditor = true;// Not sure if I can move this here...
		sender.sendMessage("");
		sender.sendMessage(
				PluginMessages.prefix + ChatColor.BOLD + ChatColor.DARK_AQUA + "---------Command Group---------");
		sender.sendMessage(PluginMessages.prefix + "Use " + PluginMessages.command + "/csp <number>" + ChatColor.WHITE
				+ " to selection an option");
		CommandCreator.showCommandGroup(sender);
		sender.sendMessage(PluginMessages.prefix + "1: Add a command. ");
		sender.sendMessage(PluginMessages.prefix + "2: Replace a command. ");
		sender.sendMessage(PluginMessages.prefix + "3: Delete a command. ");
		sender.sendMessage(PluginMessages.prefix + "4: Clear all commands.");
		sender.sendMessage(PluginMessages.prefix + "5: Save and Exit.");
	}

	private static void commandGroupInsertError(CommandSender sender) {
		CommandCreator.showCommandGroup(sender);
		sender.sendMessage(PluginMessages.prefix + PluginMessages.error
				+ "Enter the number of the command you want to replace followed by the command you want to enter.");
		sender.sendMessage(PluginMessages.prefix + PluginMessages.command + "/csp <number> <command>");
	}

}
