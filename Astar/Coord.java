
/*
 *  ===============================================
 * Written by Dr. Chuck Liang, Hofstra University
 * ===============================================
 */

public class Coord implements Comparable<Coord>
{
	int y, x;   
	int cost;   // total estimated cost, including heuristic
	int dist;   // distance (cost) from source node, excluding heuristic estimate
	byte status=1; // 1=frontier node, 2 = interior, ...
	Coord prev; // pointer to previous coordinate on path.
	Coord(int a, int b) {y=a; x=b;}

	/*
    public boolean equals(coord c) // two coords are same if x,y's are same
    {
	return (x==c.x && y==c.y);
    }
	 */

	public boolean equals(Object oc) // conforms to old java specs
	{
		if (oc==null || !(oc instanceof Coord)) return false;
		Coord c = (Coord)oc;
		return (x==c.x && y==c.y);
	}

	public int compareTo(Coord c) // compares cost
	{
		return cost - c.cost;   // note: reverses relationship
	}

} // coord
