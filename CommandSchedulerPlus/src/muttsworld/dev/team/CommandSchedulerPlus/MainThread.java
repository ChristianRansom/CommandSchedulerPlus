package muttsworld.dev.team.CommandSchedulerPlus;

public class MainThread extends CommandRunnerThread {
	
	private long sleepTime;
	private volatile boolean running = true;

	//Main Constructor - No default Constructor since I want to ensure the thread is created with its field given
	public MainThread(AVLTree<ScheduledCommand> commands2, long aSleepTime, CommandSchedulerPlus commandSchedulerPlus) {
		super(commands2, commandSchedulerPlus);
		sleepTime = aSleepTime;
	}

	@Override
	public void run() {
		while(running){
			super.run();
			// Let the thread sleep for a while.
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted.");
			}
	
			System.out.println("DEBUG: Thread exiting.");
		}
	}
	
	public void stopThread(){
		running = false;
	}
}
