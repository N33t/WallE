package source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import source.Position;
import source.TypeNum;

public class Type {
	public TypeNum type;
	public Position l1, l2, l3, l4;
	public char agentDir;
	public char boxDir;
	
	public Type(Position waitLocation) {
		this.type = TypeNum.NOP;
		this.l1 = waitLocation;
	}
	
	public Type(Position agentFrom, Position agentTo, char agentDir) {
		this.type = TypeNum.MOV;
		this.l1 = agentFrom;
		this.l2 = agentTo;
		this.agentDir = agentDir;
	}
	
	public Type(Position aFrom, Position aTo,
				Position bFrom, Position bTo,
				TypeNum tp, char agentDir, char boxDir)
	{
		this.l1 = aFrom;
		this.l2 = aTo;
		this.l3 = bFrom;
		this.l4 = bTo;
		this.type = tp;
		this.agentDir = agentDir;
		this.boxDir = boxDir;
	}
}
