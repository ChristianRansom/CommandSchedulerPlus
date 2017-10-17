package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class MainThread implements Runnable {
	private Thread t;
	public RedBlackTree<ScheduledCommand> commands;
	private long sleepTime;
	private volatile boolean running = true;

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
	public MainThread(RedBlackTree<ScheduledCommand> commands2, long aSleepTime) {
		commands = commands2;
		sleepTime = aSleepTime;
	}

	@Override
	public void run() {
		System.out.println("DEBUG: Running thread");
		while(running){
			System.out.println("Running Commands");
			synchronized(commands){
				/*for(ScheduledCommand command : commands) {
					GregorianCalendar date = command.getDate();
					String dateString = (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) + "/"
							+ date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
					if (command.getDate().before(new GregorianCalendar())) {
						System.out.print(dateString + " is before now: " + new Date());
						System.out.print("DEBUG: running this command: " + command.getCommand());
						ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
						;
						Bukkit.dispatchCommand(console, command.getCommand());
					} else {
						System.out.print(dateString + " is NOT before now: " + new Date());
					}
				}*/
			}

			

			// Let the thread sleep for a while.
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted.");
			}
		}

		System.out.println("DEBUG: Thread exiting.");
	}

	public void start() {
		System.out.println("Starting thread");
		t = new Thread(this, "mainthread");
		t.start();
	}
	
	public void stopThread(){
		running = false;
	}
}
