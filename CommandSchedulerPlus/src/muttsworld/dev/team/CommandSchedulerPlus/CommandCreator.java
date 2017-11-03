package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.CommandSender;

public class CommandCreator {
	
	private final AVLTree<ScheduledCommand> commands; //the list of commands
	private ScheduledCommand currentCommand; //Used to hold variables while creating and adding a new command
	private ScheduledCommand editingCommand = null; //Used to store state of command we're editing in case we abort the edit
	public CommandSchedulerPlus plugin;
	boolean commandEditor = false; //Value used to handle multi-step creation of commands
	private int commandEditorOption = 0;

	//Group Command Creation Variables
	private int groupCommandEditorOption = 0;
	boolean groupCommandEditor = false; //Value used to handle command groups

	public CommandCreator(AVLTree<ScheduledCommand> theCommands, CommandSchedulerPlus plugin2){
		commands = theCommands;
		plugin = plugin2;
	}

	boolean createCommand(CommandSender sender){
		currentCommand = new ScheduledCommand();
		commandEditor = true;
		displayCommand(currentCommand, sender);
		return true;
	}

	boolean editCommand(String[] args, CommandSender sender) {
		//System.out.println("Finding command " + args[1]);
		//System.out.println("commands.getSize(): " + commands.getSize());
		editingCommand = (commands.find(Integer.parseInt(args[1])));
		//System.out.println(editingCommand);
		currentCommand = editingCommand.copy();
		commandEditor = true;
		displayCommand(currentCommand, sender);
		return true;
	}

	boolean commandEditor(String[] args, CommandSender sender) {
	    int year = 0, month = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;
	    TimeSlice timeSlice;
	    switch(commandEditorOption) {
	        case 0 :
	        	commandEditorOption = Integer.parseInt(args[0]);
	        	if(commandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(commandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : 
		        			if(!currentCommand.getCommandGroup()) {
		        				sender.sendMessage(plugin.prefix + "Enter the command you wish to schedule, or type 'commandgroup' to add multiple commands"); break;
		        			}
		        			else {
		    	        		groupCommandEditor = true;
		    		        	commandEditorOption = 0;
		    	        		displayCommandEditor(currentCommand.getCommands(), sender);
		        			}
	        			case 2 : sender.sendMessage(plugin.prefix + "Enter the date and time you want the command to run. /csp Year Month Day (24)Hours Minutes"); break;
		        		case 3 : sender.sendMessage(plugin.prefix + "Enter the time from now you want the command to run: /csp Days Hours Minutes"); break;
		        		case 4 : sender.sendMessage(plugin.prefix + "Enter the how often you want the command to repeat: /csp Days Hours Minutes"); break;
		        		case 5 : sender.sendMessage(plugin.prefix + "Enter how much time to add to delay when the command is scheduled: /csp Days Hours Minutes"); break;
		        		case 6 : sender.sendMessage(plugin.prefix + "Saving the command"); 
		        			if(editingCommand == null) {
			        			synchronized(commands){
				        			commands.insert(currentCommand);
				        		}
		        			}
		        			else{
			        			synchronized(commands){
			        				commands.delete(editingCommand);
				        			commands.insert(currentCommand);
				        		}
			        			editingCommand = null;
		        			}
				        	commandEditorOption = 0;
				        	commandEditor = false;
		        			break;
		    	        case 7 : 
		    	        	sender.sendMessage(plugin.prefix + "Exiting");
		        			editingCommand = null;
		    	        	commandEditorOption = 0;
		    	        	commandEditor = false;
		        	}
	        	}
	        	else{
	        		sender.sendMessage(plugin.prefix + "Enter an option from 1 to 8");
	        	}
	        	break;
	        case 1 : //handle new command being added
	        	if(args[0].equals("commandgroup")){
	        		groupCommandEditor = true;
		        	commandEditorOption = 0;
	        		//System.out.println(currentCommandGroup.size() + " commands found");
	        		displayCommandEditor(currentCommand.getCommands(), sender);
	        	}
	        	else { 
	        		currentCommand.setCommand(args, 0);
	        		commandEditorOption = 0;
		        	displayCommand(currentCommand, sender);
	        	}
				break; 
	        case 2 : //handle date entry for a new command
		        if(args.length == 5){ 		//TODO check for valid date like month etc. 
		        	year = Integer.parseInt(args[0]); 
		        	month = Integer.parseInt(args[1]) - 1; //-1 since months are stored 0 to 11
		        	dayOfMonth = Integer.parseInt(args[2]); 
		        	hourOfDay = Integer.parseInt(args[3]); 
		        	minute = Integer.parseInt(args[4]); 
		        	
		        	GregorianCalendar gcalendarDate = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
		        	currentCommand.setDate(gcalendarDate);
		        	
		        	sender.sendMessage(plugin.prefix + "Date Succesfully entered. Command added to be scheduled.");
		        }
	        	commandEditorOption = 0;
	        	displayCommand(currentCommand, sender);
	        	break;
	        case 3 : //Time relative from now that the command should be scheduled
        		timeSlice = timeSliceEntry(args, sender);
	        	if(timeSlice != null){  
	        		GregorianCalendar newDate = new GregorianCalendar(); //creates a date of the time now
	        		newDate.setTimeInMillis(newDate.getTimeInMillis() + (timeSlice.getMillis()));
		        	currentCommand.setDate(newDate);
		        	sender.sendMessage(plugin.prefix + "Scheduled command relative to current time."); 
		        	commandEditorOption = 0;
		        	displayCommand(currentCommand, sender);
		        	break;
		        }
	        	commandEditorOption = 0;
	        	displayCommand(currentCommand, sender);
	        	break;
	        case 4 : //set repeat time for a command
        		timeSlice = timeSliceEntry(args, sender);
	        	if(timeSlice != null){ 
		        	currentCommand.setRepeat(timeSlice);
		        	sender.sendMessage(plugin.prefix + "Date Succesfully entered. Command added to be scheduled.");
	        		displayCommand(currentCommand, sender);
	        		commandEditorOption = 0;
		        }
	        	else {
	        		commandEditorOption = 0;
	        		displayCommand(currentCommand, sender);
	        	}
	        	break;
	        case 5 : //Add time to scheduled run
	        	timeSlice = timeSliceEntry(args, sender);
	        	if(timeSlice != null){		
	        		GregorianCalendar newDate = new GregorianCalendar();
	        		newDate.setTimeInMillis(currentCommand.getDate().getTimeInMillis() + timeSlice.getMillis());
		        	currentCommand.setDate(newDate);
		        	sender.sendMessage(plugin.prefix + "Scheduled date extended "); 
		        	commandEditorOption = 0;
		        	displayCommand(currentCommand, sender);
		        	break;
		        }
	    }
	    return true;
	}
	
	public TimeSlice timeSliceEntry(String[] args, CommandSender sender){
		if(args.length == 3){ //TODO check for valid date like month etc. 
			int days = Integer.parseInt(args[0]); 
			int hours = Integer.parseInt(args[1]); 
			int minutes = Integer.parseInt(args[2]); 
			return new TimeSlice(days, hours, minutes);
		}
		else {
			sender.sendMessage(plugin.prefix + "Usage: /csp Days Hours Minutes");
			return null;
		}
	}

	//package visibility
	boolean createCommandGroup(String[] args, CommandSender sender) {
		switch(groupCommandEditorOption) {
	        case 0 :
	        	groupCommandEditorOption = Integer.parseInt(args[0]);
	        	if(groupCommandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(groupCommandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : sender.sendMessage(plugin.prefix + "Enter the command you want to add to the group."); break;
		        		case 2 : sender.sendMessage(plugin.prefix + "Enter the number of where you want to insert a command followed by the commmand"); break;
		        		case 3 : sender.sendMessage(plugin.prefix + "Enter the number of the command you want to delete."); break;
		        		case 4 : sender.sendMessage(plugin.prefix + "Cleared all the commands"); 
		        			currentCommand.getCommands().clear();
		    	        	displayCommand(currentCommand, sender);
		        			break;
		        		case 5 : sender.sendMessage(plugin.prefix + "Command Group Saved"); 
		        			if(currentCommand.getCommands().isEmpty()) {
		        				sender.sendMessage(plugin.prefix + "No commands found in the command group... Exiting");
		        			}
		        			else{
			        			currentCommand.setCommandGroup(true);
		        			}
		        			groupCommandEditorOption = 0;
				        	groupCommandEditor = false;
				        	displayCommand(currentCommand, sender);
		        			break;
		        	}
	        	}
	        	else{
	        		sender.sendMessage(plugin.prefix + "Enter an option from 1 to 5");
	        	}
	        	break; //Breaks from outer switch case
	        case 1 : //handle new command being added to the commmandGroup
	        	currentCommand.setCommand(args, -1);
				groupCommandEditorOption = 0;
				displayCommandEditor(currentCommand.getCommands(), sender);
	        	break;
	        case 2 : //handle command insert
	        	//remove insertion place argument argument from the command
	        	currentCommand.setCommand(Arrays.copyOfRange(args, 1, args.length), Integer.parseInt(args[0])); 
				groupCommandEditorOption = 0;
				displayCommandEditor(currentCommand.getCommands(), sender);
	        	break;
	        case 3 : //handle command delete
	        	currentCommand.getCommands().remove(Integer.parseInt(args[0]) - 1);
				groupCommandEditorOption = 0;
				displayCommandEditor(currentCommand.getCommands(), sender);
	        	break;
		}
	    	
		return true;
	}

	void displayCommandEditor(ArrayList<CommandWithExecutor> arrayList, CommandSender sender) {
		sender.sendMessage(plugin.prefix + "Creating a group of commands. Enter the number of the field you wish to edit.");
		sender.sendMessage(plugin.prefix + "Note: A command group ensures that the commands in it will be run in order");
		sender.sendMessage(plugin.prefix + "Commands:");
		for(int i = 0; i < currentCommand.getCommands().size(); i++){
			sender.sendMessage(plugin.prefix + (i + 1) + ". " + currentCommand.getCommands().get(i));
		}
		sender.sendMessage(plugin.prefix + "---------------------------------------------------------");
		sender.sendMessage(plugin.prefix + "1: Add a command. ");
		sender.sendMessage(plugin.prefix + "2: Insert a command. ");
		sender.sendMessage(plugin.prefix + "3: Delete a command. ");
		sender.sendMessage(plugin.prefix + "4: Clear all commands.");
		sender.sendMessage(plugin.prefix + "5: Save and Exit.");
	}

	String displayDate(GregorianCalendar date){
		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
			+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
			":" + date.get(Calendar.MINUTE);
		return dateString;
	}

	void displayCommand(ScheduledCommand aCommand, CommandSender sender) {
		GregorianCalendar now = new GregorianCalendar();
		now.setTimeInMillis((aCommand.getDate().getTimeInMillis() - now.getTimeInMillis()));
		sender.sendMessage(plugin.prefix + "Creating a new command. Enter the number of the field you wish to edit.");
		sender.sendMessage(plugin.prefix + "1: Command/s to be run: " + aCommand.getCommand());
		sender.sendMessage(plugin.prefix + "2: Date to be run: " + displayDate(aCommand.getDate()));
		sender.sendMessage(plugin.prefix + "3: Time until run: " + simpleDate(now));
		if(!aCommand.getRepeat().isZero()){
			sender.sendMessage(plugin.prefix + "4: Repeating Every: " + (aCommand.getRepeat()));
		}
		else {
			sender.sendMessage(plugin.prefix + "4: Repeating: false");
		}
		sender.sendMessage(plugin.prefix + "5: Extend time till next run. Add time to the next scheduled run.");
		sender.sendMessage(plugin.prefix + "6: Save Command.");
		sender.sendMessage(plugin.prefix + "7: Exit.");
	}

	String simpleDate(GregorianCalendar date){
		long millis = date.getTimeInMillis();
		long days = TimeUnit.MILLISECONDS.toDays(millis);
	    millis -= TimeUnit.DAYS.toMillis(days);
	    long hours = TimeUnit.MILLISECONDS.toHours(millis);
	    millis -= TimeUnit.HOURS.toMillis(hours);
	    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
	    millis -= TimeUnit.MINUTES.toMillis(minutes);
	    //long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
	    StringBuilder sb = new StringBuilder(64);
	    sb.append(days);
	    sb.append(" Days ");
	    sb.append(hours);
	    sb.append(" Hours ");
	    sb.append(minutes);
	    sb.append(" Minutes ");
	    //sb.append(seconds);
	    //sb.append(" Seconds");
	    return(sb.toString());
	}

	
	
	
}
