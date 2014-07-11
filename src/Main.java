import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
	static int numCPUs = 1;
	static int ramLimit = 100;
	/*
	 *  
	 * Need to add multiple CPU support. Change the way the CPU executes to
	 * execute a job only if there is a wait on last instruction. 
	 * 
	 * Load the Readyqueue with jobs on each CPU cycle, set limit on Readyqueue
	 * to the same as RAM 
	 * 
	 * Handle Readyqueue being full on transfer from waits
	 * 
	 * Print the terminate queue out with printReg(Job) at the end of program
	 * 
	 * Add all of the statistics
	 * 
	 * Make YouTube Video
	 */

	static int cycleCount = 0;
	static List<Job> ram = new ArrayList<Job>();
	static List<Job> ReadyQueue = new ArrayList<Job>();
	static List<Job> IOqueue = new ArrayList<Job>();
	static List<Job> WaitQueue = new ArrayList<Job>();
	static List<Job> hdd = new ArrayList<Job>();
	static List<Job> terminate = new ArrayList<Job>();
	static List<Core> cores = new ArrayList<Core>();

	public static void main(String[] args) {

		initCores(); // initializing the cores in the system
		
		hdd = readAndWrite(); // Read all the jobs from the file to the HD
								// object.

		while (!hdd.isEmpty() || !ReadyQueue.isEmpty() || !WaitQueue.isEmpty()
				|| !ram.isEmpty()) {
			ram = fifo();
			STS();  // Takes jobs from RAM and put them in the RQ
			sendToCores(); 
			checkWaitQueue();  
			checkIOqueue();
			cycleCount++;
		}
	}
	
	/* Sends a job to a core if it is ready for a new job.
	 * Otherwise it sends it the job it was working on last 
	 */
	public static void sendToCores() {
		for (int i = 0; i < cores.size(); i++) {
			if(cores.get(i).isReady()){
				cores.add(i, executeCommand(cores.get(i), ReadyQueue.get(0)));
			} else {
				cores.add(i, executeCommand(cores.get(i), cores.get(i).theJob));
			}
		}
	}

	public static Core executeCommand(Core theCore, Job curJob) {
		if (!theCore.isReady()){
			theCore.setReady(false);
		}
		if (curJob.programCounter != curJob.getInstr().size()) {
			String strCommand = curJob.getInstr().get(curJob.programCounter);
			String[] commandArr = strCommand.split(", ");
			if (commandArr[1].equals("mul")) {
				curJob.myCPU.mul(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
				theCore.setTheJob(curJob);
			} else if (commandArr[1].equals("sub")) {
				curJob.myCPU.sub(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
				theCore.setTheJob(curJob);
			} else if (commandArr[1].equals("add")) {
				curJob.myCPU.add(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
				theCore.setTheJob(curJob);
			} else if (commandArr[1].equals("div")) {
				curJob.myCPU.div(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
				theCore.setTheJob(curJob);
			} else if (commandArr[1].equals("rcl")) {
				curJob.myCPU.rcl(commandArr[2].charAt(0));
				curJob.programCounter++;
				theCore.setTheJob(curJob);
			} else if (commandArr[1].equals("sto")) {
				curJob.myCPU.sto(Integer.parseInt(commandArr[4]));
				curJob.programCounter++;
				theCore.setTheJob(curJob);
			} else if (commandArr[1].equals("_rd")
					|| commandArr[1].equals("_wr")) {
				curJob.programCounter++;
				curJob.setIOtime(Integer.parseInt(commandArr[4]));
				moveToIOQ(curJob);
				theCore.setReady(true);
				theCore.setTheJob(null);
			} else if (commandArr[1].equals("_wt")) {
				curJob.programCounter++;
				curJob.setWaitTime(Integer.parseInt(commandArr[4]));
				moveToWaitQ(curJob);
				theCore.setReady(true);
				theCore.setTheJob(null);
			}
		} else {
			terminate.add(curJob);
		}
		return theCore;
	}

	public static void removeFromRQ() {
		printReg(ReadyQueue.get(0));
		ReadyQueue.remove(0);
	}

	public static void moveToWaitQ(Job job) {
		WaitQueue.add(job);
		ReadyQueue.remove(0);
	}

	public static void moveToIOQ(Job job) {
		IOqueue.add(job);
		ReadyQueue.remove(0);
	}

	public static void checkWaitQueue() {
		// insert decrement of all jobs here.
		
		for (int i = 0; i<WaitQueue.size(); i++){
			WaitQueue.get(i).decWaitTime();
		}

		if (!WaitQueue.isEmpty()) {
			for (int i = 0; i < WaitQueue.size(); i++) {
				if (WaitQueue.get(i).waitTime == cycleCount) { // be sure to
																// change this
																// to 0 not
																// cycle count.
					WaitQueue.get(i).setWaitTime(0);
					ReadyQueue.add(WaitQueue.get(i));
					WaitQueue.remove(i);
					checkWaitQueue();
				}
			}
		}
	}

	public static void checkIOqueue() {
		// insert decrement of all jobs here.
		for (int i = 0; i<IOqueue.size(); i++){
			IOqueue.get(i).decIOTime();
		}
		if (!IOqueue.isEmpty()) {
			for (int i = 0; i < IOqueue.size(); i++) {
				if (IOqueue.get(i).IOtime == cycleCount) { // be sure to change
															// this to 0 not
															// cycle count.
					IOqueue.get(i).setIOtime(0);
					ReadyQueue.add(IOqueue.get(i));
					IOqueue.remove(i);
					checkIOqueue();
				}
			}
		}
	}

	public static void STS() {
		for (int i = 0; i < ram.size(); i++) {
			Job job = ram.get(i);
			ReadyQueue.add(job);
			ram.remove(i);
			i--;
		}
	}

	// Scheduler for the FIFO algorithm
	public static List<Job> fifo() {
		int jobCount = 0;
		List<Job> temp = new ArrayList<Job>();
		for (int i = 0; i < hdd.size(); i++) {
			Job job = hdd.get(i);
			if ((job.getSize() + jobCount) <= ramLimit) { // if the job less
															// than
															// or
				temp.add(job); // equal to 100 add it
				jobCount += job.getSize();
				hdd.remove(job);
				i--;

			} else
				break;
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
			if ((job.getSize() + jobCount) <= ramLimit) {
				temp.add(job);
				jobCount += job.getSize();
				hdd.remove(job);
				i--;
			} else
				break;
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
			if ((job.getSize() + jobCount) <= ramLimit) {
				temp.add(job);
				jobCount += job.getSize();
				hdd.remove(job);
				i--;
			} else
				break;
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
						Job myJob = new Job(jobNum, size, priority, jobs, null,
								0, 0, 0);
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
		Job myJob = new Job(jobNum, size, priority, jobs, null, 0, 0, 0);
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
		System.out.println("Print the HDD");
		System.out.println(Arrays.toString(hdd.toArray()));

	}

	public static void printRQ() {
		System.out.println("Print the RQ");
		System.out.println(Arrays.toString(ReadyQueue.toArray()));

	}

	public static int getRamSize() {
		int size = 0;
		for (int i = 0; i < ram.size(); i++) {
			size += ram.get(i).size;
		}

		return size;
	}

	public static void printReg(Job job) {
		System.out.print("Job Number: " + job.jobNum + ": ");
		System.out.println(job.myCPU.toString());
	}
	
	public static void initCores(){
		for (int i = 0; i<ramLimit; i++){
			cores.add(null);
		}
	}

}
