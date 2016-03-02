package GUI;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import Sound.SoundController;

public class GUIButton extends JComponent{
	private static final long serialVersionUID = 8333083985075123009L;

	private BufferedImage image = null;
	private BufferedImage imageHover = null;

	private Point position = new Point(0,0);
	private String buttonName;

	private String SOUND_BUTTON_HOVER = "buttonhover2.wav";
	private boolean hovering = false;

	public GUIButton(String fileName) {
		try {
			image = ImageIO.read(GUIButton.class.getResource("startMenuImages/" + fileName + ".png"));
			imageHover = ImageIO.read(GUIButton.class.getResource("startMenuImages/" + fileName + "2.png"));
			this.setSize(image.getWidth(),image.getHeight());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.buttonName = fileName;
	}

	public GUIButton(String directory, String fileName) {
		try {
			image = ImageIO.read(GUIButton.class.getResource(directory + fileName + ".png"));
			imageHover = ImageIO.read(GUIButton.class.getResource(directory + fileName + "2.png"));
			this.setSize(image.getWidth(),image.getHeight());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.buttonName = fileName;
	}

	protected void paintButton(Graphics g, int x, int y, Point mousePoint) {
		super.paintComponent(g);
		position.x = x;
		position.y = y;

		if( containsPoint(mousePoint) ){
			g.drawImage(imageHover, position.x, position.y, getWidth(), getHeight(), null);

			if( !hovering ){
				hovering = true;
				SoundController.play(SOUND_BUTTON_HOVER);
			}
		}
		else{
			g.drawImage(image, position.x, position.y, getWidth(), getHeight(), null);
			hovering = false;
		}
	}

	public boolean containsPoint(Point point){
		return imageHover != null &&
				point.x >= position.x &&
				point.x <= position.x+getWidth() &&
				point.y >= position.y &&
				point.y <= position.y+getHeight();
	}

	/**
	 * @return the buttonName
	 */
	public String getButtonName() {
		return buttonName;
	}
}
