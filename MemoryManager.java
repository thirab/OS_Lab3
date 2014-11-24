import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author tai-lanhirabayashi
 *
 */
//Based on assumption that processes do not grow or shrink, 
//no compaction is performed by the memory manager, 
//and the paging scheme assumes that all of process’s 
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
	boolean paging = false;
	int totalMemory;
	int freeMemory;
	int processCount =0;
	int holeCount =0;
	int pageSize = 32;
	int failed = 0;
	MemoryObject freeList = null;
	MemoryObject takenList = null;
	//use a map to track pid to location in arrayList
	HashMap pidToLocation = new HashMap();

	public MemoryManager(int bytes, int policy) {
		if(bytes<0){
			 // this is impossible
			System.out.println("Error you must set memory");
		}
		// Use segmentation if policy==0, paging if policy==1 }
		if(policy == 1){
			paging = true;
			System.out.println("we are using paging");
			//TODO create pages
			pagingSetup();
		}else{
			System.out.println("We are using segmentation");
			freeList = new MemoryObject(0,totalMemory,totalMemory,-99);
			holeCount++;
		}
		// intialize memory with these many bytes.
		totalMemory=bytes;
		freeMemory=bytes;

	}
	
	public void pagingSetup(){
		int numPages = totalMemory / pageSize;
		for(int i =0; i<numPages ;i++){
			//create the pages
			addPageToEnd(freeList);
		}
		//free pages have been created 
	}
	
	/**
	 * addPageToEnd adds an additional page
	 * 
	 * @param start
	 */
	public void addPageToEnd(MemoryObject start){
		if(start ==null){
			start = new MemoryObject(0,0+pageSize,pageSize,-99 );
			return;
		}
		MemoryObject s = start;
		while(s.getFollowing()!=null){
			s=s.getFollowing();
		}
		int place = s.getEnd();
		MemoryObject next = new MemoryObject(place,place+pageSize,pageSize,-99);
		holeCount++;
		s.setFollowing(next);
	}
	
	public int allocate(int bytes, int pid, int text_size, int data_size, int heap_size)
	{
		if(totalMemory >= (freeMemory + bytes)){
			//there is enough room
			if(paging){
				return allocatePaging(bytes, pid);
			}
			return allocateSegmentation(bytes, pid, text_size, data_size, heap_size);
		}else{
			failed++;
			//there is not enough room in memory
		}
		
		return 0;
	}
	
	public int allocatePaging(int bytes, int pid){
		int pagesNeeded = bytes / pageSize;
		int mod = bytes % pageSize;
		if(mod !=0){
			pagesNeeded++;
		}
		int pagesAllocated =0;
		//TODO 
		return 0;
	}
	
	public int allocateSegmentation(int bytes, int pid, int text_size, int data_size, int heap_size){
		if(bytes != (text_size+data_size+heap_size)){
			//there is something wrong
			//error 
			return -1;
		}
		MemoryObject current = freeList;

		while(current!=null){
			int size = current.getEnd() - current.getStart();
			if( size >=bytes){
				if(size >bytes+16){
					//
					//cut memory save some free
				}else{
					//move to taken stack
					
				}
			}
			
			current = current.getFollowing();
		}
		failed ++;
		return -1;
	}
	public int deallocate(int pid)
	{ //deallocate memory allocated to this process
		
		//Find if this ID is in the taken memory list (does it exist)
		MemoryObject next = takenList;
		boolean removed = false;
		
		while(next!=null){
			if(next.getID() == pid){
				MemoryObject previous = next.getPrevious();
				MemoryObject following = next.getFollowing();
				previous.setFollowing(following);
				following.setPrevious(previous);
				
				
				next.resetId();
				//call addNodeToFre to handle check to see if anything in free shares a border with next
				addNodeToFree(next);
				//TODO do removal, return 1
				//if it does exist remove it from the taken list, add that amount to the free list
				//in adding if it links (aka shares a boundry with any other memoryObject, add them together. 
				//create a single mem O (aka change the range of one)
				following = next;
				removed=true;
				if(!paging){
					
					return 1;
				}
			}
			next=next.getFollowing();
		}
		if(removed){
			return 1;
		}
		// return 1 if successful, -1 otherwise with an error message 
		
		
		return -1;
	}
	
	public void addNodeToFree(MemoryObject o){
		int start = o.getStart();
		int end = o.getEnd();
		MemoryObject current = freeList;
		
		//TODO should this be sorted in some way? change alg to sort in future?
		if(current == null){
			current = o;
			current.resetId();
		}
		MemoryObject next = current.getFollowing();
		if(next == null){
			o.resetId();
			current.setFollowing(o);
			tryCombineNodes(current,o);
		}
		while(next.getFollowing()!=null){
			next=next.getFollowing();
		}
		o.resetId();
		next.setFollowing(o);
		tryCombineNodes(next,o);
		
		//check to see if anything in free shares a border with next
	}
	
	// maybe this could become a memory Object function?
	public void tryCombineNodes(MemoryObject one, MemoryObject two){
		//TODO the combination
		int oneStart = one.getStart();
		int twoStart = two.getStart();
		int oneEnd = one.getEnd();
		int twoEnd = two.getEnd();
		if(oneStart-1 == twoEnd){
			two.setEnd(oneEnd);
			two.addMem(one.getMem());
			two.setFollowing(one.getFollowing());
			
			//combine ...
		}else if(twoStart-1 == twoEnd){
			one.setEnd(twoEnd);
			one.addMem(two.getMem());
			one.setFollowing(two.getFollowing());
		}
	}
	
	public void printMemoryState()
	{ // print out current state of memory
		// the output will depend on the memory allocator being used.
		// SEGMENTATION Example:
		// Memory size = 1024 bytes, allocated bytes = 179, free = 845
		System.out.println("The Memory size is: " + totalMemory + "The allocated bytes: " +
				(totalMemory - freeMemory) + "Free memory is: " + freeMemory);
		MemoryObject f = freeList;
		MemoryObject t = takenList;
		if(paging){
			System.out.println("Memory size: " + totalMemory + " there are " +totalMemory/pageSize + "pages");
		}else{
			System.out.println("Memory size: " + totalMemory);
			System.out.println("There are " + processCount + " processes and " + holeCount + "holes");
		}
		System.out.println("There have been " +failed +" failed additions");
		if(paging){
			//TODO print pages
		}else{
			//TODO print segmentation
		}
		
		//		allocate this many bytes to the process with
		//      this id assume that each pid is unique to a process
		//		if using the Segmentation allocator: text_size, data_size, and heap_size are the size of each segment. Verify that:
		//		text_size + data_size + heap_size = bytes
		//		if using the paging allocator, simply ignore the segment size variables return 1 if successful
		//		return -1 if unsuccessful; print an error indicating
		//whether there wasn't sufficient memory or whether you ran into external fragmentation
		// There are currently 10 holes and 3 active process
		// Hole list:
		// hole 1: start location = 0, size = 202
		// ...
		// Process list:
		// process id=34, size=95 allocation=95
		// text start=202, size=25
		// data start=356, size=16
		// heap start=587, size=54
		// process id=39, size=55 allocation=65
		// ...
		// Total Internal Fragmentation = 10 bytes
		// Failed allocations (No memory) = 2
		// Failed allocations (External Fragmentation) = 7 //
		// PAGING Example:
		// Memory size = 1024 bytes, total pages = 32
		// allocated pages = 6, free pages = 26
		// There are currently 3 active process
		// Free Page list:
		// 2,6,7,8,9,10,11,12...
		// Process list:
		// Process id=34, size=95 bytes, number of pages=3
		// Virt Page 0 -> Phys Page 0 used: 32 bytes
		// Virt Page 1 -> Phys Page 3 used: 32 bytes
		// Virt Page 2 -> Phys Page 4 used: 31 bytes
		// Process id=39, size=55 bytes, number of pages=2
		// Virt Page 0 -> Phys Page 1 used: 32 bytes
		// Virt Page 1 -> Phys Page 13 used: 23 bytes
		// Process id=46, size=29 bytes, number of pages=1
		// Virt Page 0 -> Phys Page 5 used: 29 bytes //
		// Total Internal Fragmentation = 13 bytes
		// Failed allocations (No memory) = 2
		// Failed allocations (External Fragmentation) = 0 //
	}
}

