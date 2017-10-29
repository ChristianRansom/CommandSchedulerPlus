package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class CommandCreator {
	
	private final AVLTree<ScheduledCommand> commands; //the list of commands
	private ScheduledCommand currentCommand; //Used to hold variables while creating and adding a new command
	private ScheduledCommand editingCommand = null;
	public CommandSchedulerPlus plugin;
	boolean commandEditor = false; //Temp value to handle multi-step creation of commands

	//Group Command Creation Variables
	private ArrayList<String> currentCommandGroup = null;
	private int groupCommandEditorOption = 0;
	private int commandEditorOption = 0;
	boolean groupCommandEditor = false;

	public CommandCreator(AVLTree<ScheduledCommand> theCommands){
		commands = theCommands;
	}

	//package visibility
	boolean createCommandGroup(String[] args) {
		
		String command;
		switch(groupCommandEditorOption) {
	        case 0 :
	        	groupCommandEditorOption = Integer.parseInt(args[0]);
	        	if(groupCommandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(groupCommandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : System.out.println("Enter the command you want to add to the group."); break;
		        		case 2 : System.out.println("Enter the number of where you want to insert a command followed by the commmand"); break;
		        		case 3 : System.out.println("Enter the number of the command you want to delete."); break;
		        		case 4 : System.out.println("Cleared all the commands"); 
		        			currentCommandGroup.clear();
		    	        	displayCommand(currentCommand);
		        			break;
		        		case 5 : System.out.println("Command Group Saved"); 
		        			if(currentCommandGroup == null) {
			        			System.out.println("No commands found in the command group... Exiting");
		        			}
		        			else{
			        			currentCommand.setCommands(currentCommandGroup);
			        			currentCommand.setCommandGroup(true);
		        			}
		        			groupCommandEditorOption = 0;
				        	groupCommandEditor = false;
				        	displayCommand(currentCommand);
		        			break;
		        	}
	        	}
	        	else{
		    		System.out.println("Enter an option from 1 to 5");
	        	}
	        	break; //Breaks from outer switch case
	        case 1 : //handle new command being added to the commmandGroup
	        	command = String.join(" ", Arrays.copyOfRange(args, 0, args.length)); //remove 'add' argument from the command
	        	currentCommandGroup.add(command);
				groupCommandEditorOption = 0;
				displayCommandEditor(currentCommandGroup);
	        	break;
	        case 2 : //handle command insert
	        	command = String.join(" ", Arrays.copyOfRange(args, 1, args.length)); //remove 'add' argument from the command
	        	currentCommandGroup.add(Integer.parseInt(args[0]) - 1, command);
				groupCommandEditorOption = 0;
				displayCommandEditor(currentCommandGroup);
	        	break;
	        case 3 : //handle command delete
	        	currentCommandGroup.remove(Integer.parseInt(args[0]) - 1);
				groupCommandEditorOption = 0;
				displayCommandEditor(currentCommandGroup);
	        	break;
		}
	    	
		return true;
	}

	boolean editCommand(String[] args) {
		System.out.println("Finding command " + args[1]);
		System.out.println("commands.getSize(): " + commands.getSize());
		editingCommand = (commands.find(Integer.parseInt(args[1])));
		System.out.println(editingCommand);
		currentCommand = editingCommand.copy();
		commandEditor = true;
		displayCommand(currentCommand);
		return true;
	}

	boolean createCommand(){
		currentCommand = new ScheduledCommand();
		commandEditor = true;
		displayCommand(currentCommand);
		return true;
	}

	void displayCommand(ScheduledCommand aCommand) {
		GregorianCalendar now = new GregorianCalendar();
		now.setTimeInMillis((aCommand.getDate().getTimeInMillis() - now.getTimeInMillis()));
		System.out.println("Creating a new command. Enter the number of the field you wish to edit.");
		System.out.println("1: Command/s to be run: " + aCommand.getCommand());
		System.out.println("2: Date to be run: " + displayDate(aCommand.getDate()));
		System.out.println("3: Time until run: " + simpleDate(now));
		if(!aCommand.getRepeat().isZero()){
			System.out.println("4: Repeating Every: " + (aCommand.getRepeat()));
		}
		else {
			System.out.println("4: Repeating: false");
		}
		System.out.println("5: Player or Consol run: not implemented.");
		System.out.println("6: Extend time till next run. Add time to the next scheduled run.");
		System.out.println("7: Save Command.");
		System.out.println("8: Exit.");
	}

	boolean commandEditor(String[] args) {
	    int year = 0, month = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;
	    switch(commandEditorOption) {
	        case 0 :
	        	commandEditorOption = Integer.parseInt(args[0]);
	        	if(commandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(commandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : 
		        			if(!currentCommand.getCommandGroup()) {
		        				System.out.println("Enter the command you wish to schedule, or type 'commandgroup' to add multiple commands"); break;
		        			}
		        			else { //duplicate code TODO
		    	        		groupCommandEditor = true;
		    	        		currentCommandGroup = currentCommand.getCommands();
		    		        	commandEditorOption = 0;
		    	        		//System.out.println(currentCommandGroup.size() + " commands found");
		    	        		displayCommandEditor(currentCommandGroup);
		        			}
	        			case 2 : System.out.println("Enter the date and time you want the command to run. /csp Year Month Day (24)Hour Seconds"); break;
		        		case 3 : System.out.println("Enter the time from now you want the command to run: /csp Days Hours Minute Seconds"); break;
		        		case 4 : System.out.println("Enter the how often you want the command to repeat: /csp Days Hours Minute Seconds"); break;
		        		case 6 : System.out.println("Enter how much time to add to delay when the command is scheduled: /csp Days Hours Minute Seconds"); break;
		        		case 7 : System.out.println("Saving the command"); 
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
		    	        case 8 : 
		    	        	System.out.println("Exiting");
		        			editingCommand = null;
		    	        	commandEditorOption = 0;
		    	        	commandEditor = false;
		        		//case 5 : System.out.println("");
		        	}
	        	}
	        	else{
		    		System.out.println("Enter an option from 1 to 8");
	        	}
	        	break;
	        case 1 : //handle new command being added
	        	if(args[0].equals("commandgroup")){
	        		groupCommandEditor = true;
	        		currentCommandGroup = currentCommand.getCommands();
		        	commandEditorOption = 0;
	        		//System.out.println(currentCommandGroup.size() + " commands found");
	        		displayCommandEditor(currentCommandGroup);
	        	}
	        	else {
					String command = String.join(" ", Arrays.copyOfRange(args, 0, args.length)); //remove 'add' argument from the command
					currentCommand.setCommand(command);
		        	commandEditorOption = 0;
		        	displayCommand(currentCommand);
	        	}
				break; 
	        case 2 : //handle date entry 
		        if(args.length == 5){ 		//TODO check for valid date like month etc. 
		        	year = Integer.parseInt(args[0]); 
		        	month = Integer.parseInt(args[1]) - 1; //-1 since months are stored 0 to 11
		        	dayOfMonth = Integer.parseInt(args[2]); 
		        	hourOfDay = Integer.parseInt(args[3]); 
		        	minute = Integer.parseInt(args[4]); 
		        	
		        	GregorianCalendar gcalendarDate = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute);
		        	currentCommand.setDate(gcalendarDate);
		        	
		        	System.out.println("Date Succesfully entered. Command added to be scheduled.");
		        }
	        	commandEditorOption = 0;
	        	displayCommand(currentCommand);
	        	break;
	        case 3 : //handle date entry 
	        	if(args.length == 4){ 		//TODO check for valid date like month etc. 
	        		dayOfMonth = Integer.parseInt(args[0]); 
	        		hourOfDay = Integer.parseInt(args[1]); 
	        		minute = Integer.parseInt(args[2]); 
	        		int second = Integer.parseInt(args[3]); 
	        		
	        		GregorianCalendar newDate = new GregorianCalendar(); //creates a date of the time now
	        		newDate.setTimeInMillis(newDate.getTimeInMillis() 
		        			+ (new TimeSlice(dayOfMonth, hourOfDay, minute, second).getMillis()));
		        	currentCommand.setDate(newDate);
		        	
		        	System.out.println("Scheduled command relative to current time."); 
		        	commandEditorOption = 0;
		        	displayCommand(currentCommand);
		        	break;
		        }
	        	commandEditorOption = 0;
	        	displayCommand(currentCommand);
	        	break;
	        case 4 : 
	        	if(args.length == 4){ 		//TODO check for valid date like month etc. 
	        		dayOfMonth = Integer.parseInt(args[0]); 
	        		hourOfDay = Integer.parseInt(args[1]); 
	        		minute = Integer.parseInt(args[2]); 
	        		int second = Integer.parseInt(args[3]); 
		        	
		        	currentCommand.setRepeat(new TimeSlice(dayOfMonth, hourOfDay, minute, second));
		        	
		        	System.out.println("Date Succesfully entered. Command added to be scheduled.");
		        }
	        	commandEditorOption = 0;
	        	displayCommand(currentCommand);
	        	break;
	        case 6 : //TODO extract out this date handling. duplicate code
	        	if(args.length == 4){ 		//TODO check for valid date like month etc. 
	        		dayOfMonth = Integer.parseInt(args[0]); 
	        		hourOfDay = Integer.parseInt(args[1]); 
	        		minute = Integer.parseInt(args[2]); 
	        		int second = Integer.parseInt(args[3]); 
	        		GregorianCalendar newDate = new GregorianCalendar();
	        		newDate.setTimeInMillis(currentCommand.getDate().getTimeInMillis() 
		        			+ (new TimeSlice(dayOfMonth, hourOfDay, minute, second).getMillis()));
		        	currentCommand.setDate(newDate);
		        	
		        	System.out.println("Scheduled date extended "); 
		        	commandEditorOption = 0;
		        	displayCommand(currentCommand);
		        	break;
		        }
	    }
	    return true;
	
	}

	void displayCommandEditor(ArrayList<String> commandGroup2) {
		System.out.println("Creating a group of commands. Enter the number of the field you wish to edit.");
		System.out.println("Note: A command group ensures that the commands in it will be run in order");
		System.out.println("Commands:");
		for(int i = 0; i < currentCommandGroup.size(); i++){
			System.out.println((i + 1) + ". " + currentCommandGroup.get(i));
		}
		System.out.println("1: Add a command. ");
		System.out.println("2: Insert a command. ");
		System.out.println("3: Delete a command. ");
		System.out.println("4: Clear all commands.");
		System.out.println("5: Save and Exit.");
	}

	String displayDate(GregorianCalendar date){
		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
			+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
			":" + date.get(Calendar.MINUTE);
		return dateString;
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
