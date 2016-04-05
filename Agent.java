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
//Include check for previous plans)
//Add support for no-operation 
//Right now it chooses first box it finds. Choose "best" box instead (based on heuristic).
//Make plan based on resultant moves
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public class Agent implements Runnable {
	private int id;
	private Position position;

	public Agent(int id, Position position) {
		this.id = id;
		this.position = position;
	}
	
	public double heuristic(Position one, Position two) {
		double a = Math.pow(Math.abs(one.x - two.x),2);
		double b = Math.pow(Math.abs(one.y - two.y),2);
		return Math.sqrt(a + b);
	}
	
	///////////////////////////////////////////////////////////// Classes and their comparators
	private class PosNode {
		public Position pos;
		public ArrayList<Character> moves;
		private Position boxPos;
		
		@Override
		public boolean equals(Object other) {
			PosNode ptr = (PosNode) other;
			return (pos.x == ptr.pos.x && pos.y == ptr.pos.y);
		}
		
		public PosNode(Position pos, ArrayList<Character> moves, Position boxPos) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
		}
		
		public PosNode(Position pos, Position boxPos) {
			this.pos = pos;
			this.moves = new ArrayList<Character>();
			this.boxPos = boxPos;
		}
		
		public PosNode(Position pos) {
			this.pos = pos;
			this.moves = new ArrayList<Character>();
		}
	}
	
	private class PosNodeComp implements Comparator<PosNode>{
	
		@Override
		public int compare(PosNode p1, PosNode p2) {
			return heuristic(p1.pos, p1.boxPos) > heuristic(p2.pos, p2.boxPos) ? 1 : -1;
		}
	}
	
	private class PosBoxNode {
		public Position pos;
		public Position boxPos;
		public ArrayList<String> moves;
		private Position goalPos;
		
		@Override
		public boolean equals(Object other) {
			PosBoxNode ptr = (PosBoxNode) other;
			return (this.pos.x == ptr.pos.x && this.pos.y == ptr.pos.y && this.boxPos.x == ptr.boxPos.x && this.boxPos.y == ptr.boxPos.y );
		}
		
		public PosBoxNode(Position pos, ArrayList<String> moves, Position boxPos, Position goalPos) {
			this.pos = pos;
			this.moves = moves;
			this.boxPos = boxPos;
			this.goalPos = goalPos;
		}
		
		public PosBoxNode(Position pos, Position boxPos, Position goalPos) {
			this.pos = pos;
			this.moves = new ArrayList<String>();
			this.boxPos = boxPos;
			this.goalPos = goalPos;
		}
		
		public PosBoxNode(Position pos, Position boxPos) {
			this.pos = pos;
			this.moves = new ArrayList<String>();
			this.boxPos = boxPos;
		}
	}
	
	private class PosBoxNodeComp implements Comparator<PosBoxNode> {
	
		@Override
		public int compare(PosBoxNode p1, PosBoxNode p2) {
			return heuristic(p1.boxPos, p1.goalPos) > heuristic(p2.boxPos, p2.goalPos) ? 1 : -1;
		}
	}
	
	///////////////////////////////////////////////////////////// Private functions
	private Position newPosInDirection(Position pos, char dir) {
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
			System.err.println("newPosInDirection failed. Wrong direction. Given: '" + dir + "'");
		}
		return newPos;
	}
	
	private boolean isLegalMove(Position pos, char dir) {
		Position newPos = newPosInDirection(pos, dir);
		return GameMap.isCellFree(newPos);
	}
	
	private boolean isLegalPush(Position agentPos, Position boxPos, char dir) {
		Position newPos = newPosInDirection(boxPos, dir);
		return GameMap.isCellFree(newPos) && !agentPos.equals(newPos);
	}
	
	private boolean isLegalPull(Position agentPos, Position boxPos, char dir) {
		Position newPos = newPosInDirection(agentPos, dir);
		return GameMap.isCellFree(newPos) && !boxPos.equals(newPos);
	}
	
	private TreeSet<PosNode> makeMove(TreeSet<PosNode> frontier, PosNode node, char dir) {
		ArrayList<Character> tmp = new ArrayList<Character>(); 
		if (isLegalMove(node.pos, dir)) {
			tmp.addAll(node.moves);
			tmp.add(dir);
			frontier.add(new PosNode(newPosInDirection(node.pos, dir), tmp, node.boxPos));
		}
		return frontier;
	}
	
	private TreeSet<PosBoxNode> makePush(TreeSet<PosBoxNode> frontier, PosBoxNode node, char dir) {
		ArrayList<String> tmp = new ArrayList<String>(); 
		if (isLegalPush(node.pos, node.boxPos, dir)) {
			tmp.addAll(node.moves);
			tmp.add("push" + dir);
			frontier.add(new PosBoxNode(node.boxPos, tmp, newPosInDirection(node.boxPos, dir), node.goalPos));
		}
		return frontier;
	}
	
	private TreeSet<PosBoxNode> makePull(TreeSet<PosBoxNode> frontier, PosBoxNode node, char dir) {
		ArrayList<String> tmp = new ArrayList<String>(); 
		if (isLegalPull(node.pos, node.boxPos, dir)) {
			tmp.addAll(node.moves);
			tmp.add("pull" + dir);
			frontier.add(new PosBoxNode(newPosInDirection(node.pos, dir), tmp, node.pos, node.goalPos));
		}
		return frontier;
	}
	
	private TreeSet<PosNode> moveExplore(TreeSet<PosNode> frontier, PosNode node) {
		ArrayList<Character> tmp;
		frontier = makeMove(frontier, node, 'E');
		frontier = makeMove(frontier, node, 'W');
		frontier = makeMove(frontier, node, 'S');
		frontier = makeMove(frontier, node, 'N');
		return frontier;
	}

	private TreeSet<PosBoxNode> moveBoxExplore(TreeSet<PosBoxNode> boxFrontier, PosBoxNode node) {
		boxFrontier = makePush(boxFrontier, node, 'E');
		boxFrontier = makePush(boxFrontier, node, 'W');
		boxFrontier = makePush(boxFrontier, node, 'S');
		boxFrontier = makePush(boxFrontier, node, 'N');
		boxFrontier = makePull(boxFrontier, node, 'E');
		boxFrontier = makePull(boxFrontier, node, 'W');
		boxFrontier = makePull(boxFrontier, node, 'S');
		boxFrontier = makePull(boxFrontier, node, 'N');
		return boxFrontier;
	}
	
	///////////////////////////////////////////////////////////// The function that executes it all!
	public void run() {
		final Goal goal = GameMap.getUnsolvedGoal();
		
		Boolean boxFound = false;

		System.err.println("Goal pos = (" + goal.pos.x + "," + goal.pos.y + ")");
		
		//Find box that can be used (Currently only finds one. Doesn't find best (closest) box (still only eucledian distance available. Chosen best box can still be bad).)
		Position boxPosition = new Position(-1,-1);
		for (int x = 0; x < GameMap.size()[0]; x++) {
			for (int y = 0; y < GameMap.size()[1]; y++) {
				 if (GameMap.BoxAt(new Position(x,y)) == Character.toUpperCase(goal.name)) {
					boxPosition = new Position(x,y);
					System.err.println("Box pos = (" + x + "," + y + ")");
					boxFound = true;
					break;
				 }
			}
		}
		
		if (boxFound) {
			//Find path to box
				TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
				Position agentEndPosition = new Position(-1,-1);
				ArrayList<Character> resultMoves = new ArrayList<Character>();
				frontier.add(new PosNode(position, boxPosition));
				while (!frontier.isEmpty()) {
					PosNode node = frontier.pollFirst();
					//System.err.println("Check node. Moves are = " + node.moves + ", and position is (" + node.pos.x + "," + node.pos.y + ")");
					if (node.pos.nextTo(boxPosition)) { //Next to box?
						resultMoves = node.moves;
						agentEndPosition = node.pos;
						break;
					}
					frontier = moveExplore(frontier, node);
				}
				
				System.err.println("result moves = " + resultMoves);
			
			//Find path that moves box on top of goal. (We assume we are next to box initially).				
				TreeSet<PosBoxNode> boxFrontier = new TreeSet< PosBoxNode >(new PosBoxNodeComp());;
				ArrayList<String> resultBoxMoves = new ArrayList<String>();
				
				boxFrontier.add(new PosBoxNode(agentEndPosition, boxPosition, goal.pos));
				
				while (!boxFrontier.isEmpty()) {
					PosBoxNode node = boxFrontier.pollFirst();
					if (node.boxPos.equals(goal.pos)) { //On goal?
						resultBoxMoves = node.moves;
						break;
					}
					boxFrontier = moveBoxExplore(boxFrontier, node);
				}
				
				System.err.println("result box moves = " + resultBoxMoves);
				
			// TODO: Create plan based on the resulting moves
			
		} else {
			System.err.println("box not found");
		}
	}
}
