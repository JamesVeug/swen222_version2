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
public class FloatingNoPower {

	private static boolean increase = true;
	private static int delay = 0;
	private static int totalAnimations = 5;
	private static int animationNum = 0;

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
	public static void reDraw(Graphics g, Point pt, int width, int height, Point offset) {

		BufferedImage img = MakeImageMap.images.get("NoPower" + animationNum);
		int imgHeight = ((int) img.getHeight(null) / 250);

		g.drawImage(img, offset.x + pt.x - width, offset.y + pt.y
				- ((width * imgHeight)), width * 2, height * imgHeight, null);

		delay++;
		if (delay == 6){
			delay = 0;
			

			if( increase ) animationNum++; else animationNum--;
			if( animationNum == totalAnimations || animationNum == 0 ){
				increase = !increase ;
			}
		}

	}

}
