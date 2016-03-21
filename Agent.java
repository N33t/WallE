package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import source.*;


public class Agent implements Runnable {
	private int id;
	private Position position;

	public Agent(int id, Position position) {
		this.id = id;
		this.position = position;
	}

	public void run() {
		Plan firstPlan;
		Goal goal = GameMap.getUnsolvedGoal();
		
		Boolean boxFound = false;

		//Find box that can be used (Currently only finds one.)
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
			// Create list of directions the box has to be moved in, in order to get to goal
			// Will look something like [N, N, E, N, E, E, S]. It's a char list.
			
			// Create list of direction the agent has to move in order to get to an adjacent square to the box.
			// Same structures as with box
			
				class PosNode {
					public Position pos;
					public ArrayList<Character> moves;
					
					//@Override
					public Boolean equals(PosNode other) {
						return (pos.x == other.pos.x && pos.y == other.pos.y);
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

				//For now, do BFS
				
				ArrayList<PosNode> exploredPositions = new ArrayList<PosNode>();
				ArrayList<PosNode> frontier = new ArrayList<PosNode>();
				
				frontier.add(new PosNode(position));
				
				ArrayList<Character> resultMoves = new ArrayList<Character>();
				while (!frontier.isEmpty()) {
					PosNode pos = frontier.remove(0);
					System.err.println("Check node. Moves are = " + pos.moves + ", and position is (" + pos.pos.x + "," + pos.pos.y + ")");
					ArrayList<Character> tmp;
					exploredPositions.add(pos);
					if (Math.abs(pos.pos.x - boxPosition.x) + Math.abs(pos.pos.y - boxPosition.y) <= 1) { //Next to box?
						resultMoves = pos.moves;
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
			
			// Based on agents position next to box, convert box-path to a list of moves.
				
				
				//////////////////////
				//////////////////////TO DO: Implement heuristic og sorter Frontier baseret på den.
				//////////////////////
				
				
				// do same thing as above, but with all possible moves
			
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
