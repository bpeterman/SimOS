import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
	static int numCPUs=1;
	static int ramSize=100;
	
	
	static int cycleCount = 0;
	static List<Job> ram = new ArrayList<Job>();
	static List<Job> ReadyQueue = new ArrayList<Job>();
	static List<Job> IOqueue = new ArrayList<Job>();
	static List<Job> WaitQueue = new ArrayList<Job>();
	static List<Job> hdd = new ArrayList<Job>();

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

	public static void executeCommand() {
		if (!ReadyQueue.isEmpty()) {
			if (ReadyQueue.get(0).programCounter != ReadyQueue.get(0)
					.getInstr().size()) {
				if (ReadyQueue.get(0).programCounter == 0) {
					CPU myCPU = new CPU(1, 3, 5, 7, 9);
					ReadyQueue.get(0).setMyCPU(myCPU);
				}
				String strCommand = ReadyQueue.get(0).getInstr()
						.get(ReadyQueue.get(0).programCounter);
				String[] commandArr = strCommand.split(", ");
				if (commandArr[1].equals("mul")) {
					ReadyQueue.get(0).myCPU.mul(commandArr[2].charAt(0),
							commandArr[3].charAt(0));
					ReadyQueue.get(0).programCounter++;
				} else if (commandArr[1].equals("sub")) {
					ReadyQueue.get(0).myCPU.sub(commandArr[2].charAt(0),
							commandArr[3].charAt(0));
					ReadyQueue.get(0).programCounter++;
				} else if (commandArr[1].equals("add")) {
					ReadyQueue.get(0).myCPU.add(commandArr[2].charAt(0),
							commandArr[3].charAt(0));
					ReadyQueue.get(0).programCounter++;
				} else if (commandArr[1].equals("div")) {
					ReadyQueue.get(0).myCPU.div(commandArr[2].charAt(0),
							commandArr[3].charAt(0));
					ReadyQueue.get(0).programCounter++;
				} else if (commandArr[1].equals("rcl")) {
					ReadyQueue.get(0).myCPU.rcl(commandArr[2].charAt(0));
					ReadyQueue.get(0).programCounter++;
				} else if (commandArr[1].equals("sto")) {
					ReadyQueue.get(0).myCPU
							.sto(Integer.parseInt(commandArr[4]));
					ReadyQueue.get(0).programCounter++;
				} else if (commandArr[1].equals("_rd")
						|| commandArr[1].equals("_wr")) {
					ReadyQueue.get(0).programCounter++;
					ReadyQueue.get(0).setIOtime(
							Integer.parseInt(commandArr[4]) + cycleCount);
					moveToIOQ(ReadyQueue.get(0));
				} else if (commandArr[1].equals("_wt")) {
					ReadyQueue.get(0).programCounter++;
					ReadyQueue.get(0).setWaitTime(
							Integer.parseInt(commandArr[4]) + cycleCount);
					moveToWaitQ(ReadyQueue.get(0));
				}
			} else {
				removeFromRQ();
			}
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
		if (!WaitQueue.isEmpty()) {
			for (int i = 0; i < WaitQueue.size(); i++) {
				if (WaitQueue.get(i).waitTime == cycleCount) {
					WaitQueue.get(i).setWaitTime(0);
					ReadyQueue.add(WaitQueue.get(i));
					WaitQueue.remove(i);
					checkWaitQueue();
				}
			}
		}
	}

	public static void checkIOqueue() {
		if (!IOqueue.isEmpty()) {
			for (int i = 0; i < IOqueue.size(); i++) {
				if (IOqueue.get(i).IOtime == cycleCount) {
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
			if ((job.getSize() + jobCount) <= ramSize) { // if the job less than or
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
