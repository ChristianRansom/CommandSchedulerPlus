package muttsworld.dev.team.CommandSchedulerPlus;

public class CommandSaverThread implements Runnable {
	protected Thread t;
	public AVLTree<ScheduledCommand> commands;
	private ScheduledCommand currentCommand;

	public CommandSaverThread(ScheduledCommand aCurrentCommand) {
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
