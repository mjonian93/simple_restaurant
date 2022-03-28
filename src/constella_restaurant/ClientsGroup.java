package constella_restaurant;

import java.util.Random;
import java.util.UUID;

public class ClientsGroup extends Thread {
	
	private UUID cgID;
	private int clientQTY;
	private RestManager restManager;
	
	public ClientsGroup(UUID uuid, int clientQTY, RestManager restManager) {
		// TODO Auto-generated constructor stub
		this.cgID = uuid;
		this.clientQTY = clientQTY;
		this.restManager = restManager;
	}
	
	@Override
	public void run() {
		
		try {
			if(restManager.requestTable(cgID, clientQTY)) {
				havingMeal();
				restManager.leaveTable(cgID, clientQTY);
			}else {
				restManager.leaveDoorQueue(cgID);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void havingMeal() {
		try {
			sleep(60 * 1000 * (new Random().nextInt(10)+30));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
