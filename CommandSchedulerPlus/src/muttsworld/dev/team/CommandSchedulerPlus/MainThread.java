package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import muttsworld.dev.team.CommandSchedulerPlus.BST.TreeNode;

public class MainThread implements Runnable {
	private Thread t;
	public AVLTree<ScheduledCommand> commands;
	private long sleepTime;
	private volatile boolean running = true;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
	public MainThread(AVLTree<ScheduledCommand> commands2, long aSleepTime) {
		commands = commands2;
		sleepTime = aSleepTime;
	}

	@Override
	public void run() {
		
		System.out.println("DEBUG: Running thread");
		while(running){
			System.out.println("Running Commands");
			synchronized(commands){
				/*for(ScheduledCommand command : commands) {
					GregorianCalendar date = command.getDate();
					String dateString = (date.get(Calendar.MONTH) + 1) + "/" + date.get(Calendar.DATE) + "/"
							+ date.get(Calendar.YEAR) + " " + date.get(Calendar.HOUR) + ":" + date.get(Calendar.MINUTE);
					if (command.getDate().before(new GregorianCalendar())) {
						System.out.print(dateString + " is before now: " + new Date());
						System.out.print("DEBUG: running this command: " + command.getCommand());
						ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
						Bukkit.dispatchCommand(console, command.getCommand());
					} else {
						System.out.print(dateString + " is NOT before now: " + new Date());
					}
				}*/
				
				
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
			                current = current.right;
			            }
			            else if (nowCommand.compareTo(current.element) < 0){
			                current = current.left;
			                //System.out.println("Now is greater than run date so not running this one...");
			            }
		            }
		        }
			}

			// Let the thread sleep for a while.
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted.");
			}
		}

		System.out.println("DEBUG: Thread exiting.");
	}
	
	//does some magic... As we move down the tree comparing current time, when we move right, every element to the left should be run
	public void runCommands(TreeNode<ScheduledCommand> node){
		if(node == null) return;
		runCommands(node.left);
		runCommands(node.right);
		Bukkit.dispatchCommand(console, node.element.getCommand());

		//System.out.println("DEBUG: this command should be run " + node.element);
	}

	public void start() {
		System.out.println("Starting thread");
		t = new Thread(this, "mainthread");
		t.start();
	}
	
	public void stopThread(){
		running = false;
	}
}
