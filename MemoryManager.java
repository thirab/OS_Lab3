/**
 * 
 * @author tai-lanhirabayashi
 *
 */
//Based on assumption that processes do not grow or shrink, 
//no compaction is performed by the memory manager, 
//and the paging scheme assumes that all of processÕs 
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

	public MemoryManager(int bytes, int policy) {
		// intialize memory with these many bytes.
	}
		// Use segmentation if policy==0, paging if policy==1 }
		public int allocate(int bytes, int pid, int text_size, int data_size, int heap_size)
		{
			return 0;
		}
		public int deallocate(int pid)
		{ //deallocate memory allocated to this process
		// return 1 if successful, -1 otherwise with an error message 
			return 0;
		}
		public void printMemoryState()
		{ // print out current state of memory
		// the output will depend on the memory allocator being used.
		// SEGMENTATION Example:
		// Memory size = 1024 bytes, allocated bytes = 179, free = 845
//		allocate this many bytes to the process with this id assume that each pid is unique to a process
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

