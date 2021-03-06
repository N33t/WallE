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
import source.Storage;

public class GameMap {
	
	private static GameMap singleton = null;

	public static boolean walls[][];
	public static char goals[][];
	public static char boxes[][];
	public static char agents[][];
	public static int agentsAmount;
	public ArrayList<Goal> unsolvedGoals;
	public static int MAX_ROW;
	public static int MAX_COLUMN;
	public static Map< Character, String > colors = new HashMap< Character, String >();
	
	public static ArrayList<ArrayList<Position>> boxPositionsFrom = new ArrayList<ArrayList<Position>>();
	public static ArrayList<ArrayList<Position>> boxPositionsTo = new ArrayList<ArrayList<Position>>();
	public static ArrayList<ArrayList<Position>> agentPositionsFrom = new ArrayList<ArrayList<Position>>();
	public static ArrayList<ArrayList<Position>> agentPositionsTo = new ArrayList<ArrayList<Position>>();
	public static ArrayList<Character> boxCharacters = new ArrayList<Character>();
	public static ArrayList<Character> agentCharacters = new ArrayList<Character>();
	
	//public static Map< int , Map<Position, char>>
	
	//List that holds a HashMap over the positions of boxes and agents to time i (list index)
	public static ArrayList<Map<Position, Move>> timeController;
	public static ArrayList<ArrayList<Plan>> plans;
	
	public static JobManager jobManager = new JobManager();
	public static Storage storage;
	
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
				//////System.err.err.println(move);
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
				for (int j = 0; j < boxPositionsTo.size(); j++) {
					//////System.err.err.println("Move poses=" + move.type.l3 + " and " + move.type.l4 + ". BoxPos = " + boxPositionsTo.get(j).get(boxPositionsTo.get(j).size()-1));
					
					//Find box being moved
					if (move.type.l3 != null && move.type.l4 != null && boxPositionsTo.get(j).get(boxPositionsTo.get(j).size()-1).equals(move.type.l3)) {
					
						//Compare list length to time
						int timeDifference = move.time+1 - boxPositionsTo.get(j).size();
						//Add "NoOps" to make list length correct
						if (timeDifference > 0) {
							for (int k = 0; k < timeDifference; k++) {
								boxPositionsTo.get(j).add(boxPositionsTo.get(j).get(boxPositionsTo.get(j).size()-1));
								boxPositionsFrom.get(j).add(boxPositionsTo.get(j).get(boxPositionsTo.get(j).size()-1));
							}
						}
						boxPositionsFrom.get(j).add(move.type.l3);
						boxPositionsTo.get(j).add(move.type.l4);
							
						//////System.err.err.println("Box moved from" + move.type.l3 + ", Last = " + boxPositionsFrom.get(j).get(boxPositionsFrom.get(j).size()-1));
						//////System.err.err.println("Box moved to" + move.type.l4 + ", Last = " + boxPositionsTo.get(j).get(boxPositionsTo.get(j).size()-1));
						break;
					} 
				}

				if (move.type.type == TypeNum.NOP) {
					//////System.err.err.println("Noop");
					agentPositionsFrom.get(plan.id).add(move.type.l1);
					agentPositionsTo.get(plan.id).add(move.type.l1);
				} else {
					//Add new move to list
					agentPositionsFrom.get(plan.id).add(move.type.l1);
					agentPositionsTo.get(plan.id).add(move.type.l2);
				}
				
				//////System.err.err.println("boxFrom:" + boxPositionsFrom + "\nboxTo" + boxPositionsTo);
				
				time++;	
			}			
		}
		//for (int k = 0; k < boxPositionsTo.get(1).size(); k++) {
		//	////System.err.err.println("box BT. time=" + k + ", " + boxPositionsTo.get(1).get(k));
		//	////System.err.err.println("box BF. time=" + k + ", " + boxPositionsFrom.get(1).get(k));
		//}
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
	public static boolean isPositionOccupiedToTime(Position p, int time){
		//boolean initialBlocked = (boxes[p.x][p.y] != (char)0 || walls[p.x][p.y]);
		//if(t > timeController.size())
		//	return initialBlocked;
		//Move m;
		//try {
		//	m = timeController.get(t).get(p);
		//	//Object[] poses = timeController.get(t).keySet().toArray();
		//	//for (int i = 0; i < poses.length; i++) {
		//	//	if (poses[i].equals(p)) {
		//	//		return true;
		//	//	}
		//	//}
		//	//////System.err.err.println(GameMap.timeController.get(0).keySet().toArray()[0].equals(new Position(8,1)));
		//} catch (java.lang.IndexOutOfBoundsException e) {
		//	//return false;
		//	return initialBlocked;
		//}
		//if(m == null)
		//	//return false;
		//	return initialBlocked;
		//return true;
		
		//if (p.equals(new Position(1,3))) {
		//	////System.err.err.println("1,3! time=" + time + ",fromList= " + boxPositionsFrom.get(0).size() + ", toList=" + boxPositionsTo.get(0).size());
		//	////System.err.err.println("fr=" + boxPositionsFrom.get(1) + ", t=" + boxPositionsTo.get(1));
		//	
		//	Position pos1 = time >= boxPositionsTo.get(0).size() ? boxPositionsTo.get(0).get(boxPositionsTo.get(0).size()-1) : boxPositionsTo.get(0).get(time);
		//	Position pos2 = time >= boxPositionsFrom.get(0).size() ? boxPositionsFrom.get(0).get(boxPositionsFrom.get(0).size()-1) : boxPositionsFrom.get(0).get(time);
		//	////System.err.err.println("fromPos = " + pos2 + ", toPos = " + pos1);
		//	
		//}
		//
		//if (p.equals(new Position(9,3))) {
		//	////System.err.err.println("9,3! time=" + time + ",fromList= " + agentPositionsFrom.get(0).size() + ", toList=" + agentPositionsTo.get(0).size());
		//	//////System.err.err.println("fr=" + agentPositionsFrom.get(0) + ", t=" + agentPositionsTo.get(0));
		//	
		//	Position pos1 = time > agentPositionsTo.get(0).size() ? agentPositionsTo.get(0).get(agentPositionsTo.get(0).size()-1) : agentPositionsTo.get(0).get(time);
		//	Position pos2 = time > agentPositionsFrom.get(0).size() ? agentPositionsFrom.get(0).get(agentPositionsFrom.get(0).size()-1) : agentPositionsFrom.get(0).get(time);
		//	////System.err.err.println("fromPos = " + pos2 + ", toPos = " + pos1);
		//	
		//}
		
		//System.err.println("occ given " + p);
		
		time++; //Offset to make it correct
		if (p.x == -1 || p.y == -1 || p.x >= MAX_COLUMN || p.y >= MAX_ROW) {
			return true;
		}
		if (walls[p.x][p.y]) return true;
		
		////System.err.err.println("Asking for time" + time + ", box: " + boxPositionsFrom.get(1).get(time) + ", " + boxPositionsTo.get(1).get(time) + ", agent: " + agentPositionsFrom.get(1).get(time) + ", " + agentPositionsTo.get(1).get(time));
		////System.err.err.println("Asking for time" + time + ", agent: " + agentPositionsFrom.get(1).get(time) + ", " + agentPositionsTo.get(1).get(time));
		
		for (int i = 0; i < boxPositionsTo.size(); i++) {
			try{
				if (boxPositionsTo.get(i).get(time).equals(p)) {
					//////System.err.err.println("Return boxTo");
					return true;
				}
				if (boxPositionsFrom.get(i).get(time).equals(p)) { 
					//////System.err.err.println("Return boxFrom");
					return true;
				}
			} catch (java.lang.IndexOutOfBoundsException e) {
				if (boxPositionsTo.get(i).get(boxPositionsTo.get(i).size()-1).equals(p)){
					//////System.err.err.println("Return OOB box");
					return true;
				}
			}
		}
		for (int i = 0; i < agentPositionsTo.size(); i++) {
			try{
				if (agentPositionsTo.get(i).get(time).equals(p)) {
					////System.err.err.println("Return agentTo");
					return true;
				}
				
				if (p.equals(agentPositionsFrom.get(i).get(time))) {
					//////System.err.err.println("Return agentTo");
					return true;
				}
			} catch (java.lang.IndexOutOfBoundsException e) {
				if (agentPositionsTo.get(i).get(agentPositionsTo.get(i).size()-1).equals(p)) {
					//////System.err.err.println("Return OOB agent");
					return true;
				}
			}
		}
		//////System.err.err.println("false");
		return false;
	}
	
	//Returns time until cell is free
	//return -1 if the cell will never be free
	public static int cellFreeIn(int currentTime, Position pos){
		if (walls[pos.x][pos.y]) return -1;
		//int time = 0;
		//for(int t = currentTime; t < timeController.size(); t++){
		//	
		//	if(!isPositionOccupiedToTime(pos, t)) {
		//		//////System.err.err.println(pos + "is not occupied to " + t);
		//		return time;
		//	}
		//	time++;
		//}
		//return -1;
		
		int maxTime = 0;
		for (int i = 0; i < boxPositionsTo.size(); i++) {
			maxTime = maxTime < boxPositionsTo.get(i).size() ? boxPositionsTo.get(i).size() : maxTime;
		}
		for (int i = 0; i < agentPositionsTo.size(); i++) {
			maxTime = maxTime < agentPositionsTo.get(i).size() ? agentPositionsTo.get(i).size() : maxTime;
		}
		int returnValue = -1;
		for (int i = currentTime; i < maxTime; i++) {
			if (!isPositionOccupiedToTime(pos, i)) {
				return i-currentTime;
			}
		}
		return -1;
	}

	public static void printMasterPlan()
	{	
	
		for(int i = 0; i < agentsAmount; i++)
		{
			////System.err.err.println("Agent " + i + " has # of plans " + plans.get(i).size());
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
		int asdf = 0;
		while(!done){
			asdf++;
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
					count++;
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
			if(count >= agentsAmount || time >= 50000)
					done = true;
			time++;
			for(int t = 0; t < agentsAmount; t++)
			{
				if(plans.get(t).size() > currentPlan[t] && plans.get(t).get(currentPlan[t]).subplans.get(plans.get(t).get(currentPlan[t]).subplans.size()-1).stop < time)
				{
					//////System.err.err.println("Finished a plan");
					currentPlan[t]++;	
				}
			}
			/*
			*	TEST CODE
			*/ 
			if(time > 100000){
				////System.err.err.println("Print master plan stuck in loop");
			}
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
	
	public static Boolean isWall(Position pos) throws Exception {
		if (pos.x == -1 || pos.y == -1) { 
			error("GameMap.isCellFree: input position was -1!"); 
		}
		return walls[pos.x][pos.y];
	}
	
	public static char BoxAt(Position pos) {
		return (boxes[pos.x][pos.y]);
	}
	
	    
    public static Position agentPositionAtTime(int ID, int time) throws Exception {
        time++;
        
        if (ID >= agentPositionsTo.size()) {
            error("GameMap.agentPositionAtTime failed. agent ID " + ID + " does not exist");    
        }
        try {
            return agentPositionsTo.get(ID).get(time);
        } catch (java.lang.IndexOutOfBoundsException e) {
            return agentPositionsTo.get(ID).get(agentPositionsTo.get(ID).size()-1);
        }
    }
	
	public static char boxAtTime(Position pos, int time){
		//returns the character of the box at a time position to a given time.
		//if (time == 0) return BoxAt(pos);
		time++; //Offset to make it correct
		for (int i = 0; i < boxPositionsTo.size(); i++) {
			//////System.err.err.println("boxAtTime.To. time=" + time + ", pos=" + pos + ", return = " + boxPositionsTo.get(i).get(time+1));
			//////System.err.err.println("boxAtTime.From. time=" + time + ", pos=" + pos + ", return = " + boxPositionsFrom.get(i).get(time+1));
			try {
				if (pos.equals(boxPositionsTo.get(i).get(time))) {
					//////System.err.err.println("Returning");
					return boxCharacters.get(i);
				}
			} catch (java.lang.IndexOutOfBoundsException e) {
				if (pos.equals(boxPositionsTo.get(i).get(boxPositionsTo.get(i).size()-1))) return boxCharacters.get(i);
			}
			
			//try {
			//	if (pos.equals(boxPositionsFrom.get(i).get(time))) {
			//		//////System.err.err.println("Returning");
			//		return boxCharacters.get(i);
			//	}
			//} catch (java.lang.IndexOutOfBoundsException e) {
			//	if (pos.equals(boxPositionsFrom.get(i).get(boxPositionsFrom.get(i).size()-1))) return boxCharacters.get(i);
			//}
			
		}
		return 0;
	}
	
	public static char GoalAt(Position pos) {
		return (goals[pos.x][pos.y]);
	}
	
	public static char AgentAt(Position pos) {
		return (agents[pos.x][pos.y]);
	}

	public static char agentAtTime(Position pos, int time){
		//returns the character of the agent at a time position to a given time.
		time++; //Offset to make it correct
		for (int i = 0; i < agentPositionsTo.size(); i++) {
			//////System.err.err.println("boxAtTime.To. time=" + time + ", pos=" + pos + ", return = " + boxPositionsTo.get(i).get(time+1));
			//////System.err.err.println("boxAtTime.From. time=" + time + ", pos=" + pos + ", return = " + boxPositionsFrom.get(i).get(time+1));
			try {
				if (pos.equals(agentPositionsTo.get(i).get(time))) {
					//////System.err.err.println("Returning");
					return agentCharacters.get(i);
				}
			} catch (java.lang.IndexOutOfBoundsException e) {
				if (pos.equals(agentPositionsTo.get(i).get(agentPositionsTo.get(i).size()-1))) return agentCharacters.get(i);
			}
			
			//try {
			//	if (pos.equals(boxPositionsFrom.get(i).get(time))) {
			//		//////System.err.err.println("Returning");
			//		return boxCharacters.get(i);
			//	}
			//} catch (java.lang.IndexOutOfBoundsException e) {
			//	if (pos.equals(boxPositionsFrom.get(i).get(boxPositionsFrom.get(i).size()-1))) return boxCharacters.get(i);
			//}
			
		}
		return 0;
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
					ArrayList<Position> posList = new ArrayList<Position>();
					ArrayList<Position> posList2 = new ArrayList<Position>();
					posList.add(new Position(i,curLine));
					posList2.add(new Position(i,curLine));
					agentPositionsTo.add(posList);
					agentPositionsFrom.add(posList2);
					agentCharacters.add(chr);
				} else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
					boxes[i][curLine] = chr;
					ArrayList<Position> posList = new ArrayList<Position>();
					ArrayList<Position> posList2 = new ArrayList<Position>();
					posList.add(new Position(i,curLine));
					posList2.add(new Position(i,curLine));
					boxPositionsTo.add(posList);
					boxPositionsFrom.add(posList2);
					boxCharacters.add(chr);
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

