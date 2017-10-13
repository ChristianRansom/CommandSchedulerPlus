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
	public List<ScheduledCommand> commands;
	private long sleepTime;
    
	public MainThread(List<ScheduledCommand> commands2, long aSleepTime) {
		commands = commands2;
		sleepTime = aSleepTime;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		 System.out.println("DEBUG: Running thread" );
         do {
            System.out.println("Running Commands");
			synchronized(commands) {
				Iterator<ScheduledCommand> iterator = commands.iterator(); 
				ScheduledCommand command;
				while (iterator.hasNext()){
					command = iterator.next();
            		GregorianCalendar date = command.getDate();
            		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
					+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
					":" + date.get(Calendar.MINUTE); 
					if(command.getDate().before(new GregorianCalendar())){
	            		System.out.print(dateString + " is before now: " + new Date());
	            		System.out.print("DEBUG: running this command: " + command.getCommand());
	                    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();;
			            Bukkit.dispatchCommand(console, command.getCommand());
					}
					else{
	            		System.out.print(dateString + " is NOT before now: " + new Date());
					}						
				}
	            
	            	
            }
	            
	            // Let the thread sleep for a while.
			try {
				Thread.sleep(sleepTime);
     		}
     		catch (InterruptedException e) {
     			System.out.println("Thread interrupted.");
     		}
         }
         while(true);
         
	     //System.out.println("DEBUG: Thread exiting.");
	}

	
	public void start () {
		System.out.println("Starting thread");
    	t = new Thread (this, "mainthread");
    	t.start();
	}
}
