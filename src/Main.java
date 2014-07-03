import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
	static List<Job> ram = new ArrayList<Job>();
	static List<Job> ReadyQueue = new ArrayList<Job>();
	static List<Job> IOqueue = new ArrayList<Job>();
	static List<Job> WaitQueue = new ArrayList<Job>();
	static List<Job> hdd = new ArrayList<Job>();
	
	public static void main(String[] args) {

		hdd = readAndWrite(); // write the contents of the file into the
								// List<Job> object

		ram = fifo();
		//printRam using the FIFO LTS scheduler
		printRam("FIFO");
		printHDD();
		STS();
		printRQ();
		
		

	}

	public static void STS(){
		for (int i = 0; i < ram.size(); i++) {
			Job job = ram.get(i);
			ReadyQueue.add(job);
		}
	}
	
	
	
	
	
	// Scheduler for the FIFO algorithm
	public static List<Job> fifo() {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		for (int i = 0; i < hdd.size(); i++) {
			Job job = hdd.get(i);
			if ((job.getSize() + jobCount) <= 100) { // if the job less than or
				temp.add(job); // equal to 100 add it
				jobCount += job.getSize();
				hdd.remove(job);
			
			}
		}
		return temp;
	}

	// Scheduler for the SJF algorithm
	public static List<Job> sjf() {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		Collections.sort(hdd, Job.sjf);
		for (int i = 0; i < hdd.size(); i++) {
			Job job = hdd.get(i);
			if ((job.getSize() + jobCount) <= 100) {
				temp.add(job);
				jobCount += job.getSize();
				hdd.remove(job);
			}
		}
		return temp;
	}

	// Scheduler for the priority algorithm
	public static List<Job> priority() {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		Collections.sort(hdd, Job.thePriority);
		for (int i = 0; i < hdd.size(); i++) {
			Job job = hdd.get(i);
			if ((job.getSize() + jobCount) <= 100) {
				temp.add(job);
				jobCount += job.getSize();
				hdd.remove(job);
			}
		}
		return temp;
	}

	// This reads all the data from the file and stores it in the hdd object.
	public static List<Job> readAndWrite() {
		int jobCount = 0;
		int jobNum = 0;
		int size = 0;
		int priority = 0;
		
		List<String> jobs = new ArrayList<String>();
	
		BufferedReader br = null;
	
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("ugradPart1.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Job")) {
					if (jobCount != 0) {
						Job myJob = new Job(jobNum, size, priority, jobs,
								"new", null, 0);
						hdd.add(myJob);
						myJob = null;
						jobs = new ArrayList<String>();
					}
					String[] parts = sCurrentLine.split(", ");
					jobNum = Integer.parseInt(parts[0].replace("Job ", ""));
					size = Integer.parseInt(parts[1]);
					priority = Integer.parseInt(parts[2]);
					jobCount++;
	
				} else {
					jobs.add(sCurrentLine);
				}
	
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		Job myJob = new Job(jobNum, size, priority, jobs, "new", null, 0);
		hdd.add(myJob);
		myJob = null;
		jobs = null;
		return hdd;
	
	}

	public static void printRam(String alg) {
		System.out.println("Algorithm: " + alg);
		System.out.println(Arrays.toString(ram.toArray()));
	}

	public static void printHDD() {
		System.out.println(Arrays.toString(hdd.toArray()));

	}
	
	public static void printRQ() {
		System.out.println(Arrays.toString(ReadyQueue.toArray()));

	}

}
