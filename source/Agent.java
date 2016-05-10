package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Comparator;

import source.*;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////// TO DO: 
//Include check for previous plans) - Done
//Add support for no-operation 
//Right now it chooses first box it finds. Choose "best" box instead (based on heuristic).
//Make plan based on resultant moves - Done
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Agent {
	private int id;
	private Position position;
	private int time;

	public int getId(){
		return this.id;
	}
	
	public Agent(int id, Position position) {
		this.id = id;
		this.position = position;
		this.time = 0;
	}
	
	public double heuristic(Position one, Position two, int time) {
		double a = Math.pow(Math.abs(one.x - two.x),2);
		double b = Math.pow(Math.abs(one.y - two.y),2);
		return Math.sqrt(a + b) + time;
	}
	
	public double eucledian(Position one, Position two) {
		double a = Math.pow(Math.abs(one.x - two.x),2);
		double b = Math.pow(Math.abs(one.y - two.y),2);
		return Math.sqrt(a + b);
	}
	
	private static void error( String msg ) throws Exception {
		throw new Exception( "GSCError: " + msg );
	}
	
	///////////////////////////////////////////////////////////// Classes and their comparators
	private class PosNode {
		public Position pos;
		public ArrayList<Type> moves;
		private Position boxPos;
		public int time;
		public ArrayList<Position> explored;
		public ArrayList<Position> boxJobs; //Boxes in the way that need to be moved
		public ArrayList<Position> agentJobs; //Agents in the way that need to move themselves.
		public ArrayList<Position> path = new ArrayList<Position>();
		
		@Override
		public boolean equals(Object other) {
			PosNode ptr = (PosNode) other;
			return (pos.x == ptr.pos.x && pos.y == ptr.pos.y);
		}
		
		public String toString() {
			String str = "Pos = (" + pos.x + "," + pos.y + "), time = " + time;
			return str;
		}
		
		public PosNode(Position pos, ArrayList<Type> moves, Position boxPos, int time, ArrayList<Position> explored) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.time = time;
			this.explored = explored;
		}
		
		public PosNode(Position pos, ArrayList<Type> moves, Position boxPos, int time) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.time = time;
			this.explored = new ArrayList<Position>();
		}
		
		public PosNode(Position pos, ArrayList<Type> moves, Position boxPos, ArrayList<Position> explored, ArrayList<Position> boxJobs, ArrayList<Position> path, int time, ArrayList<Position> agentJobs) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.time = time;
			this.explored = explored;
			this.boxJobs = new ArrayList<Position>();
			this.boxJobs.addAll(boxJobs);
			this.agentJobs = new ArrayList<Position>();
			this.agentJobs.addAll(agentJobs);
			this.path = path;
		}
		
		public PosNode(Position pos, Position boxPos, ArrayList<Position> explored, int time) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.boxPos = boxPos;
			this.time = time;
			this.explored = explored;
			this.boxJobs = new ArrayList<Position>();
			this.agentJobs = new ArrayList<Position>();
		}
		
		public PosNode(Position pos) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.time = 0;
			this.explored = new ArrayList<Position>();
			this.boxJobs = new ArrayList<Position>();
			this.agentJobs = new ArrayList<Position>();
		}
	}
	
	private class PosNodeComp implements Comparator<PosNode>{
	
		@Override
		public int compare(PosNode p1, PosNode p2) {
			return heuristic(p1.pos, p1.boxPos, p1.time) > heuristic(p2.pos, p2.boxPos, p2.time) ? 1 : -1;
		}
	}
	
	private class PosBoxNode {
		public Position pos;
		public Position boxPos;
		public ArrayList<Type> moves;
		private Position goalPos;
		public int time;
		public ArrayList<Position> explored;
		
		@Override
		public boolean equals(Object other) {
			PosBoxNode ptr = (PosBoxNode) other;
			return (this.pos.x == ptr.pos.x && this.pos.y == ptr.pos.y && this.boxPos.x == ptr.boxPos.x && this.boxPos.y == ptr.boxPos.y );
		}
		
		public PosBoxNode(Position pos, ArrayList<Type> moves, Position boxPos, Position goalPos, int time, ArrayList<Position> explored) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.goalPos = goalPos;
			this.time = time;
			this.explored = explored;
		}
		
		public PosBoxNode(Position pos, Position boxPos, Position goalPos, int time) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.boxPos = boxPos;
			this.goalPos = goalPos;
			this.time = time;
			this.explored = new ArrayList<Position>();
		}
		
		public PosBoxNode(Position pos, Position boxPos, Position goalPos) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.boxPos = boxPos;
			this.goalPos = goalPos;
			this.time = 0;
			this.explored = new ArrayList<Position>();
		}
	}
	
	private class PosBoxNodeComp implements Comparator<PosBoxNode> {
	
		@Override
		public int compare(PosBoxNode p1, PosBoxNode p2) {
			return heuristic(p1.boxPos, p1.goalPos, p1.time) > heuristic(p2.boxPos, p2.goalPos, p2.time) ? 1 : -1;
		}
	}
	
	///////////////////////////////////////////////////////////// Private functions
	private Position newPosInDirection(Position pos, char dir) throws Exception {
		Position newPos = new Position(-1, -1);;
		if (dir == 'E') {
			newPos = new Position(pos.x + 1, pos.y);
		} else if (dir == 'W') {
			newPos = new Position(pos.x - 1, pos.y);
		} else if (dir == 'S') {
			newPos = new Position(pos.x, pos.y + 1);
		} else if (dir == 'N') {
			newPos = new Position(pos.x, pos.y - 1);
		} else {
			
			error("newPosInDirection failed. Wrong direction. Given: '" + dir + "'");
		}
		return newPos;
	}
	
	private char positionsToDir(Position p1, Position p2) throws Exception {
		char rtn = 0;
		if (eucledian(p1,p2) <= 1) {
			if (p1.x - p2.x == 0) {
				if (p1.y - p2.y == 1) {
					rtn = 'N';
				} else {
					rtn = 'S';
				}
			} else if (p1.x - p2.x == 1) {
				rtn = 'W';
			} else {
				rtn = 'E';
			}
		} else {
			error("positionsToDir failed. Positions are not neighbours");
		}
		return rtn;
	}
	
	private boolean isLegalMove(Position pos, char dir, int time) throws Exception {
		Position newPos = newPosInDirection(pos, dir);
		//return GameMap.isCellFree(newPos) && !GameMap.isPositionOccupiedToTime(newPos, time);
		//if(id == 0) //system.err.println("Checking new pos " + newPos + ", bool= " + GameMap.isPositionOccupiedToTime(newPos, time) + ", time= " + time);
		return !GameMap.isPositionOccupiedToTime(newPos, time);
	}
	
	private boolean isLegalPush(Position agentPos, Position boxPos, char dir, int time) throws Exception {
		Position newPos = newPosInDirection(boxPos, dir);
		//if (id == 0){
		//	System.err.println("is " + newPos + " occ? " + GameMap.isPositionOccupiedToTime(newPos, time));
		//	System.err.println(GameMap.agentPositionsTo.get(1));
		//}
		return !agentPos.equals(newPos) && !GameMap.isPositionOccupiedToTime(newPos, time);
	}
	
	private boolean isLegalPull(Position agentPos, Position boxPos, char dir, int time) throws Exception {
		Position newPos = newPosInDirection(agentPos, dir);
		//return GameMap.isCellFree(newPos) && !boxPos.equals(newPos) && !GameMap.isPositionOccupiedToTime(newPos, time);
		//System.err.println("LegalPull" + newPos + ", occ=" + GameMap.isPositionOccupiedToTime(newPos, time) + "agentAtTime = " + GameMap.agentAtTime(newPos, time));
		return !boxPos.equals(newPos) && (!GameMap.isPositionOccupiedToTime(newPos, time) || GameMap.agentAtTime(newPos, time) == '0' + id);
	}
	
	private TreeSet<PosNode> makeMove(TreeSet<PosNode> frontier, PosNode node, char dir) throws Exception {
		Position newPos = newPosInDirection(node.pos, dir);
		if (isLegalMove(node.pos, dir, node.time) && !node.explored.contains(newPos)) {
			ArrayList<Type> tmp = new ArrayList<Type>(); 
			ArrayList<Position> tmp2 = new ArrayList<Position>();
			tmp2.addAll(node.explored);
			tmp2.add(newPos);
			tmp.addAll(node.moves);
			tmp.add(new Type(node.pos, newPos, dir));
			frontier.add(new PosNode(newPos, tmp, node.boxPos, node.time+1, tmp2));
		}
		return frontier;
	}
	
	private TreeSet<PosNode> initialMove(TreeSet<PosNode> frontier, PosNode node, char dir) throws Exception {
		Position newPos = newPosInDirection(node.pos, dir);
		////system.err.println("pos = " + newPos + ", time = " + node.time + ", occ = " + GameMap.isPositionOccupiedToTime(newPos, node.time) + ",box=" +  GameMap.boxAtTime(newPos, node.time));
		if ((!GameMap.isPositionOccupiedToTime(newPos, node.time) || GameMap.boxAtTime(newPos, node.time) != (char)0 || GameMap.agentAtTime(newPos, node.time) != (char)0) && !node.explored.contains(newPos)) {
		//if ((GameMap.isCellFree(newPos) || GameMap.boxes[newPos.x][newPos.y] != (char)0) && !node.explored.contains(newPos)) {
			////system.err.println("not occ");
			ArrayList<Type> tmp = new ArrayList<Type>(); 
			ArrayList<Position> tmp2 = new ArrayList<Position>();
			tmp2.addAll(node.explored);
			tmp2.add(newPos);
			tmp.addAll(node.moves);
			tmp.add(new Type(node.pos, newPos, dir));
			ArrayList<Position> path = new ArrayList<Position>();
			path.addAll(node.path);
			path.add(node.pos);
			frontier.add(new PosNode(newPos, tmp, node.boxPos, tmp2, node.boxJobs, path, node.time, node.agentJobs));
		}
		return frontier;
	}
	
	private TreeSet<PosBoxNode> makePush(TreeSet<PosBoxNode> frontier, PosBoxNode node, char dir) throws Exception {
		ArrayList<Type> tmp = new ArrayList<Type>(); 
		ArrayList<Position> tmp2 = new ArrayList<Position>();
		Position newPos = newPosInDirection(node.boxPos, dir);
		tmp2.addAll(node.explored);
		tmp2.add(newPos);
		if (isLegalPush(node.pos, node.boxPos, dir, node.time) && !node.explored.contains(newPos)) {
			//if (id == 1) {
			//	//system.err.println("Doing push to " + newPos);
			//}
			tmp.addAll(node.moves);
			char agentDir = positionsToDir(node.pos, node.boxPos); 
			tmp.add(new Type(node.pos, node.boxPos, node.boxPos, newPos, TypeNum.PUS, agentDir, dir));
			frontier.add(new PosBoxNode(node.boxPos, tmp, newPos, node.goalPos, node.time+1, tmp2));
		}
		return frontier;
	}
	
	private TreeSet<PosBoxNode> makePull(TreeSet<PosBoxNode> frontier, PosBoxNode node, char dir) throws Exception {
		ArrayList<Type> tmp = new ArrayList<Type>(); 
		ArrayList<Position> tmp2 = new ArrayList<Position>();
		Position newPos = newPosInDirection(node.pos, dir);
		tmp2.addAll(node.explored);
		tmp2.add(newPos);
		if (isLegalPull(node.pos, node.boxPos, dir, node.time) && !node.explored.contains(newPos)) {
			tmp.addAll(node.moves);
			char boxDir = positionsToDir(node.pos, node.boxPos);
			tmp.add(new Type(node.pos, newPos, node.boxPos, node.pos, TypeNum.PUL, dir, boxDir));
			frontier.add(new PosBoxNode(newPos, tmp, node.pos, node.goalPos, node.time+1, tmp2));
		}
		return frontier;
	}
	
	private TreeSet<PosNode> initialExplore(TreeSet<PosNode> frontier, PosNode node) throws Exception {
		frontier = initialMove(frontier, node, 'E');
		frontier = initialMove(frontier, node, 'W');
		frontier = initialMove(frontier, node, 'S');
		frontier = initialMove(frontier, node, 'N');
		return frontier;
	}
	
	private TreeSet<PosNode> moveExplore(TreeSet<PosNode> frontier, PosNode node) throws Exception {
		frontier = makeMove(frontier, node, 'E');
		frontier = makeMove(frontier, node, 'W');
		frontier = makeMove(frontier, node, 'S');
		frontier = makeMove(frontier, node, 'N');
		return frontier;
	}

	private TreeSet<PosBoxNode> moveBoxExplore(TreeSet<PosBoxNode> boxFrontier, PosBoxNode node) throws Exception {
		boxFrontier = makePush(boxFrontier, node, 'E');
		boxFrontier = makePush(boxFrontier, node, 'W');
		boxFrontier = makePush(boxFrontier, node, 'S');
		boxFrontier = makePush(boxFrontier, node, 'N');
		boxFrontier = makePull(boxFrontier, node, 'E');
		boxFrontier = makePull(boxFrontier, node, 'W');
		boxFrontier = makePull(boxFrontier, node, 'S');
		boxFrontier = makePull(boxFrontier, node, 'N');
		//boxFrontier.add(new PosBoxNode(node.pos, node.moves, node.boxPos, node.goalPos, time, node.explored)); //NoOp action
		return boxFrontier;
	}
	
	private int largest(int a, int b, int c, int d) {
		int one = (a > b) ? a : b;
		int two = (c > d) ? c : d;
		return (one > two) ? one : two;
	}
	
	private int smallest(int a, int b, int c, int d) {
		int one = (a < b) ? a : b;
		int two = (c < d) ? c : d;
		return (one < two) ? one : two;
	}

    private Position quickStoreBox(ArrayList<Position> path, Position boxPos, int time) throws Exception {
        //Returns the nearest free position to the box that is not on the path
        //System.err.println("Quick storing, path= " + path);
        Position returnPosition = new Position(-1,-1);
        ArrayList<Position> explored = new ArrayList<Position>();
        ArrayList<Position> frontier = new ArrayList<Position>();
        frontier.add(boxPos);
        
        while (!frontier.isEmpty()) {
            Position pos = frontier.get(0);
            frontier.remove(0);
            if (!GameMap.isPositionOccupiedToTime(pos, time) && !path.contains(pos)) {
                returnPosition = pos;
                break;
            }
            //System.err.println("Checking " + pos);
            if (!explored.contains(pos)) {
                if (isLegalMove(pos,'W', time)) {
                    frontier.add(newPosInDirection(pos,'W'));
                }
                if (isLegalMove(pos,'E', time)) {
                    frontier.add(newPosInDirection(pos,'E'));
                }
                if (isLegalMove(pos,'S', time)) {
                    frontier.add(newPosInDirection(pos,'S'));
                }
                if (isLegalMove(pos,'N', time)) {
                    frontier.add(newPosInDirection(pos,'N'));
                }
            }
            explored.add(pos);
        }
        
        if (returnPosition.equals(new Position(-1,-1))) {
            System.err.println("Quickstorage couldn't find storage.");
        }
        System.err.println("Return QuickStore " + returnPosition);
        return returnPosition;
    }

	
	private Plan buildPlan(ArrayList<Type> resultMoves) {
		return buildPlan(resultMoves, new ArrayList<Type>());
	}
	
	private Plan buildPlan(ArrayList<Type> resultMoves, ArrayList<Type> resultBoxMoves) {
		Plan thePlan = new Plan();
		ArrayList<Move> moves = new ArrayList<Move>();
		ArrayList<Move> boxMoves = new ArrayList<Move>();
		int moveN = -1;
		int moveS = Integer.MAX_VALUE;
		int moveE = -1;
		int moveW = Integer.MAX_VALUE;
		int boxMoveN = -1;
		int boxMoveS = Integer.MAX_VALUE;
		int boxMoveE = -1;
		int boxMoveW = Integer.MAX_VALUE;
		for (Type typ : resultMoves) { 
			Move mov = new Move(time, typ);
			
			if (typ.type == TypeNum.MOV) {
				moveN = (typ.l1.y > typ.l2.y) ? (typ.l1.y > moveN) ? typ.l1.y : moveN : (typ.l2.y > moveN) ? typ.l2.y : moveN ;
				moveS = (typ.l1.y < typ.l2.y) ? (typ.l1.y < moveS) ? typ.l1.y : moveS : (typ.l2.y < moveS) ? typ.l2.y : moveS ;
				moveE = (typ.l1.x > typ.l2.x) ? (typ.l1.x > moveE) ? typ.l1.x : moveE : (typ.l2.x > moveE) ? typ.l2.x : moveE ;
				moveW = (typ.l1.x < typ.l2.x) ? (typ.l1.x < moveW) ? typ.l1.x : moveW : (typ.l2.x < moveW) ? typ.l2.x : moveW ;
			}
			
			moves.add(mov);
			time++;
		}
		////system.err.println("Move Bounds (N,S,E,W) = (" + moveN + ", " + moveS + ", " + moveE + ", " + moveW + ")");
		
		if (!moves.isEmpty()) {
			Plan.SubPlan partOne = thePlan.new SubPlan(moveN, moveE, moveS, moveW, moves);
			//system.err.print("Move time = (" + partOne.start + "," + partOne.stop + "),");
			thePlan.addSubplan(partOne);
		}
		for (Type typ : resultBoxMoves) { 
			Move mov = new Move(time, typ);
			
			if (typ.type == TypeNum.PUS || typ.type == TypeNum.PUL) {
				int largestY = largest(typ.l1.y, typ.l2.y, typ.l3.y, typ.l4.y);
				int largestX = largest(typ.l1.x, typ.l2.x, typ.l3.x, typ.l4.x);
				int smallestY = smallest(typ.l1.y, typ.l2.y, typ.l3.y, typ.l4.y);
				int smallestX = smallest(typ.l1.x, typ.l2.x, typ.l3.x, typ.l4.x);
				boxMoveN =  largestY > boxMoveN ? largestY : boxMoveN;
				boxMoveS =  smallestY < boxMoveS ? smallestY : boxMoveS;
				boxMoveE =  largestX > boxMoveE ? largestX : boxMoveE;
				boxMoveW =  smallestX < boxMoveW ? smallestX : boxMoveS;
			}
			
			boxMoves.add(mov);
			time++;
		}
		////system.err.println("boxMove Bounds (N,S,E,W) = (" + boxMoveN + ", " + boxMoveS + ", " + boxMoveE + ", " + boxMoveW + ")");
		
		if (!boxMoves.isEmpty()) {
			Plan.SubPlan partTwo = thePlan.new SubPlan(boxMoveN, boxMoveE, boxMoveS, boxMoveW, boxMoves);
			//system.err.println("boxMove time= (" + partTwo.start + "," + partTwo.stop + ")");
			thePlan.addSubplan(partTwo);
		}
		
		//system.err.println("Sizes: m = " + moves.size() + ", bm = " + boxMoves.size());
		
		thePlan.id = id;
		return thePlan;
	}
	
	private class InitialExploreNode {
		public PosNode node;
		public PreCondition preC;
		
		public InitialExploreNode() {
			node = null;
			preC = null;
		}
	}
	
	private InitialExploreNode initialRouting(Position startPosition, Position endPosition, JobManager.Job parentJob) throws Exception {
		int startTime = Integer.MAX_VALUE;
		InitialExploreNode returnNode = new InitialExploreNode();
		TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
		Position agentInitialEndPosition = new Position(-1,-1);
		ArrayList<Type> resultInitialMoves = new ArrayList<Type>();
		ArrayList<Position> ex = new ArrayList<Position>();
		ex.add(startPosition);
		frontier.add(new PosNode(startPosition, endPosition, ex, startTime));
		PosNode endNode = new PosNode(new Position(-1,-1));
		
		
		
		//System.err.println("StartPos = " + startPosition);
		//System.err.println("endPos = " + endPosition);
		
		while (!frontier.isEmpty()) {
			PosNode node = frontier.pollFirst();
			//System.err.println("Check node: " + node.pos + ", node.agentJobs = " + node.agentJobs);
			if (node.pos.nextTo(endPosition)) { //Next to box?
				//system.err.println("job: g, agent pathed initial to box");
				resultInitialMoves = node.moves;
				agentInitialEndPosition = node.pos;
				node.path.add(node.pos);
				endNode = node;
				break;
			}
			////system.err.println((GameMap.boxes[node.pos.x][node.pos.y] == 0));
			//System.err.println("Box on " + node.pos + " at time " + time + "?" + (char) (GameMap.boxAtTime(node.pos, node.time)));
			if (GameMap.boxAtTime(node.pos, node.time) != (char)0 && GameMap.cellFreeIn(node.time, node.pos) == -1 && !node.pos.equals(startPosition)) { //GameMap.boxes[node.pos.x][node.pos.y] != 0
				////system.err.println("cellFree= " + GameMap.cellFreeIn(0, node.pos));
				////system.err.println("box-block for job found");
				node.boxJobs.add(node.pos);
			}
			//System.err.println("time=" + node.time + " agentAtTime=" + GameMap.agentAtTime(node.pos, node.time) + ", cellFreeIn = " + GameMap.cellFreeIn(node.time, node.pos));
			
			
			if (GameMap.agentAtTime(node.pos, node.time) != (char)0 && GameMap.cellFreeIn(node.time, node.pos) == -1 && GameMap.agentAtTime(node.pos, node.time) != '0' + id && !node.pos.equals(startPosition)) {
				////system.err.println("cellFree= " + GameMap.cellFreeIn(0, node.pos));
				//System.err.println("agent-block for job found" + node.pos);
				node.agentJobs.add(node.pos);
			}
			frontier = initialExplore(frontier, node);
		}
		//System.err.println("length of boxJobs=" + endNode.boxJobs.size());
		
		if ((!endNode.boxJobs.isEmpty() || !endNode.agentJobs.isEmpty()) && agentInitialEndPosition != new Position(-1,-1)) { // 
			System.err.println("Found path to box, but it is blocked. Creating jobs!");
			System.err.println("boxJobs=" + endNode.boxJobs);
			System.err.println("agentJobs=" + endNode.agentJobs);
			//System.err.println("Mypath = " + endNode.path);
			//Create Precondition.
			ArrayList<JobManager.Job> preCJobs = new ArrayList<JobManager.Job>();
			//System.err.println("Creating jobs, path = " + endNode.path + ", agentInitialEndPosition= " + agentInitialEndPosition);
			for (int i = 0; i < endNode.boxJobs.size(); i++) {
				//JobManager.Job theJob = GameMap.jobManager.new Job(0,'b', endNode.boxJobs.get(i), GameMap.colors.get(GameMap.boxes[endNode.boxJobs.get(i).x][endNode.boxJobs.get(i).y]), endNode.path);
				JobManager.Job theJob = GameMap.jobManager.new Job(2,'b', endNode.boxJobs.get(i), GameMap.colors.get(GameMap.boxAtTime(endNode.boxJobs.get(i), endNode.time)), endNode.path, parentJob);
				preCJobs.add(theJob); //TODO: Fix priority and char?
				////system.err.println("col " + theJob.color);
			}
			for (int i = 0; i < endNode.agentJobs.size(); i++) {
				JobManager.Job theJob = GameMap.jobManager.new Job(2,'a', endNode.agentJobs.get(i), GameMap.colors.get(GameMap.agentAtTime(endNode.agentJobs.get(i), endNode.time)), endNode.path, parentJob);
				preCJobs.add(theJob); //TODO: Fix priority and char?
			}
			////system.err.println("preCJobs = " + preCJobs.size());
			PreCondition preC = new PreCondition(preCJobs, id);
			//job.preConds.add(preC);
			returnNode.preC = preC;
			
		} 
		//else if ((endNode.boxJobs.isEmpty() || endNode.agentJobs.isEmpty()) && agentInitialEndPosition.equals(new Position(-1,-1))) {
		//	error("Didn't initial path, but no jobs created");
		//}
		returnNode.node = endNode;
		return returnNode;
	}
	
	///////////////////////////////////////////////////////////// The function that executes it all!
	public Plan createPlan(JobManager.Job job) throws Exception {
		//final Goal goal = GameMap.getUnsolvedGoal();
		//final JobManager.Job job = GameMap.jobManager.getPriorityJob(id);
		int startTime = 0;
		////system.err.println(GameMap.plans.get(id).size());
		if(GameMap.plans.get(id).size() > 0) startTime = GameMap.plans.get(id).get(GameMap.plans.get(id).size() - 1).end + 1;
		Plan thePlan = new Plan();
		if (job != null) {
			
			Boolean boxFound = false;
			
			String preCColor = "";
			
			System.err.println("Starting Job. My position = " + position);
			
			if (job.jobType == 'g') {
				System.err.println("goal Pos=" + job.jobPos + ", pri=" + job.Priority);
				//Find box that can be used (Currently only finds one. Doesn't find best (closest) box (still only eucledian distance available. Chosen best box can still be bad).)
				Position boxPosition = new Position(-1,-1);
				//system.err.println("Agent " + id + " Job Start time= " + startTime);
				//system.err.println("Goal pos = (" + job.jobPos.x + "," + job.jobPos.y + ")");
				for (int x = 0; x < GameMap.size()[0]; x++) {
					for (int y = 0; y < GameMap.size()[1]; y++) {
						////system.err.println("pos=" + new Position(x,y) + "GM= " + GameMap.boxAtTime(new Position(x,y), startTime));
						if (GameMap.boxAtTime(new Position(x,y), startTime) == Character.toUpperCase(job.goal)) {
							////system.err.println("box color = " + GameMap.colors.get(Character.toUpperCase(job.goal)) + ", agent color = " + color );
							if (GameMap.colors.get(Character.toUpperCase(job.goal)) == GameMap.colors.get((char)('0' + id))) {
								boxPosition = new Position(x,y);
								//system.err.println("Box pos = (" + x + "," + y + ")");
								boxFound = true;
								break;
							} else {
								preCColor = GameMap.colors.get(Character.toUpperCase(job.goal));
							}
						}
					}
				}
				
				
				if (boxFound) {
					//Figure out if there is a path to the box. Record all positions / paths blocked by boxes.
					// If we cannot find a path to our box, create job for alle the boxes that blocked paths.
					System.err.println("type: g: box! " + boxPosition);
					//Do initial
						InitialExploreNode initial = initialRouting(position, boxPosition, job);
                        if (initial.preC != null) {
                            job.preConds.add(initial.preC);
							return thePlan;
						}
					//Do the actual pathing
					//Find path to box
						TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
                        ArrayList<Position> ex = new ArrayList<Position>();
						ArrayList<Type> resultMoves = new ArrayList<Type>();
						//ArrayList<Position> ex = new ArrayList<Position>();
						Position agentEndPosition = new Position(-1,-1);
						ex.add(position);
						frontier.add(new PosNode(position, boxPosition, ex, startTime)); 
						int endMoveTime = 0;
						while (!frontier.isEmpty()) {
							PosNode node = frontier.pollFirst();
							if(node.time > 1000) error("Can't move to box!");
							////system.err.println("Check node: " + node.pos);
							if (node.pos.nextTo(boxPosition)) { //Next to box?
								//system.err.println("End pos = " + node.pos.toString() + " boxPos = " + boxPosition.toString());
								//system.err.println("Box!");
								resultMoves = node.moves;
								agentEndPosition = node.pos;
								endMoveTime = node.time;	
								break;
							}
							
							frontier = moveExplore(frontier, node);
							if (node.pos == position) {
								ArrayList<Type> tmp = new ArrayList<Type>();
								tmp.addAll(node.moves);
								tmp.add(new Type(node.pos));
								frontier.add(new PosNode(node.pos, tmp, node.boxPos, node.time+1, node.explored));
							}
						}
						
						if (resultMoves.isEmpty() && agentEndPosition.equals(new Position(-1,-1))) {
							error("Can't move to box!");
						}
						
					//Can we path from box to goal?
						InitialExploreNode initialBox = initialRouting(boxPosition, job.jobPos, job);
						if (initialBox.preC != null) {
							job.preConds.add(initialBox.preC);
							return thePlan;
						}
					//Find path that moves box on top of goal. (We assume we are next to box initially).
						TreeSet<PosBoxNode> boxFrontier = new TreeSet< PosBoxNode >(new PosBoxNodeComp());;
						ArrayList<Type> resultBoxMoves = new ArrayList<Type>();
						
						boxFrontier.add(new PosBoxNode(agentEndPosition, boxPosition, job.jobPos, endMoveTime));
						
						while (!boxFrontier.isEmpty()) {
							PosBoxNode node = boxFrontier.pollFirst();
							//System.err.println("GoalChecking node " + node.pos);
							if (node.boxPos.equals(job.jobPos)) { //On goal?
								resultBoxMoves = node.moves;
								position = node.pos;
								break;
							}
							boxFrontier = moveBoxExplore(boxFrontier, node);
							if (node.pos == agentEndPosition) {
								ArrayList<Type> tmp = new ArrayList<Type>();
								tmp.addAll(node.moves);
								tmp.add(new Type(node.pos));
								boxFrontier.add(new PosBoxNode(node.pos, tmp, node.boxPos, node.goalPos, node.time+1, node.explored));
							}
						}
						
						if (resultBoxMoves.isEmpty() && !boxPosition.equals(job.jobPos)) {
							//system.err.println("Can't move box to goal!");
						}
						
					//Create list of moves for creating plan. Also create bounds
						//thePlan = buildPlan(resultInitialMoves, resultBoxMoves);
						thePlan = buildPlan(resultMoves, resultBoxMoves); //actualMoves
					System.err.println("Returning goal-plan for agent " + id);
					System.err.println("pos = " + position + ", time = " + time);
					job.solved = true;
					GameMap.storage.destoreBox(boxPosition, endMoveTime);
					GameMap.storage.storeBox(job.jobPos, time);
					
				} else {
					//job.preConds(0).isSolvable = false;
					//system.err.println("box not found");
				}
			} else if (job.jobType == 'b') {
				//error("Job type move-box not supported yet!");
				//This is given to the agent if it needs to move a box out of the way for another agent. That is, an agent is stuck behind a box and cannot move.
				//The job contains the box position and a list of positions where the box shouldn't be. (That is, the path the other agent want to move on).
				//Figure out what desired position we want to move it to
				System.err.println("type: b: box=" + job.jobPos);
				//Do initial
                    InitialExploreNode initial = initialRouting(position, job.jobPos,job);
                    if (initial.preC != null) {
                        job.preConds.add(initial.preC);
                        return thePlan;
                    }
				System.err.println("type b: Initial done");
				//Find path to box
					TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
					Position agentEndPosition = new Position(-1,-1);
					ArrayList<Type> resultMoves = new ArrayList<Type>();
					ArrayList<Position> ex = new ArrayList<Position>();
					ex.add(position);
					frontier.add(new PosNode(position, job.jobPos, ex, startTime));
					PosNode endNode = new PosNode(new Position(-1, -1));
					while (!frontier.isEmpty()) {
						PosNode node = frontier.pollFirst();
						if (node.pos.nextTo(job.jobPos)) { //Next to box?
							resultMoves = node.moves;
							agentEndPosition = node.pos;
							endNode = node;
							break;
						}
						
						frontier = moveExplore(frontier, node);
						if (node.pos == position) {
							if (node.time > 10000) {
								error("Type b: path to box over 9000");
							}
							ArrayList<Type> tmp = new ArrayList<Type>();
							tmp.addAll(node.moves);
							tmp.add(new Type(node.pos));
							frontier.add(new PosNode(node.pos, tmp, node.boxPos, node.time+1, node.explored));
						}
					}
					
					if (resultMoves.isEmpty() && agentEndPosition.equals(new Position(-1,-1))) {
						//system.err.println("Can't move to box!");
					}
					
				System.err.println("pathed to box.");// EndPos = " + agentEndPosition + ", path = " + endNode.path);
					
				//Move box to desired position
					//Find nearest storage					
					Position storagePosition = GameMap.storage.getNearestStorage(job.jobPos, endNode.time, id);
					if (storagePosition == null) {
						System.err.println("Backup storage");
						storagePosition = quickStoreBox(job.path, job.jobPos, startTime);
					}
	
					//Do initial
					InitialExploreNode initialBox = initialRouting(job.jobPos, storagePosition, job);
					if (initialBox.preC != null) {
						job.preConds.add(initialBox.preC);
						return thePlan;
					}
					
					TreeSet<PosBoxNode> boxFrontier = new TreeSet< PosBoxNode >(new PosBoxNodeComp());
					ArrayList<Type> resultBoxMoves = new ArrayList<Type>();
					
					boxFrontier.add(new PosBoxNode(agentEndPosition, job.jobPos, storagePosition, startTime));
					PosBoxNode selfOnStorageNode = new PosBoxNode(agentEndPosition, job.jobPos, storagePosition, startTime);
					System.err.println(storagePosition);
					while (!boxFrontier.isEmpty()) {
						PosBoxNode node = boxFrontier.pollFirst();
						//System.err.println("checking boxNode. " + node.pos);
						if (node.pos.equals(storagePosition)) {
							//We found a way to put the agent on the storagePosition. Save it.
							//System.err.println("Saving temp node");
							selfOnStorageNode = node;
						}

						if (node.boxPos.equals(storagePosition)) { //On goal?
							resultBoxMoves = node.moves;
							position = node.pos;
							break;
						}
						boxFrontier = moveBoxExplore(boxFrontier, node);
					}
					
					if (resultBoxMoves.isEmpty() && resultBoxMoves.isEmpty()) {
						System.err.println("JobType b: Can't move box to storage!");
						//System.err.println("solfOnStorageNode.moves = " + selfOnStorageNode.moves);
						if (!selfOnStorageNode.moves.isEmpty()) {
							System.err.println("But I can move myself there. Doing that");
							resultBoxMoves = selfOnStorageNode.moves;
						}
					}
					
				//Build plan
					thePlan = buildPlan(resultMoves, resultBoxMoves);
					System.err.println("Returning boxMove-plan");
					System.err.println("pos = " + position + ", time = " + time);
					job.solved = true;
					job.preConditionFor.Priority = job.preConditionFor.Priority + 1;
			
					//if (GameMap.goals[job.jobPos.x][job.jobPos.y] != (char)0) {
					//	System.err.println("boxMove plan destroys goal. Update relevant job. FIX!");
					//}
					
					GameMap.storage.destoreBox(job.jobPos, endNode.time);
					GameMap.storage.storeBox(storagePosition, time);

				//Update job if needed
				//System.err.println("goal=" + GameMap.goals[job.jobPos.x][job.jobPos.y] + ", box=" + Character.toUpperCase(GameMap.boxAtTime(job.jobPos, startTime)));
					if (GameMap.goals[job.jobPos.x][job.jobPos.y] == Character.toLowerCase(GameMap.boxAtTime(job.jobPos, startTime))) {
						System.err.println("Updating goal job");
						GameMap.jobManager.updateGoalJob(job.jobPos);
					}
					
			} else if (job.jobType == 'a') {
				//Agent is in the way and needs to move out of the way
				//Position desiredPosition;
				//Find our desired position
					//Position storagePosition = quickStoreBox(job.path, job.jobPos, startTime);
					//int startTime; //Fix
					Position storagePosition = GameMap.storage.getNearestStorage(job.jobPos, startTime, this.id);
					if (storagePosition == null) {
						storagePosition = quickStoreBox(job.path, job.jobPos, startTime);
					}
					
				//Do initial
					InitialExploreNode initial = initialRouting(job.jobPos, storagePosition, job);
					if (initial.preC != null) {
						job.preConds.add(initial.preC);
						return thePlan;
					}
					
				//Path to the position
					TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
					Position agentEndPosition = new Position(-1,-1);
					ArrayList<Type> resultMoves = new ArrayList<Type>();
					ArrayList<Position> ex = new ArrayList<Position>();
					ex.add(position);
					frontier.add(new PosNode(position, job.jobPos, ex, startTime));
					while (!frontier.isEmpty()) {
						PosNode node = frontier.pollFirst();
						if (node.pos.equals(storagePosition)) { //On desired position?
							resultMoves = node.moves;
							agentEndPosition = node.pos;
							break;
						}
						
						frontier = moveExplore(frontier, node);
						if (node.pos == position) {
							ArrayList<Type> tmp = new ArrayList<Type>();
							tmp.addAll(node.moves);
							tmp.add(new Type(node.pos));
							frontier.add(new PosNode(node.pos, tmp, node.boxPos, node.time+1, node.explored));
						}
					}
					
					if (resultMoves.isEmpty() && agentEndPosition.equals(new Position(-1,-1))) {
						System.err.println("Can't move out of the way!");
					}
					
					thePlan = buildPlan(resultMoves);
					System.err.println("Returning agentMove-plan");
					System.err.println("pos = " + agentEndPosition + ", time = " + time);
					job.solved = true;
				
			}
		} else {
			//error("Recieved null job");
			System.err.println("Agent got Null job");
		}
		return thePlan;
	}

	public void deletePlan(Plan p){} //TODO: Why is this here...?
	
}
