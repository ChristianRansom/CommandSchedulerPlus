package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

public class MainThread implements Runnable {
	private Thread t;
	public ArrayList<ScheduledCommand> commands = 
			(ArrayList<ScheduledCommand>) Collections.synchronizedList(new ArrayList<ScheduledCommand>());
	private long sleepTime;
    private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();;
    
	public MainThread(ArrayList<ScheduledCommand> theCommands, long aSleepTime) {
		commands = theCommands;
		sleepTime = aSleepTime;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		 System.out.println("DEBUG: Running thread" );
	      try {
	         do {
	            System.out.println("Running Commands");
	            
	            for(ScheduledCommand aCommand : commands){
		            Bukkit.dispatchCommand(console, aCommand.getCommand());
		            System.out.print(aCommand.getCommand());
	            }
	            
	            // Let the thread sleep for a while.
	            Thread.sleep(sleepTime);
	         }
	         while(true);
	      }catch (InterruptedException e) {
	         System.out.println("Thread interrupted.");
	      }
	      System.out.println("DEBUG: Thread exiting.");
	}

	
	public void start () {
		System.out.println("Starting thread");
    	t = new Thread (this, "mainthread");
    	t.start();
	}
}
