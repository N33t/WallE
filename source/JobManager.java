package source;

import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.ArrayList;

//import source.Job;

public class JobManager {
	
	//public static PriorityQueue<Job> jobs;
	public static ArrayList<Job> jobs;
	
	public JobManager(){
		Comparator<Job> comparator = new JobPriorityComparator();
		//jobs = new PriorityQueue<Job>(10, comparator);
		jobs = new ArrayList<Job>();
	}
	
	public boolean goalsFulfilled(){
		
		for(int i = 0; i < jobs.size(); i++){
			if(jobs.get(i).jobType.eqauls('g')){
				if(jobs.get(i).solved){	
				}else{
					return false,
				}
			}
		}
		return true;
		
	}
	
	private static boolean agentFulfillsPreConditions(Job job, int agentID) {
		//Correct color
		//System.err.println("jobColor = " + job.color + "Map color = " + GameMap.colors.get((char)(agentID + '0')));
		boolean color = job.color == GameMap.colors.get((char)(agentID + '0'));
		for (int i = 0; i < job.preConds.size(); i++) {
			PreCondition preC = job.preConds.get(i);
			//Jobs
			for (int j = 0; j < preC.jobs.size(); j++) {
				if (preC.agentID == agentID) {
					System.err.println("Going to for loop");
					for (int k = 0; k < preC.jobs.size(); k++) {
						if (!preC.jobs.get(i).solved) {
							System.err.println("Returning False");
							return false;
						}
					}
					System.err.println("Ended for loop");
				}
			}
		}
		return color;
	}
	
	private static Job preCondJob(Job job, int agentID) {
		//System.err.println(agentID + " preconds? " + agentFulfillsPreConditions(job, agentID));
		//System.err.println(job);
		if (!job.solved && agentFulfillsPreConditions(job, agentID)) {
			return job;
		}
		for (int i = 0; i < job.preConds.size(); i++) {
			Job returnedJob;
			PreCondition preC = job.preConds.get(i);
			//System.err.println("size = " + preC.jobs.size());
			for (int j = 0; j < preC.jobs.size(); j++) {
				returnedJob = preCondJob(preC.jobs.get(j), agentID);
				//System.err.println("ReturnedJob = " + returnedJob);
				if (returnedJob != null) return returnedJob;
			}
		}
		//Agent cannot complete any procondition-job.
		return null;
	}
	
	//Returns job to agent
	public static Job getPriorityJob(int agentID){
		//System.err.println("We have " + jobs.size() + " jobs.");
		//return jobs.poll();
		Job jobGet;
		for (int i = 0; i < jobs.size(); i++) {
			jobGet = preCondJob(jobs.get(i), agentID);
			if (jobGet != null) {
				return jobGet;
			}
		}
		return null;
	}
	
	//Adds a job to the job queue
	public static void addJobToQueue(Job j){
		jobs.add(j);
	}
	
	//Job class responsible for 
	class Job {
		
		private int Priority = 0;
		
		//Enum instead?
		//enum prioty = {LOW_PRIORITY = 1, MID_PRIORITY = 2, HIGH_PRIORITY = 3};
		public static final int LOW_PRIORITY = 1;
		public static final int MID_PRIORITY = 2;
		public static final int HIGH_PRIORITY = 3;
		public char jobType;
		public String color;
		/*	
		public enum jobType {
			MOVE_SELF_OUT_OF_THE_WAY, 
			MOVE_BOX_OUT_OF_THE_WAY,
			COMPLETE_TASK
		};
		*/
		public Position jobPos = null;
		public Position jobPos2 = null;
		ArrayList<Position> path = new ArrayList<Position>(); // Used for box-move jobs. If such a job is submitted, the agent picking up the job must know what path to move the box away from.
		
		public char goal = 0;
		
		public ArrayList<PreCondition> preConds;
		
		public boolean solved = false;
		
		public Job(int Priority, char jobType, Position p1, String color, ArrayList<Position> path){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			preConds = new ArrayList<PreCondition>();
			this.color = color;
			this.path = path;
		}
		
		public Job(int Priority, char jobType, Position p1, ArrayList<PreCondition> preConds, String color){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.preConds = preConds;
			this.color = color;
		}
		
		public Job(int Priority, char jobType, Position p1, char goal, String color, ArrayList<PreCondition> preConds){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.goal = goal;
			this.color = color;
			this.preConds = preConds;
		}
		
		public Job(int Priority, char jobType, Position p1, ArrayList<PreCondition> preConds){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.preConds = preConds;
		}
		
		//public Job(int Priority, char jobType, Position p1, char goal, ArrayList<PreCondition> preConds){
		//	this.Priority = Priority;
		//	this.jobType = jobType;
		//	this.jobPos = p1;
		//	this.goal = goal;
		//	this.preConds = preConds;
		//}
		
		public Job(int Priority, char jobType, Position p1, char goal, String color){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.goal = goal;
			this.color = color;
			preConds = new ArrayList<PreCondition>();
		}
		
		public Job(int Priority, char jobType, Position p1, Position p2){
			this.Priority = Priority;
			this.jobType = jobType;
			this.jobPos = p1;
			this.jobPos2 = p2;
			preConds = new ArrayList<PreCondition>();
		}
		
		public int getPriority(){
			return Priority;
		}
		
		public char getJobType(){
			return this.jobType;
		}
		
		public void setPriority(int Priority){
			this.Priority = Priority;
		}
		
		public void setJobType(char jobType){
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