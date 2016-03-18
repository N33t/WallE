package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import source.Position;

public class Goal {
	public char name;
	public Position pos;

	public Goal(char name, Position pos) {
		this.name = name;
		this.pos = pos;
	}

	public Goal(Goal g){
		this.name = g.name;
		this.pos = g.pos;
	}

}