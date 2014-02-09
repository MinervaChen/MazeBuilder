// Minerva Chen, Max Peterson
// 11/5/2013
// MazeBuilder
//
// Creates and writes a solvable ASCII maze without cycles to a file. The dimensions
// of the maze are the height and width passed as command line parameters. The maze
// is generated randomly.
import java.io.*;
import java.util.*;

public class MazeBuilder {
   public static void main(String[] args) {
      checkArgs(args);
      int mazeHeight = Integer.parseInt(args[0]);
      int mazeWidth = Integer.parseInt(args[1]);
		PrintStream mazeStream = null;
		try {
      	mazeStream = new PrintStream(new File(args[2]));
		} 	catch (FileNotFoundException e) {
			System.err.println("error: " + args[2] + " not writeable.");
			System.exit(1);
		}
      Random r = new Random();
		// the maze is generated through a series of unions of initially disjoint sets.
		// each cell of the maze is initally represented by a set of one element, cells 
		// numbered 0 through mazeHeight * mazeWidth - 1. To create a path through the
		// maze, sets are unioned. Unioning 1 and 2, for example means a path from 1
		// to 2 now exists (and vice-versa). The maze is solvable when only one set 
		// remains, because then every cell is reachable from every other.
		// Cycles do not occur because a path is only created at a cell if those 
		// cells are disjoint sets at the time.
      DisjointSets maze = new MyDisjSets(mazeHeight * mazeWidth);
      Set<Integer> knockdowns = new HashSet<Integer>(); // the set of edges 
				// not included in the maze; the set of included edges is implicit   
      
		while (maze.numSets() > 1) {
         int cell = r.nextInt(mazeHeight * mazeWidth);
         List<Integer> disjNeighbors = getDisjNeighbors(cell, mazeHeight, mazeWidth, maze);
         if (!disjNeighbors.isEmpty()) {
				// negative target means the wall knocked down is either above or below
				// cell; positive target is left / right
            int target = disjNeighbors.get(r.nextInt(disjNeighbors.size())); 
            updateKnockdowns(knockdowns, cell, target, mazeHeight); 
            maze.union(maze.find(Math.abs(target)), maze.find(cell)); // make path
         }
      }
		writeMaze(mazeStream, knockdowns, mazeHeight, mazeWidth);
   }
	
	// adds the edge corresponding to the wall being knocked down (target) to the
	// set of edges not included in the maze (knockdowns). The cell and maze 
	// height are used to decide which wall should be knocked down.
	public static void updateKnockdowns(Set<Integer> knockdowns, int cell, int target, 
	int height) {	
		// the scheme with these walls / edges here is as follows: Suppose we're at
		// cell k of the maze. Then the left wall of the cell is wall k, the right 
		// wall is wall k + 1, the wall above is wall -k, and the wall below is 
		// wall (-k - height).
		if (target >= 0) // knocking down a wall to cell's right or cell's left
			knockdowns.add(cell - target > 0 ? cell : target);
		else // knocking down a wall above or below cell
			knockdowns.add(cell + target < 0 ? target : -cell);
	}
	
	// returns true if a path exists between cells m and n, false otherwise.
	public static boolean pathExists(int m, int n, DisjointSets maze) {
      return maze.find(m) == maze.find(n);
	}

	// returns a list of the cells sharing a border with cell. Cells which 
	// are currently reachable from cell are excluded from the list.
   public static List<Integer> getDisjNeighbors(int cell, int mazeHeight, 
   int mazeWidth, DisjointSets maze) {
      List<Integer> disjNeighbors = new ArrayList<Integer>();
      int aboveNeighbor = cell - mazeWidth;
      int belowNeighbor = cell + mazeWidth;
      int rightNeighbor = cell + 1;
      int leftNeighbor  = cell - 1; 
      if (aboveNeighbor > 0 && !pathExists(cell, aboveNeighbor, maze))
         disjNeighbors.add(-aboveNeighbor); // walls above and below a cell are
					// denoted by negative numbers; calling these neighbors negative here
					// helps later with the set knockdowns.
      if (belowNeighbor < mazeWidth * mazeHeight && !pathExists(cell, belowNeighbor, maze))
         disjNeighbors.add(-belowNeighbor);
      if (rightNeighbor % mazeWidth != 0 && rightNeighbor < mazeWidth * mazeHeight  
      && !pathExists(cell, rightNeighbor, maze)) // mod test false means we're on the edge
         disjNeighbors.add(rightNeighbor);
      if (cell % mazeWidth != 0 && leftNeighbor >= 0 
		&& !pathExists(cell, leftNeighbor, maze))
         disjNeighbors.add(leftNeighbor); 
      return disjNeighbors;
   }

	// writes ASCII maze of dimensions height and width to the given output file.
	// knockdowns is the set of edges that are not to be included in the maze.
   public static void writeMaze(PrintStream mazeStream, Set<Integer> knockdowns, 
   int height, int width) {
		// draw maze except lower border
      for (int i = 0; i < height; i++)	{
         for (int j = 0; j < width; j++) { 
            mazeStream.print("+");
				// negative elements in knockdowns denote walls above or below a cell
            if (!knockdowns.contains(-(j + i * width)))
               mazeStream.print("-");
            else
               mazeStream.print(" ");
         }
         mazeStream.println("+");
         for (int j = 0; j < width; j++)
				// Positive elements in knockdowns denote walls to the left or right of a 
				// cell. i + j test in if is to make the entrance to the maze be the 
				// upper left corner.
            if (!knockdowns.contains(j + i * width) && i + j != 0)
               mazeStream.print("| ");
            else 
               mazeStream.print("  ");
			if (i < height - 1) // in every case except last row
         	mazeStream.println("|");
			else 
				mazeStream.println(); // make maze exit in lower right corner
      }
		// draw bottom border
      for (int i = 0; i < width; i++)
			mazeStream.print("+-");
      mazeStream.println("+");
   }

	// makes sure there are exactly three command line arguments and that
	// the first two are positive integers. If not, prints error message to
	// stderr and exits failure.
   public static void checkArgs(String[] args) {
      if (args.length != 3) {
  			System.err.print("Illegal number of arguments! "); 
			System.err.println("usage: <integer: maze height> <integer: maze width>" +
					" <output file name>");
			System.exit(1);
		} else {
			Scanner checker = new Scanner(args[0] + " " + args[1]);
			for (int i = 0; i < 2; i++) {
				if (!checker.hasNextInt() || checker.nextInt() <= 0) {
					System.err.print("Illegal argument(s)! First two command line parameters " + 
							"must be positive integers (0 not allowed). ");
					System.exit(1);
				}
			}
		}
	}
}
