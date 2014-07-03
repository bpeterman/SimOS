import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
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
		ram = fifo();
		// printRam using the FIFO LTS scheduler
		printRam("FIFO");
		printHDD();
		STS();
		printRam("Just Print");
		printRQ();
		loopExecuteSeq();

	}

	public static void loopExecuteSeq() {
		while (!ReadyQueue.isEmpty() || !WaitQueue.isEmpty()
				|| !IOqueue.isEmpty()) {
			executeCommand();
			checkWaitQueue();
			checkIOqueue();
		}
	}
	
	public static void printReg(Job job){
		System.out.print("Job Number: "+ job.jobNum + ": ");
		System.out.println(job.myCPU.toString());
	}

	public static void executeCommand() {
		if (!ReadyQueue.isEmpty()){
			Job job = ReadyQueue.get(0);
			String strCommand = job.getInstr().get(job.programCounter);
			String[] commandArr = strCommand.split(", ");
			cycleCount++;
			if (commandArr[1].equals("mul")) {
				job.myCPU.mul(commandArr[2].charAt(0), commandArr[3].charAt(0));
				job.programCounter++;
			} else if (commandArr[1].equals("sub")) {
				job.myCPU.sub(commandArr[2].charAt(0), commandArr[3].charAt(0));
				job.programCounter++;
			} else if (commandArr[1].equals("add")) {
				job.myCPU.add(commandArr[2].charAt(0), commandArr[3].charAt(0));
				job.programCounter++;
			} else if (commandArr[1].equals("div")) {
				job.myCPU.div(commandArr[2].charAt(0), commandArr[3].charAt(0));
				job.programCounter++;
			} else if (commandArr[1].equals("rcl")) {
				job.myCPU.rcl(commandArr[2].charAt(0));
				job.programCounter++;
			} else if (commandArr[1].equals("sto")) {
				job.myCPU.sto(commandArr[4].charAt(0));
				job.programCounter++;
			} else if (commandArr[1].equals("_rd") || commandArr[1].equals("_wr")) {
				job.programCounter++;
				job.setIOtime(Integer.parseInt(commandArr[4]) + cycleCount);
				moveToIOQ(job);
			} else if (commandArr[1].equals("_wt")) {
				job.programCounter++;
				job.setWaitTime(Integer.parseInt(commandArr[4]) + cycleCount);
				moveToWaitQ(job);
			}
			
			if (job.programCounter==job.getInstr().size() && !ReadyQueue.isEmpty()){
				printReg(job);
				ReadyQueue.remove(0);
			}
		} else {
			cycleCount++;
		}
	}

	public static void moveToWaitQ(Job job) {
		if (job.programCounter==job.getInstr().size() && !ReadyQueue.isEmpty()){
			
		}else{
		WaitQueue.add(job);
		ReadyQueue.remove(0);
		}
	}

	public static void moveToIOQ(Job job) {
		if (job.programCounter==job.getInstr().size() && !ReadyQueue.isEmpty()){
			
		}else{
		IOqueue.add(job);
		ReadyQueue.remove(0);
		}
	}

	public static void checkWaitQueue() {
		if (!WaitQueue.isEmpty()) {
			for (int i = 0; i < WaitQueue.size(); i++) {
				if (WaitQueue.get(i).waitTime == cycleCount) {
					WaitQueue.get(i).setWaitTime(0);
					ReadyQueue.add(WaitQueue.get(i));
					WaitQueue.remove(i);
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
			if ((job.getSize() + jobCount) <= 100) { // if the job less than or
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
			if ((job.getSize() + jobCount) <= 100) {
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
			if ((job.getSize() + jobCount) <= 100) {
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
		CPU myCPU = new CPU(1, 3, 5, 7, 9);
		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("ugradPart1.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Job")) {
					if (jobCount != 0) {
						Job myJob = new Job(jobNum, size, priority, jobs,
								myCPU, 0, 0, 0);
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
		Job myJob = new Job(jobNum, size, priority, jobs, myCPU, 0, 0, 0);
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

	// public static boolean checkInter() {
	//
	// Job testJob = null;
	// boolean ch1 = false;
	// boolean ch2 = false;
	// if (!IOqueue.isEmpty()) {
	// for (int j = 0; j < IOqueue.size(); j++) {
	// testJob = IOqueue.get(j);
	// if (cycleCount >= testJob.IOtime) {
	// testJob.setProcessState("ready");
	// testJob.setIOtime(0);
	// IOqueue.remove(j);
	// ReadyQueue.add(0, testJob);
	// ch1 = true;
	// }
	// }
	// }
	// if (!WaitQueue.isEmpty())
	// for (int j = 0; j < WaitQueue.size(); j++) {
	// testJob = WaitQueue.get(j);
	// if (cycleCount >= testJob.waitTime) {
	// testJob.setProcessState("ready");
	// testJob.setWaitTime(0);
	// WaitQueue.remove(j);
	// ReadyQueue.add(0, testJob);
	// ch2 = true;
	// }
	// }
	// if (ch1 == true || ch2 == true) {
	// return true;
	// }
	//
	// return false;
	// }
	//
	// public static void execCPUdriver() {
	// while (!ReadyQueue.isEmpty()) {
	// Job job = ReadyQueue.get(0);
	// if (job.getProcessState().equals("ready")) {
	// job.setProcessState("running");
	// if (!job.getInstr().isEmpty()) {
	// for (int i = job.getProgramCounter(); i < job.getInstr()
	// .size(); i++) {
	// // Section to determine if there is something ready to
	// // be moved
	// job.setProcessState("ready");
	// if (checkInter())
	// break;
	// job.setProcessState("running");
	//
	// // End section to see if there is something ready
	// String strCommand = job.getInstr().get(i);
	// String[] commandArr = strCommand.split(", ");
	// if (commandArr[1].equals("mul")) {
	// job.myCPU.mul(commandArr[2].charAt(0),
	// commandArr[3].charAt(0));
	// // Increase the program counter
	// job.programCounter++;
	// cycleCount++;
	// } else if (commandArr[1].equals("sub")) {
	// job.myCPU.sub(commandArr[2].charAt(0),
	// commandArr[3].charAt(0));
	// // Increase the program counter
	// job.programCounter++;
	// cycleCount++;
	// } else if (commandArr[1].equals("add")) {
	// job.myCPU.add(commandArr[2].charAt(0),
	// commandArr[3].charAt(0));
	// // Increase the program counter
	// job.programCounter++;
	// cycleCount++;
	// } else if (commandArr[1].equals("div")) {
	// job.myCPU.div(commandArr[2].charAt(0),
	// commandArr[3].charAt(0));
	// // Increase the program counter
	// job.programCounter++;
	// cycleCount++;
	// } else if (commandArr[1].equals("rcl")) {
	// job.myCPU.rcl(commandArr[2].charAt(0));
	// // Increase the program counter
	// job.programCounter++;
	// cycleCount++;
	// } else if (commandArr[1].equals("sto")) {
	// job.myCPU.sto(commandArr[4].charAt(0));
	// // Increase the program counter
	// job.programCounter++;
	// cycleCount++;
	// } else if (commandArr[1].equals("_rd")
	// || commandArr[1].equals("_wr")) {
	// job.programCounter++;
	// cycleCount++;
	// job.setIOtime(Integer.parseInt(commandArr[4])
	// + cycleCount);
	// job.setProcessState("IOwaiting");
	// IOqueue.add(job);
	// ReadyQueue.remove(job);
	// break;
	// } else if (commandArr[1].equals("_wt")) {
	// job.programCounter++;
	// cycleCount++;
	// job.setWaitTime(Integer.parseInt(commandArr[4])
	// + cycleCount);
	// job.setProcessState("waiting");
	// WaitQueue.add(job);
	// ReadyQueue.remove(job);
	// break;
	// }
	// }
	// if (job.programCounter == job.instr.size()) {
	// System.out.print(job.jobNum + ":  ");
	// System.out.println(job.myCPU.toString());
	// ReadyQueue.remove(job);
	// }
	// }
	// }
	// }
	// }
	//
	// public static void execCPU() {
	// while ((!ReadyQueue.isEmpty()) || (!WaitQueue.isEmpty())
	// || (!IOqueue.isEmpty())) {
	// execCPUdriver();
	// checkInter();
	// cycleCount++;
	// }
	// }
	//
}
