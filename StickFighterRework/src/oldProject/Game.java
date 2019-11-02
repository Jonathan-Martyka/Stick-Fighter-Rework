package oldProject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Game extends GameLoop {

	public Font f;
	public Font f1 = new Font("Times New Roman", Font.BOLD, 24);

	public void init() {
		setSize(854, 460);
		Thread thPlayer = new Thread(this);
		thPlayer.start();
		offscreen = createImage(854, 480);
		d = offscreen.getGraphics();
		addKeyListener(this);
		f = d.getFont();
	} 

	public void paint(Graphics g) {
		d.clearRect(0, 0, 854, 480);
		d.drawImage(background, 0, 0, this);
		if (newMedkit == true) {
			d.drawImage(medkit.getMedi(), medkit.getX(), medkit.getY(), this);
		}
//		d.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);
		for (int i = 0; i < enemyClass.size(); i++) {
			if (enemyClass.get(i).isAlive) {
				if (enemyClass.get(i).isAttacking && enemyClass.get(i).faceLeft) {
					d.drawImage(enemyClass.get(i).enemy,
							enemyClass.get(i).tempX, enemyClass.get(i).y, this);
				} else {
					d.drawImage(enemyClass.get(i).enemy, enemyClass.get(i).x,
							enemyClass.get(i).y, this);
				}
			}
		}
		if (isAttacking && faceLeft) {
			d.drawImage(player, tempX, y, this);
		} else
			d.drawImage(player, x, y, this);
		d.setFont(f1);
		d.drawString("Wave " + (currentWave + 1), 387, 103);
		d.setFont(f);
		d.drawString("Enemies Remaining: " + enemiesRemaining, 5, 20);
		d.drawString("Score: " + (int) Math.pow(nKilled, 1.5), 5, 40);
		drawHealthBar();
		if (isAlive == false) {
			gameOver();
		}
		g.drawImage(offscreen, 0, 0, this);
	}

	public void update(Graphics g) {
		paint(g);
	}

	// Draw health bar at top of screen
	public void drawHealthBar() {
		d.setColor(Color.GREEN);
		d.setFont(f1);
		d.drawString("Health", 387, 43);
		d.setFont(f);
		d.fillRect(227, 48, health * 100, 25);
		d.setColor(Color.BLACK);
		d.drawLine(327, 48, 327, 73);
		d.drawLine(427, 48, 427, 73);
		d.drawLine(527, 48, 527, 73);
		d.drawRect(227, 48, 400, 25);
	}

	// Draw "Game Over"
	public void gameOver() {
		d.setFont(new Font("Papyrus", Font.BOLD + Font.ITALIC, 32));
		d.setColor(Color.RED);
		d.drawString("Game Over", 340, 160);
		d.drawString("You Died", 360, 195);
		d.setFont(f);
		d.setColor(Color.BLACK);
	}
}
