package GUI;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class GUITextButton extends GUIButton{
	private static final long serialVersionUID = 8333083985075123009L;
	private String text = "127.0.0.1";
	
	public GUITextButton(String fileName, String text) {
		super(fileName);
		this.text = text;
	}

	protected void paintButton(Graphics g, int x, int y, Point mousePoint) {
		super.paintButton(g,x,y,mousePoint);
		
		g.drawString(text, x+20, y+20);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
