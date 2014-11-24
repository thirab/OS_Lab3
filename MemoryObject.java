
public class MemoryObject {
	int pid;
	int start;
	int end;
	int memoryUsed;
	
	MemoryObject previous = null;
	MemoryObject following = null;
	public MemoryObject(int s, int e, int mem, int id){
		start= s;
		end= e;
		memoryUsed= mem;
		pid = id;
	}
	public void setStart(int i){
		start = i;
	}
	public void setMem(int mem){
		memoryUsed = mem;
	}
	public void setPID(int p){
		pid=p;
	}
	public int getMem(){
		return memoryUsed;
	}
	public void addMem(int i){
		memoryUsed += i;
	}
	public void setEnd(int i){
		end = i;
	}
	public int getStart(){
		return start;
	}
	public int getEnd(){
		return end;
	}
	public void resetId(){
		pid=-99;
	}
	public void setPrevious(MemoryObject p){
		previous = p;
	}
	public void setFollowing(MemoryObject f){
		following = f;
	}
	public MemoryObject getPrevious(){
		return previous;
	}
	public MemoryObject getFollowing(){
		return following;
	}
	public int getID(){
		return pid;
	}
	
	public void removeNodeFromList(){
		previous.setFollowing(following);
		following.setPrevious(previous);
	}
}
