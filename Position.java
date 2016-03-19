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
}