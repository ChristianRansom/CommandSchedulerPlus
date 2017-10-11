package muttsworld.dev.team.CommandSchedulerPlus;

public class MainThread implements Runnable {
	private Thread t;
	   
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 System.out.println("Running thread" );
	      try {
	         for(int i = 50; i > 0; i--) {
	            System.out.println("Thread: " + i);
	            // Let the thread sleep for a while.
	            Thread.sleep(50);
	         }
	      }catch (InterruptedException e) {
	         System.out.println("Thread interrupted.");
	      }
	      System.out.println("Thread exiting.");
	}

	
	public void start () {
		System.out.println("Starting thread");
    	t = new Thread (this, "mainthread");
    	t.start ();
	}
}
