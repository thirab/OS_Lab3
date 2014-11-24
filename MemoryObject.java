
public class MemoryObject {

	int start;
	int end;
	int memoryUsed;
	int pid;
	MemoryObject previous = null;
	MemoryObject following = null;
	public MemoryObject(int s, int e, int mem, int id){
		start= s;
		end= e;
		memoryUsed= mem;
		pid=id;
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
}
