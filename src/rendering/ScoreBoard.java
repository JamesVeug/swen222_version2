package rendering;

import gameLogic.Score;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map.Entry;

import javax.swing.JPanel;

/**
 * Draws the scoreBoard to the Panel
 *
 * @author Leon North
 *
 */
public class ScoreBoard {

	JPanel panel;
	double width;
	double height;
	double buffer;
	double cellHeight;
	double cellWidth;
	public static final double STARTWIDTH = 1280;
	public static final double STARTSCOREHEIGHT = 135;
	public static final int NUMCARDS = 6;

	public ScoreBoard(JPanel panel) {
		this.panel = panel;
	}

	/**
	 * Draws the scoreBoard to the Panel
	 * @param g : Graphics object
	 * @param score : score object with a map of all scores in it
	 * @author Leon North
	 */
	public void redraw(Graphics g, Score score){

		width = ((panel.getWidth() / STARTWIDTH) * STARTSCOREHEIGHT);
		height = width;
		buffer = (width / STARTSCOREHEIGHT) * 10;
		int x = (int) width * NUMCARDS + (int)(buffer*2);
		int y = (int)(panel.getHeight() - height - (buffer/2));

		g.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		g.fillRoundRect(x, y, (int)width, (int)height, (int)buffer, (int)buffer);

		g.setColor(Color.WHITE);
		int i = 1;
		if (score != null){
			for (Entry<String, Integer> entry: score.getScore().entrySet()){
				//String score = avatars.get(i).getPlayerName()+": "+avatars.get(i).getScore();
				g.drawString(entry.getKey()+": "+entry.getValue(), (int)(x+buffer), (int)(y + i*(buffer*2)));
				i++;
			}
		}
	}
}
