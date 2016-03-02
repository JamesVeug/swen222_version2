package GUI;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import GUI.DrawingPanel.States;
import Sound.SoundController;
import rendering.Direction;
import networking.ChatMessage;
import networking.Move;
/**
 * Class deals with anything to do with the keyboard, from moving around, typing in the chat box, changing angles
 * and activating/deactivating the help screen
 * @author Daphne Wang and Leon North
 */
public class KeyBoard implements KeyListener{

	private DrawingPanel panel;
	private Set<Integer> keysDown = new HashSet<Integer>();

	private String SOUND_INVENTORY_TOGGLE = "inventory_toggle2.wav";
	
	
	public KeyBoard(DrawingPanel d){
		panel = d;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	/**
	 * A method for the key board class which responds to key pressing
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		boolean previouslyDown = false;
		
		// Shift does nothing
		if (e.getKeyCode() == KeyEvent.VK_SHIFT){ return;}
		
		// Record key
		if ( !keysDown.contains(e.getKeyCode()) ){
			keysDown.add(new Integer(e.getKeyCode()));
			previouslyDown = true;
			
		}
		
		// Enter Chat mode
		if(panel.isChatMode()){
			panel.addToCurrentMessage( Character.toString(e.getKeyChar()) );
		}
		
		if( previouslyDown ){
			actionKeys(e, "Start");
		}
	}


	/**
	 * Responds to pressing specfic keys on the keyboard
	 * @author Daphne Wang and Leon North
	 * @param e2 
	 */
	private void actionKeys(KeyEvent event, String action) {

		if (event.getKeyCode() == (KeyEvent.VK_ALT)){
			boolean b = panel.isChatMode();
			panel.setChatMode(!b);
			panel.setCurrentMessage("");
		}
		if (event.getKeyCode() == (KeyEvent.VK_Z)){
			boolean b = panel.isScoreBoard();
			panel.setScoreBoard(!b);
		}
		if (event.getKeyCode() == (KeyEvent.VK_I)){
			boolean b = panel.showInventory();
			panel.setShowInventory(!b);
			new SoundController().play(SOUND_INVENTORY_TOGGLE);
		}
		if ( panel.isChatMode() ){

			if (event.getKeyCode() == (KeyEvent.VK_ENTER)){
				try {
					(panel.getGameClient()).sendChatMessageToServer( panel.getCurrentMessage() );
				} catch (IOException e) {
					e.printStackTrace();
				}
				panel.setCurrentMessage("");
			} else if (event.getKeyCode() == (KeyEvent.VK_BACK_SPACE)){
				String message = panel.getCurrentMessage();
				if (message.length() > 1){panel.setCurrentMessage(message.substring(0, message.length()-2));}
				else if (message.length() == 1){panel.setCurrentMessage("");}
			}

		}
		else{
			if (event.getKeyCode() == (KeyEvent.VK_CONTROL)) {
				int d = panel.getDirection();
				panel.setDirection( (d+1)%4 );
				//sendDirection();
			}
			if(event.getKeyCode() == (KeyEvent.VK_W)){
				moveForward(action);
			}
			if(event.getKeyCode() == (KeyEvent.VK_A)){
				moveLeft(action);
			}
			if(event.getKeyCode() == (KeyEvent.VK_S)){
				moveBack(action);
			}
			if(event.getKeyCode() == (KeyEvent.VK_D)){
				moveRight(action);
			}
			if(event.getKeyCode() == (KeyEvent.VK_O)){
				Charge(action);
			}
			if(event.getKeyCode() == (KeyEvent.VK_ENTER)){
				Interact(action);
			}
			if(event.getKeyCode() == (KeyEvent.VK_ESCAPE)){
				if(panel.getState() == States.MainMenu){
					panel.setState(States.Game);
				}
				else{
					panel.setState(States.MainMenu);
				}
			}

		}
	}

	public void Interact(String action) {
		Move move = new Move((panel.getGameClient()).getPlayer(), "interact", "");

		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Helper method which sends direction to the server to register local changes
	 */
	private void sendDirection() {
		/*if (dir.toLowerCase().equals("east")){ dir = "West";}
		else if (dir.toLowerCase().equals("west")){ dir = "East";}
		Move move = new Move((panel.getGameClient()).getPlayer(), "");
		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * Takes a direction
	 * @param direction
	 * @return
	 */
	private Direction calcDirection(String moveCommand){
		int dir = panel.getDirection();
		int key = Direction.getKeyDirection(moveCommand);
		int change = dir + key;
		change = change % 4;
		return Direction.get(change);
	}

	/**
	 * Method to deal with charging the battery/attacking with your avatar.
	 * @author Leon North
	 */
	private void Charge(String action) {
		Move move = new Move((panel.getGameClient()).getPlayer(), "O", action);

		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author Daphne Wang and Leon North
	 */
	private void moveRight(String action) {
		String moveCommand = Direction.getMove(1);
		Move move = new Move(panel.getGameClient().getPlayer(), Direction.getMove(calcDirection(moveCommand)), action);

		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * @author Daphne Wang and Leon North
	 */
	private void moveBack(String action) {
		String moveCommand = Direction.getMove(2);
		Move move = new Move(panel.getGameClient().getPlayer(), Direction.getMove(calcDirection(moveCommand)), action);

		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	/**
	 * @author Daphne Wang and Leon North
	 */
	private void moveLeft(String action) {
		String moveCommand = Direction.getMove(3);
		Move move = new Move(panel.getGameClient().getPlayer(), Direction.getMove(calcDirection(moveCommand)), action);

		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @author Daphne Wang and Leon North
	 */
	private void moveForward(String action) {
		String moveCommand = Direction.getMove(0);
		Move move = new Move(panel.getGameClient().getPlayer(), Direction.getMove(calcDirection(moveCommand)), action);
		
		try {
			panel.getGameClient().sendMoveToServer(move);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		keysDown.remove(e.getKeyCode());
		if(e.getKeyCode() == (KeyEvent.VK_W) || 
				e.getKeyCode() == (KeyEvent.VK_A) ||
				e.getKeyCode() == (KeyEvent.VK_S) ||
				e.getKeyCode() == (KeyEvent.VK_D)){
			actionKeys(e, "Stop");
		}

	}
}
