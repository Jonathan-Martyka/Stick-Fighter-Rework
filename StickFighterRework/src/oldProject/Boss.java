package oldProject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Boss {
	private int x, y;
	private BufferedImage boss;
	public Boss(int x){
		this.x = x;
		y = 246;
		try {
			boss = ImageIO.read(new File("resources\\boss99(2).png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public int getX() {
		return x;
	}
	public int getY(){
		return y;
	}
	public BufferedImage getImage(){
		return boss;
	}
}
