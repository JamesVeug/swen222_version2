package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import networking.GameClient;
import networking.Player;

/**
 * The JFrame class which wraps around the whole game. This launches various things like the panel, top menu
 * Setting up default exit options and window sizes.
 * @author Daphne Wang
 */

public class WindowFrame extends JFrame implements WindowListener{
	private DrawingPanel panel = new DrawingPanel(this);


	public WindowFrame() {
		super("An Adventure Game");
		add(panel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1280, 720);
		setMinimumSize(new Dimension(800, 530));
		setVisible(true);
	}


	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent arg0) {
		panel.quit();		
	}


	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}