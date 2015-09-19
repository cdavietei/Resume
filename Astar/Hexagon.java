
/*
 *  ===============================================
 * Written by Dr. Chuck Liang, Hofstra University
 * ===============================================
 */


import java.awt.Polygon;

/*   Picture hexagon oriented as follows:

     nw /\ ne
     w |  | e
     sw \/ se
*/

public class Hexagon extends Polygon
{
    //public Hexagon() { super(); }  // implicit constructor
    int x;        // graphical center coordinates of hexagon;
    int y;        // not to be confused with any array coordinates
    int rad;      // radius = side distance
    int hpdist;   // 1/2 distance from center to side

    // neighbors, directions and movement vectors
    public static final int West = 0;
    public static final int NorthWest = 1;
    public static final int NorthEast = 2;
    public static final int East = 3;
    public static final int SouthEast = 4;
    public static final int SouthWest = 5;
    //movement vectors for w,nw,ne,e,se,sw, DX different for even,odd rows
    public static int[] DY = {0,-1,-1,0,1,1};
    public static int[][] DX = {{-1,-1,0,1,0,-1},{-1,0,1,1,1,0}};
    // example: y+DY[4], x+DX[y%2][4] give coordinates of southeast neighbor

    public static int calchpdist(int radius) // rad determines radius of hex
    {
	double d = Math.sqrt(3.0*radius*radius/4.0);
	int hpdist = (int)(d+0.5);
	return hpdist;
    }

    // principal constructor
    public Hexagon(int x, int y, int r) // r is radius  (x coord first!)
    {
	super();  // create base instance
	this.x = x; this.y=y;  this.rad = r;
	hpdist = calchpdist(r);
	addPoint(x-hpdist,y-r/2); // left upper corner
	addPoint(x,y-r); // top tip
	addPoint(x+hpdist,y-r/2); // right upper corner
	addPoint(x+hpdist,y+r/2); // right lower
	addPoint(x,y+r); // lower tip
	addPoint(x-hpdist,y+r/2); // left lower
    }// makehex constructor

}//Hexagon polygon
