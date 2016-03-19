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
import source.Type;

public class Move {

	public int time;
	public Type type;

	public Move(int t, Type tp) {
		this.time = t;
		this.type = tp;
	}
}
