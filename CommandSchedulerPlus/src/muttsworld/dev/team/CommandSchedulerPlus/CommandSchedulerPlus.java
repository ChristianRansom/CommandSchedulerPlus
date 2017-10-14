package muttsworld.dev.team.CommandSchedulerPlus;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.LinkedList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	
	//Using Collections.synchronizedList to handle the synchronization easy without having to do it myself
	//A linked list is better for inserting elements into the middle of it. Array list would have to move everything over
	//Not decided on which to use yet.... 
	public LinkedList<ScheduledCommand> commands = 
			(LinkedList<ScheduledCommand>)Collections.synchronizedList(new LinkedList<ScheduledCommand>());

	//This warning is thrown by the readObject... idk how the warning could possibly be avoided... 
    @SuppressWarnings("unchecked")
	@Override
    public void onEnable() {
    	
    	// ***** Load command list from file ******
    	try {
			// read object from file
			FileInputStream fis = new FileInputStream("C:\\Users\\Christian Ransom\\Desktop\\1.12.2_Server\\plugins\\CommandSchedulerPlus\\CommandSchedulerPlus.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			commands = (LinkedList<ScheduledCommand>)ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			System.out.print("No command data found. Making a new one...");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    	
    	//generate default config or load in configured values
        config.addDefault("ScheduleTimer", 60);
        config.options().copyDefaults(true);
        saveConfig();
        
        //Start MainThread
        MainThread mainthread = new MainThread(commands, (long)config.getDouble("ScheduleTimer") * 1000);
        mainthread.start();
        
        //Set up the command handling class to be able to receive command events
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