package muttsworld.dev.team.CommandSchedulerPlus;


import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import muttsworld.dev.team.CommandSchedulerPlus.BST.TreeNode;

public class CommandRunnerThread implements Runnable {
	
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	
	
	public CommandRunnerThread(AVLTree<ScheduledCommand> commands2) {
		commands = commands2;
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
		            if (nowCommand.compareTo(current.element) >= 0) {
		            	//System.out.println("DEBUG: this command should be run " + current.element);
		        		Bukkit.dispatchCommand(console, current.element.getCommand());
		                runCommands(current.left);
		                current.left = null; //Cuts off left subtree 
		                parent = current;
		                current = current.right;
		                commands.delete(parent.element); //deletes parent of the left subtree to call a tree re-balance
		            }
		            else if (nowCommand.compareTo(current.element) < 0){
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
		Bukkit.dispatchCommand(console, node.element.getCommand());

		//System.out.println("DEBUG: this command should be run " + node.element);
	}
	
	public void execucteCommmand(TreeNode<ScheduledCommand> node){
		Bukkit.dispatchCommand(console, node.element.getCommand());
		if(!node.element.getRepeat().equals(new GregorianCalendar(0, 0, 0, 0, 0))){
			GregorianCalendar newDate = new GregorianCalendar();
			GregorianCalendar difference = new GregorianCalendar();
			//Calculate difference from when its scheduled run time and now
			difference.setTimeInMillis(newDate.getTimeInMillis() - node.element.getDate().getTimeInMillis());
			newDate.setTimeInMillis(node.element.getRepeat().getTimeInMillis() % difference.getTimeInMillis());
			System.out.println("repeate % difference: " + newDate.toString());
			commands.insert(node.element.copy());
		}
	}

	public void start() {
		System.out.println("Starting thread");
		t = new Thread(this, "mainthread");
		t.start();
	}
}
