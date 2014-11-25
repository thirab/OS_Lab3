Group Members:
  - Tai-Lan Hirabayashi
  - Pavlina Lejskova
  
MemoryManagement folder content:
  Memory Management Program
  - MemoryManager.java
  - Process.java
  - Hole.java
  - sample.txt
  Additional File
  - README.txt
  - report.pdf
 
Instructions:
  - open command line in the 'MemoryManagement' folder
  - to compile the files type:
 
      javac *.java
    
  - to run the program type:
      java MemoryManager textfilename
      
- replace 'textfilename' with the name of the file that containts the input
  (the text file has to be in the 'MemoryManagement' folder and has to have the same
  format as 'sample.txt')
        
Sample Results Using 'sample.txt':

	> java MemoryManager sample.txt //SEGMENTATION
	

Created a memory manager with segmentation policy and 1024 bytes.
================================================================
Memory size = 1024, allocated bytes = 758, free = 266
There are currently 2 holes and 2 active processes
Hole list:
	hole 1: start location = 0, size = 234
	hole 2: start location = 992, size = 32

Process list:
process id = 2, size = 458 bytes, allocation = 458 bytes
	text start = 234, size = 300 bytes
	data start = 534, size = 98 bytes
	heap start = 632, size = 60 bytes
process id = 3, size = 300 bytes, allocation = 300 bytes
	text start = 692, size = 150 bytes
	data start = 842, size = 80 bytes
	heap start = 922, size = 70 bytes
Total Internal Fragmentation =  0 bytes
Failed allocations (No memory) =  0
Failed allocations (External fragmentation) = 0


Error: Cannot allocate memory to process 5 due to insufficient memory
Memory size = 1024, allocated bytes = 404, free = 620
There are currently 2 holes and 2 active processes
Hole list:
	hole 1: start location = 234, size = 458
	hole 2: start location = 862, size = 162

Process list:
process id = 4, size = 220 bytes, allocation = 234 bytes
	text start = 0, size = 90 bytes
	data start = 90, size = 60 bytes
	heap start = 150, size = 84 bytes
process id = 6, size = 170 bytes, allocation = 170 bytes
	text start = 692, size = 105 bytes
	data start = 797, size = 5 bytes
	heap start = 802, size = 60 bytes
Total Internal Fragmentation =  14 bytes
Failed allocations (No memory) =  1
Failed allocations (External fragmentation) = 0


Memory size = 1024, allocated bytes = 868, free = 156
There are currently 2 holes and 4 active processes
Hole list:
	hole 1: start location = 298, size = 26
	hole 2: start location = 894, size = 130

Process list:
process id = 8, size = 345 bytes, allocation = 345 bytes
	text start = 0, size = 50 bytes
	data start = 50, size = 110 bytes
	heap start = 324, size = 185 bytes
process id = 9, size = 128 bytes, allocation = 138 bytes
	text start = 160, size = 74 bytes
	data start = 509, size = 32 bytes
	heap start = 541, size = 32 bytes
process id = 10, size = 128 bytes, allocation = 128 bytes
	text start = 573, size = 64 bytes
	data start = 637, size = 32 bytes
	heap start = 862, size = 32 bytes
process id = 11, size = 245 bytes, allocation = 257 bytes
	text start = 669, size = 120 bytes
	data start = 234, size = 64 bytes
	heap start = 789, size = 73 bytes
Total Internal Fragmentation =  22 bytes
Failed allocations (No memory) =  1
Failed allocations (External fragmentation) = 0


        > java MemoryManager sample.txt //PAGING

Created a memory manager with paging policy and 1024 bytes.
================================================================
Memory size = 1024, total pages = 32
Allocated pages = 23, free pages = 9
There are currently 2 active processes
Free page list: 30,31,0,1,2,3,4,5,6
Process list:
process id = 2, size = 458 bytes, number of pages = 14
	virt page 0 -> phys page 7 used: 32 bytes
	virt page 1 -> phys page 8 used: 32 bytes
	virt page 2 -> phys page 9 used: 32 bytes
	virt page 3 -> phys page 10 used: 32 bytes
	virt page 4 -> phys page 11 used: 32 bytes
	virt page 5 -> phys page 12 used: 32 bytes
	virt page 6 -> phys page 13 used: 32 bytes
	virt page 7 -> phys page 14 used: 32 bytes
	virt page 8 -> phys page 15 used: 32 bytes
	virt page 9 -> phys page 16 used: 32 bytes
	virt page 10 -> phys page 17 used: 32 bytes
	virt page 11 -> phys page 18 used: 32 bytes
	virt page 12 -> phys page 19 used: 32 bytes
	virt page 13 -> phys page 20 used: 10 bytes
process id = 3, size = 300 bytes, number of pages = 9
	virt page 0 -> phys page 21 used: 32 bytes
	virt page 1 -> phys page 22 used: 32 bytes
	virt page 2 -> phys page 23 used: 32 bytes
	virt page 3 -> phys page 24 used: 32 bytes
	virt page 4 -> phys page 25 used: 32 bytes
	virt page 5 -> phys page 26 used: 32 bytes
	virt page 6 -> phys page 27 used: 32 bytes
	virt page 7 -> phys page 28 used: 32 bytes
	virt page 8 -> phys page 29 used: 12 bytes
Total Internal Fragmentation =  22 bytes
Failed allocations (No memory) =  0
Failed allocations (External fragmentation) = 0


Error: Cannot allocate memory to process 5 due to insufficient memory
Memory size = 1024, total pages = 32
Allocated pages = 11, free pages = 21
There are currently 2 active processes
Free page list: 23,24,25,26,27,28,29,7,8,9,10,11,12,13,14,15,16,17,18,19,20
Process list:
process id = 4, size = 220 bytes, number of pages = 6
	virt page 0 -> phys page 30 used: 32 bytes
	virt page 1 -> phys page 31 used: 32 bytes
	virt page 2 -> phys page 0 used: 32 bytes
	virt page 3 -> phys page 1 used: 32 bytes
	virt page 4 -> phys page 2 used: 32 bytes
	virt page 5 -> phys page 3 used: 28 bytes
process id = 6, size = 170 bytes, number of pages = 5
	virt page 0 -> phys page 4 used: 32 bytes
	virt page 1 -> phys page 5 used: 32 bytes
	virt page 2 -> phys page 6 used: 32 bytes
	virt page 3 -> phys page 21 used: 32 bytes
	virt page 4 -> phys page 22 used: 10 bytes
Total Internal Fragmentation =  38 bytes
Failed allocations (No memory) =  1
Failed allocations (External fragmentation) = 0


Memory size = 1024, total pages = 32
Allocated pages = 25, free pages = 7
There are currently 4 active processes
Free page list: 23,24,4,5,6,21,22
Process list:
process id = 8, size = 345 bytes, number of pages = 10
	virt page 0 -> phys page 25 used: 32 bytes
	virt page 1 -> phys page 26 used: 32 bytes
	virt page 2 -> phys page 27 used: 32 bytes
	virt page 3 -> phys page 28 used: 32 bytes
	virt page 4 -> phys page 29 used: 32 bytes
	virt page 5 -> phys page 7 used: 32 bytes
	virt page 6 -> phys page 8 used: 32 bytes
	virt page 7 -> phys page 9 used: 32 bytes
	virt page 8 -> phys page 10 used: 32 bytes
	virt page 9 -> phys page 11 used: 25 bytes
process id = 9, size = 128 bytes, number of pages = 4
	virt page 0 -> phys page 12 used: 32 bytes
	virt page 1 -> phys page 13 used: 32 bytes
	virt page 2 -> phys page 14 used: 32 bytes
	virt page 3 -> phys page 15 used: 32 bytes
process id = 10, size = 128 bytes, number of pages = 4
	virt page 0 -> phys page 16 used: 32 bytes
	virt page 1 -> phys page 17 used: 32 bytes
	virt page 2 -> phys page 18 used: 32 bytes
	virt page 3 -> phys page 19 used: 32 bytes
process id = 11, size = 245 bytes, number of pages = 7
	virt page 0 -> phys page 20 used: 32 bytes
	virt page 1 -> phys page 30 used: 32 bytes
	virt page 2 -> phys page 31 used: 32 bytes
	virt page 3 -> phys page 0 used: 32 bytes
	virt page 4 -> phys page 1 used: 32 bytes
	virt page 5 -> phys page 2 used: 32 bytes
	virt page 6 -> phys page 3 used: 21 bytes
Total Internal Fragmentation =  46 bytes
Failed allocations (No memory) =  1
Failed allocations (External fragmentation) = 0