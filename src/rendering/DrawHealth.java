package rendering;

import gameLogic.Avatar;
import gameLogic.Cell;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Draws the health on the bottom of the panel.
 *
 * @author Leon North
 *
 */
public class DrawHealth {

	private JPanel panel;
	private double width;
	private double height;
	private double buffer;
	private static final double STARTWIDTH = 1280;
	private static final int MAXHEALTH = 500;
	private static BufferedImage image = null;

	public DrawHealth(JPanel panel) {
		this.panel = panel;
		try {
			image = ImageIO.read(DrawHealth.class.getResource("Interface_Health.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Draws the Health bar on the panel.
	 *
	 * @param g : Graphics object
	 * @param avatar : Avatar object, the current player.
	 */
	public void redraw(Graphics g, Avatar avatar){
		width = ((panel.getWidth() / STARTWIDTH) * image.getWidth());
		height = ((panel.getWidth() / STARTWIDTH) * image.getHeight());

		int x = (int)(panel.getWidth() - (width * 4));
		int y = (int)(panel.getHeight() - height);

		//width = width /2;

//		g.setColor(Color.RED);
//		g.drawString(avatar.getCell().getBatteryLife()+"", x+10, y+20);

		Cell cell = avatar.getCell();
		int health = cell.getBatteryLife();
		int red = 255 - (int)(255*(float)health/(float)cell.getMaxBatteryLife());
		int green = (int)(255*((float)health/(float)cell.getMaxBatteryLife()));
		int blue = 0;
		
		g.setColor(new Color(red,green,blue));
		/*if (health < (MAXHEALTH/4.0)){
			g.setColor(new Color(200,10,10));
		}*/

		int healthBarHeight = (int)(((height*1.0)/MAXHEALTH*1.0) * health);
		int healthBarOffset = (int)(((height*1.0)/MAXHEALTH*1.0) * MAXHEALTH)-healthBarHeight;
		g.fillRect((int)(x+4), (int)(y+4 + healthBarOffset), (int)(width-8), healthBarHeight-8);
		
		// Draw Image
		g.drawImage(image, x, y, (int)width, (int)height, null);
	}
}
