import java.util.*;

/**
 * This code sample demonstrates the implementation of Dijkstra's Algorithm in the form of A*. 
 * This implementation is used in a hexagonal maze represented by a 2D array
 * 
 * @author Chris
 *
 */
public class MyAstar extends Astar
{
	/* Declares the variables needed for the search */
	protected byte[] costOf = {1,2,3,8};
	public int[] DY = Hexagon.DY;
	public int[][] DX = Hexagon.DX;

	/**
	 * Default constructor
	 * @param rows Number of rows
	 * @param cols Number of columns
	 */
	public MyAstar(int rows, int cols) 
	{ 
		super(rows,cols); 
	}//Constructor

	/**
	 * Finds the best possible path from the starting coordinates to the ending coordinates
	 * @param sY Starting y
	 * @param sX Starting x
	 * @param tY Target y
	 * @param tX Target x
	 * @return Returns the coord node at the target coordinates pointing to the path
	 */
	public Coord search(int sY, int sX, int tY, int tX)
	{
		/* Sets up the variables needed for the Astar Algorithm */
		Coord current = null;
		int rows, cols;
		Coord[][] I = new Coord[ROWS][COLS];
		PriorityQueue<Coord> F = new PriorityQueue<Coord>();
		boolean stop = false;
		
		/* Adds the starting cell to the Frontier and Interior Lists */
		current = new Coord(sY,sX);
		current.dist = 0;
		current.cost = hexdist(sY,sX,tY,tX);
		
		I[sY][sX] = current;		
		F.add(current);
		
		/* Loop while not at destination and Frontier not empty */
		while(!stop && F.size()>0)
		{
			//Retrieve the next closest cell from the Frontier
			current = F.poll();
			rows = current.y; 
			cols = current.x;
			current.status = 2;
			
			I[rows][cols] = current;

			for(int i=0;i<6;i++)
			{
				Coord neighbor = new Coord(rows+DY[i],cols+DX[rows%2][i]);
				
				/* Adds the neighbor to the Frontier if it is within the bounds of the 2D array */
				if(neighbor.y>=0 && neighbor.y<ROWS && neighbor.x>=0 && neighbor.x<COLS && (I[neighbor.y][neighbor.x]==null || I[neighbor.y][neighbor.x].status==1))
				{					
					neighbor.dist = costOf[M[neighbor.y][neighbor.x]] +current.dist;
					neighbor.cost = neighbor.dist +hexdist(neighbor.y,neighbor.x,tY,tX);
					neighbor.prev = current;
					neighbor.status = 1;
					
					F.add(neighbor);
					
					/* Adds the cell to the interior */
					if(I[neighbor.y][neighbor.x]==null || I[neighbor.y][neighbor.x].cost>neighbor.cost)
						I[neighbor.y][neighbor.x] = neighbor;
				}//if	
			}//for
			if(current.y == tY && current.x == tX)
				stop = true;
		}
		return current;
	}//search
}//MyAstar