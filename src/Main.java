import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
	static int numCPUs = 1;
	static int ramSize = 100;
	/*
	 * 
	 * Need to decrement waiting queues. Also change where the times are set
	 * Need to add multiple CPU support. Change the way the CPU executes to
	 * execute a job only if there is a wait on last instruction. Terminate
	 * queue
	 * 
	 * Maybe create some other CPU object to execute and store flags on state.
	 * 
	 * Load the Readyqueue with jobs on each CPU cycle, set limit on Readyqueue
	 * to the same as RAM Handle Readyqueue being full on transfer from waits
	 * 
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

	// add a terminate queue

	public static void main(String[] args) {

		hdd = readAndWrite(); // write the contents of the file into the
								// List<Job> object
		printHDD();
		while (!hdd.isEmpty()) {
			ram = fifo();
			// printRam using the FIFO LTS scheduler
			// printRam("FIFO");
			// printHDD();
			STS();
			// printRam("Just Print");
			// printRQ();
			loopExecuteSeq();
			System.out.println("Cycles: " + cycleCount);
		}
	}

	public static void loopExecuteSeq() {
		while (!ReadyQueue.isEmpty() || !WaitQueue.isEmpty()
				|| !IOqueue.isEmpty()) {
			executeCommand();
			checkWaitQueue();
			checkIOqueue();
		}
	}

	public static void printReg(Job job) {
		System.out.print("Job Number: " + job.jobNum + ": ");
		System.out.println(job.myCPU.toString());
	}

	public static void executeCommand(Job curJob) {

		if (curJob.programCounter != curJob.getInstr().size()) {
			String strCommand = curJob.getInstr().get(curJob.programCounter);
			String[] commandArr = strCommand.split(", ");
			if (commandArr[1].equals("mul")) {
				ReadyQueue.get(0).myCPU.mul(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
			} else if (commandArr[1].equals("sub")) {
				curJob.myCPU.sub(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
			} else if (commandArr[1].equals("add")) {
				curJob.myCPU.add(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
			} else if (commandArr[1].equals("div")) {
				curJob.myCPU.div(commandArr[2].charAt(0),
						commandArr[3].charAt(0));
				curJob.programCounter++;
			} else if (commandArr[1].equals("rcl")) {
				curJob.myCPU.rcl(commandArr[2].charAt(0));
				curJob.programCounter++;
			} else if (commandArr[1].equals("sto")) {
				curJob.myCPU.sto(Integer.parseInt(commandArr[4]));
				curJob.programCounter++;
			} else if (commandArr[1].equals("_rd")
					|| commandArr[1].equals("_wr")) {
				curJob.programCounter++;
				curJob.setIOtime(Integer.parseInt(commandArr[4])); // change to
																	// just the
																	// time.
				moveToIOQ(curJob);
			} else if (commandArr[1].equals("_wt")) {
				curJob.programCounter++;
				curJob.setWaitTime(Integer.parseInt(commandArr[4])); // change
																		// to
																		// just
																		// the
																		// time.
				moveToWaitQ(curJob);
			}
		} else {
			removeFromRQ();
		}

		cycleCount++;
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
			if ((job.getSize() + jobCount) <= ramSize) { // if the job less than
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
			if ((job.getSize() + jobCount) <= ramSize) {
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
			if ((job.getSize() + jobCount) <= ramSize) {
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

}
