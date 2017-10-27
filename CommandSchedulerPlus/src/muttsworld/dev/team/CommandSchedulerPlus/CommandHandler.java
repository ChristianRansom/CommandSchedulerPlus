package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;


public class CommandHandler implements CommandExecutor{
	private boolean commandEditor = false; //Temp value to handle multi-step creation of commands
	private final AVLTree<ScheduledCommand> commands; //the list of commands
	private ScheduledCommand currentCommand; //Used to hold variables while creating and adding a new command
	private int commandEditorOption = 0;
	private ScheduledCommand editingCommand = null;
	public CommandSchedulerPlus plugin;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private String[] commandGroup = null;
	private boolean groupCommandEditor = false;
	private int groupCommandEditorOption = 0;

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
    public CommandHandler(AVLTree<ScheduledCommand> commands2, CommandSchedulerPlus commandSchedulerPlus) {
    	commands = commands2;
    	plugin = commandSchedulerPlus;
	}
    //Called when a command registered to the plugin in the plugin.yml is entered
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(groupCommandEditor){
			return createCommandGroup(args);
		}
		else if(commandEditor){
			return commandEditor(args);
		}
		else if(args[0].equals("create")){
			return createCommand();
		}
		else if(args[0].equals("forceupdate")){
			return forceupdate();
		}
		else if(args[0].equals("forcerun")){
			return forceRun(args);
		}
		else if(args[0].equals("listcommands") || args[0].equals("commandlist") || args[0].equals("list")){
			return listCommands();
		}
		else if(args[0].equals("edit")){
			return editCommand(args);
		}
		else if(args[0].equals("delete")){
			return deleteCommand(args);
		}
		else if(args[0].equals("preorder")){
			commands.preOrder();
			return true;
		}
		else if(args[0].equals("updatesize")){
			commands.updateSize();
			return true;
		}
		else if(args[0].equals("time")){
			System.out.print(displayDate(new GregorianCalendar()));
			return true;
		}
		else {
			System.out.println("Uknown command usage");
			return true;
		}
	}
	
	private boolean createCommandGroup(String[] args) {
		switch(groupCommandEditorOption) {
	        case 0 :
	        	groupCommandEditorOption = Integer.parseInt(args[0]);
	        	if(commandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(commandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : System.out.println("Enter the command you want to add to the group."); break;
		        		case 2 : System.out.println("Enter the number of where you want to insert a command."); break;
		        		case 3 : System.out.println("Enter the number of the command you want to delete."); break;
		        		case 4 : System.out.println("Cleared all the commands"); 
		        			//Clears the commands....
		        			break;
		        		case 5 : System.out.println("Command Group Saved"); 
		        			if(commandGroup == null) {
			        			System.out.println("No commands entered... Exiting");
		        			}
		        			else{
			        			currentCommand.setCommand(String.join("/", Arrays.copyOfRange(commandGroup, 0, args.length)));
		        			}
		        			groupCommandEditorOption = 0;
				        	commandEditor = false;
		        			break;
		        	}
	        	}
	        	else{
		    		System.out.println("Enter an option from 1 to 5");
	        	}
	        	break; //Breaks from outer switch case
	        case 1 : //handle new command being added
	        	break;
	        case 2 :
	        	break;
	        case 3 :
	        	break;
		}
        	
		return false;
	}
	private boolean forceRun(String[] args) {
		
		//System.out.println("Finding command " + args[1]);
		synchronized(commands) {
			commandGroup = commands.find(Integer.parseInt(args[1])).getCommand().split("[/]+");
			System.out.println(commandGroup.length + " commands found");
			for(String aCommand : commandGroup) {
				System.out.println("Running: " + aCommand);
				Bukkit.dispatchCommand(console, aCommand);
			}
		}
		
		
		return true;
	}
	private boolean deleteCommand(String[] args) {
		System.out.println("Finding command " + args[1]);
		synchronized(commands) {
			commands.delete(commands.find(Integer.parseInt(args[1])));
		}
		return true; 
	}
	private boolean editCommand(String[] args) {
		System.out.println("Finding command " + args[1]);
		System.out.println("commands.getSize(): " + commands.getSize());
		editingCommand = (commands.find(Integer.parseInt(args[1])));
		System.out.println(editingCommand);
		currentCommand = editingCommand.copy();
		commandEditor = true;
		displayCommand(currentCommand);
		return true;
	}
	private boolean createCommand(){
		currentCommand = new ScheduledCommand();
		commandEditor = true;
		displayCommand(currentCommand);
		return true;
	}
	
	private void displayCommand(ScheduledCommand aCommand) {
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
		
	private boolean commandEditor(String[] args) {
        int year = 0, month = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;
        switch(commandEditorOption) {
	        case 0 :
	        	commandEditorOption = Integer.parseInt(args[0]);
	        	if(commandEditorOption > 0 && commandEditorOption <= 8) {
		        	switch(commandEditorOption){ //Should move all messages here for maintainability 
		        		case 1 : System.out.println("Enter the command you wish to schedule, or type 'commandgroup' to add multiple commands"); break;
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
	        		displayCommandEditor(commandGroup);
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

	private void displayCommandEditor(String[] commandGroup2) {
		System.out.println("Creating a group of commands. Enter the number of the field you wish to edit.");
		System.out.println("Note: A command group ensures that the commands in it will be run in order");
		System.out.println("1: Add a command. ");
		System.out.println("2: Insert a command. ");
		System.out.println("3: Delete a command. ");
		System.out.println("4: Save command group.");
		System.out.println("5: Exit.");
	}
	
	private boolean listCommands() {
		synchronized(commands){
			commands.inOrder();
		}
		return true;
	}

	private boolean forceupdate() {
		CommandRunnerThread thread = new CommandRunnerThread(commands, plugin);
		thread.start();
		return true;
	}
	
	private String displayDate(GregorianCalendar date){
		String dateString = (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.DATE) 
			+ "/" + date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) +
			":" + date.get(Calendar.MINUTE);
		return dateString;
	}
	
	private String simpleDate(GregorianCalendar date){
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
