package constella_restaurant;

public class Waiter extends Thread {
	
	private volatile boolean terminate;
	private RestManager restManager;

	public Waiter(RestManager restManager) {
		// TODO Auto-generated constructor stub
		terminate = false;
		this.restManager = restManager;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		while(!terminate) {
			try {
				restManager.handleRequests();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setTerminate(boolean terminate) {
		this.terminate = terminate;
	}
}
