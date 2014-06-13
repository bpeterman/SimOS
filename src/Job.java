import java.util.Arrays;
import java.util.List;
import java.util.Comparator;

public class Job {
	int jobNum;
	int size;
	int priority;
	List<String> instr;

	public Job(int jobNum, int size, int priority, List<String> instr) {
		this.jobNum = jobNum;
		this.size = size;
		this.priority = priority;
		this.instr = instr;
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
		return String.format("Job Number: " + jobNum + " Size: " + size
				+ " Priority: " + priority + " Intructions: "
				+ Arrays.toString(instr.toArray()));
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
			//return no1 - no2;

			/* For descending order */
			return no2-no1;
		}
	};

}
