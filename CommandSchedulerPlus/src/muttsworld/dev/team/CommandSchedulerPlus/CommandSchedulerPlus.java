package muttsworld.dev.team.CommandSchedulerPlus;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;



public class CommandSchedulerPlus extends JavaPlugin {
	
	private FileConfiguration config = getConfig();
	MainThread mainthread;
	
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
			System.out.print("No command data found. Making a new one...");
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
    	
    	System.out.println("Stopping Main Thread");
    	mainthread.stopThread();
    	try {
    		
    		System.out.println("Saving commands");
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