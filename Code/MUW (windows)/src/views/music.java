package views;

import java.io.File;
import java.net.URL;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

@SuppressWarnings("unused")
public class music {

	public static void playTheme(URL url){
		
	    try{
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(url));
	        FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        volume.setValue(-1 * 20);
	        clip.start();
	    } catch (Exception e){
	        e.printStackTrace();
	    }
	    
	}
	
	public static void playSound(URL url){
		
	    try{
	        Clip clip = AudioSystem.getClip();
	        clip.open(AudioSystem.getAudioInputStream(url));
	        clip.start();
	    } catch (Exception e){
	        e.printStackTrace();
	    }
	    
	}
	
}
