package muttsworld.dev.team.CommandSchedulerPlus;


import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	public CommandList commands = new CommandList();
	
	
    @Override
    public void onEnable() {
    	
    	//sets up the CommanadHandler class to handle csp commands
        
        config.addDefault("ScheduleTimer", 100);
        config.options().copyDefaults(true);
        saveConfig();
        
        
        if (config.getDouble("ScheduleTimer") != 100) {
        	System.out.println("ScheduledTimer doesn't equal 100!");
        }
        else {
        	System.out.println("ScheduledTimer equals 100!");
        }
        
        this.getCommand("csp").setExecutor(new CommandHandler(commands));
        
        MainThread mainthread = new MainThread(commands);
        mainthread.start();

    }

    @Override
    public void onDisable() {

    }
    

}
