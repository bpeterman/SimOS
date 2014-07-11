
public class Core {
	
	boolean ready = true;
	Job theJob;

	public Core(boolean ready, Job theJob) {
			this.ready=ready;
			this.theJob = theJob;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public Job getTheJob() {
		return theJob;
	}

	public void setTheJob(Job theJob) {
		this.theJob = theJob;
	}

	
	
	
}
