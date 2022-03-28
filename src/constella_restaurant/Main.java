package constella_restaurant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RestManager restManager = new RestManager();
		Waiter waiter = new Waiter(restManager);
		List<ClientsGroup> clients = new ArrayList<ClientsGroup>();
		
		Random random = new Random();
		int maxClients = (random.nextInt(10)+30);
		
		waiter.start();
		
		for (int i=0; i<maxClients; i++) {
			ClientsGroup cGroup = new ClientsGroup(UUID.randomUUID(), (random.nextInt(4)+2), restManager);
			clients.add(cGroup);
			cGroup.start();
			try {
				Thread.sleep(60 * 1000 * (new Random().nextInt(10)+20));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Iterator<ClientsGroup> iterator = clients.iterator();
		
		while(iterator.hasNext()) {
			try {
				iterator.next().join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		waiter.setTerminate(true);
		restManager.closeRestaurant();
		
		try {
			waiter.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
