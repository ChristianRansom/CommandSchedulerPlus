package muttsworld.dev.team.CommandSchedulerPlus;

import java.util.ArrayList;

public class CommandList {
	private final ArrayList<ScheduledCommand> commands = new ArrayList<ScheduledCommand>();
	
	public CommandList(){
		
	}
	
	
	
    public synchronized void addCommand(ScheduledCommand command){
    	commands.add(command);
    }



	public void listCommands() {
		for(ScheduledCommand aCommand : commands){
			System.out.print(aCommand);
		}
		
	}
    
    
    

}
