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

	public boolean walls[][];
	public char goals[][];
	public char boxes[][];
	public char agents[][];
	public int agentsAmount;
	public ArrayList<Goal> unsolvedGoals;
	private int MAX_ROW;
	private int MAX_COLUMN;

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

	public int[] size() {
		return new int[] {MAX_ROW, MAX_COLUMN};
	}

	public static Goal getUnsolvedGoal() {
		Goal ret = new Goal(singleton.unsolvedGoals.get(0));
		return ret;
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
		walls = new boolean[rows][cols];
		goals = new char[rows][cols];
		boxes = new char[rows][cols];
		agents = new char[rows][cols];
		for (int i = 0; i < rows; i++) {
			Arrays.fill(agents[i],(char) 0);
		}
		MAX_ROW = rows;
		MAX_COLUMN = cols;


		int curLine = 0;
		line = lines.get(curLine);
		
		while ( !line.equals( "" ) ) {
			for ( int i = 0; i < line.length(); i++ ) {
				char chr = line.charAt( i );

				if ( '+' == chr ) { // Walls
					walls[curLine][i] = true;
				} else if ( '0' <= chr && chr <= '9' ) { // Agents
					agents[curLine][i] = chr;
					agentsAmount++;
				} else if ( 'A' <= chr && chr <= 'Z' ) { // Boxes
					boxes[curLine][i] = chr;
				} else if ( 'a' <= chr && chr <= 'z' ) { // Goal cells
					goals[curLine][i] = chr;
					unsolvedGoals.add(new Goal(chr, new Position(curLine,i)));
				}
			}
			curLine++;
			line = lines.get(curLine);
		}
	}
}

