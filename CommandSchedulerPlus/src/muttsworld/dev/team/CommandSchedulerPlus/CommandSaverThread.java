package muttsworld.dev.team.CommandSchedulerPlus;

import org.bukkit.command.CommandSender;

public class CommandSaverThread implements Runnable {
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	private ScheduledCommand currentCommand;
	private CommandSender sender;

	public CommandSaverThread(ScheduledCommand aCurrentCommand, AVLTree<ScheduledCommand> commands2, CommandSender sender2) {
		this.currentCommand = aCurrentCommand;
		this.commands = commands2;
		this.sender = sender2;
	}
	
	@Override
	public void run() {
		currentCommand.saveUUIDs(sender);
		synchronized(commands){
			commands.insert(currentCommand);
		}
		sender.sendMessage(PluginMessages.prefix + "Command saved. ");
	}

	public void start() {
		//System.out.println("Starting thread");
		t = new Thread(this, "CommandSaverThread");
		t.start();
	}
}
