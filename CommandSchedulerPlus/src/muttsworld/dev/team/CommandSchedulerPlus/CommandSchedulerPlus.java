package muttsworld.dev.team.CommandSchedulerPlus;



import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	public final ArrayList<ScheduledCommand> commands = 
		(ArrayList<ScheduledCommand>) Collections.synchronizedList(new ArrayList<ScheduledCommand>());

	
	
    @Override
    public void onEnable() {
    	
    	//sets up the CommanadHandler class to handle csp commands
        
        config.addDefault("ScheduleTimer", 60);
        config.options().copyDefaults(true);
        saveConfig();
        
        this.getCommand("csp").setExecutor(new CommandHandler(commands));
        
        MainThread mainthread = new MainThread(commands, (long)config.getDouble("ScheduleTimer") * 1000);
        mainthread.start();

    }

    @Override
    public void onDisable() {

    }
    

}
