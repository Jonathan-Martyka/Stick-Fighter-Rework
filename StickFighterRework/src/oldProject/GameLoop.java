package oldProject;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GameLoop extends Applet implements Runnable, KeyListener {

	public int x, y, currentWave, health, nKilled, tempX;
	public Image offscreen;
	public Graphics d;
	public boolean jump, down, left, right, faceLeft, isAttacking, isAlive;
	public BufferedImage background, player;
	public BufferedImage[] walk = new BufferedImage[12];
	public BufferedImage[] attack = new BufferedImage[4];
	public int counterMain, counterAttack;
	public double counterGrav = 4;
	public boolean falling;
	public EnemyLoop tempEnemy;
	public volatile ArrayList<EnemyLoop> enemyClass = new ArrayList<EnemyLoop>();
	public volatile ArrayList<Thread> enemyThread = new ArrayList<Thread>();
	public volatile int enemiesRemaining;
	public Medkit medkit;
	public Boss boss = new Boss(100);
	public boolean newMedkit, mediAvailable;
	public int errorNumber = 0;
	public Audio sound = new Audio();
	
	public void playSound(String fileName){
		sound.play(fileName);
	}

	// Loading player animation images
	public void loadImages() {
		try {
			background = ImageIO.read(new File("resources\\background.png"));
			for (int i = 0; i < walk.length; i++)
				// Right: 0-3, stance 8, jump 10. Left: 4-7, stance 9, jump 11
				walk[i] = ImageIO.read(new File("resources\\walk" + i + "(edit).png"));
			for (int i = 0; i < attack.length; i++)
				// Right: 0-1. Left: 2-3
				attack[i] = ImageIO.read(new File("resources\\pAttack" + i + ".png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// Create amount of enemies for current wave
	public void initializeEnemies(final int amount) {
		new Thread(new Runnable() {
			public void run() {
				enemiesRemaining = amount;
				int startX;
				for (int i = 0; i < amount; i++) {
					if ((int) (Math.random() * 2) + 1 == 1)
						startX = 904;
					else
						startX = -50;
					tempEnemy = new EnemyLoop(startX);
					enemyThread.add(new Thread(tempEnemy));
					enemyThread.get(i).start();
					enemyClass.add(tempEnemy);
					tempEnemy = null;
					try {
						if(currentWave < 18){
							Thread.sleep(1000-(currentWave*50));
						}
						else{
							Thread.sleep(100);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		stop();

	}

	// Clear dead enemies and initialize enemies for new wave
	public void newWave() {
		for (int i = enemyClass.size() - 1; i >= 0; i--) {
			enemyThread.remove(i);
			enemyClass.remove(i);
		}
		currentWave++;
		enemiesRemaining = 4 + (int) (Math.sqrt(currentWave) * 10);
		initializeEnemies(enemiesRemaining);
		if (mediAvailable == false && (int) (Math.random() * 3) == 0) {
			mediAvailable = true;
		}
	}

	// Player attempts to hit enemies
	@SuppressWarnings("deprecation")
	public void attack() {
		try {
			int currentXE;
			int eWidth;
			int currentXP;
			int pWidth = player.getWidth();
			if (faceLeft) {
				currentXP = tempX;
			} else {
				currentXP = x;
			}
			for (int i = 0; i < enemyClass.size(); i++) {
				if (enemyClass.get(i).isAlive && y > enemyClass.get(i).y - 50) {
					currentXE = enemyClass.get(i).x;
					eWidth = enemyClass.get(i).enemy.getWidth();
					if (faceLeft
							&& (currentXE + eWidth >= currentXP && currentXE
									+ eWidth <= currentXP + pWidth)
							|| (currentXE <= currentXP && currentXE + eWidth >= currentXP
									+ pWidth)) {
						enemyClass.get(i).isAlive = false;
						enemyThread.get(i).stop();
						enemiesRemaining--;
						nKilled++;
					} else if (!faceLeft && currentXE - currentXP < pWidth
							&& currentXE - currentXP > 0) {
						enemyClass.get(i).isAlive = false;
						enemyThread.get(i).stop();
						enemiesRemaining--;
						nKilled++;
					}
				}
			}
			if (enemiesRemaining == 0) {
				newWave();
			}
		} catch (NullPointerException e) {
			System.out.println(e
					+ " on enemyClass.get(i).enemy.getWidth() in attack() Occurrence #" + ++errorNumber);
			attack();
		}
	}

	// Player action loop
	public void run() {
		isAlive = true;
		x = 420;
		y = 330;
		loadImages();
		player = walk[8];
		health = 4;
		currentWave = 0;
		enemiesRemaining = 4;
		faceLeft = false;
		isAttacking = false;
		nKilled = 0;
		counterAttack = 0;
		newMedkit = false;
		mediAvailable = false;
		initializeEnemies(enemiesRemaining);
		playSound("C:/Users/Jonathan/Documents/School/Junior Year/Semester 2/APCS/New folder/2D Fighting Game/src/Goexor_-_Poi.wav");

		while (isAlive) {
			if (y < 330 && jump != true) {
				y += 10;
			}
			counterMain++;
			if (counterMain >= 20) {
				counterMain = 0;
			}
			if (isAttacking == false) {
				if (right == true) {
					switch (counterMain) {
					case 0:
						player = walk[0];
						break;
					case 5:
						player = walk[1];
						break;
					case 10:
						player = walk[2];
						break;
					case 15:
						player = walk[3];
						break;
					}
					x += 4;
				}
				if (left == true) {
					switch (counterMain) {
					case 0:
						player = walk[4];
						break;
					case 5:
						player = walk[5];
						break;
					case 10:
						player = walk[6];
						break;
					case 15:
						player = walk[7];
						break;
					}
					x -= 4;
				}
			} else {
				if (jump = true) {
					jump = false;
					if (y < 330) {
						falling = true;
						counterGrav = 4;
					}
				}
				if (!faceLeft) {
					switch (counterAttack) {
					case 0:
						player = attack[0];
						break;
					case 5:
						player = attack[1];
						attack();
						break;
					}
				} else {
					switch (counterAttack) {
					case 0:
						player = attack[2];
						tempX = x - player.getWidth() + walk[9].getWidth();
						break;
					case 5:
						player = attack[3];
						tempX = x - player.getWidth() + walk[9].getWidth();
						attack();
						break;
					}
				}
				counterAttack++;
				if (counterAttack >= 10) {
					counterAttack = 0;
					if (!faceLeft)
						player = walk[8];
					else
						player = walk[9];
					isAttacking = false;
				}
			}
			if (jump == true) {
				counterGrav += 0.05;
				y += (int) ((Math.sin(counterGrav) + Math.cos(counterGrav)) * 5);
				if (counterGrav >= 7)
					counterGrav = 4;
				if (!faceLeft == true)
					player = walk[10];
				if (faceLeft == true)
					player = walk[11];
			}
			if (y > 330) {
				y = 330;
			}
			if (y == 330 && falling == true) {
				falling = false;
				if (!faceLeft == true)
					player = walk[8];
				if (faceLeft == true)
					player = walk[9];
			}
			if (x < 0) {
				x = 0;
			} else if (x > 854 - player.getWidth()) {
				x = 854 - player.getWidth();
			}
			if (newMedkit == true && health < 4) {
				if (Math.abs(x - medkit.getX()) < medkit.getMedi().getWidth()
						&& y - medkit.getY() < medkit.getMedi().getHeight()) {
					newMedkit = false;
					medkit = null;
					health++;
				}
			}
			if (newMedkit == false && mediAvailable) {
				medkit = new Medkit((int) (Math.random() * 617 + 100));
				mediAvailable = false;
				newMedkit = true;
			}
			repaint();
			for (int i = 0; i < enemyClass.size(); i++) {
				enemyClass.get(i).updatePCoordinates(x, y, player.getWidth());
				if (enemyClass.get(i).damage == true) {
					health--;
					enemyClass.get(i).damage = false;
					if (health <= 0) {
						isAlive = false;
						break;
					}
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Check if certain keys are pressed
	public void keyPressed(KeyEvent e) {
		// Press left arrow key
		if (e.getKeyCode() == 37) {
			left = true;
			faceLeft = true;
		}
		// Press up arrow key
		if (e.getKeyCode() == 38) {
			if (falling == false) {
				jump = true;
			}
		}
		// Press right arrow key
		if (e.getKeyCode() == 39) {
			right = true;
			faceLeft = false;
		}
		// Press space bar
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			if (isAttacking == false) {
				isAttacking = true;
				tempX = x - player.getWidth() + walk[9].getWidth();
			}
		}
	}

	// Check if certain pressed keys are released
	public void keyReleased(KeyEvent e) {
		// Release left arrow key
		if (e.getKeyCode() == 37) {
			left = false;
			player = walk[9];
		}
		// Release up arrow key
		if (e.getKeyCode() == 38) {
			jump = false;
			if (y < 330) {
				falling = true;
				counterGrav = 4;
			}
		}
		// Release right arrow key
		if (e.getKeyCode() == 39) {
			right = false;
			player = walk[8];
		}
	}

	public void keyTyped(KeyEvent e) {

	}

}

/*-----------------------------End of GameLoop-----------------------------
 ---------------------------------------------------------------------------
 -------------------------------Start of EnemyLoop------------------------*/

class EnemyLoop extends Applet implements Runnable {

	public int x, y, tempX, speed;
	public int xP, yP, widthP;
	public int counterEnemy, counterAttack;
	public BufferedImage[] walk = new BufferedImage[8];
	public BufferedImage enemy = walk[0];
	public BufferedImage[] attack = new BufferedImage[4];
	public boolean isAlive = true;
	public boolean isAttacking, faceLeft;
	public boolean damage = false;

	// Constructor with starting enemy x coordinate
	public EnemyLoop(int startX) {
		x = startX;
	}

	// Loading enemy animation images
	public void loadImages() {
		try {
			for (int i = 0; i < walk.length; i++)
				// Right: 0-3. Left: 4-7
				walk[i] = ImageIO.read(new File("resources\\enemy" + i + "(edit).png"));
			for (int i = 0; i < attack.length; i++)
				// Right: 0-1. Left: 2-3
				attack[i] = ImageIO.read(new File("resources\\eAttack" + i + ".png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// Update enemy awareness of player's x and y coordinates as well as
	// player's width
	public void updatePCoordinates(int xValue, int yValue, int width) {
		xP = xValue;
		yP = yValue;
		widthP = width;
	}

	// Enemy attempts to attack player
	public void enemyAttack() {
		int widthE = enemy.getWidth();
		if (y - yP < 50) {
			if (faceLeft && xP + widthP >= tempX
					&& xP + widthP <= tempX + widthE) {
				damage = true;
			} else if (!faceLeft && xP - x < widthE && xP - x > 0) {
				damage = true;
			}
		}
	}

	// Enemy action loop
	public void run() {
		loadImages();
		y = 330;
		speed = (int) (Math.random() * 3 + 1);
		isAttacking = false;
		counterAttack = 0;

		if (x <= 0) {
			faceLeft = false;
			enemy = walk[0];
		} else {
			faceLeft = true;
			enemy = walk[4];
		}

		while (isAlive) {
			if (isAttacking == false) {
				counterEnemy++;
				if (counterEnemy >= 20) {
					counterEnemy = 0;
				}
				if (x < xP) {
					if (faceLeft == true) {
						faceLeft = false;
					}
					switch (counterEnemy) {
					case 0:
						enemy = walk[0];
						break;
					case 5:
						enemy = walk[1];
						break;
					case 10:
						enemy = walk[2];
						break;
					case 15:
						enemy = walk[3];
						break;
					}
					x += speed;
				}
				if (x > xP) {
					if (faceLeft == false) {
						faceLeft = true;
					}
					switch (counterEnemy) {
					case 0:
						enemy = walk[4];
						break;
					case 5:
						enemy = walk[5];
						break;
					case 10:
						enemy = walk[6];
						break;
					case 15:
						enemy = walk[7];
						break;
					}
					x -= speed;
				}
				if (Math.abs(x - xP) < 70 && y - yP < 50) {
					isAttacking = true;
					tempX = x - enemy.getWidth() + walk[0].getWidth();
				}
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				if (!faceLeft) {
					switch (counterAttack) {
					case 0:
						enemy = attack[0];
						break;
					case 5:
						enemy = attack[1];
						enemyAttack();
						break;
					}
				} else {
					switch (counterAttack) {
					case 0:
						enemy = attack[2];
						tempX = x - enemy.getWidth() + walk[0].getWidth();
						break;
					case 5:
						enemy = attack[3];
						tempX = x - enemy.getWidth() + walk[0].getWidth();
						enemyAttack();
						break;
					}
				}
				counterAttack++;
				if (counterAttack >= 10) {
					counterAttack = 0;
					if (!faceLeft)
						enemy = walk[0];
					else
						enemy = walk[4];
					isAttacking = false;
				}
				try {
					Thread.sleep(110 - (speed * 10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// repaint();

		}
	}
}