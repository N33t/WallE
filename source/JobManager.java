package source;

import java.util.PriotyQueue;

import source.Job;

public class JobManager {
	
	public static PriotyQueue<Job> jobs;
	
	public JobManager(){
		Comparator<Job> comparator = new JobPriotyComparator();
		Jobs =   = new PriotyQueue<Job>(10, comparator);
	}
	
	//Returns the highest priotized job
	public static Job getPriotyJob(){
		return jobs.poll();
	}
	
	//Adds a job to the job queue
	public static void addJobToQueue(Job j){
		jobs.add(j);
	}
	
	//Job class responsible for 
	class Job(){
		
		private int prioty = 0;
		private char jobType = 'n';
		
		private Position jobPos = null;
		private Position jobPos2 = null;
		
		public Job(int prioty, char jobType, Position p1){
			this.prioty = prioty;
			this.jobType = jobType;
			this.jobPos = p1;
		}
		
		public Job(int prioty, char jobType, Position p1, Position p2){
			this.prioty = prioty;
			this.jobType = jobType;
			this.jobPos = p1;
			this.jobPos2 = p2;
		}
		
		public int getPrioty(){
			return prioty;
		}
		
		public char getJobType(){
			return jobType;
		}
		
		public void setPrioty(int prioty){
			this.prioty = prioty;
		}
		
		public void setJobType(char jobType){
			this.jobType = jobType
		}
	}
	
	class JobPriotyComparator implements Comparator<Job>
	{
		@Override
		public int compare(Job a, Job b)
		{
			if (a.getPrioty() < b.getPrioty())
				return -1;
			if (a.getPrioty() > b.getPrioty())
				return 1;
			return 0;
		}
	}
	
}