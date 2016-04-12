package source;

import java.util.PriorityQueue;
import java.util.Comparator;

//import source.Job;

public class JobManager {
	
	public static PriorityQueue<Job> jobs;
	
	public JobManager(){
		Comparator<Job> comparator = new JobPriorityComparator();
		jobs = new PriorityQueue<Job>(10, comparator);
	}
	
	//Returns the highest priotized job
	public static Job getPriorityJob(){
		return jobs.poll();
	}
	
	//Adds a job to the job queue
	public static void addJobToQueue(Job j){
		jobs.add(j);
	}
	
	//Job class responsible for 
	class Job {
		
		private int Priority = 0;
		public enum jobType {
			MOVE_SELF_OUT_OF_THE_WAY, 
			MOVE_BOX_OUT_OF_THE_WAY,
			COMPLETE_TASK
		}
		
		public Position jobPos = null;
		public Position jobPos2 = null;
		
		public char goal = 0;
		
		public Job(int Priority, enum jobType, Position p1){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
		}
		
		public Job(int Priority, enum jobType, Position p1, char goal){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.goal = goal;
		}
		
		public Job(int Priority, enum jobType, Position p1, Position p2){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.jobPos2 = p2;
		}
		
		public int getPriority(){
			return Priority;
		}
		
		public char getJobType(){
			return jobType;
		}
		
		public void setPriority(int Priority){
			this.Priority = Priority;
		}
		
		public void setJobType(enum jobType){
			this.jobType = jobType;
		}
	}
	
	class JobPriorityComparator implements Comparator<Job>
	{
		@Override
		public int compare(Job a, Job b)
		{
			if (a.getPriority() < b.getPriority())
				return -1;
			if (a.getPriority() > b.getPriority())
				return 1;
			return 0;
		}
	}
	
}