import java.awt.List;
import java.awt.Point;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Search for shortest paths between start and end points on a circuit board
 * as read from an input file using either a stack or queue as the underlying
 * search state storage structure and displaying output to the console or to
 * a GUI according to options specified via command-line arguments.
 * 
 * @author mvail
 */
public class CircuitTracer {

	/** launch the program
	 * @param args three required arguments:
	 *  first arg: -s for stack or -q for queue
	 *  second arg: -c for console output or -g for GUI output
	 *  third arg: input file name 
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			printUsage();
			System.exit(1);
		}
		try {
			new CircuitTracer(args); //create this with args
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/** Print instructions for running CircuitTracer from the command line. */
	private static void printUsage() {
		// any command line args
		System.out.println("Invaild command line arguments");
		System.out.println("Usage: java CircuitTracker arg1 arg2 fileName ");
		System.out.println("\narg1:\n-s -- use a stack for storage\n-q -- use a queue for storage");
		System.out.println("\narg2:\n-c -- run program in console mode\n-g -- run program in GUI mode");
		System.out.println("\nfileName:\ninput file name");
	}
	
	/** 
	 * Set up the CircuitBoard and all other components based on command
	 * line arguments.
	 * 
	 * @param args command line arguments passed through from main()
	 */
	private CircuitTracer(String[] args) {
		boolean stackC = false;
		boolean queueC = false;
		String argZero = args[0];
		if(argZero.charAt(0) == '-' && argZero.charAt(1) == 's' || argZero.charAt(1) == 'q')
		{
			if(argZero.charAt(1) == 's')
			{
				stackC = true;
				//System.out.println("Stack");
			}
			else if(argZero.charAt(1) == 'q')
			{
				queueC = true;
				//System.out.println("Queue");
			}
		}
		else
		{
			printUsage();
			System.exit(1);
		}
		boolean con = false;
		boolean gui = false;
		if(args[1].charAt(0) == '-' && args[1].charAt(1) == 'c' || args[1].charAt(1) == 'g')
		{
			if(args[1].charAt(1) == 'c')
			{
				con = true;
				//System.out.println("Console");
			}
			else if(args[1].charAt(1) == 'g')
			{
				con = true;
				System.out.println("GUI not available at the moment procedding in the console");
			}
		}
		else
		{
			printUsage();
			System.exit(1);
		}
			
		String fileName = args[2];
		
		ArrayList<TraceState> bestPath = new ArrayList<TraceState>();
		Point starting;
		int row;
		int col;
		try {
			CircuitBoard circuitBoard = new CircuitBoard(fileName);
			row = circuitBoard.numRows();
			col = circuitBoard.numCols();
			//System.out.println(circuitBoard);
			if(stackC) //stack
			{
				Stack<TraceState> stack = new Stack<TraceState>();
				starting = circuitBoard.getStartingPoint();
				//System.out.println("Starting: " + starting + "\n");
				
				//test if above the starting is empty
				if(starting.y != 0)
				{
					if(circuitBoard.isOpen(starting.y - 1, starting.x))
					{
						//System.out.println("Above");
						TraceState newTrace = new TraceState(circuitBoard, starting.y - 1, starting.x);
						stack.push(new TraceState(circuitBoard, starting.y - 1, starting.x));
						//System.out.println(newTrace);
					}
				}
				//testing if space to the right is open
				if(starting.x != circuitBoard.numCols() - 1)
				{
					if(circuitBoard.isOpen(starting.y, starting.x + 1))
					{
						//System.out.println("Right");
						TraceState newTrace = new TraceState(circuitBoard, starting.y, starting.x + 1);
						stack.push(new TraceState(circuitBoard, starting.y, starting.x + 1));
						//System.out.println(newTrace);
					}
				}
				
				//testing the spot under stating
				if(starting.y != circuitBoard.numRows() - 1)
				{
					if(circuitBoard.isOpen(starting.y + 1, starting.x))
					{
						//System.out.println("under");
						TraceState newTrace = new TraceState(circuitBoard, starting.y + 1, starting.x);
						stack.push(new TraceState(circuitBoard, starting.y + 1, starting.x));
						//System.out.println(newTrace);
					}
				}
				
				//checking the spot to the left
				if(starting.x != 0)
				{
					if(circuitBoard.isOpen(starting.y, starting.x - 1))
					{
						//System.out.println("Left");
						TraceState newTrace = new TraceState(circuitBoard, starting.y, starting.x - 1);
						stack.push(new TraceState(circuitBoard, starting.y, starting.x - 1));
						//System.out.println(newTrace);
					}
				}
				
				int pathLength;
				
				while(stack.empty() == false)
				{
					//pop the top traceState
					TraceState current = stack.pop();
					//check if complete
					if(current.isComplete())
					{
						//System.out.println("Path Complete!");
						//System.out.println("Path length: " + current.pathLength());
						//get path length
						pathLength = current.pathLength();
						//compare it to other is best path array
						if(bestPath.isEmpty())
						{
							bestPath.add(current);
						}
						else if(bestPath.get(0).pathLength() > pathLength)
						{
							//if better replace 
							bestPath.clear();
							bestPath.add(current);
						}
						else if(bestPath.get(0).pathLength() == pathLength)
						{
							//is = add
							bestPath.add(current);
						}
						//if worse move on
					}
					else
					{
						Point lastPoint = starting;
						lastPoint.x = current.getCol();
						lastPoint.y = current.getRow();
						//System.out.println(current);
				
						
						//checking above
						if(lastPoint.x != 0)
						{
							if(current.isOpen(lastPoint.y, lastPoint.x - 1))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y, lastPoint.x - 1);
								stack.push(new TraceState(current, lastPoint.y, lastPoint.x - 1));
							}
						}
						//check below
						if(lastPoint.y != row - 1)
						{
							if(current.isOpen(lastPoint.y + 1, lastPoint.x))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y + 1, lastPoint.x);
								stack.push(new TraceState(current, lastPoint.y + 1, lastPoint.x));
								//System.out.println(newTrace);
							}
						}
						//check to the left
						if(lastPoint.x != 0)
						{
							if(current.isOpen(lastPoint.y, lastPoint.x - 1))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y, lastPoint.x - 1);
								stack.push(new TraceState(current, lastPoint.y, lastPoint.x - 1));
								//System.out.println(newTrace);
							}
						}
						//check to the right
						if(lastPoint.x != col - 1)
						{							
							if(current.isOpen(lastPoint.y, lastPoint.x + 1))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y, lastPoint.x + 1);
								stack.push(new TraceState(current, lastPoint.y, lastPoint.x + 1));
								//System.out.println(newTrace);
							}
						}
					}
					
				}
				//getting the best path boards and print them to the consol 
				//array name is bestPath
				//System.out.println(bestPath.toString());
				for (int i = 0; i < bestPath.size(); i++)
				{
					System.out.println(bestPath.get(i));
				}
			}
			else //queue
			{
				Queue<TraceState> queue = new ArrayDeque<TraceState>();
				
				starting = circuitBoard.getStartingPoint();
				//System.out.println("Starting: " + starting + "\n");
				
				//test if above the starting is empty
				if(starting.y != 0)
				{
					if(circuitBoard.isOpen(starting.y - 1, starting.x))
					{
						//System.out.println("Above");
						TraceState newTrace = new TraceState(circuitBoard, starting.y - 1, starting.x);
				//		queue.add(new TraceState(circuitBoard, starting.y - 1, starting.x));
						queue.add(newTrace);
						//System.out.println(newTrace);
					}
				}
				//testing if space to the right is open
				if(starting.x != circuitBoard.numCols() - 1)
				{
					if(circuitBoard.isOpen(starting.y, starting.x + 1))
					{
						//System.out.println("Right");
						TraceState newTrace = new TraceState(circuitBoard, starting.y, starting.x + 1);
						queue.add(new TraceState(circuitBoard, starting.y, starting.x + 1));
						//System.out.println(newTrace);
					}
				}
				
				//testing the spot under stating
				if(starting.y != circuitBoard.numRows() - 1)
				{
					if(circuitBoard.isOpen(starting.y + 1, starting.x))
					{
						//System.out.println("under");
						TraceState newTrace = new TraceState(circuitBoard, starting.y + 1, starting.x);
						queue.add(new TraceState(circuitBoard, starting.y + 1, starting.x));
						//System.out.println(newTrace);
					}
				}
				
				//checking the spot to the left
				if(starting.x != 0)
				{
					if(circuitBoard.isOpen(starting.y, starting.x - 1))
					{
						//System.out.println("Left");
						TraceState newTrace = new TraceState(circuitBoard, starting.y, starting.x - 1);
						queue.add(new TraceState(circuitBoard, starting.y, starting.x - 1));
						//System.out.println(newTrace);
					}
				}
				
				int pathLength;
				
				while(queue.size() != 0)
				{
					//pop the top traceState
					TraceState current = queue.poll();
					//check if complete
					if(current.isComplete())
					{
						//System.out.println("Path Complete!");
						//System.out.println("Path length: " + current.pathLength());
						//get path length
						pathLength = current.pathLength();
						//compare it to other is best path array
						if(bestPath.isEmpty())
						{
							bestPath.add(current);
						}
						else if(bestPath.get(0).pathLength() > pathLength)
						{
							//if better replace 
							bestPath.clear();
							bestPath.add(current);
						}
						else if(bestPath.get(0).pathLength() == pathLength)
						{
							//is = add
							bestPath.add(current);
						}
						//if worse move on
					}
					else
					{
						Point lastPoint = starting;
						lastPoint.x = current.getCol();
						lastPoint.y = current.getRow();
						//System.out.println(current);
				
						
						//checking above
						if(lastPoint.x != 0)
						{
							if(current.isOpen(lastPoint.y, lastPoint.x - 1))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y, lastPoint.x - 1);
								queue.add(new TraceState(current, lastPoint.y, lastPoint.x - 1));
							}
						}
						//check below
						if(lastPoint.y != row - 1)
						{
							if(current.isOpen(lastPoint.y + 1, lastPoint.x))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y + 1, lastPoint.x);
								queue.add(new TraceState(current, lastPoint.y + 1, lastPoint.x));
								//System.out.println(newTrace);
							}
						}
						//check to the left
						if(lastPoint.x != 0)
						{
							if(current.isOpen(lastPoint.y, lastPoint.x - 1))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y, lastPoint.x - 1);
								queue.add(new TraceState(current, lastPoint.y, lastPoint.x - 1));
								//System.out.println(newTrace);
							}
						}
						//check to the right
						if(lastPoint.x != col - 1)
						{							
							if(current.isOpen(lastPoint.y, lastPoint.x + 1))
							{
								TraceState newTrace = new TraceState(current, lastPoint.y, lastPoint.x + 1);
								queue.add(new TraceState(current, lastPoint.y, lastPoint.x + 1));
								//System.out.println(newTrace);
							}
						}
					}	
			}
		}
			for (int i = 0; i < bestPath.size(); i++)
			{
				System.out.println(bestPath.get(i));
			}
			
		} catch (FileNotFoundException e) {
			System.out.println(fileName + " is an invalid file name");
		} 
		//TODO: run the search for best paths
	
		
		//TODO: output results to console or GUI, according to specified choice

	}
	
} // class CircuitTracer
