package com.Hollux.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;



public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 75;
	
	//tech constant but a settings option should be able to change these
	static int defaultBodyParts = 6;
	static int defApplesEaten = 0;
	static char defDirection = 'R';
	
	
	final int[] xCord = new int[GAME_UNITS];
	final int[] yCord = new int[GAME_UNITS];
	
	int bodyParts = defaultBodyParts;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = defDirection;
	boolean running = false;
	Timer timer;
	Random rand;

	//constant phrases and fonts
	static final String GAME_FONT = "Ink Free";
	
	//key adapter/listener
	MyKeyAdapter customAdapter = new MyKeyAdapter();
	
	public GamePanel() {
		rand = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(customAdapter);
		startGame();
	}
	
	public void startGame() {
		addApple();
		running = true;
		timer = new Timer(DELAY,this);
		timer.start();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
		
	}
	
	public void draw(Graphics g) {
		
		if(running) {
			//create grid
			for(int i=0; i<SCREEN_WIDTH/UNIT_SIZE; i++) {
				int squareSize = i*UNIT_SIZE;							//pixels out of total screen size covered
				g.drawLine(0, squareSize, SCREEN_WIDTH, squareSize);	//horizontal lines
				g.drawLine(i*UNIT_SIZE, 0, squareSize, SCREEN_HEIGHT);	//vertical lines
				
			}
			
			g.setColor(Color.RED);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			
			//drawing snake body
			for(int i=0; i<bodyParts; i++) {
				if(i==0) {
					g.setColor(Color.GREEN);
					g.fillRect(xCord[i], yCord[i], UNIT_SIZE, UNIT_SIZE);
				}
				else{
					g.setColor(new Color(131, 255, 122));
					g.fillRect(xCord[i], yCord[i], UNIT_SIZE, UNIT_SIZE);
				}
					
			}
			
			//score
			g.setColor(Color.RED);
			g.setFont(new Font(GAME_FONT, Font.BOLD,40));
			FontMetrics fontMetrics = getFontMetrics(g.getFont());
			g.drawString("Score: " + applesEaten, (SCREEN_WIDTH-fontMetrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
			
			//fix direction
			if(customAdapter.getNeedBool()){
				customAdapter.setNeedBool(false);
				char newDirection = KeyEvent.getKeyText(customAdapter.getLastCode()).substring(0,1).toCharArray()[0];	//retrieves the last direction 
				//System.out.println("changing direction to: " + newDirection);	//debug
				direction = newDirection;
			}
			
		}
		else {
			gameOver(g);
		}
		
		
	}
	
	public void addApple() {
		appleX = (rand.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE ) ;
		appleY = rand.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE) * UNIT_SIZE;
	}
	
	public void move() {
		for(int i=bodyParts; i>0; i--) {
			xCord[i] = xCord[i-1];
			yCord[i] = yCord[i-1];
		}
		/*loop starts at the tail
		 * the loop tells each block to replace the one closer to the head and it stops at the head
		 */
		
		switch (direction) {
		case 'U':
			yCord[0] = yCord[0]-UNIT_SIZE;
			break;
		case 'D':
			yCord[0] = yCord[0]+UNIT_SIZE;
			break;
		case 'L':
			xCord[0] = xCord[0]-UNIT_SIZE;
			break;
		case 'R':
			xCord[0] = xCord[0]+UNIT_SIZE;
			break;
		default:
			break;
		}
		/*++y -> down
		 *--y -> up
		 *++x -> right
		*/
	}
	
	public void checkApple() {
		if(xCord[0] == appleX && yCord[0] == appleY) {
			bodyParts++;
			applesEaten++;
			addApple();
		}
	}
	
	public void checkCollisions() {
		
		//checks if head touches body
		for(int i=bodyParts ; i>0; i--) {
			if(xCord[0] == xCord[i] && yCord[0] == yCord[i]) {
				running = false;
			}//head collided with body
		}
		//checks if border is touched
		if(xCord[0] < 0) {
			running = false;
		}
		if(xCord[0] > SCREEN_WIDTH-UNIT_SIZE) {
			running = false;
		}
		if(yCord[0] < 0) {
			running = false;
		}
		if(yCord[0] > SCREEN_HEIGHT-UNIT_SIZE) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
		
	}
	
	public void gameOver(Graphics g) {
		g.setColor(Color.RED);
		g.setFont(new Font(GAME_FONT, Font.BOLD,75));
		FontMetrics fontMetrics = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH-fontMetrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		
		g.setFont(new Font(GAME_FONT, Font.BOLD,40));
		FontMetrics fontMetrics2 = getFontMetrics(g.getFont());
		g.drawString("Score: " + applesEaten, (SCREEN_WIDTH-fontMetrics2.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		
		Integer lastCode;
		int currentX;
		int currentY;
		private boolean needToFixError = false;
		public boolean getNeedBool() {
			return needToFixError;
		}
		public void setNeedBool(boolean newVal) {
			needToFixError = newVal;
		}
		public int getLastCode() {
			return lastCode;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			//ignore duplicate inputs
			if(lastCode!=null && lastCode == e.getKeyCode()) {
				return;
			}
			
			//if the snake hasn't moved yet: store the value it was going to move to and ignore this input
			if(running && currentX == xCord[0] && currentY == yCord[0]) {
				//System.out.println("Key pressed: " + KeyEvent.getKeyText(e.getKeyCode()) + " Last Key: " + KeyEvent.getKeyText(lastCode));	//debug
				lastCode = e.getKeyCode();
				needToFixError = true;
				return;
			}
			
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if(direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if(direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if(direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if(direction != 'U') {
					direction = 'D';
				}
				break;
			case KeyEvent.VK_R:
				running = true;
				System.out.println("now running");	//not working
				Arrays.fill(xCord,0);
				Arrays.fill(yCord,0);
				direction = defDirection;
				applesEaten = defApplesEaten;
				bodyParts = defaultBodyParts;
				timer.start();
				repaint();
				break;
			default:
				break;
			}
			//save positions before the next change
				lastCode = e.getKeyCode();
				currentX = xCord[0];
				currentY = yCord[0];
			
		}
	}
	
	
}
