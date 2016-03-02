package Sound;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SoundController {

	private static Mixer.Info[] mixInfos = AudioSystem.getMixerInfo();
	private static Mixer mixer = AudioSystem.getMixer(mixInfos[0]);
	private static DataLine.Info dataInfo = new DataLine.Info(Clip.class, null);
	private static boolean muted = false;
	
	public static void play(String fileName){
		if( muted ){
			return;
		}
		
		try {
			URL soundURL = SoundController.class.getResource(fileName);
			if( soundURL == null ){
				throw new IOException("could not find File: " + fileName);
			}
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundURL);

			Clip clip = (Clip)mixer.getLine(dataInfo);
			clip.open(audioStream);
			clip.start();


		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		final String PICKUP = "pickup.wav";
		final String DROP = "drop.wav";
		final String ZAP = "inventory_toggle2.wav";


		JFrame frame = new JFrame("FGEWG");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,200);

		JPanel panel = new JPanel();
		JButton pickup = new JButton("Pickup");
		pickup.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				play(ZAP);

			}
		});
		panel.add(pickup);

		JButton drop = new JButton("Drop");
		drop.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				play(DROP);

			}
		});
		panel.add(drop);
		frame.add(panel);

		frame.setVisible(true);
	}
}
