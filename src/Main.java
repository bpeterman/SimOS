import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		List<String> test = new ArrayList<String>();
		List<String> test1 = new ArrayList<String>();
		test.add("This is a test");
		test.add("One more test");
		test1.add("This is a test");
		test1.add("One more test");
		List<Job> hdd = new ArrayList<Job>();
		List<Job> ram = new ArrayList<Job>();

		
		hdd=readAndWrite();
		ram=fifo(hdd);
		printRam(ram);
		printSpaces("FIFO");
		printRam(ram);

	}

	public static void printRam(List<Job> ram) {
		System.out.println(Arrays.toString(ram.toArray()));
	}
	
	public static void printSpaces(String alg) {
		System.out.println("\nAlgorithm: "+alg);
	}

	public static void printHDD(List<Job> hdd) {
		System.out.println(Arrays.toString(hdd.toArray()));

	}

	public static List<Job> fifo(List<Job> hdd) {
		return hdd;
	}

	public static List<Job> sjf(List<Job> hdd) {
		return hdd;
	}

	public static List<Job> priority(List<Job> hdd) {
		return hdd;
	}

	public static List<Job> readAndWrite() {
		int jobCount=0;
		int jobNum=0;
		int size=0;
		int priority=0;
		List<Job> hdd = new ArrayList<Job>();
		List<String> jobs = new ArrayList<String>();
		
		BufferedReader br = null;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("ugradPart1.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				if (sCurrentLine.contains("Job")){
					if(jobCount!=0){					
						Job myJob = new Job(jobNum, size, priority, jobs);
						hdd.add(myJob);
						myJob=null;
						jobs = new ArrayList<String>();
					}
					String[] parts = sCurrentLine.split(", ");
					jobNum = Integer.parseInt(parts[0].replace("Job ",""));
					size=Integer.parseInt(parts[1]);
					priority=Integer.parseInt(parts[2]);
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
		myJob=null;
		jobs=null;
		return hdd;

	}

}
