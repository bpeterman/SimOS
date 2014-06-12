import java.util.Arrays;
import java.util.List;


public class Job {
	int jobNum;
	int size;
	int priority;
	List<String> instr;
	
	public Job(int jobNum, int size, int priority, List<String> instr){
			this.jobNum=jobNum;
			this.size=size;
			this.priority=priority;
			this.instr=instr;		
	}

	public int getJobNum() {
		return jobNum;
	}

	public void setJobNum(int jobNum) {
		this.jobNum = jobNum;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public List<String> getInstr() {
		return instr;
	}

	public void setInstr(List<String> instr) {
		this.instr = instr;
	}
	public String toString() {
        return String.format("Job Number: "+jobNum+" Size: "+size+" Priority: "+priority+" Intructions: "+Arrays.toString(instr.toArray()));
    }

}
