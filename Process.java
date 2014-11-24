public class Process {
	int pid;
	int size;
	int policy = MemoryManager.policy;

	int text_address, text_size;
	int data_address, data_size;
	int heap_address, heap_size;
	
	int PAGE_SIZE = MemoryManager.PAGE_SIZE;
	int[] pages;
	
	/**
	 * Initialize process for paging
	 * @param pid process id
	 * @param size number of bytes to be allocated to this process
	 **/
	public Process(int pid,int size){
		this.pid = pid;
		this.size = size;
		
		// Initialize an array of pages
		if (policy == 1){
				int pagesNeeded = size/PAGE_SIZE;
				pages= new int[pagesNeeded];
		}
	}
	
	/**
	 * Calculate internal fragmentation of the process.
	 * 
	 * @return the number of bytes that represent internal fragmentation
	 **/
	public int getInternalFragmentation(){
		int fragmentation = 0;
		
		// Segmentation
		if(policy == 0){
			fragmentation = (text_size + data_size + heap_size)-size;
		}
		
		// Paging
		if (policy == 1){
			fragmentation = size % PAGE_SIZE;
		}
		return fragmentation;
	}
	
	/**
	* Print information about this process
	**/
	public void printProcessInformation(){
		System.out.println("Process id = " + pid + ", size = " + size + " bytes,");
		
		// Segmentation
		if (policy == 0){
			int allocation = text_size + data_size + heap_size;
			System.out.println("allocation = " + allocation + "bytes");
			System.out.println("text start = " + text_address + ", size = " + text_size);
			System.out.println("data start = " + data_address + ", size = " + data_size);
			System.out.println("heap start = " + heap_address + ", size = " + heap_size);
		}
		
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
				System.out.println("Virt page " + i + "-> Phys page " + pages[i] + " used: " + bytesUsed + "bytes");
			}
		}
	}
}