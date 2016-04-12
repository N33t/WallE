package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import source.Move;

public class Plan {
	
	private int score;
	
	public int getScore(){
		return this.score;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public ArrayList<SubPlan> subplans;
	
	public Plan() {
			subplans = new ArrayList<SubPlan>();
	}
	
	public void addSubplan(SubPlan subplan){
		this.subplans.add(subplan);
	}
	
	//Get move to the time t
	public Move getMoveToTime(int t){ 
		int time = 0;
		for(int i = 0; i < subplans.size(); i++){
			if((subplans.get(i).moves.size() + time) > t){
				return subplans.get(i).moves.get(t - time);
			}else{
				time += subplans.get(i).moves.size();
			}
		}	
		return null;	
	}
	
	class SubPlan {
		//Bounding box defined by
		public int top, right, bottom, left;
		public ArrayList<Move> moves = new ArrayList<Move>();
		public int start, stop;
		
		public SubPlan(int n, int e, int s, int w, ArrayList<Move> mvs) {
			this.top = n;
			this.right = e;
			this.bottom = s;
			this.left = w;
			moves.addAll(mvs);
			start = moves.get(0).time;
			stop = moves.get(moves.size()-1).time;
		}
		
	}
}
