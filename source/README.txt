>****************<
>* HAL 9000 AKA *<
>*    WALL-E    *<
>****************<

AI Programming Project.

Current implementation goals :-
		
	-> Plan Maker
		-> More intelligent (Nikolaj)
			-> Add possibility for all types of jobs
		-> Re-planner
		-> Clean up (Nikolaj)
		
	-> Main (plan-maker?)
		-> Make plans until entire level is solved. Don't just create one.
		-> Final plan-evaluation. Evaluate all plans (penalty points for some bad plans?) (Djoni)
		
	-> GameMap
		-> jobs
			-> Heuristic for agents. Give most appropriate job to agent
			-> Pre-condition, post-condition
		-> Function to delete plans
		-> Analyse map to figure out where to "store" agents and boxes if they are not needed (Samuel)
		-> When printing plans, print them for each agent. Currently, it assumes each agent submits at most one plan. That is wrong, fix.
		-> Update boxCharacters and boxPositions lists
	
	-> Conflicts
		-> Conflict Resolution (Flowchart) (Carl)
		-> Detection (how? Where? - Hard conflict detection while building plan)
		
------------------How to run

1. Compile java code
Navigate to the folder containing source files and execute command
javac *.java

2. Start server
Navigate to the folder containing your server.jar and execute command
java -jar server.jar -l levels/SAsimple1.lvl -c "java source.WallE" -g

The path to the level can be changed for different levels.
The code in quotation marks should be the code for running WallE. You may need to specify a path depending on where it is located.


--------------Conflicts
---Hard conflicts
- Box in the way
- Agent in the way
- Effect of plan destroys pre-condition of other plan

---Soft conflicts
- Delays

---------------- Formating for outputting of comands
They are executed step by step. All agents need to make a move in a step
A step is enclosed by "[]". Inside these, each agents action for that step is. They are seperated by comma.
Example is:
[Move(E), Push(E,E), Move(N)]

In this level, there are 3 agents. The first agent moves east, the second pushes east, and the last one moves north.
When we have created all actions for a step, execute them with System.out.println().
After each step, we need to flush the buffer.
Example code:
System.out.println("[Move(E), Move(N)]");
System.out.flush();
System.out.println("[Move(E), Push(E,E)]");
System.out.flush();

This code executes two steps, with each agent taking an action in each step.

For any Push action, the first direction boxes position relative to the agent. The second direction is the direction it pushes the box in. (ie. Push(E,S). The box is located east of the agent and it pushes the box south)
For any pull action, the first direction is direction the agent moves, the second direction is the boxes position relative to the agent. (ie. Pull(S,E). The box is located to the east of the agent and the agent moves south)


