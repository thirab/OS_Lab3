import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Memory management tool that simulates the segmentation and the paging policy.
 * 
 * Assumptions: processes do not grow or shrink, no compaction is performed by 
 * the memory manager, the paging scheme assumes that all of process's pages are
 * resident in the main memory
 */
public class MemoryManager {
	// Policy
	protected static int policy;
	
	// Page size, memory size
	protected final static int PAGE_SIZE = 32;
	private int bytes, pages;
	
	// Map of pids to processes
	TreeMap<Integer, Process> memory;
	
	// List of free pages
	LinkedList<Integer> freePages;
	
	// Sorted list of holes
	TreeSet<Hole> holes;
	
	// Allocation failures
	int noMemoryFails = 0;
	int externalFragmentationFails = 0;

	/**
	 * Initializes the main memory and sets the policy.
	 * @param bytes the number of bytes the memory should be initialized to
	 * @param policy the policy to be used
	 */
	public MemoryManager(int bytes, int p) {
		// Check that the memory size is not negative
		assert bytes >= 0: "Error: You must assign memory";
		
		// Initialize the policy and the memory size
		policy = p;
		this.bytes = bytes;
				
		// Print out the initial information about the memory manager
		String policyString = "";
		if(policy == 0){
			policyString = "segmentation";
		}
		if(policy == 1){
			policyString = "paging";
		}
		System.out.println("Created a memory manager with " + policyString +" policy and " + bytes +" bytes.");
		System.out.println("================================================================");
		
		// Segmentation:
		if (policy == 0){
			//System.out.println("We are using segmentation...");
			holes = new TreeSet<Hole>();
			holes.add(new Hole(0, bytes));
		}
		
		// Paging:
		else if (policy == 1){
			//System.out.println("We are using paging...");
			freePages = new LinkedList<Integer>();
			pages =  bytes/PAGE_SIZE;
			for (int i = 0; i < pages; i++){
				freePages.add(i);
			}
		}
		memory = new TreeMap<Integer, Process>();
	}
	
	/**
	 * Allocates memory for the specified process
	 * @param bytes the amount of bytes to be allocated
	 * @param pid the id of the process
	 * @param text_size the size of the text segment
	 * @param data_size the size of the data segment
	 * @param heap_size the size of the heap segment
	 * @return 1 if the allocation was successful, -1 if the allocation was not successful
	 */
	public int allocate(int bytes, int pid, int text_size, int data_size, int heap_size){
		//System.out.println("Allocating " + bytes +" bytes to process " + pid + "...");
		
		// Initialize a new process
		Process p = new Process(pid, bytes);
		
		// Paging:
		if (policy == 1) {
			int pagesNeeded = p.pages.length;
			// Not enough memory
			if (pagesNeeded > freePages.size()) {
				System.out.println("Error: Cannot allocate memory to process " + pid + " due to insufficient memory");
				noMemoryFails++;
				return -1;
			}
			// Assign pages
			for (int i = 0; i < pagesNeeded; i++) {
				p.pages[i] = freePages.poll();
			}
			// Add process to memory
			memory.put(pid, p);
		}
		
		// Segmentation:
		else if (policy == 0){
			// Calculate unallocated bytes
			int freeBytes = getFreeBytes();
			
			// Not enough memory
			if (bytes > freeBytes){
				System.out.println("Error: Cannot allocate memory to process " + pid + " due to insufficient memory");
				noMemoryFails++;
				return -1;
			}
			
			// Allocate the process memory in segments using the first fit allocation policy
			// If allocation of one segment fails, deallocate all segments and return -1
			Hole h;
			
			if ((h = findHole(text_size)) != null){
				p.text_address = h.address;
				p.text_size = allocateSegment(text_size, h);
			
				if ((h = findHole(data_size)) != null){
					p.data_address = h.address;
					p.data_size = allocateSegment(data_size, h);
				
					if ((h = findHole(heap_size)) != null){
						p.heap_address = h.address;
						p.heap_size = allocateSegment(heap_size, h);
			
						// Allocation was successful
						memory.put(pid, p);
						return 1;
					}
					// Allocation was not successful
					else{
						deallocateSegment(p.data_address,p.data_size);
						deallocateSegment(p.text_address,p.text_size);
					}
				}
				else{
					deallocateSegment(p.text_address,p.text_size);
				}
			}
			externalFragmentationFails++;
			return -1;
		}// End segmentation
		return 1;
	}
	
	/**
	 * Calculates the amount of free bytes
	 * @return free bytes
	 */
	private int getFreeBytes(){
		int freeBytes = 0;
		
		Iterator<Hole> i = holes.iterator();
		while (i.hasNext()){
			freeBytes += i.next().size;
		}
		
		return freeBytes;
	}
	
	/**
	 * Finds a hole equal or bigger than the size specified
	 * using the first fit allocation policy.
	 * @param size the minimum size of the hole
	 * @return the hole that fulfills the size parameters
	 */
	private Hole findHole(int size){
		Hole h;
		
		Iterator<Hole> i = holes.iterator();
		while (i.hasNext()){
			h = i.next();
			if(h.size >= size){
				return h;
			}
		}
		// Appropriate hole was not found
		return null;
	}
	
	/**
	 * Allocates a segment of memory
	 * @param size the size of the segment
	 * @param h the hole where to place it
	 * @return 1 if the allocation was successful, -1 if the allocation was not successful
	 */
	private int allocateSegment (int size, Hole h){
		assert h != null: "The hole is null";
		
		int sizeSeg = 0;
		
		// The segment is allocated part of the hole
		if(h.size - size >= 16){
			sizeSeg = size;
			h.address += size;
			h.size -= size;
		}
		// The segment is allocated the entire hole
		else{
			sizeSeg = h.size;
			holes.remove(h);
		}
		return sizeSeg;
	}
	
	/**
	 * Deallocates a segment of memory
	 * @param address the address of the segment
	 * @param size the size of the segment
	 * @return 1 if the allocation was successful, -1 if the allocation was not successful
	 */
	private int deallocateSegment(int address, int size){
		assert size != 0: "Error: The segment is empty";
		
		Hole h = new Hole (address, size);
		
		// Gets the preceding hole and merges it with the new hole if needed
		Hole before = holes.lower(h);
		if (before != null && before.address + before.size == address){
			h.address = before.address;
			h.size = h.size+before.size;
			holes.remove(before);
		}
		
		// Gets the next hole and merges it with the new hole if needed
		Hole after = holes.higher(h);
		if (after != null && after.address == h.address + h.size){
			h.size = h.size + after.size;
			holes.remove(after);
		}
		
		// Adds the hole to the list
		if (holes.add(h)){
			return 1;
		}
		
		// Deallocation fails
		else{
			return -1;
		}
	}
	
	/**
	 *  Deallocates memory of the specified process
	 * @param pid the process that has memory to be deallocated
	 * @return 1 if the allocation was successful, -1 if the allocation was not successful
	 */
	/**
	 * @param pid
	 * @return
	 */
	public int deallocate(int pid){ 
		//System.out.println("Deallocating process " + pid + "...");
		
		// The process
		Process p = memory.get(pid);
		if(p == null){
			return -1;
		}
		
		// Segmentation:
		if (policy == 0){
			deallocateSegment(p.text_address, p.text_size);
			deallocateSegment(p.data_address, p.data_size);
			deallocateSegment(p.heap_address, p.heap_size);
		}
		
		// Paging
		else if (policy == 1){
			int [] pages = p.pages;
			for (int i = 0; i < pages.length; i++){
				freePages.add(pages[i]);
			}
		}
		
		// Remove the process from memory
		memory.remove(pid);
		return 1;
	}
	
	
	/**
	 * Prints the state of the memory
	 */
	public void printMemoryState()
	{ 
		// Segmentation:
		if (policy == 0) {
			int freeBytes = getFreeBytes();
			System.out.println("Memory size = " + bytes + ", allocated bytes = " + (bytes - freeBytes) + ", free = " + freeBytes);
			System.out.println("There are currently " + holes.size() + " holes and " + memory.size() + " active processes");
			printHoles();
		}
		
		// Paging:
		else if(policy == 1){
			System.out.println("Memory size = " + bytes + ", total pages = " + pages);
			System.out.println("Allocated pages = " + (pages - freePages.size()) + ", free pages = " + freePages.size());
			System.out.println("There are currently " + memory.size() + " active processes");
			
			// Free page list
			System.out.print("Free page list: ");
			Iterator<Integer> iter = freePages.iterator();
			while(iter.hasNext()){
				System.out.print(iter.next());
				if (iter.hasNext()){
					System.out.print(",");
				}
			}
		}
		// Shared information
		printProcessList();
		printFragmentation();
	}
	
	/**
	 * Prints the list of holes
	 */
	private void printHoles(){
		System.out.println("Hole list:");
		int holeNum = 1;
		Hole h;
		Iterator<Hole> iter = holes.iterator();
		while (iter.hasNext()){
			h = iter.next();
			System.out.println("\thole " + holeNum + ": start location = " + h.address + ", size = " + h.size);
			holeNum++;
		}
	}
	
	/**
	 * Prints the list of processes
	 */
	private void printProcessList(){
		System.out.println("\nProcess list:");
		for (Map.Entry<Integer, Process> e:memory.entrySet()){
			e.getValue().printProcessInformation();
		}
	}
	
	/**
	 * Prints the information about fragmentation and failed allocations
	 */
	private void printFragmentation(){
		int internalFragmentation = 0;
		for (Map.Entry<Integer, Process> e:memory.entrySet()){
			internalFragmentation += e.getValue().getInternalFragmentation();
		}
		System.out.println("Total Internal Fragmentation =  " + internalFragmentation  + " bytes");
		
		System.out.println("Failed allocations (No memory) =  " + noMemoryFails);
		System.out.println("Failed allocations (External fragmentation) = " + externalFragmentationFails +"\n\n");
	}
	
	public static void main(String[] args) throws IOException {
		// Take in a text file as a command line argument
		File file = null;
		if (0 < args.length) {
			String filename = args[0];
			file = new File(filename);
			}
		else {
				System.out.println("Invalid arguments count: " + args.length);
				System.exit(0);
				}
		
		// Read the text file and create jobs
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		// Read in the first line (memory size and policy)
		String line = reader.readLine();
		String[] initialize = line.split(" ");
		if(initialize.length > 2){
			System.out.println("Invalid input file format: More than 2 entries on the first line (" + initialize.length + ")");
			System.exit(0);
		}
		int memory = Integer.parseInt(initialize[0]);
		int temp = Integer.parseInt(initialize[1]);
		int policy = -1;
		if (temp == 0 || temp == 1){
			policy =  temp;
		}
		else{
			System.out.println("Invalid input file format: Policy can only be 0 or 1, but was: " + temp + ".");
			System.exit(0);
		}
		
		// New instance of the MemoryManager
		MemoryManager mm = new MemoryManager(memory, policy);
		
		// Read the rest of the file
		while ((line = reader.readLine()) != null) {
			Scanner s = new Scanner(line);
			s.useDelimiter(" ");
			
			// Action
			String action = s.next();
			if(action.equals("P")){
				mm.printMemoryState();
			}
			else if(action.equals("D")){
				int pid = s.nextInt();
				mm.deallocate(pid);
			}
			else if(action.equalsIgnoreCase("A")){
				int size = s.nextInt();
				int pid = s.nextInt();
				int text = s.nextInt();
				int data = s.nextInt();
				int heap = s.nextInt();
				mm.allocate(size, pid, text, data, heap);
			}
			else{
				System.out.println("Invalid input file format: Action can only be A, D or P, but was: " + action + ".");
				System.exit(0);
			}
		}
		reader.close();
	}
	
}

