package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;

public class CommandList {
	private final ArrayList<ScheduledCommand> commands = new ArrayList<ScheduledCommand>();
	
	public CommandList(){
		
	}
	
	
	
    public synchronized void addCommand(ScheduledCommand command){
    	System.out.print("DEBUG: adding " + command + " to the command list");
    	commands.add(command);
    	System.out.print(commands);
    }



	public void listCommands() {
		for(ScheduledCommand aCommand : commands){
			System.out.print(aCommand);
		}
		
	}
    
    
    

}
