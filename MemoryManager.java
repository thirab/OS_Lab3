import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author tai-lanhirabayashi
 *
 */
//Based on assumption that processes do not grow or shrink, 
//no compaction is performed by the memory manager, 
//and the paging scheme assumes that all of process's 
//pages are resident in the main memory.

//Write a segmentation based allocator which allocates three segments 
//for each process: text, stack, and heap. 
//The memory region within each segment must be contiguous, but the three
//segments do not need to be placed contiguously. Instead, you should use either 
//a best fit, first fit, or worst fit memory allocation policy to find a free region 
//for each segment. The policy choice is yours, but you must explain why you picked that
//policy in your report. When a segment is allocated within a hole, if the remaining 
//space is less than 16 bytes then the segment should be allocated the full hole. 
//This will cause some internal fragmentation but prevents the memory allocator 
//from having to track very small holes. 
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
	 * 
	 * @param bytes the number of bytes the memory should be initialized to
	 * @param policy the policy to be used (PAGING or SEGMENTATION)
	 */
	public MemoryManager(int bytes, int p) {
		System.out.println("Creating a memory manager with " + bytes +" bytes and policy " + policy);
		assert bytes >= 0: "Error: You must assign memory";
		
		policy = p;
		this.bytes = bytes;
		
		// Segmentation:
		if (policy == 0){
			System.out.println("We are using segmentation...");
			holes = new TreeSet<Hole>();
			holes.add(new Hole(0, bytes));
		}
		
		// Paging:
		if (policy == 1){
			System.out.println("We are using paging...");
			freePages = new LinkedList<Integer>();
			pages =  bytes/PAGE_SIZE;
			for (int i = 0; i < pages; i++){
				freePages.add(i);
			}
		}
		memory = new TreeMap<Integer, Process>();
	}
	
	public int allocate(int bytes, int pid, int text_size, int data_size, int heap_size)
	{
		System.out.println("Allocating " + bytes +" bytes to process " + pid);
		
		// Initialize a new process
		Process p = new Process(pid, bytes);
		
		// Paging
		if (policy == 1) {
			int pagesNeeded = p.pages.length;
			// Not enough memory
			if (pagesNeeded > freePages.size()) {
				System.out.println("Error: The memory is not sufficient");
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
		
		// Segmentation
		else if (policy == 0){
			// Calculate unallocated bytes
			int freeBytes = getFreeMemory();
			
			// Not enough memory
			if (bytes > freeBytes){
				System.out.println("Error: The memory is not sufficient");
				noMemoryFails++;
				return -1;
			}
			
			// Allocate the process by segments
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
					}else{
						deallocateSegment(p.data_address,p.data_size);
						deallocateSegment(p.text_address,p.text_size);
					}
				
				}else{
					deallocateSegment(p.text_address,p.text_size);
				}
			}
			// Allocation failed
			externalFragmentationFails++;
			return -1;
		}// End segmentation
		return 1;
	}
	
	/**
	 * Calculate free bytes
	 * 
	 * @return free bytes
	 */
	private int getFreeMemory(){
		int freeBytes = 0;
		
		Iterator<Hole> i = holes.iterator();
		while (i.hasNext()){
			freeBytes += i.next().size;
		}
		
		return freeBytes;
	}
	
	/**
	 * Finds a hole equal or bigger than the size specified.
	 * 
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
		return null;
	}
	
	/**
	 * Allocates a segment of memory of a specific size.
	 * 
	 * @param size the size of the segment
	 * @param h the hole where to place it
	 * @return
	 */
	private int allocateSegment (int size, Hole h){
		assert h != null: "The hole is null";
		
		int sizeSeg = 0;
		
		if(h.size - size >= 16){
			sizeSeg = size;
			h.address += size;
			h.size -= size;
		}
		else{
			sizeSeg = h.size;
			holes.remove(h);
		}
		return sizeSeg;
	}
	
	private int deallocateSegment(int address, int size){
		assert size != 0: "Error: The segment is empty";
		
		Hole h = new Hole (address, size);
		
		Hole before = holes.lower(h);
		Hole after = holes.higher(h);
		
		if (before != null && before.address + before.size == address){
			h.address = before.address;
			h.size = h.size+before.size;
			holes.remove(before);
		}
		
		if (after != null && after.address == h.address + h.size){
			h.size = h.size + after.size;
			holes.remove(after);
		}
		
		if (holes.add(h)){
			return 1;
		}else{
		return -1;
		}
	}
	

	public int deallocate(int pid)
	{ 
		System.out.println("Deallocating process " + pid + "...");
		Process p = memory.get(pid);
		
		if(p == null){
			return -1;
		}
		
		// Segmentation
		if (policy == 0){
			deallocateSegment(p.text_address, p.text_size);
			deallocateSegment(p.data_address, p.data_size);
			deallocateSegment(p.heap_address, p.heap_size);
		}
		
		// Paging
		if (policy == 1){
			int [] pages = p.pages;
			for (int i = 0; i < pages.length; i++){
				freePages.add(pages[i]);
			}
		}
		memory.remove(pid);
		return 1;
	}
	
	
	public void printMemoryState()
	{ 
		// Segmentation
		if (policy == 0) {
			int freeBytes = getFreeMemory();
			System.out.println("Memory size = " + bytes + ", allocated bytes = " + (bytes - freeBytes) + ", free = " + freeBytes);
			System.out.println("There are currently" + holes.size() + " holes and " + memory.size() + " active processes");
			
			printHoles();
			printProcessList();
			printFragmentation();
		}
		
		if(policy == 1){
			System.out.println("Memory size = " + bytes + ", total pages = " + pages);
			System.out.println("Allocated pages = " + pages + ", free pages = " + freePages.size());
			System.out.println("There are currently " + memory.size() + " active processes");
			System.out.println("Free page list: ");
			// Free page list:
			Iterator<Integer> iter = freePages.iterator();
			while(iter.hasNext()){
				System.out.print(iter.next());
				if (iter.hasNext()){
					System.out.print(",");
				}
			}
			
			printProcessList();
			printFragmentation();
		}
	}
	
	private void printHoles(){
		System.out.println("Hole list:");
		int holeNum = 1;
		Hole h;
		Iterator<Hole> iter = holes.iterator();
		while (iter.hasNext()){
			h = iter.next();
			System.out.println("hole " + holeNum + ": start location = " + h.address + ", size = " + h.size);
			holeNum++;
		}
	}
	
	private void printProcessList(){
		System.out.println("Process list:");
		for (Map.Entry<Integer, Process> e:memory.entrySet()){
			e.getValue().printProcessInformation();
		}
	}
	
	private void printFragmentation(){
		int internalFragmentation = 0;
		for (Map.Entry<Integer, Process> e:memory.entrySet()){
			internalFragmentation += e.getValue().getInternalFragmentation();
		}
		
		System.out.println("Total Internal Fragmentation =  " + internalFragmentation);
		
		System.out.println("Failed allocations (No memory) =  " + noMemoryFails);
		System.out.println("Failed allocations (External fragmentation) = " + externalFragmentationFails);
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

