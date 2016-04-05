>****************<
>* HAL 9000 AKA *<
>*    WALL-E    *<
>****************<

AI Programming Project.

Current implementation goals :-

	-> Plan class
		--> SubPlan class
		--> Move Class
		--> Move Types
		
	-> Plan Maker
		-> Planner
		-> Re-planner
		-> Plan register
		
How to run

1. Compile java code
Navigate to the folder containing source files and execute command
javac *.java

2. Start server
Navigate to the folder containing your server.jar and execute command
java -jar server.jar -l levels/SAsimple1.lvl -c "java source.WallE" -g

The path to the level can be changed for different levels.
The code in quotation marks should be the code for running WallE. You may need to specify a path depending on where it is located.