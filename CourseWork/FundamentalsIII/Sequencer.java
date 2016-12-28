import java.io.*;

/**
 * Calculates the DNA Alignment score given by implementing the Needleman–Wunsch algorithm via dynamic programming
 * and outputs the aligned sequences
 *
 * Allows for a custom scoring scheme through inheriting the Sequencer class and overriding the constructor
 * @author Chris
 * created on 2015/04/20
 */
public class Sequencer {

	//Declares all the member variables
	protected char[] row;
	protected char[] col;

	protected int[][] matrix;
	protected int[][] trace;

	protected String topSeq;
	protected String connections;
	protected String bottomSeq;

	public int match;
	public int mismatch;
	public int gap;

	/**
	 * Constructor that takes two DNA sequences to compare
	 * @param seq1 The first DNA sequence
	 * @param seq2 The second DNA sequence
	 */
	public Sequencer(String seq1, String seq2) {
		/* Initializes an array to hold the sequence characters */
		col = new char[seq1.length()+1];
		row = new char[seq2.length()+1];

		/* Creates an empty space at the first element of the array */
		col[0] = row[0] = '*';

		/* Sets the scoring scheme */
		setScheme(1,0,0);

		/* Inserts the DNA sequence into its respective array */
		for(int i=1; i<col.length; i++)
			col[i] = seq1.charAt(i-1);

		for(int i=1; i<row.length; i++)
			row[i] = seq2.charAt(i-1);
	}//Sequencer(String, String)

	/**
	 * initializes all the member variables
	 */
	public void initialize() {
		/* Creates a 2D array to store the alignment values */
		matrix = new int[row.length][col.length];

		/* Creates a 2D array to trace back the sequence */
		trace = new int[row.length][col.length];

		/* Sets the border values for the two arrays */
		for(int i=0; i<matrix.length;i++)
		{
			matrix[i][0] = 0;
			trace[i][0] = 2;
		}

		for(int i=0; i<matrix[0].length;i++)
			matrix[0][i] = trace[0][i] = 0;

		/* Initializes the output variables */
		topSeq = bottomSeq = connections = "";
	}//initialize()

	/**
	 * Calculates the alignment values for the sequence
	 */
	public void fillMatrix() {
		/* Creates variables used to calculate the alignment values */
		int up, left, dia, max, prev;

		/* fills the 2D array with the alignment values */
		for(int i=1; i<matrix.length;i++)
		{
			for(int j=1; j<matrix[i].length;j++)
			{
				/* Calculates the possible scores for the current cell */
				dia = matrix[i-1][j-1]+score(i,j);
				up = matrix[i-1][j]+w();
				left = matrix[i][j-1]+w();

				/*
				 * 0 left
				 * 1 diagonal
				 * 2 up
				 */

				max = dia;
				prev = 1;

				//Finds the max of all the scores
				if(up>max) {
					max = up;
					prev = 2;
				}
				if(left>max && left>up) {
					max = left;
					prev = 0;
				}

				//Inserts the value into the current cell
				matrix[i][j] = max;
				//Inserts a pointer to the previous cell
				trace[i][j] = prev;

			}
		}
	}//fillMatrix()

	/**
	 * Prints out the 2D array containing the alignment values for debugging
	 */
	public void printMatrix()
	{
		for(int i=0;i<matrix.length;i++)
		{
			for(int j=0;j<matrix[i].length;j++)
				System.out.print(matrix[i][j]+" ");
			System.out.println();
		}
	}//printMatrix()

	/**
	 * Builds the aligned DNA sequences and their connections
	 */
	public void traceback() {
		/* Sets variables used to traceback the sequence */
		int i, j, dir;
		i = matrix.length-1;
		j = matrix[0].length-1;

		/* Creates direction vectors for i and j */
		int[] dI = {0,1,1};
		int[] dJ = {1,1,0};

		/* Starts tracing back through the 2D array */
		while(i!=0 || j!=0) {
			dir = trace[i][j];

			/* Creates an array of the possible characters to be added to the sequence */
			char[] top = {col[j],col[j],'_'};
			char[] bottom = {'_',row[i],row[i]};
			char[] connection = {' ',' ',' '};

			/* Adds a connection if the characters match */
			if(row[i]==col[j])
				connection[1] = '|';

			/* Adds the character to the front of each sequence */
			topSeq = top[dir] + topSeq;
			bottomSeq = bottom[dir] + bottomSeq;
			connections = connection[dir] + connections;

			/* Changes the position based on the direction */
			i -= dI[dir];
			j -= dJ[dir];
		}//while
	}//traceBack()

	/**
	 * Prints out the aligned sequences
	 */
	public void printSequence() {
		System.out.printf("%s\n%s\n%s\nAlignment Score: %i\n",
			topSeq,connections,bottomSeq,matrix[row.length-1][col.length-1]);
	}//printSequence()

	/**
	 * Prints out the alignment score of the two DNA sequences
	 */
	public void printAlignmentScore() {
		System.out.println("Alignment Score: "+matrix[row.length-1][col.length-1]);
	}//printAlignmentScore()

	/**
	 * Returns the score for two characters
	 * @param i The index of the first sequence
	 * @param j The index of the second sequence
	 * @return Returns the score for the two characters
	 */
	public int score(int i, int j) {
		if(row[i] == col[j])
			return match;
		else
			return mismatch;
	}//score(int, int)

	/**
	 * Returns the gap penalty
	 * @return The gap penalty
	 */
	public int w() {
		return gap;
	}//w()

	/**
	 * Sets a new scoring scheme
	 * @param match The new match score
	 * @param mismatch The new mismatch penalty
	 * @param gap The new gap penalty
	 */
	public void setScheme(int match, int mismatch, int gap)
	{
		this.match = match;
		this.mismatch = mismatch;
		this.gap = gap;
	}//setScheme(int, int, int)

	/**
	 * Builds a random DNA sequence of lenght n
	 * @param n The length of the DNA Sequence
	 * @return The new DNA sequence
	 */
	public static String randseq(int n) {
		char[] S = new char[n];
		String DNA = "ACGT";
		for(int i=0;i<n;i++)
		{
			int r = (int)(Math.random()*4);
			S[i] = DNA.charAt(r);
		}
		return new String(S);
	}//randseq(int)

	/**
	 * Reads a DNA sequence from a file
	 * @param file The name of the file to be read
	 * @return The DNA sequence read from the file
	 */
	public static String read(String file)
	{
		String seq = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			seq = br.readLine();
			br.close();
		}
		catch (IOException ie) {
			System.out.println("IO Error \n"+ie.getLocalizedMessage());
			System.exit(1);
		}
		return seq;
	}//read(String)

	/**
	 * Accepts command-line arguments in the form of either two DNA sequences or the lengths of two sequences to randomly create
	 * Also accepts a third argument 'adv' to dictate which class is used
	 * @param args The command-line arguments given at run-time
	 */
	public static void main(String[] args) {
		Sequencer seq;
		String seq1, seq2;
		if(args[0].charAt(0) > (int)'9') {
			seq1 = args[0];
			seq2 = args[1];
		}
		else {
			seq1 = randseq(Integer.parseInt(args[0]));
			seq2 = randseq(Integer.parseInt(args[1]));
		}

		if(args.length >= 3 && args[2].equals("adv"))
			seq = new AdvSeq(seq1,seq2);
		else
			seq = new Sequencer(seq1,seq2);

		seq.initialize();
		seq.fillMatrix();
		seq.traceback();
		seq.printSequence();

	}//main(String[])
}//Sequencer class

/**
 * A Class that overrides the original Sequencer class in order to implement an advanced scoring scheme
 *
 * @author Chris
 *
 */
class AdvSeq extends Sequencer
{
	/**
	 * Constructor that implements the advanced scoring scheme
	 * @param seq1 The first DNA sequence
	 * @param seq2 The second DNA sequence
	 */
	public AdvSeq(String seq1, String seq2) {
		super(seq1,seq2);
		setScheme(2,-1,-2);
	}//AdvSeq(String, String)
}//AdvSeq class
