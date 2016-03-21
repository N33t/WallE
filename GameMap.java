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

public class GameMap {

	private static GameMap singleton = new GameMap( );

	public static boolean walls[][];
	public static char goals[][];
	public static char boxes[][];
	public static char agents[][];
	public static int agentsAmount;
	public ArrayList<Goal> unsolvedGoals;
	private static int MAX_ROW;
	private static int MAX_COLUMN;

	private GameMap() {
		this.agentsAmount = 0;
		this.unsolvedGoals = new ArrayList<Goal>();
	}

	public static GameMap getInstance( ) {
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
	
	public static Boolean isCellFree(Position pos) {
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

		int colorLines = 0, levelLines = 0;

		// Read lines specifying colors
		while ( ( line = serverMessages.readLine() ).matches( "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$" ) ) {
			line = line.replaceAll( "\\s", "" );
			String[] colonSplit = line.split( ":" );
			color = colonSplit[0].trim();

			for ( String id : colonSplit[1].split( "," ) ) {
				colors.put( id.trim().charAt( 0 ), color );
			}
			colorLines++;
		}
		
		if ( colorLines > 0 ) {
			error( "Box colors not supported" );
		}

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
					unsolvedGoals.add(new Goal(chr, new Position(curLine,i)));
				}
			}
			curLine++;
			line = lines.get(curLine);
		}
	}
}

