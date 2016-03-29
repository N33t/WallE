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
		return 3*Math.sqrt(a + b);
	}

	public void run() {
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////// TO DO: REWTIRE AND ADD CHECK FOR ILLEGAL MOVE/////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Plan firstPlan;
		final Goal goal = GameMap.getUnsolvedGoal();
		
		Boolean boxFound = false;

		System.err.println("Goal pos = (" + goal.pos.x + "," + goal.pos.y + ")");
		
		//Find box that can be used (Currently only finds one.)
		Position tempBoxPosition = new Position(-1,-1);
		for (int x = 0; x < GameMap.size()[0]; x++) {
			for (int y = 0; y < GameMap.size()[1]; y++) {
				 if (GameMap.BoxAt(new Position(x,y)) == Character.toUpperCase(goal.name)) {
					tempBoxPosition = new Position(x,y);
					System.err.println("Box pos = (" + x + "," + y + ")");
					boxFound = true;
					break;
				 }
			}
		}
		
		if (boxFound) {
			final Position boxPosition = tempBoxPosition;
			// Create list of directions the box has to be moved in, in order to get to goal
			// Will look something like [N, N, E, N, E, E, S]. It's a char list.
			
			// Create list of direction the agent has to move in order to get to an adjacent square to the box.
			// Same structures as with box
			
				class PosNode {
					public Position pos;
					public ArrayList<Character> moves;
					
					@Override
					public boolean equals(Object other) {
						PosNode ptr = (PosNode) other;
						return (pos.x == ptr.pos.x && pos.y == ptr.pos.y);
					}
					
					public PosNode(Position pos, ArrayList<Character> moves) {
						this.pos = pos;
						this.moves = moves;
					}
					
					public PosNode(Position pos) {
						this.pos = pos;
						this.moves = new ArrayList<Character>();
					}
				}
				
				class PosNodeComp implements Comparator<PosNode>{
				
					@Override
					public int compare(PosNode p1, PosNode p2) {
						return heuristic(p1.pos, boxPosition) > heuristic(p2.pos, boxPosition) ? 1 : -1;
					}
				}

				//Do A* search
				
				ArrayList<PosNode> exploredPositions = new ArrayList<PosNode>();
				//ArrayList<PosNode> frontier = new ArrayList<PosNode>();
				
				//The "min-heap" that contains our PosNodes.
				TreeSet<PosNode> frontier = new TreeSet< PosNode >(new PosNodeComp());;
				
				frontier.add(new PosNode(position));
				
				Position agentEndPosition = new Position(-1,-1);
				
				ArrayList<Character> resultMoves = new ArrayList<Character>();
				while (!frontier.isEmpty()) {
					PosNode pos = frontier.pollFirst();
					System.err.println("Check node. Moves are = " + pos.moves + ", and position is (" + pos.pos.x + "," + pos.pos.y + ")");
					ArrayList<Character> tmp;
					exploredPositions.add(pos);
					if (Math.abs(pos.pos.x - boxPosition.x) + Math.abs(pos.pos.y - boxPosition.y) <= 1) { //Next to box?
						resultMoves = pos.moves;
						agentEndPosition = pos.pos;
						break;
					}
					if (GameMap.isCellFree(new Position(pos.pos.x + 1, pos.pos.y)) && !exploredPositions.contains(new PosNode(new Position(pos.pos.x + 1, pos.pos.y)))) {
						tmp = new ArrayList<Character>(); 
						tmp.addAll(pos.moves);
						tmp.add('E');
						frontier.add(new PosNode(new Position(pos.pos.x + 1, pos.pos.y), tmp));
					}
					if (GameMap.isCellFree(new Position(pos.pos.x - 1, pos.pos.y)) && !exploredPositions.contains(new PosNode(new Position(pos.pos.x - 1, pos.pos.y)))) {
						tmp = new ArrayList<Character>(); 
						tmp.addAll(pos.moves);
						tmp.add('W');
						frontier.add(new PosNode(new Position(pos.pos.x - 1, pos.pos.y), tmp));
					}
					if (GameMap.isCellFree(new Position(pos.pos.x, pos.pos.y + 1)) && !exploredPositions.contains(new PosNode(new Position(pos.pos.x, pos.pos.y + 1)))) {
						tmp = new ArrayList<Character>(); 
						tmp.addAll(pos.moves);
						tmp.add('S');
						frontier.add(new PosNode(new Position(pos.pos.x, pos.pos.y + 1), tmp));
					}
					if (GameMap.isCellFree(new Position(pos.pos.x, pos.pos.y - 1)) && !exploredPositions.contains(new PosNode(new Position(pos.pos.x, pos.pos.y - 1)))) {
						tmp = new ArrayList<Character>(); 
						tmp.addAll(pos.moves);
						tmp.add('N');
						frontier.add(new PosNode(new Position(pos.pos.x, pos.pos.y - 1), tmp));
					}
				}
				
				System.err.println("result moves = " + resultMoves);
			
			// Convert agents path to a list of moves
			
			// Based on agents position next to box, convert box-path to a list of moves. We assume agent is next to box.
				//Result moves will be either Push or Pull and a direction. Example: [PushE, PushW, PullS, PullW]
				
				class PosBoxNode {
					public Position pos;
					public Position boxPos;
					public ArrayList<String> moves;
					
					@Override
					public boolean equals(Object other) {
						PosBoxNode ptr = (PosBoxNode) other;
						//System.err.println("This = (" + this.pos.x + "," + this.pos.y + ")" + " other = (" + other.pos.x + "," + other.pos.y + ")");
						//System.err.println("This = (" + this.boxPos.x + "," + this.boxPos.y + ")" + " other = (" + other.boxPos.x + "," + other.boxPos.y + ")");
						return (this.pos.x == ptr.pos.x && this.pos.y == ptr.pos.y && this.boxPos.x == ptr.boxPos.x && this.boxPos.y == ptr.boxPos.y );
					}
					
					public PosBoxNode(Position pos, ArrayList<String> moves, Position boxPos) {
						this.pos = pos;
						this.moves = moves;
						this.boxPos = boxPos;
					}
					
					public PosBoxNode(Position pos, Position boxPos) {
						this.pos = pos;
						this.moves = new ArrayList<String>();
						this.boxPos = boxPos;
					}
				}
				
				class PosBoxNodeComp implements Comparator<PosBoxNode> {
				
					@Override
					public int compare(PosBoxNode p1, PosBoxNode p2) {
						return heuristic(p1.pos, goal.pos) > heuristic(p2.pos, goal.pos) ? 1 : -1;
					}
				}
				
				//Do A* search
				
				ArrayList<PosBoxNode> exploredBoxPositions = new ArrayList<PosBoxNode>();
				TreeSet<PosBoxNode> boxFrontier = new TreeSet< PosBoxNode >(new PosBoxNodeComp());;
				
				//Add initial node
				if (agentEndPosition.x - boxPosition.x == 0 && agentEndPosition.y - boxPosition.y == 1) {
					boxFrontier.add(new PosBoxNode(agentEndPosition, new Position(agentEndPosition.x, agentEndPosition.y + 1)));
				} else if (agentEndPosition.x - boxPosition.x == 0 && agentEndPosition.y - boxPosition.y == -1) {
					boxFrontier.add(new PosBoxNode(agentEndPosition, new Position(agentEndPosition.x, agentEndPosition.y - 1)));
				} else if (agentEndPosition.x - boxPosition.x == 1 && agentEndPosition.y - boxPosition.y == 0) {
					boxFrontier.add(new PosBoxNode(agentEndPosition, new Position(agentEndPosition.x - 1, agentEndPosition.y)));
				} else if (agentEndPosition.x - boxPosition.x == -1 && agentEndPosition.y - boxPosition.y == 0) {
					boxFrontier.add(new PosBoxNode(agentEndPosition, new Position(agentEndPosition.x + 1, agentEndPosition.y)));
				} else {
					System.err.println("Initial boxPosition Fail");
				}
				
				ArrayList<String> resultBoxMoves = new ArrayList<String>();
				while (!boxFrontier.isEmpty()) {
					PosBoxNode node = boxFrontier.pollFirst();
					//System.err.println("Check boxNode. Moves are = " + node.moves + ", and position is (" + node.pos.x + "," + node.pos.y + ")");
					System.err.println("goal pos = (" + goal.pos.x + "," + goal.pos.y + ")");
					System.err.println("Box pos = (" + node.boxPos.x + "," + node.boxPos.y + ")");
					System.err.println("explored Amount = " + exploredBoxPositions.size());
					ArrayList<String> tmp;
					exploredBoxPositions.add(node);
					if (node.boxPos.x == goal.pos.x && node.boxPos.y == goal.pos.y) { //On goal?
						System.err.println("Box Done");
						resultBoxMoves = node.moves;
						break;
					}
					if (GameMap.isCellFree(new Position(node.boxPos.x + 1, node.boxPos.y)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), new Position(node.boxPos.x + 1, node.boxPos.y)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PushE");
						boxFrontier.add(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), tmp, new Position(node.boxPos.x + 1, node.boxPos.y)));
					}
					if (GameMap.isCellFree(new Position(node.boxPos.x - 1, node.boxPos.y)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), new Position(node.boxPos.x - 1, node.boxPos.y)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PushW");
						boxFrontier.add(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), tmp, new Position(node.boxPos.x - 1, node.boxPos.y)));
					}
					if (GameMap.isCellFree(new Position(node.boxPos.x, node.boxPos.y + 1)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), new Position(node.boxPos.x, node.boxPos.y + 1)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PushS");
						boxFrontier.add(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), tmp, new Position(node.boxPos.x, node.boxPos.y + 1)));
					}
					if (GameMap.isCellFree(new Position(node.boxPos.x, node.boxPos.y - 1)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), new Position(node.boxPos.x, node.boxPos.y - 1)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PushN");
						boxFrontier.add(new PosBoxNode(new Position(node.boxPos.x, node.boxPos.y), tmp, new Position(node.boxPos.x, node.boxPos.y - 1)));
					}
					if (GameMap.isCellFree(new Position(node.pos.x + 1, node.pos.y)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.pos.x + 1, node.pos.y), new Position(node.pos.x, node.pos.y)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PullE");
						boxFrontier.add(new PosBoxNode(new Position(node.pos.x + 1, node.pos.y), tmp, new Position(node.pos.x, node.pos.y)));
					}
					if (GameMap.isCellFree(new Position(node.pos.x - 1, node.pos.y)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.pos.x - 1, node.pos.y), new Position(node.pos.x, node.pos.y)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PullW");
						boxFrontier.add(new PosBoxNode(new Position(node.pos.x - 1, node.pos.y), tmp, new Position(node.pos.x, node.pos.y)));
					}
					if (GameMap.isCellFree(new Position(node.pos.x, node.pos.y + 1)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.pos.x, node.pos.y + 1), new Position(node.pos.x, node.pos.y)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PullS");
						boxFrontier.add(new PosBoxNode(new Position(node.pos.x, node.pos.y + 1), tmp, new Position(node.pos.x, node.pos.y)));
					}
					if (GameMap.isCellFree(new Position(node.pos.x, node.pos.y - 1)) && !exploredBoxPositions.contains(new PosBoxNode(new Position(node.pos.x, node.pos.y - 1), new Position(node.pos.x, node.pos.y)))) {
						tmp = new ArrayList<String>(); 
						tmp.addAll(node.moves);
						tmp.add("PullN");
						boxFrontier.add(new PosBoxNode(new Position(node.pos.x, node.pos.y - 1), tmp, new Position(node.pos.x, node.pos.y)));
					}
				}
				
				//if (GameMap.isCellFree(new Position(pos.pos.x, pos.pos.y - 1)) && !exploredPositions.contains(new PosNode(new Position(pos.pos.x, pos.pos.y - 1)))) {
				
				System.err.println("result box moves = " + resultBoxMoves);
				
			
		} else {
			System.err.println("box not found");
		}
	}

	//private int heuristic() {
	//	double sum = 0;
	//	
	//	for (int i = 0; i<goals.size(); i++) {
	//		Coord goal = goals.get(i);
	//		//Find corresponding box
	//		double shortestLength = Integer.MAX_VALUE;
	//		double robotDist = 0;
	//		for (int k = 0; k < n.boxes.length; k++) {
	//			for (int l = 0; l < n.boxes[0].length; l++) {
	//				if ( n.boxes[k][l] == goal.chr-32) {
	//					//match! calculate length and store if it's shorter
	//					double length = Math.abs(Math.sqrt(Math.pow(goal.x-k,2) + Math.pow(goal.y-l,2)));
	//					if (length < shortestLength) { 
	//						shortestLength = length;
	//						if (goal.x!=k || goal.y!=l) {
	//							double lengthToRobot = 100*Math.abs(Math.sqrt(Math.pow(n.agentRow-k,2) + Math.pow(n.agentCol-l,2)));
	//							robotDist = lengthToRobot;
	//						}
	//						else
	//						{
	//							shortestLength = 0;
	//							robotDist = 0;
	//						}
	//					}
	//				}
	//			}
	//		}
	//		sum += shortestLength + robotDist;
	//	}
	//	return (int) Math.round(sum);
	//}
	//
	////Attempt to find plan to satisfy


	//Find path that satisfies plan


}
