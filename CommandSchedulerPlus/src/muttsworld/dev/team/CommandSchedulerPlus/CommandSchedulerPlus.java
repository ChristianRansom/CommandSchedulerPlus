package muttsworld.dev.team.CommandSchedulerPlus;


import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
    @Override
    public void onEnable() {
    	
    	//sets up the CommanadHandler class to handle csp commands
        this.getCommand("csp").setExecutor(new CommandHandler());
    }

    @Override
    public void onDisable() {

    }
    

}
