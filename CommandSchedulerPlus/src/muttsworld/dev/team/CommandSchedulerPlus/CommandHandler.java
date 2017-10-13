package muttsworld.dev.team.CommandSchedulerPlus;


import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandHandler implements CommandExecutor{
	private boolean waitingForDate = false;
	private final List<ScheduledCommand> commandList;
	private ScheduledCommand currentCommand;
	
	
    public CommandHandler(List<ScheduledCommand> commands) {
    	commandList = commands;
		// TODO Auto-generated constructor stub
	}


	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int year = 0, month = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;
		

		if(!waitingForDate){
			
			//TODO check for valid date like month etc. 
			if(args[0].equals("add")){
				//handle new command being added
				String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length)); //remove 'add' argument from the command
				currentCommand = new ScheduledCommand();
				currentCommand.setCommand(command);
				waitingForDate = true;
				
				System.out.println("Good job, now enter a date. /csp Year Month Day Hour Seconds");
				return true;
			}
			else {
				if(args[0].equals("listcommands") || args[0].equals("commandlist")){
					synchronized(commandList) {
						Iterator<ScheduledCommand> iterator = commandList.iterator(); 
						while (iterator.hasNext())
			            System.out.println(iterator.next());
					}
					return true;
				}
				else {
					System.out.println("Uknown command usage");
					return true;
				}
			}
	    	
		}
		else {
			//handle date entry 
	        if(args.length == 5){
	        	year = Integer.parseInt(args[0]); 
	        	month = Integer.parseInt(args[1]) - 1; //-1 since months are stored 0 to 11
	        	dayOfMonth = Integer.parseInt(args[2]); 
	        	hourOfDay = Integer.parseInt(args[3]); 
	        	minute = Integer.parseInt(args[4]); 
	        	
	        	GregorianCalendar gcalendarDate = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
	        	currentCommand.setDate(gcalendarDate);
				
				synchronized(commandList) {
					commandList.add(currentCommand);
				}
	        		        	
	        	System.out.println("Date Succesfully entered. Command added to be scheduled.");
	        	waitingForDate = false;
				return true;

	        }   
	        else {
	        	System.out.println("Waiting for a date. Usage: /csp Year Month Day Hour Seconds");
	        	
				return true;

	    	}
		}

    	
    	/*
    	
    	
    	if (sender instanceof Player) {
            Player player = (Player) sender;

        }

        String str = String.join(" ", args);
        
        
        GregorianCalendar gcalendar = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
        
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();;
        Bukkit.dispatchCommand(console, str);
        System.out.print(str);
        
        
        
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", 
                "Oct", "Nov", "Dec"};
             
        // Display current time and date information.
        System.out.print("Date: ");
        System.out.print(months[gcalendar.get(Calendar.MONTH)]);
        System.out.print(" " + gcalendar.get(Calendar.DATE) + " ");
        System.out.println(year = gcalendar.get(Calendar.YEAR));
        System.out.print("Time: ");
        System.out.print(gcalendar.get(Calendar.HOUR) + ":");
        System.out.print(gcalendar.get(Calendar.MINUTE) + ":");
        System.out.println(gcalendar.get(Calendar.AM_PM));

        // Test if the current year is a leap year
        if(gcalendar.isLeapYear(year)) {
           System.out.println("The current year is a leap year");
        }else {
           System.out.println("The current year is not a leap year");
        }
        
        // If the player (or console) uses our command correct, we can return true
        return true;*/
    }
}
