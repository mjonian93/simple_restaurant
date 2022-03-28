package constella_restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class RestManager {
	
	List<Table> tableList;
	Map<UUID, Table> mapClgrpTable;
	Map<UUID, Semaphore> doorQueue;
	Map<UUID, Integer> waitingList;
	Map<UUID, Integer> satList;
	Map<UUID, Semaphore> leavingQueue;
	
	Semaphore waiterQueue;
	
	private volatile boolean restaurantOpen;
	
	private final int maxTables = 12;
	
	public RestManager() {
		// TODO Auto-generated constructor stub
		tableList = new ArrayList<Table>();
		mapClgrpTable = new HashMap<UUID, Table>();
		doorQueue = new HashMap<UUID, Semaphore>();
		waitingList = new HashMap<UUID, Integer>();
		satList = new HashMap<UUID, Integer>();
		leavingQueue = new HashMap<UUID, Semaphore>();
		
		waiterQueue = new Semaphore(0);
		
		createTables();
		
		restaurantOpen = true;
	}
	
	public boolean requestTable(UUID clgrpID, int clientQTY) throws InterruptedException {
		//Add to the clientQueue
		Semaphore doorSemaphore = new Semaphore(0);
		Boolean ret = false;
		
		synchronized (doorQueue) {
			doorQueue.put(clgrpID, doorSemaphore);
		}
		synchronized (waitingList) {
			waitingList.put(clgrpID, clientQTY);			
		}
		
		waiterQueue.release();
		ret = doorSemaphore.tryAcquire((new Random().nextInt(15)+15), TimeUnit.MINUTES);
		
		return ret;
	}
	
	public void handleRequests() throws InterruptedException {
		waiterQueue.acquire();
		
		if(restaurantOpen) {
		
			//Handle leavingQueue
			for(UUID cgID : leavingQueue.keySet()) {
				mapClgrpTable.get(cgID).releaseSeats(satList.get(cgID));
				mapClgrpTable.remove(cgID);
				satList.remove(cgID);
				synchronized (leavingQueue) {
					leavingQueue.get(cgID).release();
					leavingQueue.remove(cgID);
				}
			}
			
			//Handle doorQueue
			for (UUID cgID : doorQueue.keySet()) {
				int clientQTY;
				synchronized (waitingList) {
					clientQTY = waitingList.get(cgID);
				}
				Iterator<Table> it = tableList.iterator();
				while(it.hasNext()) {
					Table table = it.next();
					if((table.getCapacity()-table.getSat())>=clientQTY) {
						table.sitPeople(clientQTY);
						satList.put(cgID, clientQTY);
						synchronized (waitingList) {
							waitingList.remove(cgID);
						}
						mapClgrpTable.put(cgID, table);
						synchronized (doorQueue) {
							doorQueue.get(cgID).release();
							doorQueue.remove(cgID);
						}
					}
				}	
			}
		}
	}
	
	public void leaveTable(UUID clgrpID, int clientQTY) throws InterruptedException {
		Semaphore leavingSemaphore = new Semaphore(0);
		
		synchronized (leavingQueue) {
			leavingQueue.put(clgrpID, leavingSemaphore);
		}
		
		waiterQueue.release();
		leavingSemaphore.acquire();	
	}
	
	public void leaveDoorQueue(UUID clgrpID) {
		synchronized (doorQueue) {
			doorQueue.remove(clgrpID);
		}
		synchronized (waitingList) {
			waitingList.remove(clgrpID);
		}
	}

	private void createTables() {
		for(int i=0; i<maxTables; i++) {
			tableList.add(new Table((new Random().nextInt(6)+4)));
		}
	}
	
	public void closeRestaurant() {
		restaurantOpen = false;
		waiterQueue.release();
	}
}
