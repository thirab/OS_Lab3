/**
 * Represents a process stored in memory
 */
public class Process {
	// Process id
	int pid;
	
	// Process size
	int size;
	
	// Policy (0=>segmentation or 1=>paging)
	int policy = MemoryManager.policy;

	// Fragments
	int text_address, text_size;
	int data_address, data_size;
	int heap_address, heap_size;
	
	// Paging
	int PAGE_SIZE = MemoryManager.PAGE_SIZE;
	int[] pages;
	
	/**
	 * Initializes process and setup for paging
	 * @param pid process id
	 * @param size number of bytes to be allocated to this process
	 **/
	public Process(int pid,int size){
		this.pid = pid;
		this.size = size;
		
		// Initialize an array of pages
		if (policy == 1){
				int pagesNeeded = size/PAGE_SIZE;
				pages = new int[pagesNeeded];
		}
	}
	
	/**
	 * Calculates internal fragmentation of the process
	 * @return the number of bytes that represent internal fragmentation
	 **/
	public int getInternalFragmentation(){
		int fragmentation = 0;
		
		// Segmentation
		if(policy == 0){
			fragmentation = (text_size + data_size + heap_size) - size;
		}
		
		// Paging
		else if (policy == 1){
			fragmentation = size % PAGE_SIZE;
		}
		
		return fragmentation;
	}
	
	/**
	* Prints information about this process
	**/
	public void printProcessInformation(){
		System.out.print("process id = " + pid + ", size = " + size + " bytes, ");
		
		// Segmentation
		if (policy == 0){
			int allocation = text_size + data_size + heap_size;
			System.out.println("allocation = " + allocation + " bytes");
			System.out.println("\ttext start = " + text_address + ", size = " + text_size + " bytes");
			System.out.println("\tdata start = " + data_address + ", size = " + data_size + " bytes");
			System.out.println("\theap start = " + heap_address + ", size = " + heap_size + " bytes");
		}
		
		// Paging
		else if(policy == 1){
			System.out.println("number of pages = " + pages.length);
			
			int bytesUsed = PAGE_SIZE;
			for (int i=0; i < pages.length; i++){
				if(i == pages.length -1){
					bytesUsed = size%PAGE_SIZE;
					if(bytesUsed==0){
						bytesUsed=PAGE_SIZE;
					}
				}
				System.out.println("\tvirt page " + i + " -> phys page " + pages[i] + " used: " + bytesUsed + " bytes");
			}
		}
	}
}