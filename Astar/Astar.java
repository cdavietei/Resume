
import java.util.*;

/*
 *  ===============================================
 * Written by Dr. Chuck Liang, Hofstra University
 * ===============================================
 */

public class Astar 
{
	public static final int OPEN = 0;
	public static final int FOREST = 1;  
	public static final int DESERT = 2;
	public static final int WATER = 3;

	public static final int W = Hexagon.West;
	public static final int E = Hexagon.East;
	public static final int NW = Hexagon.NorthWest;
	public static final int SW = Hexagon.SouthWest;
	public static final int NE = Hexagon.NorthEast;
	public static final int SE = Hexagon.SouthEast;
	public static int[] DY = Hexagon.DY;
	public static int[][] DX = Hexagon.DX;

	public int ROWS, COLS; // size of map in array coords

	public int M[][];  // the map itself  M[y][x].val gives value

	// constructor
	public Astar(int r0, int c0)  // typically 32x44
	{
		M = new int[r0][c0];
		ROWS=r0;  COLS=c0;

		// generate random map  (initially all OPEN)
		int GENS = 12;  // number of generations
		double NFACTOR = 0.12;  //0.12;   tweak for best results
		double p, r, f, d;  // for random probability calculation
		int generation;  int i, j;
		for(generation=0;generation<GENS;generation++)
		{
			for(i=0;i<ROWS;i++) 
				for(j=0;j<COLS;j++)
				{
					p = 0.0035; // base probability factor
					f = 0.001;
					d = 0.00055;
					// calculate probability of water based on surrounding cells
					for(int k=0;k<6;k++)
					{
						int ni = i + DY[k], nj = j + DX[i%2][k];
						if (ni>=0 && ni<ROWS && nj>=0 && nj<COLS && M[ni][nj]==WATER) p+= NFACTOR;
						if (ni>=0 && ni<ROWS && nj>=0 && nj<COLS && M[ni][nj]==FOREST) f+= NFACTOR;
						if (ni>=0 && ni<ROWS && nj>=0 && nj<COLS && M[ni][nj]==DESERT) d+= NFACTOR;
					}
					r = Math.random();
					if (r<=p) M[i][j] = WATER;
					if(r<=f) M[i][j] = FOREST;
					if(r<=d) M[i][j] = DESERT;
				} // for each cell i, j
		} // for each generation

	} //constructor

	// determines euclidean distance between y1,x1 and y2,x2, rounds off
	public static int distance(int y1, int x1, int y2, int x2)
	{
		int dy = (y1-y2);  int dx = (x1-x2);
		return (int) (0.5 + Math.sqrt((dx*dx) +(dy*dy)));
	}

	// determines distance properly in hex coordinates:
	// distance is max of |dx|, |dy|, and |dx-dy|
	public static int hexdist(int y1, int x1, int y2, int x2)
	{
		int dx = x1-x2, dy = y1-y2;
		int dd = Math.abs(dx - dy);
		dy = Math.abs(dy);
		int max = Math.abs(dx);
		if (dy>max) max = dy;
		if (dd>max) max = dd;
		return max;
	}

	/*******  THE FOLLOWING IS THE METHOD YOU HAVE TO WRITE *******/

	// create path from from source to target, return end of path coord.
	public Coord search(int sy, int sx, int ty, int tx)
	{
		return null;  // this means there is no path
	}//search
	// You should override this function in a subclass.
}//astar

/* Here's the simplest subclass: 
import java.util.*;

public class myastar extends astar
{
    public myastar(int r, int c) { super(r,c); }
}
 */
