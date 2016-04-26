package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import source.GameMap;
import source.Agent;


public class Position {
	public int x;
	public int y;
	
	public Position() {
		this.x = -1;
		this.y = -1;
	}
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		Position ptr = (Position) other;
		return (this.x == ptr.x && this.y == ptr.y);
	}
	
	@Override
	public int hashCode() {
		return 1000 * x + y;
	}
	
	public boolean nextTo(Position other) {
		//Are the two positions neighbours?
		return this.x == other.x ? Math.abs(this.y - other.y) <= 1 : this.y == other.y ? Math.abs(this.x - other.x) <= 1 : false ;
	}
	
	public String toString() {
		String str = "Position(" + x + "," + y + ")";
		return str;
	}
}