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
		synchronized(commands){
			TreeNode<ScheduledCommand> current = commands.getRoot();
			ScheduledCommand nowCommand = new ScheduledCommand(); //just holds the current time
            //System.out.println("DEBUG: Searching for commands to run");
			if (current == null) {
	            //System.out.println("DEBUG: There are no commands to run");
	        }
	        else {
	        	//Finds which commands in the tree need to run
	            while (current != null){ 
	            	//if now date is greater than current node, then this node, and all left of it needs to run
		            if (nowCommand.compareTo(current.element) > 0) {
		            	//System.out.println("DEBUG: this command should be run " + current.element);
		        		commandsToRun.add(current.element);
		        		commandsToDelete.add(current.element); 
		                TreeNode<ScheduledCommand> temp = current.left;
		        		current.left = null; //Cuts off left subtree 
		        		//System.out.println("Cutting off left subtree");
		        		addCommands(temp); //temp saves the current.left to access after we cut it off
		                current = current.right;
		            }
		            else if (nowCommand.compareTo(current.element) <= 0){
		                current = current.left;
		            }
		            
	            } //End While
	            
	            //Delete the nodes that aren't cut off from the tree
	            for(ScheduledCommand command : commandsToDelete){
	            	commands.delete(command);
	            }
	        }
		} //end synchronized
		
		for(ScheduledCommand command : commandsToRun){
        	//System.out.println("Preorder:");
            //commands.preOrder();
			executeCommand(command);
		}
		commandsToRun.clear();
		commandsToDelete.clear();
	}
		
	//does some magic... As we move down the tree comparing current time, when we move right, every element to the left should be run
	public void addCommands(TreeNode<ScheduledCommand> node){
		if(node == null) return;
		addCommands(node.left);
		addCommands(node.right);
		commandsToRun.add(node.element);
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
		
		//if(scheduled time + repate > now)
		if((command.getDate().getTimeInMillis() + command.getRepeat().getMillis()) > newDate.getTimeInMillis()){
		    //newschedule = scheduledtime + repeat
			newScheduleTime.setTimeInMillis(command.getDate().getTimeInMillis() + command.getRepeat().getMillis());
		}
		else {
			//newschedule = now + ((now - oldschedule) % repat)
			newScheduleTime.setTimeInMillis(newDate.getTimeInMillis() + 
					((newDate.getTimeInMillis() - command.getDate().getTimeInMillis()) %  command.getRepeat().getMillis()));
		}
		ScheduledCommand newCommand = command.copy(); 
		//This keeps it running according the the expected repeat time, so it doesn't shift over time due to restarts
		
		newCommand.setDate(newScheduleTime);
		newCommand.setRepeat(command.getRepeat());
		//System.out.println("Reschedule Inserting " + newCommand);

		synchronized(commands) {
			commands.insert(newCommand);
		}
	}

	public void start() {
		//System.out.println("Starting thread");
		t = new Thread(this, "mainthread");
		t.start();
	}
}
