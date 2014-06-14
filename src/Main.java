import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List<Job> hdd = new ArrayList<Job>();
		List<Job> ram = new ArrayList<Job>();
		hdd = readAndWrite(); // write the contents of the file into the
								// List<Job> object

		ram = fifo(hdd);
		printSpaces("FIFO");
		printRam(ram);
		ram = new ArrayList<Job>(); // clear the ram for the next algorithm

		ram = sjf(hdd);
		printSpaces("Shortest Job First");
		printRam(ram);
		ram = new ArrayList<Job>(); // clear the ram for the next algorithm

		ram = priority(hdd);
		printSpaces("Priority");
		printRam(ram);
		ram = new ArrayList<Job>(); // clear the ram for the next algorithm

	}

	public static void printRam(List<Job> ram) {
		System.out.println(Arrays.toString(ram.toArray()));
	}

	public static void printSpaces(String alg) {
		System.out.println("Algorithm: " + alg);
	}

	public static void printHDD(List<Job> hdd) {
		System.out.println(Arrays.toString(hdd.toArray()));

	}

	// Scheduler for the FIFO algorithm
	public static List<Job> fifo(List<Job> hdd) {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		for (Job job : hdd) {
			if ((job.getSize() + jobCount) <= 100) { // if the job less than or
				temp.add(job); // equal to 100 add it
				jobCount += job.getSize();
				job = null;
			}
		}
		return temp;
	}

	// Scheduler for the SJF algorithm
	public static List<Job> sjf(List<Job> hdd) {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		Collections.sort(hdd, Job.sjf);
		for (Job job : hdd) {
			if ((job.getSize() + jobCount) <= 100) {
				temp.add(job);
				jobCount += job.getSize();
				job = null;
			}
		}
		return temp;
	}

	// Scheduler for the priority algorithm
	public static List<Job> priority(List<Job> hdd) {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		Collections.sort(hdd, Job.thePriority);
		for (Job job : hdd) {
			if ((job.getSize() + jobCount) <= 100) {
				temp.add(job);
				jobCount += job.getSize();
				job = null;
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
		List<Job> hdd = new ArrayList<Job>();
		List<String> jobs = new ArrayList<String>();

		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("ugradPart1.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Job")) {
					if (jobCount != 0) {
						Job myJob = new Job(jobNum, size, priority, jobs);
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
		Job myJob = new Job(jobNum, size, priority, jobs);
		hdd.add(myJob);
		myJob = null;
		jobs = null;
		return hdd;

	}

}
