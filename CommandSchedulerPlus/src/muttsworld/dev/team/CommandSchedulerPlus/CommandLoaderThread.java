package muttsworld.dev.team.CommandSchedulerPlus;

public class CommandLoaderThread implements Runnable {
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	private ScheduledCommand currentCommand;

	public CommandLoaderThread(ScheduledCommand aCurrentCommand) {
		this.currentCommand = aCurrentCommand;
	}
	
	@Override
	public void run() {
		currentCommand.saveUUIDs();
	}

	public void start() {
		//System.out.println("Starting thread");
		t = new Thread(this, "CommandSaverThread");
		t.start();
	}
}
