package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
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

	void createCommand(CommandSender sender){
		currentCommand = new ScheduledCommand();
		commandEditor = true;
		displayCommandMenu(currentCommand, sender);
	}

	void editCommand(String[] args, CommandSender sender) {
		if(args.length < 2){
			sender.sendMessage(plugin.prefix + plugin.error + "Usage: " + plugin.command + "/csp edit <number>");
			return;
		}
		//System.out.println("Finding command " + args[1]);
		//System.out.println("commands.getSize(): " + commands.getSize());
		
	    try
	    {
	    	Integer.parseInt(args[1]);
	    } catch (NumberFormatException ex)
	    {
			sender.sendMessage(plugin.prefix + plugin.error + "Usage: " + plugin.command + "/csp edit <number>");
	        return;
	    }
		synchronized(commands) {
			if(Integer.parseInt(args[1]) <= commands.getSize() && Integer.parseInt(args[1]) > 0){
				editingCommand = (commands.find(Integer.parseInt(args[1])));
			}
			else {
				sender.sendMessage(plugin.prefix + plugin.error + "There is no command with that number. Use " 
						+ plugin.command + "/csp list " + plugin.error + "to see the commands.");
				return;
			}
		} //TODO extract all messages and errors into a separate file
		//System.out.println(editingCommand);
		currentCommand = editingCommand.copy();
		commandEditor = true;
		displayCommandMenu(currentCommand, sender);
	}

	void commandEditor(String[] args, CommandSender sender) {
	    TimeSlice timeSlice;	    
	    switch(commandEditorOption) {
	        case 0 :
	    		if(args.length < 1){
	    			displayCommandMenu(currentCommand, sender);
					sender.sendMessage(plugin.prefix + plugin.error + "Enter an option from 1 to 7.");
	    			return;
	    		}
	    		if(!args[0].equals("")){
		            try
		            {
		    			commandEditorOption = Integer.parseInt(args[0]);
		            } 
		            catch (NumberFormatException ex)
		            {
		    			displayCommandMenu(currentCommand, sender);
						sender.sendMessage(plugin.prefix + plugin.error + "Enter an option from 1 to 7.");
		                return;
		            }
	    			
	    		}
	    		else {
	        		displayCommandGroupMenu(currentCommand.getCommands(), sender);
	    			return;
	    		}
	        	if(commandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(commandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : 
		        			if(!currentCommand.getCommandGroup()) { 
		        				sender.sendMessage(plugin.prefix + "Enter the command you wish to schedule, or enter " + 
		        						plugin.command + "/csp commandgroup " + ChatColor.WHITE + "to add multiple commands");		
		        			}
		        			else {
		    	        		groupCommandEditor = true;
		    		        	commandEditorOption = 0;
		    	        		displayCommandGroupMenu(currentCommand.getCommands(), sender);
		        			}
		        			break;
	        			case 2 : sender.sendMessage(plugin.prefix + "Enter the date and time you want the command to run: " + plugin.command + "/csp Year Month Day (24)Hours Minutes"); break;
		        		case 3 : sender.sendMessage(plugin.prefix + "Enter the time from now you want the command to run: " + plugin.command + "/csp Days Hours Minutes"); break;
		        		case 4 : sender.sendMessage(plugin.prefix + "Enter the how often you want the command to repeat: " + plugin.command + "/csp Days Hours Minutes"); break;
		        		case 5 : sender.sendMessage(plugin.prefix + "Enter how much time to add to delay when the command is scheduled: " + plugin.command + "/csp Days Hours Minutes"); break;
		        		case 6 :  //Saving Command
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
				        	sender.sendMessage(plugin.prefix + "Command Saved. Exiting the editor."); 
		        			break;
		    	        case 7 : 
		    	        	sender.sendMessage(plugin.prefix + "Exiting");
		        			editingCommand = null;
		    	        	commandEditorOption = 0;
		    	        	commandEditor = false;
		        	}
	        	}
	        	else{
	        		sender.sendMessage(plugin.prefix + plugin.error + "Enter an option from 1 to 7");
    	        	commandEditorOption = 0;
	        	}
	        	break;
	        case 1 : //handle new command being added
	        	if(!(args.length < 1)){
		        	if(args[0].equals("commandgroup")){
		        		groupCommandEditor = true;
			        	commandEditorOption = 0;
		        		//System.out.println(currentCommandGroup.size() + " commands found");
		        		displayCommandGroupMenu(currentCommand.getCommands(), sender);
		        	}
		        	else { 
		        		//System.out.println("args 1 " + args[0]);
		        		//System.out.println("charat0 " + (args[0].charAt(0)));
		    			currentCommand.setCommand(args, 0);
		        		commandEditorOption = 0;
			        	displayCommandMenu(currentCommand, sender);
		        	}
	        	}
	        	else {
	        		sender.sendMessage(plugin.prefix + plugin.error + "Enter the command you wish to schedule, or enter " 
	        					+ plugin.command + "/csp commandgroup " + plugin.error + "to add multiple commands");
	        	}
				break; 
	        case 2 : //handle date entry for a new command
		        
        	
	        	GregorianCalendar gcalendarDate = dateEntry(args, sender);
	        	if(gcalendarDate == null) {
	        		sender.sendMessage(plugin.prefix + plugin.error + "Use " + plugin.command + "/csp Year Months Days Hours Minutes "
							+ plugin.error + "to set date and time you want the command to run.");
	        	}
	        	else {
	        		currentCommand.setDate(gcalendarDate);
	        		sender.sendMessage(plugin.prefix + "Date Succesfully entered. Command added to be scheduled.");
	        	}
		        
	        	commandEditorOption = 0;
	        	displayCommandMenu(currentCommand, sender);
	        	break;
	        case 3 : //Time relative from now that the command should be scheduled
        		timeSlice = timeSliceEntry(args);
	        	if(timeSlice != null){  
	        		GregorianCalendar newDate = new GregorianCalendar(); //creates a date of the time now
	        		newDate.setTimeInMillis(newDate.getTimeInMillis() + (timeSlice.getMillis()));
		        	currentCommand.setDate(newDate);
		        	sender.sendMessage(plugin.prefix + "Scheduled command relative to current time."); 
		        	commandEditorOption = 0;
		        	displayCommandMenu(currentCommand, sender);
		        }
	        	else {
					sender.sendMessage(plugin.prefix + plugin.error +  "Use " + plugin.command + "/csp Days Hours Minutes " +
							plugin.error + "to Scheduled the command relative to current the time.");
	        		return;
	        	}
	        	commandEditorOption = 0;
	        	displayCommandMenu(currentCommand, sender);
	        	break;
	        case 4 : //set repeat time for a command
        		timeSlice = timeSliceEntry(args);
	        	if(timeSlice != null){ 
		        	currentCommand.setRepeat(timeSlice);
		        	sender.sendMessage(plugin.prefix + "Date Succesfully entered. Command added to be scheduled.");
	        		displayCommandMenu(currentCommand, sender);
	        		commandEditorOption = 0;
		        }
	        	else {
					sender.sendMessage(plugin.prefix + plugin.error +  "Usage: " + plugin.command + "/csp Days Hours Minutes");
	        		return;
	        	}
	        	break;
	        case 5 : //Add time to scheduled run
	        	timeSlice = timeSliceEntry(args);
	        	if(timeSlice != null){		
	        		GregorianCalendar newDate = new GregorianCalendar();
	        		newDate.setTimeInMillis(currentCommand.getDate().getTimeInMillis() + timeSlice.getMillis());
		        	currentCommand.setDate(newDate);
		        	sender.sendMessage(plugin.prefix + "Scheduled date extended. "); 
		        	commandEditorOption = 0;
		        	displayCommandMenu(currentCommand, sender);
		        	break;
		        }
	    }
	}
	
	TimeSlice timeSliceEntry(String[] args){
		if(args.length == 3){ 
		    try
		    {
				int days = Integer.parseInt(args[0]); 
				int hours = Integer.parseInt(args[1]); 
				int minutes = Integer.parseInt(args[2]); 
				return new TimeSlice(days, hours, minutes);
		    } catch (NumberFormatException ex)
		    {
		        return null;
		    }

		}
		else {
			return null;
		}
	}

	//package visibility
	void commandGroupEditor(String[] args, CommandSender sender) {
		switch(groupCommandEditorOption) {
	        case 0 :
	    		if(args.length < 1){
	    			displayCommandGroupMenu(currentCommand.getCommands(), sender);
	    			sender.sendMessage(plugin.prefix + plugin.error + " Use " + plugin.command + "/csp <number>" +  plugin.error + " to selection an option");
	    			return;
	    		}
	    		if(!args[0].equals("")){
	    		    try
	    		    {
		    			groupCommandEditorOption = Integer.parseInt(args[0]);
	    		    } catch (NumberFormatException ex)
	    		    {
	    		    	displayCommandGroupMenu(currentCommand.getCommands(), sender);
		    			sender.sendMessage(plugin.prefix + plugin.error + " Use " + plugin.command + "/csp <number>" +  plugin.error + " to selection an option");
	    		    	return;
	    		    }
	    		}
	    		else {
	    			displayCommandGroupMenu(currentCommand.getCommands(), sender);
	    			sender.sendMessage(plugin.prefix + plugin.error + " Use " + plugin.command + "/csp <number>" +  plugin.error + " to selection an option");
	    			return;
	    		}
	        	if(groupCommandEditorOption > 0 && groupCommandEditorOption <= 5) {
		        	switch(groupCommandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : sender.sendMessage(plugin.prefix + "Enter the command you want to add to the group."); break;
		        		case 2 : showCommandGroup(sender);
		        					sender.sendMessage(plugin.prefix + "Enter the number of command you want to replace followed by the commmand");
		        				 	sender.sendMessage(plugin.prefix + plugin.command + "/csp <number> <command>"); break;
		        		case 3 : sender.sendMessage(plugin.prefix + "Enter the number of the command you want to delete."); break;
		        		case 4 : sender.sendMessage(plugin.prefix + "Cleared all the commands"); 
		        			currentCommand.getCommands().clear();
		    				displayCommandGroupMenu(currentCommand.getCommands(), sender);
		        			groupCommandEditorOption = 0;
		        			break;
		        		case 5 : sender.sendMessage(plugin.prefix + "Command Group Saved"); 
		        			if(currentCommand.getCommands().isEmpty()) {
						sender.sendMessage(
								plugin.prefix + plugin.error + "No commands found in the command group. Adding a default command");
								String defaultCommand[] = {"this", "is", "an", "example", "command"};
								currentCommand.setCommand(defaultCommand, -1);
		        			}
		        			else{
			        			currentCommand.setCommandGroup(true);
		        			}
		        			groupCommandEditorOption = 0;
				        	groupCommandEditor = false;
				        	displayCommandMenu(currentCommand, sender);
		        			break;
		        	}
	        	}
	        	else{
		        	displayCommandMenu(currentCommand, sender);
	        		sender.sendMessage(plugin.prefix + plugin.error + "Enter an option from 1 to 5");
        			groupCommandEditorOption = 0;
	        	}
	        	break; //Breaks from outer switch case
	        case 1 : //handle new command being added to the commmandGroup
	        	if(args.length < 1){
	        		showCommandGroup(sender);
	        	 	sender.sendMessage(plugin.prefix + plugin.error + "Enter the command you want to add to the group.");
	        		return;
	        	}
	        	else {
		        	currentCommand.setCommand(args, -1);
					groupCommandEditorOption = 0;
	        	}
				displayCommandGroupMenu(currentCommand.getCommands(), sender);
	        	break;
	        case 2 : //handle command replace
	        	if(args.length < 2){
	        	commandGroupInsertError(sender);
	        	}
	        	else {
	        		//remove replace place argument argument from the command
	        		if(args[0].equals("")){
		        		commandGroupInsertError(sender);
		        		return;
	        		}
	        		else {
	        			int temp;
	        		    try //extract this safe number check
	        		    {
	        		    	temp = Integer.parseInt(args[0]); 
	        		    } catch (NumberFormatException ex)
	        		    {
			        		commandGroupInsertError(sender);
			        		return;
	        		    }
		        		if(temp - 1 < currentCommand.getCommands().size() && temp - 1 >= 0) {
		        			if((args[1].charAt(0)) == '/'){
		        				StringBuilder sb = new StringBuilder(args[1]);
		        				sb.deleteCharAt(0);
		        				String finalCommand = sb.toString();
		        				args[1] = finalCommand;
		        			}
			        		currentCommand.setCommand(Arrays.copyOfRange(args, 1, args.length), Integer.parseInt(args[0]) - 1); 
			        		groupCommandEditorOption = 0;
		        		}
		        		else {
			        		commandGroupInsertError(sender);
			        		return;
		        		}
	        		}
	        	}
				displayCommandGroupMenu(currentCommand.getCommands(), sender);
	        	break;
	        case 3 : //handle command delete 
	        	if(args.length < 1){
	        		showCommandGroup(sender);
	        		sender.sendMessage(plugin.prefix + plugin.error + "Enter the number of the command you want to delete from the command group.");
	        		return;
	        	}
	        	else {
	        		 int temp;
	        		 try
	        		    {
	        			 	temp = Integer.parseInt(args[0]);
	        		    } catch (NumberFormatException ex)
	        		    {
	    	        		showCommandGroup(sender);
	    	        		sender.sendMessage(plugin.prefix + plugin.error + "Enter the number of the command you want to delete.");
	        		        return;
	        		    }
		        	currentCommand.getCommands().remove(Integer.parseInt(args[0]) - 1);
					groupCommandEditorOption = 0;
	        	}
				displayCommandGroupMenu(currentCommand.getCommands(), sender);
	        	break;
		}
	}

	private void commandGroupInsertError(CommandSender sender) {
		showCommandGroup(sender);
		sender.sendMessage(plugin.prefix + plugin.error + "Enter the number of the command you want to replace followed by the command you want to enter.");
		sender.sendMessage(plugin.prefix + plugin.command + "/csp <number> <command>");
	}

	private void showCommandGroup(CommandSender sender) {
		sender.sendMessage(plugin.prefix + "Commands:");
		for(int i = 0; i < currentCommand.getCommands().size(); i++){
			sender.sendMessage(plugin.prefix + "    " + (i + 1) + ". " + plugin.command + "/" + currentCommand.getCommands().get(i));
		}
		sender.sendMessage(plugin.prefix + "------------------------------------");
	}

	void displayCommandGroupMenu(ArrayList<CommandWithExecutor> arrayList, CommandSender sender) {
		sender.sendMessage("");
		sender.sendMessage(plugin.prefix + ChatColor.BOLD + ChatColor.DARK_AQUA + 
				"---------Command Group---------"); 
		sender.sendMessage(plugin.prefix + "Use " + plugin.command + "/csp <number>" +  ChatColor.WHITE + " to selection an option");
		showCommandGroup(sender);
		sender.sendMessage(plugin.prefix + "1: Add a command. ");
		sender.sendMessage(plugin.prefix + "2: Replace a command. ");
		sender.sendMessage(plugin.prefix + "3: Delete a command. ");
		sender.sendMessage(plugin.prefix + "4: Clear all commands.");
		sender.sendMessage(plugin.prefix + "5: Save and Exit.");
	}

	//displays a date in a proper da
	String displayDate(GregorianCalendar date){
		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
			+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
			":";
		if(date.get(Calendar.MINUTE) < 10){
			dateString += ("0" + date.get(Calendar.MINUTE));
		}
		else {
			dateString += date.get(Calendar.MINUTE);
		}
		return dateString;
	}

	//shows a minimum number of pieces of info to  show a time slice
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

	void displayCommandMenu(ScheduledCommand aCommand, CommandSender sender) {
		GregorianCalendar now = new GregorianCalendar();
		now.setTimeInMillis((aCommand.getDate().getTimeInMillis() - now.getTimeInMillis()));
		sender.sendMessage("");
		sender.sendMessage(plugin.prefix + ChatColor.BOLD + ChatColor.DARK_AQUA + 
				"-----------Command Editor-----------"); 
		sender.sendMessage(plugin.prefix + "Creating a new command. Use " + plugin.command + "/csp <number>" +  ChatColor.WHITE + " to selection an option");
		if(aCommand.isCommandGroup()){
			showCommandGroup(sender);
			sender.sendMessage(plugin.prefix + "1: Edit Commands");
		}
		else {
			sender.sendMessage(plugin.prefix + "1: Command/s to be run: " + plugin.command + "/" + aCommand.getCommand());
		}
		sender.sendMessage(plugin.prefix + "2: Date to be run: " + ChatColor.YELLOW + displayDate(aCommand.getDate()));
		sender.sendMessage(plugin.prefix + "3: Time until run: " + ChatColor.YELLOW + simpleDate(now));
		if(!aCommand.getRepeat().isZero()){
			sender.sendMessage(plugin.prefix + "4: Repeating Every: " + ChatColor.YELLOW + (aCommand.getRepeat()));
		}
		else {
			sender.sendMessage(plugin.prefix + "4: Repeating: " + ChatColor.YELLOW + "False");
		}
		sender.sendMessage(plugin.prefix + "5: Extend time till next scheduled execution.");
		sender.sendMessage(plugin.prefix + "6: Save and exit.");
		sender.sendMessage(plugin.prefix + "7: Exit without saving.");
	}

	void quickCreate(String[] args, CommandSender sender) {
		String commandString = "";
		String repeatString = "";
		String dateString = "";
		char optionChar = 'x';
		
		
		for(int i = 1; i < args.length; i++){
			System.out.println("args[i]" + args[i]);
			if(args[i].length() < 4 && args[i].charAt(0) == '-' && args[i].charAt(1) == '-'){
				optionChar = args[i].charAt(2);
			}
			else {
			System.out.println("optionChar " + optionChar);
				switch(optionChar){
				case 'c': 
					commandString += args[i] + " ";
					break;
				case 'r':
					repeatString += args[i] + " ";
					break;
				case 'd': 
					dateString += args[i] + " ";
					break;
				}
			}
		}
		System.out.println("CommandString " + commandString);

		System.out.println("RepeatString " + repeatString);
		TimeSlice repeatSlice = timeSliceEntry(repeatString.split(" "));
		System.out.println("repeatSlice" + repeatString);
		if(repeatSlice == null) {
			sender.sendMessage(plugin.prefix + plugin.error + "Invalid repeat value. Repeat format: Days Hours Minutes. ");
			return;
    	}
		
		GregorianCalendar dateEntry = dateEntry(dateString.split(" "), sender);
		System.out.println("dateEntry " + dateString);
		if(dateEntry == null) {
			sender.sendMessage(plugin.prefix + plugin.error + "Invalid date. Date format: Year Months Days Hours Minutes. ");
			return;
    	}
		ScheduledCommand aCommand = new ScheduledCommand(commandString, dateEntry, repeatSlice);
		System.out.println("Adding this command: ");
		displayCommandMenu(aCommand, sender);
		synchronized(commands){
			commands.insert(aCommand);
		}
		return;
	}
	
	GregorianCalendar dateEntry(String[] args, CommandSender sender){
	    int year = 0, month = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;

		if(args.length != 5){ 
			return null;
		}
        try
        {
        	year = Integer.parseInt(args[0]); 
        	month = Integer.parseInt(args[1]) - 1; //-1 since months are stored 0 to 11
        	dayOfMonth = Integer.parseInt(args[2]); 
        	hourOfDay = Integer.parseInt(args[3]); 
        	minute = Integer.parseInt(args[4]); 
        } 
        catch (NumberFormatException ex)
        {
            return null;
        }
        return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);

	}
	
	
	
}
