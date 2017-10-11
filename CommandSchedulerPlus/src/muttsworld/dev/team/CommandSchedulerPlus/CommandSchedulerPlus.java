package muttsworld.dev.team.CommandSchedulerPlus;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
	 FileConfiguration config = getConfig();
	
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
        
        this.getCommand("csp").setExecutor(new CommandHandler());
        
        MainThread mainthread = new MainThread();
        mainthread.start();

    }

    @Override
    public void onDisable() {

    }
    

}
