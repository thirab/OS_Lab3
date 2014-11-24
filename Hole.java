
/**
 * @author Pavlina Lejskova
 *
 * Represents a hole in memory.
 */
public class Hole implements Comparable{
	int address;
	int size;
	
	/**
	 * Initialize a hole.
	 * 
	 * @param address the address of the hole
	 * @param size the size of the hole
	 */
	public Hole(int address, int size){
		this.address = address;
		this.size = size;
	}

	
	/*
	 * Order holes by address
	 */
	public int compareTo(Object o) {
		return address - ((Hole)o).address;
	}

}
