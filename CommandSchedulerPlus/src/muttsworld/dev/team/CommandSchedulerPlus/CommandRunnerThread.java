package muttsworld.dev.team.CommandSchedulerPlus;


import java.util.ArrayList;
import java.util.GregorianCalendar;
import muttsworld.dev.team.CommandSchedulerPlus.BST.TreeNode;

public class CommandRunnerThread implements Runnable {
	
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	public CommandSchedulerPlus plugin;
    private ArrayList<ScheduledCommand> commandsToRun = new ArrayList<ScheduledCommand>();
    private ArrayList<ScheduledCommand> commandsToDelete = new ArrayList<ScheduledCommand>();

	
	public CommandRunnerThread(AVLTree<ScheduledCommand> commands2, CommandSchedulerPlus commandSchedulerPlus) {
		commands = commands2;
		plugin = commandSchedulerPlus;
	}
	
	@Override
	public void run() {
		System.out.println("DEBUG: Running thread");
		System.out.println("Running Commands");
		synchronized(commands){
			TreeNode<ScheduledCommand> current = commands.getRoot();
			ScheduledCommand nowCommand = new ScheduledCommand(); //just holds the current time
            System.out.println("DEBUG: Searching for commands to run");
			if (current == null) {
	            System.out.println("DEBUG: There are no commands to run");
	        }
	        else {
	        	//Finds which commands in the tree need to run
	            while (current != null){ //TODO sometimes only some of the scheduled commands run
	            	//if now date is greater than current node, then this node, and all left of it needs to run
		            if (nowCommand.compareTo(current.element) > 0) {
		            	//System.out.println("DEBUG: this command should be run " + current.element);
		        		commandsToRun.add(current.element);
		        		commandsToDelete.add(current.element); 
		                TreeNode<ScheduledCommand> temp = current.left;
		        		current.left = null; //Cuts off left subtree 
		        		System.out.println("Cutting off left subtree");
		                commands.preOrder();
		        		addCommands(temp); //temp saves the current.left to access after we cut it off
		                current = current.right;
		            }
		            else if (nowCommand.compareTo(current.element) <= 0){
		                current = current.left;
		            }
		            
	            } //End While
	            
	            //Delete the nodes that aren't cut off from the tree
	            System.out.println("Commands to run: " + commandsToRun);
	            for(ScheduledCommand command : commandsToDelete){
	            	System.out.println("Preorder:");
	                commands.preOrder();
	            	commands.delete(command);
	            }
	        }
		} //end synchronized
		
		for(ScheduledCommand command : commandsToRun){
        	System.out.println("Preorder:");
            commands.preOrder();
			executeCommand(command);
		}
	}
		
	//does some magic... As we move down the tree comparing current time, when we move right, every element to the left should be run
	public void addCommands(TreeNode<ScheduledCommand> node){
		if(node == null) return;
		addCommands(node.left);
		addCommands(node.right);
		commandsToRun.add(node.element);
		//commands.delete(node.element); //Needs to delete before we reinsert...
	}
	
	public void executeCommand(ScheduledCommand command){
		//Executes the commands based on the executors
		plugin.runCommand(command.getCommands()); 
		//reschedule command 
		if(!command.getRepeat().isZero()){
			reschedule(command);
		}
	}
	
	public void reschedule(ScheduledCommand command){
		GregorianCalendar newDate = new GregorianCalendar();
		GregorianCalendar newScheduleTime = new GregorianCalendar();
		//Calculate difference from when its scheduled run time and now
		newScheduleTime.setTimeInMillis(newDate.getTimeInMillis() % command.getRepeat().getMillis());
		System.out.println("repeat % difference: " + new ScheduledCommand(newScheduleTime, "Test command").toString());
		ScheduledCommand newCommand = command.copy(); 
		//reschedule for now + repeat - currenttime % repeat 
		//This keeps it running according the the expected repeat time, so it doesn't shift over time due to restarts
		newScheduleTime.setTimeInMillis(newDate.getTimeInMillis() + (command.getRepeat().getMiliseconds() - newScheduleTime.getTimeInMillis()));
		newCommand.setDate(newScheduleTime);
		newCommand.setRepeat(command.getRepeat());
		System.out.println("Reschedule Inserting " + newCommand);
		commands.insert(newCommand);
	}

	public void start() {
		System.out.println("Starting thread");
		t = new Thread(this, "mainthread");
		t.start();
	}
}
