package muttsworld.dev.team.CommandSchedulerPlus;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	public List<ScheduledCommand> commands = 
			(List<ScheduledCommand>)Collections.synchronizedList(new ArrayList<ScheduledCommand>());

	
	
    @Override
    public void onEnable() {
    	
    	// ***** Load command list from file ******
    	ObjectInputStream objectinputstream = null;
    	try {
    	    FileInputStream streamIn = new FileInputStream("C:\\Users\\Christian Ransom\\Desktop\\1.12.2_Server\\plugins\\CommandSchedulerPlus.ser");
    	    objectinputstream = new ObjectInputStream(streamIn);
    	    List<ScheduledCommand> readCase = (List<ScheduledCommand>) objectinputstream.readObject();
    	    commands = readCase;
    	} catch (Exception e) {
    	    e.printStackTrace();
    	} finally {
    	    if(objectinputstream != null){
    	        try {
					objectinputstream .close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    } 
    	}
    	
    	
    	
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
    	//Save commands to a file
    	ObjectOutputStream oos = null;
    	FileOutputStream fout = null;
    	try{
    		System.out.println("Saving Commands");
    	    fout = new FileOutputStream("C:\\Users\\Christian Ransom\\Desktop\\1.12.2_Server\\plugins\\CommandSchedulerPlus.ser", true);
    	    oos = new ObjectOutputStream(fout);
    	    oos.writeObject(commands);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	} finally {
    	    if(oos  != null){
    	        try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    	    } 
    	}
    }
    

}
