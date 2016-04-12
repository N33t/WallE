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
	
	//List that holds a HashMap over the positions of boxes and agents to time i (list index)
	private static ArrayList<Map<Position, Move>> timeController;
	private static ArrayList<Plan> plans;
	
	public static JobManager jobManager = new JobManager();
	
	private GameMap() {
		this.agentsAmount = 0;
		this.unsolvedGoals = new ArrayList<Goal>();
		this.timeController = new ArrayList<Map<Position, Move>>();
		plans = new ArrayList<Plan>();
	}
	
	//Adds plan to the timeController
	public static void addPlanToController(Plan plan)
	{
		plans.add(plan);
		int time = 0;
		for(int i = 0; i < plan.subplans.size(); i++){
			for(int x = 0; x < plan.subplans.get(i).moves.size(); x++){
				
				if(time >= timeController.size())
					timeController.add(new HashMap<Position, Move>());
				
				Move move = plan.subplans.get(i).moves.get(x);
				
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
		evaluatePlans(plans);
	}
	
	
	public static void evaluatePlans(ArrayList<Plan> plans){
		//Set score for plans
		for(Plan p : plans){
			if(plans.size() == 1){
				p.setScore = 1;
				break;
			}
		}
	}
	
	//Request position lookup
	public static boolean isPositionOccupiedToTime(Position p, int t){
		if(t > timeController.size())
			return true;
		Move m;
		try {
			m = timeController.get(t).get(p);
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
		boolean done = false;
		int count = 0;
		int time = 0;
		String cmd = "";
		
		while(!done){
			count = 0;
			cmd = "[";
			
			for(int t = 0; t < plans.size(); t++){
				
				Move m = plans.get(t).getMoveToTime(time);
				if(m != null)
					cmd += (m.toString());
				else {
					cmd += "NoOp";
					count++;
				}
				cmd += ",";
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
			if(count >= plans.size())
					done = true;
			time++;
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
		Map< Character, String > colors = new HashMap< Character, String >();
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

		int cols = lines.get(0).length();
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
					jobManager.addJobToQueue(jobManager.new Job(0,COMPLETE_TASK, new Position(i, curLine), chr)); //TODO fix priority and char?
					//unsolvedGoals.add(new Goal(chr, new Position(i, curLine)));
				}
			}
			curLine++;
			line = lines.get(curLine);
		}
	}
}

