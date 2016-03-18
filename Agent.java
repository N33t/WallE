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
		Goal goal = GameMap.getUnsolvedGoal();

		//Find box that can be used
		//ArrayList<Position> boxes = new ArrayList<Position>();
		//for (int x = 0; x < theMap.size()[0]; x++) {
		//		for (int y = 0; y < theMap.size()[1]; y++) {
		//			
		//	}
		//}

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
