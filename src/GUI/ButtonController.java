package GUI;

import gameLogic.Container;
import gameLogic.Directional;
import gameLogic.Mountable;
import gameLogic.Toggleable;
import gameLogic.Items.Consumable;
import gameLogic.Items.Item;
import gameLogic.Items.Pickupable;
import gameLogic.Items.Rotatable;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;

import rendering.Direction;
import networking.Move;
import GUI.DrawingPanel.States;
import Sound.SoundController;

public class ButtonController {

	private static final String SOUND_BUTTON_PRESS = "buttonpress.wav";

	public DrawingPanel panel;

	GUITextButton connectIPButton = new GUITextButton("blank","127.0.0.1");

	ArrayList<GUIButton> mainMenuButtons = new ArrayList<GUIButton>();
	ArrayList<GUIButton> startButtons = new ArrayList<GUIButton>();
	ArrayList<GUIButton> joinButtons = new ArrayList<GUIButton>();
	ArrayList<GUIButton> inGameButtons = new ArrayList<GUIButton>();
	ArrayList<GUIButton> quitButtons = new ArrayList<GUIButton>();

	ArrayList<GUIButton> itemInteractButtons = new ArrayList<GUIButton>();

	GUIButton backButton = new GUIButton("back");

	public ButtonController(DrawingPanel panel) {
		this.panel = panel;

		mainMenuButtons.add(new GUIButton("start"));
		mainMenuButtons.add(new GUIButton("load"));
		mainMenuButtons.add(new GUIButton("join"));
		mainMenuButtons.add(new GUIButton("help"));
		mainMenuButtons.add(new GUIButton("quit"));
		startButtons.add(new GUIButton("singleplayer"));
		startButtons.add(new GUIButton("multiplayer"));
		joinButtons.add(connectIPButton);
		joinButtons.add(new GUIButton("confirm"));
		joinButtons.add(new GUIButton("back"));
		inGameButtons.add(new GUIButton("disconnect"));
		inGameButtons.add(new GUIButton("save"));
		inGameButtons.add(new GUIButton("help"));
		inGameButtons.add(new GUIButton("quit"));
		quitButtons.add(new GUIButton("yes"));
		quitButtons.add(new GUIButton("no"));

		itemInteractButtons.add(new GUIButton("inGameImages/","inventory_use"));
		itemInteractButtons.add(new GUIButton("inGameImages/","inventory_open"));
		itemInteractButtons.add(new GUIButton("inGameImages/","inventory_toggle"));
		itemInteractButtons.add(new GUIButton("inGameImages/","inventory_unmount"));
		itemInteractButtons.add(new GUIButton("inGameImages/","inventory_rotate"));
		itemInteractButtons.add(new GUIButton("inGameImages/","inventory_drop"));
		for(GUIButton b : itemInteractButtons){
			b.setSize(new Dimension((int)(b.getSize().getWidth()/2), (int)(b.getSize().getHeight()/2)));
		}
	}

	public void drawBackButton(Graphics g, Point mousePoint){
		backButton.paintButton(g, panel.getWidth() - (backButton.getWidth() + 5),
		panel.getHeight() - backButton.getHeight() - 5, mousePoint);
	}

	public void drawMenuButtons(Graphics g, Point mousePoint){
		// Main Menu
		if( panel.getState() == States.MainMenu ){

			if( !panel.isInGame() ){
				for( int i = 0; i < mainMenuButtons.size(); i++){
					GUIButton button = mainMenuButtons.get(i);
					button.paintButton(g, panel.getWidth()/2 - (button.getWidth()/2),
							panel.getHeight()/mainMenuButtons.size() - button.getHeight()/2 + (i*(panel.getHeight()/3)/2), mousePoint);
				}
			}
			else{
				for( int i = 0; i < inGameButtons.size(); i++){
					GUIButton button = inGameButtons.get(i);

					// Restrict save
					if( button.getButtonName().equals("save") && panel.getGameServer() == null ){
						continue;
					}
					button.paintButton(g, panel.getWidth()/2 - (button.getWidth()/2),
							panel.getHeight()/inGameButtons.size() - button.getHeight()/2 + (i*(panel.getHeight()/3)/2), mousePoint);
				}
			}
		}
		// Start
		else if( panel.getState() == States.Start ){
			for( int i = 0; i < startButtons.size(); i++){
				GUIButton button = startButtons.get(i);
				button.paintButton(g, panel.getWidth()/2 - (button.getWidth()/2),
						panel.getHeight()/joinButtons.size() - button.getHeight()/2 + (i*(panel.getHeight()/3)/2), mousePoint);
			}
			drawBackButton(g,mousePoint);
		}
		// JOIN
		else if( panel.getState() == States.Join ){
			for( int i = 0; i < joinButtons.size(); i++){
				GUIButton button = joinButtons.get(i);
				button.paintButton(g, panel.getWidth()/2 - (button.getWidth()/2),
						panel.getHeight()/joinButtons.size() - button.getHeight()/2 + (i*(panel.getHeight()/3)/2), mousePoint);
			}
			drawBackButton(g,mousePoint);
		}
		// QUIT
		else if( panel.getState() == States.Quit ){
			for( int i = 0; i < quitButtons.size(); i++){
				GUIButton button = quitButtons.get(i);
				button.paintButton(g, panel.getWidth()/2 - (button.getWidth()/2),
						panel.getHeight()/quitButtons.size() - button.getHeight()/2 + (i*(panel.getHeight()/3)/2), mousePoint);
			}
			drawBackButton(g,mousePoint);
		}
	}

	public void drawInGameButtons(Graphics g, Point mousePoint){
		if( !panel.showInventory() ) return;

		// IN-GAME
		Item item = null;
		int buttonHeight = itemInteractButtons.get(0).getHeight();
		int width = panel.getInventory().getCellWidth();

		int x = 0;
		int y = 0;


		int cell = panel.getInventory().findCellIndex(mousePoint.x, mousePoint.y);
		if( cell != -1 && panel.getGameClient().getAvatar().getInventory().size() > cell ){

			item = panel.getGameClient().getAvatar().getInventory().get(cell);
			x = panel.getInventory().getCellX(cell);
			y = panel.getInventory().getCellY(cell) - buttonHeight;
		}

		// Draw Buttons
		if( item != null ){
			for( int i = 0; i < itemInteractButtons.size(); i++){
				if( itemInteractButtonVisible(i, item) ) {
					itemInteractButtons.get(i).paintButton(g, x, y+=buttonHeight, mousePoint);
				}
			}
		}
	}

	public Item getItemOnScreen(Point mousePoint){
		int cell = panel.getInventory().findCellIndex(mousePoint.x, mousePoint.y);
		if( cell != -1 && panel.getGameClient().getAvatar().getInventory().size() > cell ){

			return panel.getGameClient().getAvatar().getInventory().get(cell);
		}
		else{

			Point mapPoint = panel.getDrawWorld().getConvertedPoint(mousePoint.x, mousePoint.y);


			// Mouse not on inventory
			return panel.getGameClient().getRoom().getItemAt(mapPoint.x, mapPoint.y);
		}
	}

	public void drawButtons(Graphics g, Point mousePoint) {

		if( panel.getState() != States.Game ){
			drawMenuButtons(g,mousePoint);
		}
		else{
			drawInGameButtons(g, mousePoint);
		}

	}

	private boolean itemInteractButtonVisible(int index, Item item){

		// Use
		if( index == 0 ){
			if( item instanceof Consumable ){
				if( item instanceof Pickupable ){
					return ((Pickupable)item).isPickedUp();
				}
				return true;
			}
			return false;
		}

		// Open
		else if( index == 1 ){ return item instanceof Container && ((Container)item).hasItems(); }

		// Toggle
		else if( index == 2 ){ return item instanceof Toggleable; }

		// Unmount
		else if( index == 3 ){ return item instanceof Mountable && ((Mountable)item).isMounted(); }

		// Rotate
		else if( index == 4 ){ return item instanceof Rotatable; }

		// Drop
		else if( index == 5 ){ return true; }

		return false;
	}

	/**
	 * Player tried pressing a button on the menu
	 * @param mousePoint
	 */
	public void buttonPressed(Point mousePoint){
		GUIButton button = null;
		if( panel.getState() == States.MainMenu ){

			// Main Menu
			if( !panel.isInGame() ){
				for( int i = 0; i < mainMenuButtons.size(); i++){
					if( mainMenuButtons.get(i).containsPoint(mousePoint) ){
						button = mainMenuButtons.get(i);
						break;
					}
				}
			}
			else{
				for( int i = 0; i < inGameButtons.size(); i++){
					// Restrict save
					if( inGameButtons.get(i).getButtonName().equals("save") && panel.getGameServer() == null ){
						continue;
					}
					else if( inGameButtons.get(i).containsPoint(mousePoint) ){
						button = inGameButtons.get(i);
						break;
					}
				}
			}
		}
		else if( panel.getState() == States.Start ){
			for( int i = 0; i < startButtons.size(); i++){
				if( startButtons.get(i).containsPoint(mousePoint) ){
					button = startButtons.get(i);
					break;
				}
			}
		}
		else if( panel.getState() == States.Join ){
			for( int i = 0; i < joinButtons.size(); i++){
				if( joinButtons.get(i).containsPoint(mousePoint) ){
					button = joinButtons.get(i);
					break;
				}
			}
		}
		else if( panel.getState() == States.Quit ){
			for( int i = 0; i < quitButtons.size(); i++){
				if( quitButtons.get(i).containsPoint(mousePoint) ){
					button = quitButtons.get(i);
					break;
				}
			}
		}
		else if( backButton.containsPoint(mousePoint) ){
			button = backButton;
		}
		// IN GAME
		else if( panel.getState() == States.Game ){
			Item item = getItemOnScreen(mousePoint);
			System.out.println("Pressed: " + item);
			if( item != null ){
				for( int i = 0; i < itemInteractButtons.size(); i++){
					if( itemInteractButtons.get(i).containsPoint(mousePoint) && itemInteractButtonVisible(i,item) ){
						button = itemInteractButtons.get(i);
						break;
					}
				}
			}
		}


		if( button == null ){
			return;
		}
		else{
			SoundController.play(SOUND_BUTTON_PRESS);
		}

		if( button.getButtonName().equals("start") ){
			panel.setState(States.Start);
		}
		else if( button.getButtonName().equals("singleplayer") ){
			panel.startGame();
		}
		else if( button.getButtonName().equals("multiplayer") ){
			panel.startGame();
		}
		else if( button.getButtonName().equals("disconnect") ){
			panel.disconnect();
		}
		else if( button.getButtonName().equals("save") ){
			//panel.getGameServer().saveGame();
		}
		else if( button.getButtonName().equals("join") ){
			panel.join();
		}
		else if( button.getButtonName().equals("confirm") ){
			if( panel.getState() == States.Join ){
				panel.connect(connectIPButton.getText());
			}
		}
		else if( button.getButtonName().equals("load") ){
			panel.load();
		}
		else if( button.getButtonName().equals("help") ){
			panel.help();
		}
		else if( button.getButtonName().equals("quit") ){
			panel.setState(States.Quit);
		}
		else if( button.getButtonName().equals("yes") ){
			if( panel.getState() == States.Quit ){
				panel.quit();
			}
		}
		else if( button.getButtonName().equals("no") ){
			if( panel.getState() == States.Quit ){
				panel.setState(States.MainMenu);
			}
		}
		else if( button.getButtonName().equals("back") ){
			panel.setState(States.MainMenu);
		}
		else if( button.getButtonName().startsWith("inventory_") ){
			String command = button.getButtonName().substring(10);
			int index = panel.getInventory().findCellIndex(mousePoint.x, mousePoint.y);
			
			
			Item item = panel.getGameClient().getAvatar().getInventory().get(index);
			Direction action = Direction.NORTH;
			if( item instanceof Directional ){
				action = Direction.getRotatedDirection(((Directional)item).getFacingDirection(), Direction.get(panel.getDirection()));
			}
			System.out.println(action);
			Move move = new Move((panel.getGameClient()).getPlayer(), command, action.toString(), index );
			
			
			
			
			try { panel.getGameClient().sendMoveToServer(move);
			} catch (IOException e1) { e1.printStackTrace(); }
		}
	}

	public void buttonReleased(Point point) {
		// TODO Auto-generated method stub

	}

}
