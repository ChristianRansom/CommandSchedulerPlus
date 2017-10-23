package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CommandHandler implements CommandExecutor{
	private boolean commandEditor = false; //Temp value to handle multi-step creation of commands
	private final AVLTree<ScheduledCommand> commands; //the list of commands
	private ScheduledCommand currentCommand; //Used to hold variables while creating and adding a new command
	private int commandEditorOption = 0;
	
	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
    public CommandHandler(AVLTree<ScheduledCommand> commands2) {
    	commands = commands2;
	}
    //<code for later> if (sender instanceof Player) { Player player = (Player) sender;
    //Called when a command registered to the plugin in the plugin.yml is entered
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(commandEditor){
			return commandEditor(args);
		}
		else if(args[0].equals("create")){
			return createCommand();
		}
		else if(args[0].equals("forceupdate")){
			return forceupdate();
		}
		else if(args[0].equals("listcommands") || args[0].equals("commandlist")){
			return listCommands();
		}
		else {
			System.out.println("Uknown command usage");
			return true;
		}
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
		if(!aCommand.getRepeate().equals(new GregorianCalendar(0, 0, 0, 0, 0))){
			System.out.println("4: Repeating: " + simpleDate(aCommand.getRepeate()));
		}
		else {
			System.out.println("4: Repeating: false");
		}
		System.out.println("5: Player or Consol run: not implemented");
		System.out.println("6: Extend time till next run: not implemented");
	}
		
	private boolean commandEditor(String[] args) {
		//TODO check for valid date like month etc. 
        int year = 0, month = 0, dayOfMonth = 0, hourOfDay = 0, minute = 0;

		if(args[0].equals("1")){
			//handle new command being added
			String command = String.join(" ", Arrays.copyOfRange(args, 1, args.length)); //remove 'add' argument from the command
			currentCommand.setCommand(command);
			
			System.out.println("Good job, now enter a date. /csp Year Month Day Hour Seconds");
			return true;
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
				
	        	//This needs to be synchronized but it is because of the Synchronized Collection
	        	//Inserts it with magical AVLTree powers to the order of O(logn)
        		synchronized(commands){
        			System.out.println("Inserting into the tree..." );
        			commands.insert(currentCommand);
        		}
	        	
	        	System.out.println("Date Succesfully entered. Command added to be scheduled.");
	        	commandEditor = false;
				return true;

	        }   
	        else {
	        	System.out.println("Waiting for a date. Usage: /csp Year Month Day Hour Seconds");
				return true;
	    	}
		}

	}

	private boolean listCommands() {
		synchronized(commands){
			commands.inOrder();
		}
		return true;
	}

	private boolean forceupdate() {
		CommandRunnerThread thread = new CommandRunnerThread(commands);
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
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
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
