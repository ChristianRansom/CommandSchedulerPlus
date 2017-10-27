package muttsworld.dev.team.CommandSchedulerPlus;


import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import muttsworld.dev.team.CommandSchedulerPlus.BST.TreeNode;

public class CommandRunnerThread implements Runnable {
	
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	public CommandSchedulerPlus plugin;

	
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
			ScheduledCommand nowCommand = new ScheduledCommand();
			TreeNode<ScheduledCommand> parent;
            System.out.println("DEBUG: Searching for commands to run");
            
			if (current == null) {
	            System.out.println("DEBUG: There are no commands to run");
	        }
	        else {
	            while (current != null){
	            	//if now date is greater than current node, then this node, and all left of it needs to run
		            if (nowCommand.compareTo(current.element) > 0) {
		            	//System.out.println("DEBUG: this command should be run " + current.element);
		        		execucteCommmand(current); //this node will be deleted, for now its saved in the parent node
		                runCommands(current.left);
		                current.left = null; //Cuts off left subtree 
		                parent = current;
		                current = current.right;
		                commands.delete(parent.element); //deletes parent of the left subtree to call a tree re-balance
		            }
		            else if (nowCommand.compareTo(current.element) <= 0){
		                current = current.left;
		            }
	            }
	        }
		}
	}
		
		
	//does some magic... As we move down the tree comparing current time, when we move right, every element to the left should be run
	public void runCommands(TreeNode<ScheduledCommand> node){
		if(node == null) return;
		runCommands(node.left);
		runCommands(node.right);
		execucteCommmand(node);
		commands.delete(node.element);
		//System.out.println("DEBUG: this command should be run " + node.element);
	}
	
	public void execucteCommmand(TreeNode<ScheduledCommand> node){
		//Player player = plugin.getServer().getPlayer("spartagon123");
		//ArrayList<String> commandStrings = new ArrayList<String>();
		String[] commandStrings = node.element.getCommand().split("[/]+");
		System.out.println(commandStrings.length + " commands found");
		for(String aCommand : commandStrings){
			System.out.println("Running: " + aCommand);
			Bukkit.dispatchCommand(console, aCommand);
		}
		if(!node.element.getRepeat().isZero()){
			GregorianCalendar newDate = new GregorianCalendar();
			GregorianCalendar newScheduleTime = new GregorianCalendar();
			//Calculate difference from when its scheduled run time and now
			newScheduleTime.setTimeInMillis(newDate.getTimeInMillis() % node.element.getRepeat().getMillis());
			System.out.println("repeat % difference: " + new ScheduledCommand(newScheduleTime, "Test command").toString());
			ScheduledCommand newCommand = node.element.copy();
			//reschedule for now + repeat - currenttime % repeat 
			//This keeps it running according the the expected repeat time, so it doesn't shift over time due to restarts
			newScheduleTime.setTimeInMillis(newDate.getTimeInMillis() + (node.element.getRepeat().getMiliseconds() - newScheduleTime.getTimeInMillis()));
			newCommand.setDate(newScheduleTime);
			newCommand.setRepeat(node.element.getRepeat());
			commands.insert(newCommand);
		}
	}

	public void start() {
		System.out.println("Starting thread");
		t = new Thread(this, "mainthread");
		t.start();
	}
}
