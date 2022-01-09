import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;
import java.util.*;

public class MazeProgram extends JPanel implements KeyListener, MouseListener {
	private static final String WARP = "W";
	private static final String DOOR_KEY = "K";
	private static final String HARD_WALL = "#";
	private static final String DOOR = "D";
	private static final String ANTIWARP = "A";
	private static final String TRAP = "T";
	private static boolean haveMasterKey = false;
	private static boolean isWarped = false;
	private static boolean isAntiWarped = false;
	JFrame frame;
	// declare an array to store the maze - Store Wall(s) in the array
	int x = 100, y = 100;
	static MazeObject myMaze = null;
	Explorer explorer = null;
	Explorer explorerEnd = null;
	int lostHealthX;
	int lostHealthY;
	boolean lostHealth = false;
	boolean wonGame = false;
	ArrayList<Wall> leftWalls;
	ArrayList<Wall> frontWalls;
	ArrayList<Wall> rightWalls;
	ArrayList<Wall> ceilings;
	ArrayList<Wall> floors;
	ArrayList<Wall> addons;
	ArrayList<Wall> doors;

	int EAST = 1;
	int SOUTH = 2;
	int WEST = 3;
	int NORTH = 4;
	int countOfMovements;
	int boundsSize;
	int health = 100;
	int startX;
	int startY;
	boolean moved = false;

	public MazeProgram() {
		//BLUE IS THE KEY, RED ARE THE DOORS THAT YOU OPEN WITH THE KEY
		//CYAN ARE TRAPS
		//GREEN ARE TELEPORTERS (CAN BE ANTIWARP WHICH TELEPORTS TO STARTING POINT, OR WARP WHICH TELEPORTS TO END
		setBoard();
		setWalls();
		frame = new JFrame();
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);
		frame.setVisible(true);
		frame.addKeyListener(this);

		// this.addMouseListener(this); //in case you need mouse clicking
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK); // this will set the background color
		g.fillRect(0, 0, 1000, 800); // since the screen size is 1000x800
										// it will fill the whole visible part
										// of the screen with a black rectangle

		// drawBoard here!

		// x & y would be used to located your
		// playable character
		// values would be set below
		// call the x & y values from your Explorer class
		// explorer.getX() and explorer.getY()

		// other commands that might come in handy

		isAntiWarped = false;

		System.out.println("Current Explorer Position ["+explorer.getX()+","+explorer.getY()+"]");
		for (Wall w : ceilings) {
			g.setColor(Color.LIGHT_GRAY);
			Polygon p = w.getPolygon();
			GradientPaint gradient = new GradientPaint(p.xpoints[3], p.ypoints[0], w.getStartColor(), p.xpoints[3],
					p.ypoints[3], w.getEndColor());
			((Graphics2D) g).setPaint(gradient);
			g.fillPolygon(p);
			g.drawPolygon(p);
		}

		for (Wall w : floors) {
			g.setColor(Color.LIGHT_GRAY);
			Polygon p = w.getPolygon();
			GradientPaint gradient = new GradientPaint(p.xpoints[3], p.ypoints[0], w.getStartColor(), p.xpoints[3],
					p.ypoints[3], w.getEndColor());
			((Graphics2D) g).setPaint(gradient);
			g.fillPolygon(w.getPolygon());
			g.drawPolygon(w.getPolygon());

		}

		for (Wall w : leftWalls) {
			Polygon p = w.getPolygon();
			GradientPaint wallGraident = new GradientPaint(p.xpoints[0], p.ypoints[3], w.getStartColor(), p.xpoints[3],
					p.ypoints[3], w.getEndColor());
			((Graphics2D) g).setPaint(wallGraident);
			g.fillPolygon(p);
			g.drawPolygon(p);
		}

		for (Wall w : rightWalls) {
			Polygon p = w.getPolygon();
			GradientPaint wallGradient = new GradientPaint(p.xpoints[0], p.ypoints[3], w.getStartColor(), p.xpoints[3],
					p.ypoints[3], w.getEndColor());
			((Graphics2D) g).setPaint(wallGradient);
			g.fillPolygon(w.getPolygon());
			g.drawPolygon(w.getPolygon());
		}

		for (Wall w : frontWalls) {
			if(w.getType().equals(HARD_WALL)){
				g.setColor(Color.GRAY);
				g.fillPolygon(w.getPolygon());
				g.drawPolygon(w.getPolygon());
			}else{
				g.setColor(w.getStartColor());
				g.fillPolygon(w.getPolygon());
				g.drawPolygon(w.getPolygon());
			}

			if(w.getType().equals(DOOR) && ((explorer.getX() == w.getX()-1 || explorer.getX() == w.getX() +1) && explorer.getY() == w.getY() && !haveMasterKey)){
				g.setColor(Color.BLACK);
				g.setFont(new Font("Times New Roman", Font.BOLD, 24));
				g.drawString("YOU NEED THE KEY TO PASS THROUGH THIS DOOR!", 200, 400);
			}
		}

		g.setColor(Color.BLACK);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
		g.drawString("Movement Count: " + countOfMovements, 300, 20);
		g.drawString("Position [" + explorer.getX() + "," + explorer.getY() + "," + explorer.getDirectionStr() + "] ",
				300, 35);



		for (Wall w : addons) {
			if (w.getX() == explorer.getX() && w.getY() == explorer.getY()) {
				if (w.getType().equals(DOOR_KEY) && !haveMasterKey) {
					try {
						BufferedImage img = ImageIO.read(new File("key.png"));
						g.drawImage(img, 250, 100, null);
						haveMasterKey = true;

						//paing some text
					} catch (Exception e) {
						// TODO: handle exception
					}
					g.setColor(Color.BLACK);

					g.drawString("CONGRATULATIONS!!! YOU HAVE RECIEVED THE KEY!", 225, 700);
				}
				if(w.getType().equals(WARP) && explorer.getX() == w.getX() && explorer.getY() == w.getY()){
					explorer.setX(explorerEnd.getX());
					explorer.setY(explorerEnd.getY());
					isWarped = true;
				}

				if(w.getType().equals(ANTIWARP) && explorer.getX() == w.getX() && explorer.getY() == w.getY()){

					explorer.setX(startX);
					explorer.setY(startY);
					explorer.setDirection(EAST);
					isAntiWarped = true;
					setWalls();

				}

				if(w.getType().equals(TRAP) && explorer.getX() == w.getX() && explorer.getY() == w.getY() && !lostHealth){
					try {
					BufferedImage img = ImageIO.read(new File("spikes.png"));
					g.drawImage(img, 350, 100, null);

					//paing some text
					} catch (Exception e) {
					// TODO: handle exception
					}
					health -=30;
					g.setColor(Color.CYAN);
					g.drawString("YOU HAVE LOST HEALTH DUE TO A TRAP!", 320, 400);
					lostHealth = true;
					g.setColor(Color.BLACK);

					g.drawString("HEALTH: " + health, 600, 20);
				}



			}
			g.setColor(Color.BLACK);
			g.drawString("HEALTH: " + health, 600, 20);
		}




 		g.setColor(Color.RED);
		if (explorer.getX() == explorerEnd.getX() && explorer.getY() == explorerEnd.getY() && !isWarped) {
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));
			g.drawString("CONGRATULATIONS! YOU HAVE WON!", 280, 400);
			g.drawString("PRESS X TO EXIT", 400, 600);
			wonGame = true;
		}else if ((explorer.getX() == explorerEnd.getX() && explorer.getY() == explorerEnd.getY() && isWarped)){
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));
			g.drawString("CONGRATULATIONS! YOU HAVE BEEN WARPED INTO THE END!", 120, 400);
			g.drawString("PRESS X TO EXIT", 400, 600);
			wonGame = true;
		}

		if(health<=0){
			repaint();
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));
			g.drawString("YOU HAVE FAILED! YOU HAVE LOST ALL OF YOUR HEALTH!", 120, 400);
			g.drawString("PRESS X TO EXIT", 400, 600);
			wonGame = true;
		}

		if(explorer.getX() == startX && explorer.getY() == startY && !moved){
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));
			g.drawString("   WELCOME TO MY MAZE!", 320, 400);
		}

		if(explorer.getX() == startX && explorer.getY() == startY && moved && !isAntiWarped){
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));
			g.drawString("YOU ARE BACK TO THE STARTING POINT!", 275, 400);
		}

		if(explorer.getX() == startX && explorer.getY() == startY && isAntiWarped){
			g.setFont(new Font("Times New Roman", Font.BOLD, 24));
			g.drawString("YOU HAVE BEEN WARPED BACK TO THE STARTING POINT!", 155, 400);


		}

	  	print2DMaze(g);


	}

	private void print2DMaze(Graphics g) {
		List<List<Wall>> walls = myMaze.getWalls();
		int spacingSize = 10;
		for (int i = 0; i < walls.size(); i++) {

			List<Wall> colWall = walls.get(i);

			for (int j = 0; j < colWall.size(); j++) {
				Wall w = colWall.get(j);
				if (w.getType().equals(TRAP)) {
					g.setColor(Color.CYAN);
					g.drawRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);
					g.fillRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);

				}else if (w.getType().equals(WARP) || w.getType().equals(ANTIWARP)) {
					g.setColor(Color.GREEN);
					g.drawRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);
					g.fillRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);

				}else if (w.getType().equals(DOOR_KEY)) {
					g.setColor(Color.BLUE);
					g.drawRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);
					g.fillRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);

				}else if (w.getType().equals(HARD_WALL)) {
					g.setColor(Color.YELLOW);
					g.drawRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);
					g.fillRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);

				}else if (w.getType().equals(DOOR)) {
					g.setColor(Color.RED);
					g.drawRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);
					g.fillRect(w.getY() * spacingSize, w.getX() * spacingSize, spacingSize, spacingSize);
				}

				if(!w.getType().equals(TRAP) && explorer.getX() == w.getX() && explorer.getY() == w.getY() ){
					lostHealth = false;
				}
			}
			// break;
			// System.out.println();
		}

		g.setColor(Color.MAGENTA);
		g.drawOval(explorer.getY() * spacingSize, explorer.getX() * spacingSize, spacingSize, spacingSize);
		g.fillOval(explorer.getY() * spacingSize, explorer.getX() * spacingSize, spacingSize, spacingSize);
	}

	public void setBoard() {
		// choose your maze design

		// pre-fill maze array here

		File name = new File("mazeTextFile.txt");
		int row = 0;

		List<List<Wall>> allWalls = new ArrayList<List<Wall>>();
		addons = new ArrayList<Wall>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(name));
			String text;
			while ((text = input.readLine()) != null) {
				System.out.println(text);
				List<Wall> rowWall = new ArrayList<>();

				boundsSize = text.length();

				for (int i = 0; i < text.length(); i++) {
					Wall w = new Wall(row, i, String.valueOf(text.charAt(i)));
					rowWall.add(w);
					if(w.getType().equals(DOOR_KEY) || (w.getType().equals(WARP) || w.getType().equals(ANTIWARP) || w.getType().equals(TRAP))){
						addons.add(w);
					}
				}

				for (int i = 0; i < rowWall.size(); i++) {
					if (rowWall.get(i).getType().equals("S")) {
						explorer = new Explorer(row, i, EAST);
						startX = row;
						startY = i;
					}
					if (rowWall.get(i).getType().equals("E")) {
						explorerEnd = new Explorer(row, i, EAST);
					}
				}

				allWalls.add(rowWall);
				row++;

			}

			myMaze = new MazeObject(allWalls);
			System.out.println();
			myMaze.printToScreen();
		} catch (IOException io) {
			System.err.println("File error");
		}

	}

	public void setWalls() {
		leftWalls = new ArrayList<Wall>();
		rightWalls = new ArrayList<Wall>();
		ceilings = new ArrayList<Wall>();
		floors = new ArrayList<Wall>();
		frontWalls = new ArrayList<Wall>();
		doors = new ArrayList<Wall>();
		//addons = new ArrayList<Wall>();

		List<List<Wall>> wall = myMaze.getWalls();

		if (explorer.getDirection() == EAST) {

		}

		Color startColor = Color.WHITE;

		for (int d = 0; d < 5; d++) {

			int topleftOuterX = 0 + 50 * d;
			int topLeftInnerX = 50 + 50 * d;
			int bottomLeftInnerX = 50 + 50 * d;
			int bottomLeftOuterX = 0 + 50 * d;

			int topleftOuterY = 0 + 50 * d;
			int topLeftInnerY = 50 + 50 * d;
			int bottomLeftInnerY = 750 - 50 * d;
			int bottomLeftOuterY = 800 - 50 * d;

			int topRightOuterX = 1000 - 50 * d;
			int topRightInnerX = 950 - 50 * d;
			int bottomRightInnerX = 950 - 50 * d;
			int bottomRightOuterX = 1000 - 50 * d;

			int topRightOuterY = 0 + 50 * d;
			int topRightInnerY = 50 + 50 * d;
			int bottomRightInnerY = 750 - 50 * d;
			int bottomRightOuterY = 800 - 50 * d;

			int[] x = { topleftOuterX, bottomLeftOuterX, bottomLeftInnerX, topLeftInnerX };
			int[] y = { topleftOuterY, bottomLeftOuterY, bottomLeftInnerY, topLeftInnerY };

			int[] x2 = { topRightOuterX, bottomRightOuterX, bottomRightInnerX, topRightInnerX };
			int[] y2 = { topRightOuterY, bottomRightOuterY, bottomRightInnerY, topRightInnerY };

			int[] floorx = { bottomLeftOuterX, bottomRightOuterX, bottomRightInnerX, bottomLeftInnerX };
			int[] floory = { bottomLeftOuterY, bottomRightOuterY, bottomRightInnerY, bottomLeftInnerY };

			int[] ceilingx = { topleftOuterX, topRightOuterX, topRightInnerX, topLeftInnerX };
			int[] ceilingy = { topleftOuterY, topRightOuterY, topRightInnerY, topLeftInnerY };

			int[] frontWallX = { topLeftInnerX - 50, topRightInnerX + 50, bottomRightInnerX + 50,
					bottomLeftInnerX - 50 };
			int[] frontWallY = { topLeftInnerY - 50, topRightInnerY - 50, bottomRightInnerY + 50,
					bottomLeftInnerY + 50 };

			int[] frontObjectX = { topLeftInnerX - 300, topRightInnerX + 300, bottomRightInnerX + 300,
					bottomLeftInnerX - 50 };
			int[] frontobjectY = { topLeftInnerY - 300, topRightInnerY - 300, bottomRightInnerY + 300,
					bottomLeftInnerY + 300 };

			Color endColor = new Color(startColor.getRed() - 50, startColor.getGreen() - 50, startColor.getBlue() - 50);

			if (explorer.getDirection() == EAST) {

				if (explorer.getY() + d < (boundsSize)) {
					List<Wall> lwList = wall.get(explorer.getX() - 1);
					Wall leftWall = lwList.get(explorer.getY() + (d));

					if (leftWall.getType().equals(HARD_WALL)) {
						Polygon p = new Polygon(x, y, 4);
						leftWall.setPolygon(p);
						leftWall.setStartColor(startColor);
						leftWall.setEndColor(endColor);
						leftWalls.add(leftWall);
					}

					if (leftWall.getType().equals(DOOR)) {
						Polygon p = new Polygon(x, y, 4);
						leftWall.setPolygon(p);
						leftWall.setStartColor(Color.RED);
						leftWall.setEndColor(Color.RED);
						leftWalls.add(leftWall);
					}

					List<Wall> rwList = wall.get(explorer.getX() + 1);
					Wall rightWall = rwList.get(explorer.getY() + (d));

					if (rightWall.getType().equals(HARD_WALL)) {
						rightWall.setPolygon(new Polygon(x2, y2, 4));
						rightWall.setStartColor(startColor);
						rightWall.setEndColor(endColor);
						rightWalls.add(rightWall);
					}
					if (rightWall.getType().equals(DOOR)) {
						Polygon p = new Polygon(x2, y2, 4);
						rightWall.setPolygon(p);
						rightWall.setStartColor(Color.RED);
						rightWall.setEndColor(Color.RED);
						rightWalls.add(rightWall);
					}

					Wall ceilingWall = new Wall(0, 0, "C");
					ceilingWall.setPolygon(new Polygon(ceilingx, ceilingy, 4));
					ceilingWall.setStartColor(startColor);
					ceilingWall.setEndColor(endColor);
					ceilings.add(ceilingWall);

					Wall flooring = new Wall(0, 0, "F");
					flooring.setPolygon(new Polygon(floorx, floory, 4));
					flooring.setStartColor(startColor);
					flooring.setEndColor(endColor);
					floors.add(flooring);

					List<Wall> frontWall = wall.get(explorer.getX());
					Wall fWall = frontWall.get(explorer.getY() + (d));

					startColor = endColor;

					if (fWall.getType().equals(HARD_WALL)
							|| fWall.getType().equals(DOOR)
							|| fWall.getY() == (boundsSize - 1)) {
						fWall.setPolygon(new Polygon(frontWallX, frontWallY, 4));
						frontWalls.add(fWall);
						break;
					}

				}

			}

			if (explorer.getDirection() == WEST) {

				if (explorer.getY() - d >= 0) {
					List<Wall> lwList = wall.get(explorer.getX() + 1);
					Wall leftWall = lwList.get(explorer.getY() - (d));

					if (leftWall.getType().equals(HARD_WALL)) {
						Polygon p = new Polygon(x, y, 4);
						leftWall.setPolygon(p);
						leftWall.setStartColor(startColor);
						leftWall.setEndColor(endColor);
						leftWalls.add(leftWall);
					}
					if (leftWall.getType().equals(DOOR)) {
						Polygon p = new Polygon(x, y, 4);
						leftWall.setPolygon(p);
						leftWall.setStartColor(Color.RED);
						leftWall.setEndColor(Color.RED);
						leftWalls.add(leftWall);
					}

					List<Wall> rwList = wall.get(explorer.getX() - 1);
					Wall rightWall = rwList.get(explorer.getY() - (d));

					if (rightWall.getType().equals(HARD_WALL)) {
						rightWall.setPolygon(new Polygon(x2, y2, 4));
						rightWall.setStartColor(startColor);
						rightWall.setEndColor(endColor);
						rightWalls.add(rightWall);
					}
					if (rightWall.getType().equals(DOOR)) {
						Polygon p = new Polygon(x2, y2, 4);
						rightWall.setPolygon(p);
						rightWall.setStartColor(Color.RED);
						rightWall.setEndColor(Color.RED);
						rightWalls.add(rightWall);
					}


					Wall ceilingWall = new Wall(0, 0, "C");
					ceilingWall.setPolygon(new Polygon(ceilingx, ceilingy, 4));
					ceilingWall.setStartColor(startColor);
					ceilingWall.setEndColor(endColor);
					ceilings.add(ceilingWall);

					Wall flooring = new Wall(0, 0, "F");
					flooring.setPolygon(new Polygon(floorx, floory, 4));
					flooring.setStartColor(startColor);
					flooring.setEndColor(endColor);
					floors.add(flooring);

					startColor = endColor;


					List<Wall> fwList = wall.get(explorer.getX());
					Wall frontWall = fwList.get(explorer.getY() - (d));
 					if(frontWall.getType().equals(DOOR)){
						frontWall.setStartColor(Color.RED);
						frontWall.setEndColor(Color.RED);
			 		}

					if (frontWall.getType().equals(HARD_WALL)
							|| frontWall.getType().equals(DOOR)
							|| frontWall.getY() == (boundsSize - 1)) {
						frontWall.setPolygon(new Polygon(frontWallX, frontWallY, 4));
						frontWalls.add(frontWall);
						break;
					}

				}
			}

			if (explorer.getDirection() == SOUTH) {

				System.out.println("E[" + explorer.getX() + "," + explorer.getY() + "]");
				int explorerX = explorer.getX();
				int explorerY = explorer.getY();

				// check in front to see if can move
				// check left and right wall
				if (explorerX + d < wall.size()) {

					Wall rightWall = null;
					Wall leftWall = null;

					List<Wall> rowInFront = wall.get(explorerX + d);
					Wall frontWall = rowInFront.get(explorerY);
					if(explorer.getY()!=boundsSize-1){
						leftWall = rowInFront.get(explorerY + 1);
					}
					if(explorer.getY()!=0){
						rightWall = rowInFront.get(explorerY - 1);
					}

					if(leftWall!=null){
					if (leftWall.getType().equals(HARD_WALL)) {
						leftWall.setPolygon(new Polygon(x, y, 4));
						leftWall.setStartColor(startColor);
						leftWall.setEndColor(endColor);
						leftWalls.add(leftWall);
					}
					}

					if(rightWall!=null){
					if (rightWall.getType().equals(HARD_WALL)) {
						rightWall.setPolygon(new Polygon(x2, y2, 4));
						rightWall.setStartColor(startColor);
						rightWall.setEndColor(endColor);
						rightWalls.add(rightWall);
					}
					}

					Wall ceilingWall = new Wall(0, 0, "C");
					ceilingWall.setPolygon(new Polygon(ceilingx, ceilingy, 4));
					ceilingWall.setStartColor(startColor);
					ceilingWall.setEndColor(endColor);
					ceilings.add(ceilingWall);

					Wall flooring = new Wall(0, 0, "F");
					flooring.setPolygon(new Polygon(floorx, floory, 4));
					flooring.setStartColor(startColor);
					flooring.setEndColor(endColor);
					floors.add(flooring);

					startColor = endColor;


					if(frontWall.getType().equals(DOOR)){
			 			frontWall.setStartColor(Color.RED);
			 			frontWall.setEndColor(Color.RED);
			 		}
					 if (frontWall.getType().equals(HARD_WALL)
							 || frontWall.getType().equals(DOOR)
							|| (frontWall.getType().equals(" ") && frontWall.getX() == wall.size())) {
						frontWall.setPolygon(new Polygon(frontWallX, frontWallY, 4));
						frontWalls.add(frontWall);
						break;
					}

				}

			}

			if (explorer.getDirection() == NORTH) {

				System.out.println("E[" + explorer.getX() + "," + explorer.getY() + "]");
				int explorerX = explorer.getX();
				int explorerY = explorer.getY();

				// check in front to see if can move
				// check left and right wall
				if (explorerX - d >= 0) {

					Wall leftWall = null;
					Wall rightWall = null;
					List<Wall> rowInFront = wall.get(explorerX - d);
					Wall frontWall = rowInFront.get(explorerY);
					if(explorer.getY()!=0){
						leftWall = rowInFront.get(explorerY - 1);
					}
					if(explorer.getY()!=boundsSize-1){
					 rightWall = rowInFront.get(explorerY + 1);
					}
					if(explorer.getY()!=0){
						if (leftWall.getType().equals(HARD_WALL)) {
							leftWall.setPolygon(new Polygon(x, y, 4));
							leftWall.setStartColor(startColor);
							leftWall.setEndColor(endColor);
							leftWalls.add(leftWall);
						}
					}

					if(rightWall!=null){
					if (rightWall.getType().equals(HARD_WALL)) {
						rightWall.setPolygon(new Polygon(x2, y2, 4));
						rightWall.setStartColor(startColor);
						rightWall.setEndColor(endColor);
						rightWalls.add(rightWall);
					}
					}

					Wall ceilingWall = new Wall(0, 0, "C");
					ceilingWall.setPolygon(new Polygon(ceilingx, ceilingy, 4));
					ceilingWall.setStartColor(startColor);
					ceilingWall.setEndColor(endColor);
					ceilings.add(ceilingWall);

					Wall flooring = new Wall(0, 0, "F");
					flooring.setPolygon(new Polygon(floorx, floory, 4));
					flooring.setStartColor(startColor);
					flooring.setEndColor(endColor);
					floors.add(flooring);

					startColor = endColor;


					if(frontWall.getType().equals(DOOR)){
			 			frontWall.setStartColor(Color.RED);
			 			frontWall.setEndColor(Color.RED);
			 		}
				 	if (frontWall.getType().equals(HARD_WALL) ||
				 			frontWall.getType().equals(DOOR)||
				 			(frontWall.getType().equals(" ") && frontWall.getX() == 0)) {

						frontWall.setPolygon(new Polygon(frontWallX, frontWallY, 4));
						frontWalls.add(frontWall);
						break;
					}
				}

			}
		}
	}

	public void keyPressed(KeyEvent e) {

		boolean move = false;
		List<List<Wall>> mazeWalls = myMaze.getWalls();
		System.out.println("Movement: " + countOfMovements);
		// System.out.println("ArrayListSize "+myMaze.getWalls().getLength());

		if(e.getKeyCode() == 88){
			System.exit(0);
		}

		if(health<=0 || wonGame){
			return;
		}
		if (e.getKeyCode() == 38) {



			if (explorer.getDirection() == EAST) {

				List<Wall> colWall = mazeWalls.get(explorer.getX());
				if (explorer.getY() + 1 < colWall.size()) {

					Wall w = colWall.get(explorer.getY() + 1);


					if ( w.getType().equals(HARD_WALL)
							|| (w.getType().equals(DOOR) && !haveMasterKey)	|| (explorer.getX() == explorerEnd.getX() && explorer.getY() == explorerEnd.getY())
						){
						System.out.println("Cant Move Further EAST");
					}else{
						explorer.move(0, 1);
						setWalls();
						countOfMovements++;
						repaint();
						moved = true;

					}
				}
			}

			if (explorer.getDirection() == SOUTH) {

				List<Wall> colWall = mazeWalls.get(explorer.getX() + 1);
				Wall wallInFront = colWall.get(explorer.getY());

				if ( wallInFront.getType().equals(HARD_WALL)
						|| (wallInFront.getType().equals(DOOR) && !haveMasterKey)
					){
					System.out.println("Cant Move Further South");
				}else{
					explorer.move(1, 0);
					setWalls();
					countOfMovements++;
					moved = true;
					repaint();
				}
			}
			if (explorer.getDirection() == NORTH) {

				Wall w = null;
				if (explorer.getX() != 0) {
					List<Wall> colWall = mazeWalls.get(explorer.getX() - 1);
					w = colWall.get(explorer.getY());
				}

				if ( w.getType().equals(HARD_WALL)
						|| (w.getType().equals(DOOR) && !haveMasterKey)
						|| explorer.getX() == 0
					){
					System.out.println("Cant Move Further NORTH");
				}else{
					explorer.move(-1, 0);
					setWalls();
					countOfMovements++;
					moved = true;
					repaint();
				}
			}
			if (explorer.getDirection() == WEST) {
				Wall w = null;
				boolean moveEx = true;

				if(explorer.getY() == startY){
					move = false;
				}

				if (explorer.getY() != startY) {
					List<Wall> colWall = mazeWalls.get(explorer.getX());
					w = colWall.get(explorer.getY() - 1);
				}

				if(w!=null){
				if ( w.getType().equals(HARD_WALL)
						|| (w.getType().equals(DOOR) && !haveMasterKey)
						|| w.getY()<0 || !moveEx
					){
					System.out.println("Cant Move Further WEST");
				}else{
					explorer.move(0, -1);
					setWalls();
					countOfMovements++;
					moved = true;
					repaint();
				}
			}
			}

		}

		if (e.getKeyCode() == 39) {

			if (explorer.getDirection() == 4) {
				explorer.setDirection(1);
				setWalls();
				repaint();
			} else {
				explorer.setDirection(explorer.getDirection() + 1);
				setWalls();
				repaint();
			}

		}

		if (e.getKeyCode() == 37) {

			if (explorer.getDirection() == 1) {
				explorer.setDirection(4);
				setWalls();
				repaint();
			} else {
				explorer.setDirection(explorer.getDirection() - 1);
				setWalls();
				repaint();
			}

		}

	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public static void main(String args[]) {
		MazeProgram app = new MazeProgram();

	}
}