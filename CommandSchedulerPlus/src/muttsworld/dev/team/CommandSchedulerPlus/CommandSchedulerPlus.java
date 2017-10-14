package muttsworld.dev.team.CommandSchedulerPlus;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

	
	
    @SuppressWarnings("unchecked")
	@Override
    public void onEnable() {
    	
    	// ***** Load command list from file ******
    	try {
			// read object from file
			FileInputStream fis = new FileInputStream("C:\\Users\\Christian Ransom\\Desktop\\1.12.2_Server\\plugins\\CommandSchedulerPlus\\CommandSchedulerPlus.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			commands = (List<ScheduledCommand>)ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			System.out.print("No command data found. Making a new one...");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
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
    	try {
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
    

}