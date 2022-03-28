package constella_restaurant;

public class Table {
	
	private int capacity;
	private int sat;
	
	public Table(int capacity) {
		// TODO Auto-generated constructor stub
		this.capacity = capacity;
		this.sat = 0;
	}
	
	public void sitPeople(int pQTY) {
		sat+=pQTY;
	}
	
	public void releaseSeats(int pQTY) {
		sat-=pQTY;
	}

	public int getCapacity() {
		return capacity;
	}

	public int getSat() {
		return sat;
	}

}
