package muttsworld.dev.team.CommandSchedulerPlus;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	MainThread mainthread;
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	public final String prefix = ChatColor.YELLOW + "[" + ChatColor.BLUE + "CSP" + ChatColor.YELLOW + "] " + ChatColor.WHITE;

	AVLTree<ScheduledCommand> commands = new AVLTree<ScheduledCommand>();
	
	//This warning is thrown by the readObject... idk how the warning could possibly be avoided... 
    @SuppressWarnings("unchecked")
	@Override
    public void onEnable() {
    	
    	// ***** Load command list from file ******
    	try {
			// read object from file
			FileInputStream fis = new FileInputStream("C:\\Users\\Christian Ransom\\Desktop\\1.12.2_Server\\plugins\\CommandSchedulerPlus\\CommandSchedulerPlus.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			commands = (AVLTree<ScheduledCommand>)ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			console.sendMessage(prefix + "No Command data file found. Making a new one...");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	//generate default config or load in configured values
        config.addDefault("ScheduleTimer", 60); //Rename Check interval
        config.options().copyDefaults(true);
        saveConfig();
        
        //Start MainThread
        mainthread = new MainThread(commands, (long)config.getDouble("ScheduleTimer") * 1000, this);
        mainthread.start();
        
        //Set up the command handling class to be able to receive command events
        this.getCommand("csp").setExecutor(new CommandHandler(commands, this));
        
    }

    @Override
    public void onDisable() {
    	
    	console.sendMessage(prefix + "Stopping Main Thread");
    	mainthread.stopThread();
    	try {
    		
    		console.sendMessage(prefix + "Saving commands");
			// write object to file
			FileOutputStream fos = new FileOutputStream("C:\\Users\\Christian Ransom\\Desktop\\1.12.2_Server\\plugins\\CommandSchedulerPlus\\CommandSchedulerPlus.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(commands);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    //Code in main class so the command handler and threads can share it
    public void runCommand(ArrayList<CommandWithExecutor> commands){
		ArrayList<CommandWithExecutor> commandStrings = commands;
		for(CommandWithExecutor aCommand : commandStrings){
			//System.out.println("Running: " + aCommand);
			String executor = aCommand.getExecutor();
			
			//System.out.println("Command Executor = " + executor);
			
			if(executor.equalsIgnoreCase("ALLPLAYERS")){ //Runs command on each online player
				Collection<? extends Player> allPlayers = Bukkit.getServer().getOnlinePlayers();
				if(allPlayers.isEmpty()){
					//System.out.println("There are no players online");
				}
				else {
					for(Player aPlayer : allPlayers){
						Bukkit.dispatchCommand(aPlayer, aCommand.getCommandString());
					}
				}
			}
			else if(executor.equalsIgnoreCase("CONSOLE")){
				Bukkit.dispatchCommand(console, aCommand.getCommandString());
			}
			else { //Player Specific Command
				Player player = this.getServer().getPlayer(executor);
				if(player != null) {
					Bukkit.dispatchCommand(player, aCommand.getCommandString());
				}
				else {
					console.sendMessage(prefix + "Player not found");
				}
			}
		}
    }
    

}