package muttsworld.dev.team.CommandSchedulerPlus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;//used on plugin prefix
import org.bukkit.command.ConsoleCommandSender;

public class MainThread extends CommandRunnerThread {
	
	private long sleepTime;
	private volatile boolean running = true;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
	public MainThread(AVLTree<ScheduledCommand> commands2, long aSleepTime, CommandSchedulerPlus commandSchedulerPlus) {
		super(commands2, commandSchedulerPlus);
		sleepTime = aSleepTime;
	}

	@Override
	public void run() {
		while(running){
			super.run();
			// Let the thread sleep for a while.
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				console.sendMessage(plugin.prefix + "Thread interrupted.");
			}
	
			console.sendMessage(plugin.prefix + "Thread exiting.");
		}
	}
	
	public void stopThread(){
		running = false;
	}
}
