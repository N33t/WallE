package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.*;

import source.Goal;
import source.Position;
import source.Plan;
import source.TypeNum;


public class GameMap {
	
	private static GameMap singleton = null;

	public static boolean walls[][];
	public static char goals[][];
	public static char boxes[][];
	public static char agents[][];
	public static int agentsAmount;
	public ArrayList<Goal> unsolvedGoals;
	private static int MAX_ROW;
	private static int MAX_COLUMN;
	public static Map< Character, String > colors = new HashMap< Character, String >();
	
	//List that holds a HashMap over the positions of boxes and agents to time i (list index)
	public static ArrayList<Map<Position, Move>> timeController;
	public static ArrayList<ArrayList<Plan>> plans;
	
	public static JobManager jobManager = new JobManager();
	
	private GameMap() {
		this.agentsAmount = 0;
		this.unsolvedGoals = new ArrayList<Goal>();
		this.timeController = new ArrayList<Map<Position, Move>>();
		//plans = new ArrayList<Plan>();
	}
	
	//Adds plan to the timeController
	public static void addPlanToController(Plan plan)
	{
		plans.get(plan.id).add(plan);
		int time = plan.subplans.get(0).start;
		for(int i = 0; i < plan.subplans.size(); i++){
			for(int x = 0; x < plan.subplans.get(i).moves.size(); x++){
				
				if(time >= timeController.size())
					timeController.add(new HashMap<Position, Move>());
				
				Move move = plan.subplans.get(i).moves.get(x);
				System.err.println(move);
				/*
				* Adds moves to the HashMap
				* These moves represents cells occupied by the agents or boxes to the time
				*/
				if(move.type.type == TypeNum.NOP){
					timeController.get(time).put(move.type.l1, move);
				}
				else if(move.type.type == TypeNum.MOV){
					timeController.get(time).put(move.type.l1, move);
					timeController.get(time).put(move.type.l2, move);
				}
				else if(move.type.type == TypeNum.PUS || move.type.type == TypeNum.PUL){
					timeController.get(time).put(move.type.l1, move);
					timeController.get(time).put(move.type.l2, move);
					timeController.get(time).put(move.type.l3, move);
					timeController.get(time).put(move.type.l4, move);
				}
				
				time++;	
			}			
		}
		//evaluatePlans(plans);
	}
	
	public static void evaluatePlans(ArrayList<Plan> plans){
		//Set score for plans
		for(int i = 0; i < plans.size() -1; i++){
			if(plans.size() == 1){
				plans.get(i).setScore(1);
				break;
			}else{
				for(int j = 0; j < plans.size() -1; j++){
					if(j==i){
						continue;
					}//end if
					if(comparePlans(plans.get(i),plans.get(j)) > 0){
						//TODO check what kind of jobs i and j are
						//Collections.swap(plans, i, j);
						//updateTimeController();
					}//end if
				}//end for
			}//end else
		}//end for
	}//end method evaluatePlans
	
	public static int comparePlans(Plan a, Plan b){
		return a.subplans.size() - b.subplans.size();
	}//end method comparePlans
	
	public static void updateTimeController(){}//end method updateTimeController
	
	//Request position lookup
	public static boolean isPositionOccupiedToTime(Position p, int t){
		if(t > timeController.size())
			return false;
		Move m;
		try {
			m = timeController.get(t).get(p);
			//Object[] poses = timeController.get(t).keySet().toArray();
			//for (int i = 0; i < poses.length; i++) {
			//	if (poses[i].equals(p)) {
			//		return true;
			//	}
			//}
			//System.err.println(GameMap.timeController.get(0).keySet().toArray()[0].equals(new Position(8,1)));
		} catch (java.lang.IndexOutOfBoundsException e) {
			return false;
		}
		if(m == null)
			return false;
		return true;
	}
	
	//Returns time until cell is free
	//return -1 if the cell will never be free
	public static int cellFreeIn(int currentTime, Position pos){
	
		int time = 0;
		for(int t = currentTime; t < timeController.size(); t++){
			
			if(isPositionOccupiedToTime(pos, t))
				return time;
			
			time++;
		}
		return -1;
	}

	public static void printMasterPlan()
	{	
	
		for(int i = 0; i < agentsAmount; i++)
		{
			System.err.println("Agent " + i + " has # of plans " + plans.get(i).size());
		}
		boolean done = false;
		int count = 0;
		int time = 0;
		String cmd = "";
		int[] currentPlan = new int[agentsAmount];
		for(int i = 0; i < agentsAmount; i++)
		{
			currentPlan[i] = 0;
		}
		while(!done){
			count = 0;
			cmd = "[";
			
			for(int t = 0; t < agentsAmount; t++){
				if(plans.get(t).size() > currentPlan[t])
				{
					Move m = plans.get(t).get(currentPlan[t]).getMoveToTime(time);
					if(m != null)
						cmd += (m.toString());
					else {
						cmd += "NoOp";
						count++;
					}
					cmd += ",";					
				}
				else {
					cmd += "NoOp";
					cmd += ",";	
				}
			}
			
			cmd = removeLastChar(cmd);
			cmd += "]";
			if (cmd.length() == 1) {
				done = true;
			} else {
				System.err.println(cmd);
				System.out.println(cmd);
				System.out.flush();
			}
			if(count >= agentsAmount || time >= 50)
					done = true;
			time++;
			for(int t = 0; t < agentsAmount; t++)
			{
				if(plans.get(t).size() > currentPlan[t] && plans.get(t).get(currentPlan[t]).subplans.get(plans.get(t).get(currentPlan[t]).subplans.size()-1).stop < time)
				{
					System.err.println("Finished a plan");
					currentPlan[t]++;	
				}
			}
			/*
			*	TEST CODE
			*/ 
			if(time > 100000)
				System.err.println("Print master plan stuck in loop");
			/*
			*	TEST CODE
			*/ 
		}
	}
	
	public static String removeLastChar(String s) {
		if (s == null || s.length() == 0) {
			return s;
		}
		return s.substring(0, s.length()-1);
	}
	
	public static GameMap getInstance( ) {
		if(singleton == null) {
	         singleton = new GameMap();
	      }
	      return singleton;
   	}

	private static void error( String msg ) throws Exception {
		throw new Exception( "GSCError: " + msg );
	}

	public static int[] size() {
		return new int[] {MAX_COLUMN, MAX_ROW};
	}

	public static Goal getUnsolvedGoal() {
		Goal ret = new Goal(singleton.unsolvedGoals.get(0));
		return ret;
	}
	
	public static Boolean isCellFree(Position pos) throws Exception {
		if (pos.x == -1 || pos.y == -1) { 
			error("GameMap.isCellFree: input position was -1!"); 
		}
		return (boxes[pos.x][pos.y] == (char)0 && walls[pos.x][pos.y] == false);
	}
	
	public static char BoxAt(Position pos) {
		return (boxes[pos.x][pos.y]);
	}
	
	public static char GoalAt(Position pos) {
		return (goals[pos.x][pos.y]);
	}
	
	public static char AgentAt(Position pos) {
		return (agents[pos.x][pos.y]);
	}

	protected void read(BufferedReader serverMessages) throws Exception {
		String line, color;

		int /*colorLines = 0,*/ levelLines = 0;

		// Read lines specifying colors
		while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
			line = line.replaceAll( "\\s", "" );
			String[] colonSplit = line.split( ":" );
			color = colonSplit[0].trim();

			for ( String id : colonSplit[1].split( "," ) ) {
				colors.put( id.trim().charAt( 0 ), color );
			}
			//colorLines++;
		}
/*		
		if ( colorLines > 0 ) {
			error( "Box colors not supported" );
		}
*/
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(new String(line));
		String curLineStr = "empty";
		while(!curLineStr.equals( "" ))
		{
			curLineStr = serverMessages.readLine();
			lines.add(new String(curLineStr));
			levelLines++;
		}
		int cols = 0;
		for(int i = 0; i < lines.size(); i++)
		{
			if(lines.get(i).length() > cols) cols = lines.get(i).length();

		}
		int rows = lines.size();
		walls = new boolean[cols][rows];
		goals = new char[cols][rows];
		boxes = new char[cols][rows];
		agents = new char[cols][rows];
		for (int i = 0; i < cols; i++) {
			Arrays.fill(agents[i],(char) 0);
			Arrays.fill(boxes[i],(char) 0);
			Arrays.fill(goals[i],(char) 0);
		}
		MAX_ROW = rows;
		MAX_COLUMN = cols;


		int curLine = 0;
		line = lines.get(curLine);
		
		while ( !line.equals( "" ) ) {
			for ( int i = 0; i < line.length(); i++ ) {
				char chr = line.charAt( i );
				if ( '+' == chr ) { // Walls
					walls[i][curLine] = true;
				} else if ( '0' <= chr && chr <= '9' ) { // Agents
					agents[i][curLine] = chr;
					agentsAmount++;
				} else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
					boxes[i][curLine] = chr;
				} else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
					goals[i][curLine] = chr;
					JobManager.Job job = jobManager.new Job(0,'g', new Position(i, curLine), chr, colors.get(Character.toUpperCase(chr)));
					jobManager.addJobToQueue(job); //TODO fix priority and char?
				}
			}
			curLine++;
			line = lines.get(curLine);
		}
		plans = new ArrayList<ArrayList<Plan>>();
		for(int i = 0; i < agentsAmount; i++)
		{
			plans.add(new ArrayList<Plan>());
		}
	}
}

