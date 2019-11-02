package oldProject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class Medkit {
	private int x, y;
	private BufferedImage medi;
	public Medkit(int x){
		this.x = x;
		y = 246;
		try {
			medi = ImageIO.read(new File("resources\\medkit.png"));
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
	public BufferedImage getMedi(){
		return medi;
	}
}
