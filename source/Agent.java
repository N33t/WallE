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

	public Agent(int id, Position position) {
		this.id = id;
		this.position = position;
	}
	
	public double heuristic(Position one, Position two, int time) {
		double a = Math.pow(Math.abs(one.x - two.x),2);
		double b = Math.pow(Math.abs(one.y - two.y),2);
		return Math.sqrt(a + b);
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
		
		@Override
		public boolean equals(Object other) {
			PosNode ptr = (PosNode) other;
			return (pos.x == ptr.pos.x && pos.y == ptr.pos.y);
		}
		
		public PosNode(Position pos, ArrayList<Type> moves, Position boxPos, int time) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.time = time;
		}
		
		public PosNode(Position pos, ArrayList<Type> moves, Position boxPos) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.time = 0;
		}
		
		public PosNode(Position pos, Position boxPos) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.boxPos = boxPos;
			this.time = 0;
		}
		
		public PosNode(Position pos) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.time = 0;
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
		
		@Override
		public boolean equals(Object other) {
			PosBoxNode ptr = (PosBoxNode) other;
			return (this.pos.x == ptr.pos.x && this.pos.y == ptr.pos.y && this.boxPos.x == ptr.boxPos.x && this.boxPos.y == ptr.boxPos.y );
		}
		
		public PosBoxNode(Position pos, ArrayList<Type> moves, Position boxPos, Position goalPos, int time) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.goalPos = goalPos;
			this.time = time;
		}
		
		public PosBoxNode(Position pos, ArrayList<Type> moves, Position boxPos, Position goalPos) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.goalPos = goalPos;
			this.time = 0;
		}
		
		public PosBoxNode(Position pos, Position boxPos, Position goalPos) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.boxPos = boxPos;
			this.goalPos = goalPos;
			this.time = 0;
		}
		
		public PosBoxNode(Position pos, Position boxPos) {
			this.pos = pos;
			this.moves = new ArrayList<Type>();
			this.boxPos = boxPos;
			this.time = 0;
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
		return GameMap.isCellFree(newPos) && GameMap.isPositionOccupiedToTime(newPos, time);
	}
	
	private boolean isLegalPush(Position agentPos, Position boxPos, char dir, int time) throws Exception {
		Position newPos = newPosInDirection(boxPos, dir);
		return GameMap.isCellFree(newPos) && !agentPos.equals(newPos) && GameMap.isPositionOccupiedToTime(newPos, time);
	}
	
	private boolean isLegalPull(Position agentPos, Position boxPos, char dir, int time) throws Exception {
		Position newPos = newPosInDirection(agentPos, dir);
		return GameMap.isCellFree(newPos) && !boxPos.equals(newPos) && GameMap.isPositionOccupiedToTime(newPos, time);
	}
	
	private TreeSet<PosNode> makeMove(TreeSet<PosNode> frontier, PosNode node, char dir, int time) throws Exception {
		ArrayList<Type> tmp = new ArrayList<Type>(); 
		if (isLegalMove(node.pos, dir, time)) {
			tmp.addAll(node.moves);
			Position newPos = newPosInDirection(node.pos, dir);
			tmp.add(new Type(node.pos, newPos, dir));
			frontier.add(new PosNode(newPos, tmp, node.boxPos, time));
		}
		frontier.add(new PosNode(node.pos, node.moves, node.boxPos, time));
		return frontier;
	}
	
	private TreeSet<PosBoxNode> makePush(TreeSet<PosBoxNode> frontier, PosBoxNode node, char dir, int time) throws Exception {
		ArrayList<Type> tmp = new ArrayList<Type>(); 
		Position newPos = newPosInDirection(node.boxPos, dir);
		if (isLegalPush(node.pos, node.boxPos, dir, time)) {
			tmp.addAll(node.moves);
			char agentDir = positionsToDir(node.pos, node.boxPos); 
			tmp.add(new Type(node.pos, node.boxPos, node.boxPos, newPos, TypeNum.PUS, agentDir, dir));
			frontier.add(new PosBoxNode(node.boxPos, tmp, newPos, node.goalPos, time));
		}
		//if (!GameMap.isPositionOccupiedToTime(newPos, time)) {
			frontier.add(new PosBoxNode(node.pos, node.moves, node.boxPos, node.goalPos, time)); //NoOp action
		//}
		return frontier;
	}
	
	private TreeSet<PosBoxNode> makePull(TreeSet<PosBoxNode> frontier, PosBoxNode node, char dir, int time) throws Exception {
		ArrayList<Type> tmp = new ArrayList<Type>(); 
		Position newPos = newPosInDirection(node.pos, dir);
		if (isLegalPull(node.pos, node.boxPos, dir, time)) {
			tmp.addAll(node.moves);
			char boxDir = positionsToDir(node.pos, node.boxPos);
			tmp.add(new Type(node.pos, newPos, node.boxPos, node.pos, TypeNum.PUL, dir, boxDir));
			frontier.add(new PosBoxNode(newPos, tmp, node.pos, node.goalPos, time));
		}
		//if (!GameMap.isPositionOccupiedToTime(newPos, time)) {
			frontier.add(new PosBoxNode(node.pos, node.moves, node.boxPos, node.goalPos, time)); //NoOp action
		//}
		return frontier;
	}
	
	private TreeSet<PosNode> moveExplore(TreeSet<PosNode> frontier, PosNode node, int time) throws Exception {
		//ArrayList<Character> tmp;
		frontier = makeMove(frontier, node, 'E', time);
		frontier = makeMove(frontier, node, 'W', time);
		frontier = makeMove(frontier, node, 'S', time);
		frontier = makeMove(frontier, node, 'N', time);
		return frontier;
	}

	private TreeSet<PosBoxNode> moveBoxExplore(TreeSet<PosBoxNode> boxFrontier, PosBoxNode node, int time) throws Exception {
		boxFrontier = makePush(boxFrontier, node, 'E', time);
		boxFrontier = makePush(boxFrontier, node, 'W', time);
		boxFrontier = makePush(boxFrontier, node, 'S', time);
		boxFrontier = makePush(boxFrontier, node, 'N', time);
		boxFrontier = makePull(boxFrontier, node, 'E', time);
		boxFrontier = makePull(boxFrontier, node, 'W', time);
		boxFrontier = makePull(boxFrontier, node, 'S', time);
		boxFrontier = makePull(boxFrontier, node, 'N', time);
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
	
	///////////////////////////////////////////////////////////// The function that executes it all!
	public Plan createPlan() throws Exception {
		//final Goal goal = GameMap.getUnsolvedGoal();
		final JobManager.Job job = GameMap.jobManager.getPriorityJob();
		Plan thePlan = new Plan();
		
		Boolean boxFound = false;

		System.err.println("Goal pos = (" + job.jobPos.x + "," + job.jobPos.y + ")");
		if (job.jobType == 'g') {
			//Find box that can be used (Currently only finds one. Doesn't find best (closest) box (still only eucledian distance available. Chosen best box can still be bad).)
			Position boxPosition = new Position(-1,-1);
			for (int x = 0; x < GameMap.size()[0]; x++) {
				for (int y = 0; y < GameMap.size()[1]; y++) {
					 if (GameMap.BoxAt(new Position(x,y)) == Character.toUpperCase(job.goal)) {
						boxPosition = new Position(x,y);
						System.err.println("Box pos = (" + x + "," + y + ")");
						boxFound = true;
						break;
					 }
				}
			}
			
			if (boxFound) {
				//Find path to box
					int planTime = 1;
					TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
					//TreeSet<PosNode> exploredNodes = new TreeSet< PosNode >(new PosNodeComp());;
					Position agentEndPosition = new Position(-1,-1);
					ArrayList<Type> resultMoves = new ArrayList<Type>();
					frontier.add(new PosNode(position, boxPosition));
					while (!frontier.isEmpty()) {
						PosNode node = frontier.pollFirst();
						//System.err.println("Check node. Moves are = " + node.moves + ", and position is (" + node.pos.x + "," + node.pos.y + ")");
						if (node.pos.nextTo(boxPosition)) { //Next to box?
							resultMoves = node.moves;
							agentEndPosition = node.pos;
							break;
						}
						
						frontier = moveExplore(frontier, node, planTime);
						planTime++;
					}
					
					if (frontier.isEmpty()) {
						System.err.println("Can't move to box!");
					}
					
				//Find path that moves box on top of goal. (We assume we are next to box initially).				
					TreeSet<PosBoxNode> boxFrontier = new TreeSet< PosBoxNode >(new PosBoxNodeComp());;
					ArrayList<Type> resultBoxMoves = new ArrayList<Type>();
					
					boxFrontier.add(new PosBoxNode(agentEndPosition, boxPosition, job.jobPos));
					
					while (!boxFrontier.isEmpty()) {
						PosBoxNode node = boxFrontier.pollFirst();
						if (node.boxPos.equals(job.jobPos)) { //On goal?
							resultBoxMoves = node.moves;
							break;
						}
						boxFrontier = moveBoxExplore(boxFrontier, node, planTime);
					}
					
					if (boxFrontier.isEmpty()) {
						System.err.println("Can't move box to goal!");
						//TODO: Submit job to be done. Move box out of the way.
							//Find position of box to move
								//What if there are more than one box?
									//Choose first one since it was encountered first = best heuristic
					}
					
				//Create list of moves for creating plan. Also create bounds
					int movTime = 0;
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
						Move mov = new Move(movTime, typ);
						
						if (typ.type == TypeNum.MOV) {
							moveN = (typ.l1.y > typ.l2.y) ? (typ.l1.y > moveN) ? typ.l1.y : moveN : (typ.l2.y > moveN) ? typ.l2.y : moveN ;
							moveS = (typ.l1.y < typ.l2.y) ? (typ.l1.y < moveS) ? typ.l1.y : moveS : (typ.l2.y < moveS) ? typ.l2.y : moveS ;
							moveE = (typ.l1.x > typ.l2.x) ? (typ.l1.x > moveE) ? typ.l1.x : moveE : (typ.l2.x > moveE) ? typ.l2.x : moveE ;
							moveW = (typ.l1.x < typ.l2.x) ? (typ.l1.x < moveW) ? typ.l1.x : moveW : (typ.l2.x < moveW) ? typ.l2.x : moveW ;
						}
						
						moves.add(mov);
						movTime++;
					}
					movTime = 0;
					System.err.println("Move Bounds (N,S,E,W) = (" + moveN + ", " + moveS + ", " + moveE + ", " + moveW + ")");
					
					for (Type typ : resultBoxMoves) { 
						Move mov = new Move(movTime, typ);
						
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
						movTime++;
					}
					
					System.err.println("boxMove Bounds (N,S,E,W) = (" + boxMoveN + ", " + boxMoveS + ", " + boxMoveE + ", " + boxMoveW + ")");
					System.err.println("Sizes: m = " + moves.size() + ", bm = " + boxMoves.size());
				
				//Create Plan
					Plan.SubPlan partOne = thePlan.new SubPlan(moveN, moveE, moveS, moveW, moves);
					Plan.SubPlan partTwo = thePlan.new SubPlan(boxMoveN, boxMoveE, boxMoveS, boxMoveW, boxMoves);
					
					thePlan.addSubplan(partOne);
					thePlan.addSubplan(partTwo);
				
			} else {
				System.err.println("box not found");
			}
		} //else if (job.type == assistMoveBox) {
			//This is given to the agent if it needs to move a box out of the way for another agent. That is, an agent is stuck behind a box and cannot move.
			//It is given a box and another plan. It should find a position close to the box that is not on the other agents plan.
			//final Position boxPosition; //The box to move
			//Position goalPosition //The position we want to move the box to
			//Figure out what desired position we want to move it to
			
			//Find path to box
			
			//Move box to desired position
			
			//Path to box
		//} else if (job.type == assistMove) {
			//Agent is in the way and needs to move out of the way
			//Position desiredPosition;
			//Find our desired position
			
			//Path to the position
			
			//Find a 
			
		//}
		System.err.println("Returning plan");
		return thePlan;
	}
}
