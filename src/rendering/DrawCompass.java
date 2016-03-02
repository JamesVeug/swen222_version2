package rendering;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Draws the compass on the panel.
 *
 * @author Leon North
 *
 */
public class DrawCompass {

	private JPanel panel;

	public DrawCompass(JPanel panel) {
		this.panel = panel;
	}

	/**
	 * Draws the compass to the panel
	 *
	 * @param g: Graphics object
	 * @param direction: Direction to draw the compass in.
	 * @author Leon North
	 */
	public void redraw(Graphics g, Direction direction){

		if (direction == Direction.EAST ){
			direction = Direction.WEST;
		}
		else if (direction == Direction.WEST ){
			direction = Direction.EAST;
		}

		java.net.URL imageURL = DrawCompass.class.getResource("Compass"+direction+".png");
		BufferedImage img = null;
		try {
			img = ImageIO.read(imageURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int size = (int)((panel.getWidth()/1280.0) * 150);
		int buffer = (int) ((panel.getWidth() / 1280.0) * 10);
		g.drawImage(img, panel.getWidth() - size - buffer, panel.getHeight() - size - buffer, size, size, null);
	}
}
