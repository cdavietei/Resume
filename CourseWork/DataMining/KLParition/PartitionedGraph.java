import java.util.*;
import java.io.*;

/**
 * An Adjacency Matrix representation of a graph. Contains methods to use the
 * Kernighan-Lin Algorithm to parition the graph into equal subsets with minimum
 * cost
 *
 * The input of this problem is a undirect graph, weighted or unweighted
 *
 * @author Chris Davie
 */
public class PartitionedGraph {

    protected int[][] mMatrix;
    protected int[] mDValues;

    protected List<Integer> mPartA, mPartB;
    protected List<Pair<Integer,Integer>> mLocked;
    protected List<Integer> mGains;

    /**
     * Create a graph with n nodes
     * @param   nodes    The number of nodes within the graph
     */
    public PartitionedGraph(int nodes) {
        mMatrix = new int[nodes][nodes];
        mDValues = new int[nodes];
        mLocked = new ArrayList<Pair<Integer,Integer>>();
        mGains = new ArrayList<Integer>();
    }//Graph()

    /**
     * Adds an undirected weighted edge in the graph between nodes u and v
     * @param   u       The first node to which the edge is added
     * @param   v       The second node to which the edge is added
     * @param   weight  The weight given to the edge
     */
    public void addEdge(int u, int v, int weight) {
        if(isValidNode(u) && isValidNode(v) && u != v)
            mMatrix[u][v] = mMatrix[v][u] = weight;
    }//addEdge(T, T, int)

    /**
     * Checks to see if the given node is contained within the graph
     * @param   node    The node index to check
     * @return  True if the node is contained within the graph, False otherwise
     */
    public boolean isValidNode(int node) {
        return node >=0 && node <mMatrix.length;
    }//isValidNode(int)

    /**
     * Runs the Kernighan-Lin Partitioning Algorithm on the grpah
     */
    public void kernLin() {
        createInitialPartition();
        while(iteration() > 0);
    }//kernLin()

    /**
     * Sets up two equal partitions required by the K-L Algorithm to start
     */
    public void createInitialPartition() {
        mPartA = new ArrayList<Integer>();
        mPartB = new ArrayList<Integer>();

        for(int i=0; i<mMatrix.length/2; i++) {
            mPartA.add(i);
            mPartB.add(mMatrix.length-1-i);
        }
    }//createInitialPartition(List<Integer>, List<Integer>)

    /**
     * Initializes the initial D values for all vertices within the graph
     */
    public void initializeDValues() {
        for(Integer i : mPartA) {
            mDValues[i] = dValue(i, mPartA, mPartB);
        }

        for(Integer i : mPartB) {
            mDValues[i] = dValue(i, mPartB, mPartA);
        }
    }//initializeDValues()

    /**
     * Updates the D value of a pair of vertices once a pair of vertices
     * has been locked
     * @param   pair  The pair of vertices to calculate D for
     */
    public void updateDValues(Pair<Integer, Integer> pair) {

        for(Integer i : mPartA) {
            mDValues[i] = dPrime(i, mPartA, mPartB, pair.getA(), pair.getB());
        }
        for(Integer i : mPartB) {
            mDValues[i] = dPrime(i, mPartB, mPartA, pair.getB(), pair.getA());
        }
    }//updateDValues()

    /**
     * Runs through one iteration of the Kernighan-Lin Algorithm
     * and returns the max gain of swapping k vertices
     * @return  The max gain given by swapping k vertices
     */
    public int iteration() {
        initializeDValues();

        int gMax = Integer.MAX_VALUE;

        for(int i=0; i<mMatrix.length/2; i++) {
            Pair<Integer, Integer> locked = findMaxGain();
            updateDValues(locked);
        }
        Pair<Integer,Integer> gK = maxK();

        int k = gK.getA();
        if(gK.getB() > 0) {
            swap(k);
        }
        unlock();
        reset();

        return gK.getB();
    }//iteration()

    /**
     * Finds a pair of vertices that provides the maximum gain for swapping
     * between the partitions
     * @return  A pair containing a vertex from Partition A and B
     */
    public Pair<Integer,Integer> findMaxGain() {
        int maxG, maxX, maxY;
        maxG = maxX = maxY = Integer.MIN_VALUE;

        for(Integer i: mPartA) {
            for(Integer j: mPartB) {
                int g = gValue(i,j);
                if(g > maxG) {
                    maxG = g;
                    maxX = i;
                    maxY = j;
                }//if
            }//for j
        }//for i

        mGains.add(maxG);
        return lock(maxX, maxY);
    }//findMaxGain()

    /**
     * Finds a k for which the gain of swapping vertices is maximised
     * @return  A pair containing the k and the maximum gain
     */
    public Pair<Integer,Integer> maxK() {
        int g, k, maxG, maxK;
        g = k = maxK = 0;
        maxG = Integer.MIN_VALUE;
        for(Integer gi : mGains) {
            g += gi;
            if(g>maxG) {
                maxK = k;
                maxG = g;
            }
            k++;
        }//for gi
        return new Pair<Integer,Integer>(maxK+1, maxG);
    }//maxK()

    /**
     * Locks a pair of vertices from being used further in the
     * iteration's calculations
     * @param  int  x  The vertex from Partition A to lock
     * @param  int  y  The vertex from Parition B to Lock
     * @return  Returns a Pair representing both vertices
     */
    public Pair<Integer,Integer> lock(int x, int y) {
        mPartA.remove((Integer)x);
        mPartB.remove((Integer)y);

        Pair<Integer,Integer> pair = new Pair<Integer, Integer>(x,y);

        mLocked.add(pair);

        return pair;
    }//swap(int, int)

    /**
     * Unlocks all vertices that are currently locked and reinserts them
     * into their respective partition
     */
    public void unlock() {
        for(Pair<Integer,Integer> pair : mLocked) {
            mPartA.add(pair.getA());
            mPartB.add(pair.getB());
        }//for pair
    }//unlock()

    /**
     * Swaps the first k locked pairs into their opposing partitions
     * @param  int  k  The first k pairs to swap
     */
    public void swap(int k) {
        for(int i=0; i<k; i++) {
            Pair<Integer,Integer> pair = mLocked.get(i);
            mPartA.add(pair.getB());
            mPartB.add(pair.getA());
        }//for i

        for(int i=0; i<k; i++)
            mLocked.remove(0);
    }//swap(int)

    /**
     * Resets all the lists used by the Kernighan-Lin Algorithm
     */
    public void reset() {
        mGains.clear();
        mLocked.clear();
    }//reset()

    /**
     * Calculates the cost crossing from the given node to a given partition
     * @param   node        The node for which to calculate the cost of
     *                      traversing within the partition
     * @param   partition   The partition with which to calculate the cost of
     *                      the given node
     * @return  The cost of traversing from the given node to all other nodes within the given partition
     */
    public int cost(int node, List<Integer> partition) {
        int cost = 0;

        for(Integer i : partition)
            cost+= mMatrix[node][i];

        return cost;
    }//cost(int, List<Integer>)

    /**
     * Calculates the D-value of a given node, given by the formula:
     *
     * D[a] = ExternalCost[a] - InternalCost[a]
     *
     * @param   node        The given node for which to calculate the D value
     * @param   internal    The partition in which the node is a part of
     * @param   external    The partition in which the node isn't a part of
     * @return  The total cost of the given node
     */
    public int dValue(int node, List<Integer> internal, List<Integer> external) {
        return  cost(node, external) - cost(node, internal);
    }//dValue(int, List<Integer>)

    /**
     * Calculates the the D-Prime value of a given node as given by the formula:
     *
     * D[x]' = D[x] + 2(Cost[x][a] - Cost[x][b])
     * D[y]' = D[y] + 2(Cost[y][b] - Cost[y][a])
     *
     * For all x in A - {a}
     * For all y in B - {b}
     *
     * a is the node from partition A to be swapped
     * b is the node from partition B to be swapped
     * @param   node    The given node for which to calculate D'
     * @param   partA   The internal partition for the node
     * @param   partB   The external partition for the node
     * @param   p       The given internal node for which to calculate the new cost
     * @param   q       The given external node for which to calculate the new cost
     * @return  The new D' value
     */
    public int dPrime(int node, List<Integer> partA, List<Integer> partB, int p, int q) {
        return mDValues[node] + 2 * (mMatrix[node][p] - mMatrix[node][q]);
    }//dPrime(int, List<Integer>, List<Integer>)

    /**
     * Calculates the gain of swapping two nodes between partitions, given by
     * the formula:
     *
     * G[x][y] = D[x] + D[y] - 2*Cost[x][y]
     * @param   x   The Node in Partition A
     * @param   y   The node in Partition B
     * @return  The gain of swapping the two nodes
     */
    public int gValue(int x, int y) {
        return mDValues[x] + mDValues[y] - 2*mMatrix[x][y];
    }//gValue(int, int)

    /**
     * Calculates the cost of cutting across from one partition to the other
     * @return  The cost of cutting across the paritions
     */
    public int cutCost() {
        int sum = 0;
        for(Integer a : mPartA) {
            for(Integer b : mPartB) {
                sum += mMatrix[a][b];
            }
        }
        return sum;
    }//cutCost()

    /**
     * Returns a pair containing the lists representing the two partitions of the graph
     * @return  A Pair object containing the two partitions
     */
    public Pair<List<Integer>,List<Integer>> getParitions() {
        return new Pair<List<Integer>,List<Integer>>(mPartA,mPartB);
    }//getParitions()


    /*====================== Debugging and Output methods ======================*/

    public void printGraph() {
        for(int i=0; i < mMatrix.length; i++) {
            for(int j=0; j < mMatrix[i].length; j++) {
                System.out.print(mMatrix[i][j]+" ");
            }//for j
            System.out.println();
        }//for i
    }//printGraph()

    public void printPartitions() {
        System.out.print("(");
        for(Integer i : mPartA)
            System.out.print(i+" ");
        System.out.print(")\n(");

        for(Integer i : mPartB)
            System.out.print(i+" ");
        System.out.println(")");


    }//printPartitions()

    public void printG(List<Integer> partA, List<Integer> partB) {
        for(Integer i: partA) {
            for(Integer j: partB) {
                System.out.printf("g(%d)(%d): %d\n",i,j,gValue(i,j));
            }
        }
    }//printG(List<Integer>, List<Integer>)

    /*====================== End of Debugging and Output methods ======================*/

    /**
     * Creates a new graph from a CSV file
     * @param   fileName    The name of the CSV file
     * @param   nodes       The number of nodes within the graph
     * @param   delimiter   Any customer field delimiter for the CSV file
     * @return  Returns a Graph object built from the CSV
     */
    public static PartitionedGraph createFromCSV(String fileName, int nodes, String delimiter) {
        PartitionedGraph graph = new PartitionedGraph(nodes);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));

            while(reader.ready()) {
                String[] line = reader.readLine().split(delimiter);

                int u, v, w;
                u = Integer.parseInt(line[0]);
                v = Integer.parseInt(line[1]);
                w = Integer.parseInt(line[2]);

                graph.addEdge(u,v,w);
            }//while ready
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return graph;
    }//createFromCSV(String, int, String)

    /*
     * Main method can be used to read in a graph from a CSV file and partition
     * it using the Kernighan-Lin Algorithm
     *
     * Commandline arguments are as follows:
     *
     * args[0]: The CSV file's path
     * args[1]: The number of nodes contained in the graph
     * args[2]: The delimiting character within the CSV file
     *
     */
    public static void main(String[] args) {

        PartitionedGraph graph;

        //Reads in a graph from a CSV file
        if(args.length >= 3) {
            graph = PartitionedGraph.createFromCSV(args[0],
                Integer.parseInt(args[1]),
                args[2]);
        }
        else {
            graph = new PartitionedGraph(6);
            graph.mMatrix = new int[][] {
                {0,1,2,3,2,4},
                {1,0,1,4,2,1},
                {2,1,0,3,2,1},
                {3,4,3,0,4,3},
                {2,2,2,4,0,2},
                {4,1,1,3,2,0}
            };
        }
        graph.kernLin();
        System.out.println("Final cut cost: "+graph.cutCost());
    }//main(String[])
}//Graph<T> class

/**
 * Utility class to store a pair of vertices
 *
 * @author Chris
 */
class Pair<A,B> {
    protected A mA;
    protected B mB;

    public Pair(A a, B b) {
        mA = a;
        mB = b;
    }//Pair(T, T)

    public A getA() {
        return mA;
    }//getA()

    public B getB() {
        return mB;
    }//getB()
}//Pair<T,T>
