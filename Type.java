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


public enum TypeNum {
    NOP,
	MOV,
	PUS,
	PUL,
}

public class Type {
	public TypeNum type;
	public Position l1, l2, l3, l4;
	
	public Type(Position waitLocation) {
		this.type = NOP;
		this.l1 = waitLocation;
	}
	
	public Type(Position agentFrom, Position agentTo) {
		this.type = MOV;
		this.l1 = agentFrom;
		this.l2 = agentTo;
	}
	
	public Type(Position aFrom, Position aTo,
				Position bFrom, Position bTo,
				TypeNum tp)
	{
		this.l1 = aFrom;
		this.l2 = aTo;
		this.l3 = bFrom;
		this.l4 = bTo;
		this.type = tp;
	}
}
