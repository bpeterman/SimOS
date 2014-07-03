import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class Job {
	int jobNum;
	int size;
	int priority;
	List<String> instr;
	CPU myCPU = new CPU(1, 3, 5, 7, 9);
	int programCounter;
	int waitTime;
	int IOtime;

	public Job(int jobNum, int size, int priority, List<String> instr,
			 CPU myCPU, int programCounter, int waitTime,
			int IOtime) {
		this.jobNum = jobNum;
		this.size = size;
		this.priority = priority;
		this.instr = instr;
		this.myCPU = myCPU;
		this.programCounter = programCounter;
		this.waitTime = waitTime;
		this.IOtime = IOtime;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public int getIOtime() {
		return IOtime;
	}

	public void setIOtime(int iOtime) {
		IOtime = iOtime;
	}


	public CPU getMyCPU() {
		return myCPU;
	}

	public void setMyCPU(CPU myCPU) {
		this.myCPU = myCPU;
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

	public int getProgramCounter() {
		return programCounter;
	}

	public void setProgramCounter(int programCounter) {
		this.programCounter = programCounter;
	}

	public static Comparator<Job> getThePriority() {
		return thePriority;
	}

	public static void setThePriority(Comparator<Job> thePriority) {
		Job.thePriority = thePriority;
	}

	public String toString() {
		return String.format("Job Number: " + jobNum + " Size: " + size
				+ " Priority: " + priority + " Intructions: "
				+ Arrays.toString(instr.toArray()) +"\n");
	}

	/* Comparator for sorting the list by size */
	public static Comparator<Job> sjf = new Comparator<Job>() {
		public int compare(Job j1, Job j2) {
			int no1 = j1.getSize();
			int no2 = j2.getSize();
			/* For ascending order */
			return no1 - no2;

			/* For descending order */
			// return no2-no1;
		}
	};

	/* Comparator for sorting the list by priority */
	public static Comparator<Job> thePriority = new Comparator<Job>() {
		public int compare(Job j1, Job j2) {
			int no1 = j1.getPriority();
			int no2 = j2.getPriority();
			/* For ascending order */
			return no1 - no2;

			/* For descending order */
			// return no2 - no1;
		}
	};

}
