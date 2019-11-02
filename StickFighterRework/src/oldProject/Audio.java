package oldProject;

import java.io.*;

import sun.audio.*;
public class Audio{
	
	public void play(String fileName){
		try {
			playAudio(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
private void playAudio(String fileName)
throws Exception{
	String wav_file = fileName;
	InputStream in = new FileInputStream(wav_file);
	
	//Commented out because this is deprecated and no longer works
//	AudioStream audio = new AudioStream(in);
//	AudioPlayer.player.start(audio);
}
}