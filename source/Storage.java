package source;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import source.Position;

public class Storage {
	
	/*
	 * Operations supported
	 * 
	 * getNearestStorage(Position p, int time) - finds nearest storage spot from a spot to a time
	 * storeBox(Position p, int time) - Stores a box at a position to a time
	 * destoreBox(Position p, int time) - Removes a box from storage updating the storage map
	 * printMap(int t) - prints the map to a time, used for debugging
	 * 
	 */
	
	char hardChar = 'h', //Indicates a hard storage spot, this is the priotized storage
		 softChar = 's', //Indicates a soft storage spot, this is the prefered storage if no hard storage is found
		 hallChar = ' ', //Indicates hallway, this is avoided as storage because it is crucial for agents to move freely
		 nullChar = '#', //Indicates a null spot in the storage map, most likely a wall
		 resChar  = 'R',  //Indicates a reserved storage spot, this spot is going to be used some time in the future
		 boxChar  = 'B';  //Indicate a spot that is already occupied by a box, such a spot updates the storage map around it.
	
	//List of storage maps to the time i
	public List<char[][]> storageMap;
	int xSize = 0, ySize = 0;
	int timeLimit;
	
	//Constructor, creates a storage map of the map
	public Storage(boolean walls[][], char boxes[][]){
		timeLimit = 0;
		xSize = walls.length;
		ySize = walls[0].length;
		
		storageMap = new ArrayList<char[][]>();
		storageMap.add(new char[xSize][ySize]);
		
		//System.err.err.println("X Size - " + xSize + ", Y Size - " + ySize);
		
		for(int y = 0; y < ySize; y++){
			for(int x = 0; x < xSize; x++){
				
				if(walls[x][y]){
					storageMap.get(0)[x][y] = nullChar;
					continue;
				}
				
				boolean SquareBlocked[] = new boolean[4], DiamondBlocked[] = new boolean[4];
				
				//Top row
				DiamondBlocked[0] = ((x - 1) < 0 || (y - 1) < 0 || walls[x-1][y-1]) ? true : false;
				SquareBlocked[0] = ((y - 1) < 0 || walls[x][y-1]) ? true : false;
				DiamondBlocked[1] = ((x + 1) >= xSize || (y - 1) < 0 || walls[x+1][y-1]) ? true : false;
				
				////System.err.err.println(x);
				
				//Middle row
				SquareBlocked[1] = ((x - 1) < 0 || walls[x-1][y]) ? true : false;
				SquareBlocked[2] = ((x + 1) >= xSize || walls[x+1][y]) ? true : false;
				
				//Bottom row
				DiamondBlocked[2] = ((x - 1) < 0 || (y + 1) >= ySize || walls[x-1][y+1]) ? true : false;
				SquareBlocked[3] = ((y + 1) >= ySize || walls[x][y+1]) ? true : false;
				DiamondBlocked[3] = ((x + 1) >= xSize || (y + 1) >= ySize|| walls[x+1][y+1]) ? true : false;
				
				
				storageMap.get(0)[x][y] = analyse(DiamondBlocked, SquareBlocked);
				
			}
		}
		
		for(int y = 0; y < ySize; y++){
			for(int x = 0; x < xSize; x++){
				if (boxes[x][y] != (char)0) {
					storeBox(new Position(x,y), 0);
				}
			}
		}
	}
	
	//Prints the map
	public void printMap(int time){
		for(int y = 0; y < ySize; y++){
			for(int x = 0; x < xSize; x++){
				//System.err.err.print(storageMap.get(time)[x][y]);
				//System.err.err.print(" ");
			}
			//System.err.err.print("\n");
		}
	}
	//####################### UPDATE MAP SECTION #######################
	//Reserves a storage spot until box is placed and updates storage map to the time
	//Also creates new copies of the latest map up until the time the box is stored
	public void storeBox(Position pos, int time){
		
		if(time > timeLimit)
			addXMaps(time - timeLimit);
		char analyzeS = analyseSpot(pos, time);
		storageMap.get(time)[pos.x][pos.y] = boxChar;
		
		int t = 0;
        
        if(analyzeS != hallChar){
			char[][] storageChanges = analyseSurrounding(pos, time);
			applyChanges(pos, storageChanges, time);
			t = time + 1;
			if(time < timeLimit){
				while( t <= timeLimit ){
					storageMap.get(t)[pos.x][pos.y] = boxChar;
					applyChanges(pos, storageChanges, t);
					t++;
				}
			}
		}
		
		t = time - 1;
		while( t >= 0 ){
			//If there already is an reservation there is no need to do anything further
			if(storageMap.get(t)[pos.x][pos.y] == boxChar)
				break;
			
			//Reserve that spot
			storageMap.get(t)[pos.x][pos.y] = resChar;
			t--;
		}
		
	}
	
	//removes a box from storage to a certain time
	public void destoreBox(Position pos, int time){
		
		if(time > timeLimit)
			addXMaps(time - timeLimit);
		
		if(storageMap.get(time)[pos.x][pos.y] != 'B'){
			//System.err.err.println("Attempting to destore box not in storage zone, null action");
			return;
		}
		
		storageMap.get(time)[pos.x][pos.y] = hallChar;
		for(int t = time; t <= timeLimit; t++){
			applyChanges(pos, analyseSurrounding(pos, t), t);
		}
		storageMap.get(time)[pos.x][pos.y] = analyseSpot(pos, time);
		
	}
	
	private void applyChanges(Position p, char[][] changes, int t){
		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 3; x++){
				if(!spotOccupied(p.x + x - 1, p.y + y - 1, t) && changes[x][y] != nullChar){
					storageMap.get(t)[p.x + x - 1][p.y + y - 1] = changes[x][y];
				}
			}
		}
	}
	
	//Analyse the storage space on a spot in a map and returns the appropriate characters
	private char[][] analyseSurrounding(Position p, int t){
		char[][] analyse = new char[3][3];
		for(int y = 0; y < 3; y++){
			for(int x = 0; x < 3; x++){
				analyse[x][y] = analyseSpot(new Position( p.x + x - 1, p.y + y - 1), t);
			}
		}
		
		analyse[1][1] = nullChar;	
		return analyse;
		
	}
	
	public char analyseSpot(Position p, int t){
		if(spotOccupied(p.x, p.y, t))
			return nullChar;
		
		boolean SquareBlocked[] = new boolean[4], DiamondBlocked[] = new boolean[4];
		//Top row
		DiamondBlocked[0] = ((p.x - 1) < 0 || (p.y - 1) < 0 || spotOccupied(p.x-1, p.y-1, t)) ? true : false;
		SquareBlocked[0] = ((p.y - 1) < 0 || spotOccupied(p.x, p.y-1, t)) ? true : false;
		DiamondBlocked[1] = ((p.x + 1) > xSize || (p.y - 1) < 0 || spotOccupied(p.x+1, p.y-1, t)) ? true : false;
		
		//Middle row
		SquareBlocked[1] = ((p.x - 1) < 0 || spotOccupied(p.x-1, p.y, t)) ? true : false;
		SquareBlocked[2] = ((p.x + 1) > xSize || spotOccupied(p.x+1, p.y, t)) ? true : false;
		
		//Bottom row
		DiamondBlocked[2] = ((p.x - 1) < 0 || (p.y + 1) > ySize || spotOccupied(p.x-1, p.y+1, t)) ? true : false;
		SquareBlocked[3] = ((p.y + 1) > ySize || spotOccupied(p.x, p.y+1, t)) ? true : false;
		DiamondBlocked[3] = ((p.x + 1) > xSize || (p.y + 1) > ySize|| spotOccupied(p.x+1, p.y+1, t)) ? true : false;
		
		char c = analyse(DiamondBlocked, SquareBlocked);
		
		return c;
	}
	
	//Helper function for analysing the map
	//dB = diamond shaped blocked, sB = square shape blocked
	private char analyse(boolean dB[], boolean sB[]){
		int SquareCount = 0, DiamondCount = 0;
		
		for(int i = 0; i < dB.length; i++){
			if(dB[i])
				DiamondCount++;
		}
			
		for(int i = 0; i < sB.length; i++){
			if(sB[i])
				SquareCount++;
		}
		
		//Storage
		if(sB[0] && sB[1] && !(dB[3] && !sB[2] && !sB[3]) || 
				sB[0] && sB[2] && !(dB[2] && !sB[3] && !sB[1]) || 
				sB[3] && sB[1] && !(dB[1] && !sB[0] && !sB[2]) || 
				sB[3] && sB[2] && !(dB[0] && !sB[1] && !sB[0]))
			return hardChar;
		
		//Hallway
		if(DiamondCount > 2)
			return hallChar;
		
		if(DiamondCount+SquareCount == 0)
			return  softChar;
		
		return hallChar;
	}
		
	public boolean spotOccupied(int x, int y, int t){
		if(storageMap.get(t)[x][y] == nullChar || storageMap.get(t)[x][y] == boxChar || storageMap.get(t)[x][y] == resChar)
			return true;
		return false;
	}
	
	//Copies the previus maps when new time limits are required
	private void addXMaps(int x){
		for(int i = 0; i < x; i++){
			storageMap.add(cloneMatrix(storageMap.get(timeLimit)));		
		}
		timeLimit = storageMap.size() - 1;
	}
	
	private char[][] cloneMatrix(char[][] src) {
	    int length = src.length;
	    char[][] target = new char[xSize][src[0].length];
	    for (int i = 0; i < length; i++) {
	        //System.err.arraycopy(src[i], 0, target[i], 0, src[i].length);
	    }
	    return target;
	}
	
	//####################### UPDATE MAP SECTION #######################
	
	//####################### PATH FINDING SECTION #######################
	
	//Returns null if no storage could be found from position, returns a soft storage if no hard storage could be found
	public Position getNearestStorage(Position p, int time, int id){
		
		if(time > timeLimit)
			addXMaps(time - timeLimit);
		
		if(p.x > xSize || p.x < 0 || p.y > ySize || p.y < 0)
			//System.err.err.println("Nearest storage request on out of bounds position");
		
		//If a wall is found or a null space returns null
		if(storageMap.get(time)[p.x][p.y] == hardChar){
			//System.err.err.println("Null position nearest storage was requested ("+ p.x+ ","+p.y+")");
			return null;
		}
		
		return bfsForStorage(p, time, id);
		
	}
	
	//BFS Search for nearest storage
	boolean[][] seen;
	Queue<node> q;
	Position firstSeenSoftStorage;
	class node{
		public Position p;
		public int t = 0;
		node(Position p, int t){
			this.p = p;
			this.t = t;
		}
	}
	
	private Position bfsForStorage(Position p, int time, int id) {
        seen = new boolean[xSize][ySize];
        q = new LinkedList<node>();
        firstSeenSoftStorage = null;
        q.add(new node(p, time));
        seen[p.x][p.y] = true;
        while (!q.isEmpty()) {
            node n = q.remove();
            ////System.err.err.println("Position of n: " + n.p.toString());
            ////System.err.err.println(n.p.toString() + " to time " + n.t);
            if(storageMap.get(n.t)[n.p.x][n.p.y] == hardChar){
            	//System.err.err.println("Hard storage found at " + "(" + n.p.x + ", " + n.p.y + ") to time - " + n.t);
            	return n.p;
            }
            if(storageMap.get(n.t)[n.p.x][n.p.y] == softChar && firstSeenSoftStorage == null)
            	firstSeenSoftStorage = n.p;
            try {
				explorePosition(n, -1, 0, n.t, id);
				explorePosition(n, 0, -1, n.t, id);
				explorePosition(n, 1, 0, n.t, id);
				explorePosition(n, 0, 1, n.t, id);
			} catch(Exception e) {}
        }
        
        if(firstSeenSoftStorage != null){
        	//System.err.err.println("Soft storage found");
        	return firstSeenSoftStorage;
        }
        
        return null;
    }
	
	private void explorePosition(node n, int x, int y, int time, int id)throws Exception{
        Position pos = new Position(n.p.x + x, n.p.y + y);

        if((storageMap.get(n.t)[pos.x][pos.y] == hallChar || 
                storageMap.get(n.t)[pos.x][pos.y] == softChar || 
                storageMap.get(n.t)[pos.x][pos.y] == hardChar) && 
                (!GameMap.isPositionOccupiedToTime(pos, time) || (GameMap.agentPositionAtTime(id, time).equals(pos)))){

        	if (!seen[n.p.x + x][n.p.y + y]) {
        		if(n.t + 1 < timeLimit) // To add one to the time the time needs to be below the time limit
        			q.add(new node(new Position(n.p.x + x, n.p.y + y), n.t + 1));
        		else // If this is not the case the time limit is taken as the current time
        			q.add(new node(new Position(n.p.x + x, n.p.y + y), n.t));
                seen[n.p.x + x][n.p.y + y] = true;
            }
        }
	}
	
	//####################### PATH FINDING SECTION #######################
}