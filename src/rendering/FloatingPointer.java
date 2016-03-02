package rendering;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Draws a floating animated pointer above the avatars head.
 *
 * @author Leon North
 *
 */
public class FloatingPointer {

	private int delay = 0;
	private int totalAnimations = 6;
	private int animationNum = 0;
	private Map<String, BufferedImage> images;

	public FloatingPointer() {
		images = MakeImageMap.images;
	}

	/**
	 * Draws a floating animated pointer above the avatars head.
	 *
	 * @param g
	 * @param pt
	 * @param width
	 * @param height
	 * @param offset :
	 * @author Leon North
	 */
	public void reDraw(Graphics g, Point pt, int width, int height, Point offset) {

		BufferedImage img = images.get("FloatingPointer" + animationNum);
		int imgHeight = ((int) img.getHeight(null) / 250);

		g.drawImage(img, offset.x + pt.x - width, offset.y + pt.y
				- ((width * imgHeight)), width * 2, height * imgHeight, null);

		delay++;
		if (delay == 6){
			delay = 0;
			animationNum = (animationNum + 1) % totalAnimations;
		}

	}

}
