package source;

import java.util.ArrayList;

import source.*;

public class PreCondition {
	ArrayList<JobManager.Job> jobs;
	int agentID;
	
	public PreCondition(ArrayList<JobManager.Job> jobs, int ID) {
		this.jobs = jobs;
		this.agentID = ID;
		jobs = new ArrayList<JobManager.Job>();
	}
	
	//public PreCondition(ArrayList<JobManager.Job> jobs, int ID) {
	//	this.jobs = jobs;
	//	this.agentID = ID;
	//	color = "";
	//	jobs = new ArrayList<JobManager.Job>();
	//}

}