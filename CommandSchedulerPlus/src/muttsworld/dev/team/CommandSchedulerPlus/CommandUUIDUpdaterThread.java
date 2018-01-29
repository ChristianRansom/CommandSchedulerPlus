package muttsworld.dev.team.CommandSchedulerPlus;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandUUIDUpdaterThread implements Runnable {
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	private CommandWithExecutor currentCommand;
	private CommandSchedulerPlus plugin;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	public CommandUUIDUpdaterThread(CommandWithExecutor aCommand, CommandSchedulerPlus plugin) {
		this.currentCommand = aCommand;
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		//System.out.println("Updating the playername based on saved UUID");
		this.currentCommand.updateUUIDCommand(); //Ensures UUID check before command execution
		Player player = plugin.getServer().getPlayer(currentCommand.getExecutor());
		if(player != null) {
			Bukkit.dispatchCommand(player, this.currentCommand.getCommandString());
		}
		else {
			console.sendMessage(PluginMessages.prefix + PluginMessages.error +  "Player not found. ");
		}
	}

	public void start() {
		//System.out.println("Starting thread");
		t = new Thread(this, "CommandLoaderThread");
		t.start();
	}
}
