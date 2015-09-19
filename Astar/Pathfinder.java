//package Dasearch;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import javax.swing.*;

public class Pathfinder extends JFrame
{
	public static boolean showtrace = true; // does not erase image

	protected Image diamondgif, mangif;  // animated gif images
	protected Graphics display;
	protected int gap = 16;  // side/radius of hexagon
	protected int yoff = 40;
	protected Astar PG;
	int rows, cols;
	int XDIM, YDIM; // window dimensions
	int gobx, goby, profx, profy;
	//    protected Image[] imageof; // image vector for terrain.
	protected Image[] imagechar; //image vector for character based on terrain.
	Color[] colorof ={Color.green,Color.darkGray,Color.orange,Color.blue}; // color corresponding to each terrain type.

	// graphical representation
	Hexagon[][] HX;
	int hpdist = Hexagon.calchpdist(gap);

	// access center graphical coordinates at cell i,j
	int getx(int i, int j) { return HX[i][j].x; }
	int gety(int i, int j) { return HX[i][j].y; }


	public void paint(Graphics g) {} // override automatic repaint

	public Pathfinder(int r, int c) // constructor
	{   rows = r; cols = c;

	HX = new Hexagon[r][c];

	for(int i=0;i<r;i++)
	{ 
		int odd = i%2;
		for(int j=0;j<c;j++)
		{
			HX[i][j] = new Hexagon(yoff/2+(j*2+odd)*hpdist,yoff+gap+(3*gap/2*i),gap);	
		}
	}//for i,j

	PG = new MyAstar(r,c);  // note it's myastar, not astar

	XDIM = cols*hpdist*2+yoff;    //((cols+1)*gap*3)/2;  
	YDIM = ((rows+1)*gap*3)/2;
	this.setBounds(0,0,XDIM+5,YDIM+yoff+5);
	this.setVisible(true); 
	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	display = this.getGraphics();

	diamondgif = Toolkit.getDefaultToolkit().getImage("gem1.gif");
	prepareImage(diamondgif,this);
	mangif = Toolkit.getDefaultToolkit().getImage("man15.gif");
	prepareImage(mangif,this);

	imagechar = new Image[4];  // image of character while on terrain type
	imagechar[Astar.OPEN] = mangif;
	imagechar[Astar.WATER] = 	    
			Toolkit.getDefaultToolkit().getImage("boat.gif");
	prepareImage(imagechar[Astar.WATER],this);

	imagechar[Astar.DESERT] = Toolkit.getDefaultToolkit().getImage("camel.gif");
	prepareImage(imagechar[Astar.DESERT],this);

	imagechar[Astar.FOREST] = Toolkit.getDefaultToolkit().getImage("bear.gif");
	prepareImage(imagechar[Astar.FOREST],this);

	try{Thread.sleep(500);} catch(Exception e) {} // Synch with system
	// draw static background as a green rectangle
	display.setColor(Color.green);
	display.fillRect(0,0,XDIM,YDIM+yoff);  // fill background

	// generate random starting positions.
	// generate initial positions of professor and diamond
	do
	{
		gobx = (int)(Math.random() * PG.COLS);  
		goby = (int)(Math.random() * PG.ROWS);
	}
	while (PG.M[goby][gobx]!=PG.OPEN);
	do 
	{
		profx = (int)(Math.random() * PG.COLS);  
		profy = (int)(Math.random() * PG.ROWS);
	}
	while (PG.M[profy][profx]!=PG.OPEN ||
			Astar.distance(goby,gobx,profy,profx)<20);

	// draw map
	drawmap();
	System.out.println(profy+","+profx+" "+goby+","+gobx);
	// draw professor and diamond, initial position
	int px = getx(profy,profx), py = gety(profy,profx); // center hx coords
	display.drawImage(imagechar[PG.M[profy][profx]],
			(px-gap/2),(py-gap/2),gap,gap,null);
	px = getx(goby,gobx); py = gety(goby,gobx);
	display.drawImage(diamondgif,px-gap/2,py-gap/2,gap,gap,null);	
	/*	
	display.drawImage(imagechar[PG.M[profy][profx]],
			  (profx*gap),(profy*gap)+yoff,gap,gap,null);
	display.drawImage(diamondgif,gobx*gap,goby*gap+yoff,gap,gap,null);
	 */
	animate();

	} // constructor


	public void animate()
	{
		// invert path.
		Coord path = PG.search(goby,gobx, profy,profx);
		//coord path = PG.search(profy,profx, goby,gobx);
		if (path==null) 
		{   display.setColor(Color.red);
		display.drawString("NO PATH TO TARGET!",50,100); 
		System.out.println("no path"); return;
		}
		int px=0, py=0; // for calculating graphical coords
		while (path!=null)
		{
			px = getx(path.y,path.x); py = gety(path.y,path.x);
			display.drawImage(imagechar[PG.M[path.y][path.x]],
					(px-gap/2),(py-gap/2),gap,gap,null);
			//	      display.drawImage(imagechar[PG.M[path.y][path.x]],
			//				(path.x*gap),(path.y*gap)+yoff,gap,gap,null);

			System.out.printf("%d,%d: %d\n",path.y,path.x,PG.M[path.y][path.x]);

			try{Thread.sleep(250);} catch(Exception se) {}
			//	      display.drawImage(imageof[PG.M[path.y][path.x]],
			//				(path.x*gap),(path.y*gap)+yoff,gap,gap,null);	
			//	      display.setColor(Color.red);
			//      	      display.fillOval((path.x*gap)+8,(path.y*gap)+yoff+8,4,4);
			// for animation:
			//	      display.drawImage(diamondgif,gobx*gap,goby*gap+yoff,gap,gap,null);	      

			if (!showtrace) // erase trail - redraw hexagon
			{
				display.setColor(colorof[ PG.M[path.y][path.x] ]);
				display.fillPolygon(HX[path.y][path.x]);
			}
			path = path.prev;
		}//with path!=null
		px = getx(goby,gobx); py = gety(goby,gobx);
		display.drawImage(diamondgif,px-gap/2,py-gap/2,gap,gap,null);
		display.drawImage(imagechar[PG.M[goby][gobx]],px-gap/2,py-gap/2,gap,gap,null);
	}//animate

	public void drawmap()
	{   
		int i, j;
		for(i=0;i<PG.ROWS;i++)
			for(j=0;j<PG.COLS;j++)
			{

				display.setColor(colorof[ PG.M[i][j] ]);
				display.fillPolygon(HX[i][j]);
				//		    display.drawImage(imageof[PG.M[i][j]],j*gap,(i*gap)+yoff,gap,gap,null);
			} 
		//try{Thread.sleep(1000);} catch(Exception e) {} 
	} // drawmap



	public static void main(String[] args)
	{
		int r = 32; int c = 44;
		Pathfinder pf = new Pathfinder(r,c);
	}

} // class pathfinder

