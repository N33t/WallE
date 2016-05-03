package source;

import java.util.ArrayList;

import source.*;

public class PreCondition {
	ArrayList<JobManager.Job> jobs = new ArrayList<JobManager.Job>();;
	int agentID;
	boolean isSolvable = true;
	
	public PreCondition(ArrayList<JobManager.Job> jobs, int ID) {
		this.jobs = jobs;
		this.agentID = ID;
		//jobs = new ArrayList<JobManager.Job>();
	}
	public PreCondition(boolean solvable, int ID) {
		this.agentID = ID;
		this.isSolvable = solvable;
		//jobs = new ArrayList<JobManager.Job>();
	}
	//public PreCondition(ArrayList<JobManager.Job> jobs, int ID) {
	//	this.jobs = jobs;
	//	this.agentID = ID;
	//	color = "";
	//	jobs = new ArrayList<JobManager.Job>();
	//}

}