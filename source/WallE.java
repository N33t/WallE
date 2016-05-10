package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import source.GameMap;
import source.Agent;
import source.Position;
import source.Storage;


public class WallE {

	public static void error( String msg ) throws Exception {
		throw new Exception( "GSCError: " + msg );
	}

	public static void main( String[] args ) throws Exception {
		BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );
		// Use stderr to print to console
		//system.err.println( "WallE initializing." );

		//Create and read the map
		GameMap theMap = GameMap.getInstance();
		theMap.read(serverMessages);
	
		GameMap.storage = new Storage(theMap.walls, theMap.boxes);
		GameMap.storage.printMap(0);
		
		//Create agents
		ArrayList<Agent> agents = new ArrayList<Agent>();

		for (int i = 0; i < theMap.agentsAmount; i++) {
			//Find position of agent with id
			for (int x = 0; x < theMap.size()[0]; x++) {
				for (int y = 0; y < theMap.size()[1]; y++) {
					if (theMap.agents[x][y] == i + '0') {
						agents.add(new Agent(i, new Position(x,y))); //GameMap.colors.get((char)(i + '0'))
					}
				}
			}
		}
		//system.err.println("agentsAmount " + theMap.agentsAmount + " agents.");
		//system.err.println("We have " + agents.size() + " agents.");
		//While loop to distribute jobs to agents
		int nextJob = 0;
		int lastAgentToSolveAJob = 0;
		int max = 0;
		while((!GameMap.jobManager.goalsFulfilled() || GameMap.jobManager.jobs.size() == 0) && max < 5){
			//max++;
			for(int i = 0; i < agents.size(); i++) {
				JobManager.Job job = GameMap.jobManager.getPriorityJob(i);
				if(job != null){
					System.err.println("ID: " + i + " Goal: " + job.goal + " Type:" + job.jobType);
					Plan agentPlan = agents.get(i).createPlan(job);
					if (!agentPlan.subplans.isEmpty()){
						GameMap.addPlanToController(agentPlan);
						
						continue;
					}else{
						//System.err.println("isEmpty");
					}
				} else {
					//System.err.println("Null job");
				}
			}
		}
		
		//System.err.println("Done, jobs= " + GameMap.jobManager.jobs.size() + ", goals?" + GameMap.jobManager.goalsFulfilled());
		
		//for (int i = 0; i < agents.size(); i++) {
		//	Plan agentPlan = agents.get(i).createPlan(GameMap.jobManager.getPriorityJobOLD(i));
		//	if (!agentPlan.subplans.isEmpty()) {
		//		GameMap.addPlanToController(agentPlan);
		//	}
		//}
		
		//for (int i = 0; i < agents.size(); i++) {
		//	Plan agentPlan = agents.get(i).createPlan(GameMap.jobManager.getPriorityJobOLD(i));
		//	if (!agentPlan.subplans.isEmpty()) {
		//		GameMap.addPlanToController(agentPlan);
		//	}
		//}

		GameMap.printMasterPlan();
		//System.out.println( "[Move(E)]" );
		//System.out.flush();
		//System.out.println( "[Pull(W,E)]" );
		//System.out.flush();
		String response = serverMessages.readLine();
	}
}
