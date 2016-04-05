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


public class WallE {

	public static void error( String msg ) throws Exception {
		throw new Exception( "GSCError: " + msg );
	}

	public static void main( String[] args ) throws Exception {
		BufferedReader serverMessages = new BufferedReader( new InputStreamReader( System.in ) );
		// Use stderr to print to console
		System.err.println( "WallE initializing." );

		//Create and read the map
		GameMap theMap = GameMap.getInstance();
		theMap.read(serverMessages);

		//Create agents
		ArrayList<Agent> agents = new ArrayList<Agent>();

		for (int i = 0; i < theMap.agentsAmount; i++) {
			//Find position of agent with id
			for (int x = 0; x < theMap.size()[0]; x++) {
				for (int y = 0; y < theMap.size()[1]; y++) {
					if (theMap.agents[x][y] == i + '0') {
						agents.add(new Agent(i,new Position(x,y)));
					}
				}
			}
		}
		System.err.println("agentsAmount " + theMap.agentsAmount + " agents.");
		System.err.println("We have " + agents.size() + " agents.");
		//Get plans
		//ArrayList<Plan> Plans = new ArrayList<Plan>();
		for (int i = 0; i < agents.size(); i++) {
			//new Thread(agents.get(i)).start();
			//Plans.add(agents.get(i).createPlan());
			GameMap.addPlanToController(agents.get(i).createPlan());
		}

		System.out.println( "[Move(E)]" );
		System.out.flush();
		System.out.println( "[Move(E)]" );
		String response = serverMessages.readLine();
	}
}
