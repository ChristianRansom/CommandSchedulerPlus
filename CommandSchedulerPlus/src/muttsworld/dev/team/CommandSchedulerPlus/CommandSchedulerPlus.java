package muttsworld.dev.team.CommandSchedulerPlus;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	public final List<ScheduledCommand> commands = 
			(List<ScheduledCommand>)Collections.synchronizedList(new ArrayList<ScheduledCommand>());

	
	
    @Override
    public void onEnable() {
    	
    	//sets up the CommanadHandler class to handle csp commands
        
        config.addDefault("ScheduleTimer", 60);
        config.options().copyDefaults(true);
        saveConfig();
        
        MainThread mainthread = new MainThread(commands, (long)config.getDouble("ScheduleTimer") * 1000);
        mainthread.start();
        
        this.getCommand("csp").setExecutor(new CommandHandler(commands));
        


    }

    @Override
    public void onDisable() {

    }
    

}
