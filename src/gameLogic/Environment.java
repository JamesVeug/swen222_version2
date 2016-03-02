package gameLogic;

import gameLogic.Items.Box;
import gameLogic.Items.GreenKey;
import gameLogic.Items.Light;
import gameLogic.Items.PurpleKey;
import gameLogic.Items.RedKey;
import gameLogic.Items.Shoes;
import gameLogic.Items.YellowKey;
import gameLogic.Tiles.Tile2D;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author griffiryan
 *
 * Procedurally generates Items to be added to the game world. Operates as a thread and genrates items afet certain times.
 */
public class Environment extends Thread implements Serializable{


	private static final long serialVersionUID = 388911180114116326L;

	private Game game;

	private int count = 0;

	private List<String> keyColors;
	private List<String> doorColors;

	// items in the game are identified using unique item ids.
	private int itemID;

	public Environment(Game game){
		this.game = game;
		this.itemID = 0;

		this.keyColors = new ArrayList<String>();

		keyColors.add("R");
		keyColors.add("G");
		keyColors.add("Y");
		keyColors.add("P");



	}

	@Override
	public void run(){
		while(true){
			if(count == 0){		// initial environment item generation
				for(int i = 1; i < 5; i++){		// at start - put lights in all rooms but arena.
					genLight(i);
					genKey(i);
				}
			}
			count++;
			try { this.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
			genBox();
			try { this.sleep(30000); } catch (InterruptedException e) { e.printStackTrace(); }
		}
	}

	private void genBox(){
		Room room = game.getRoomsInGame().get(0);
		Tile2D tile = room.getTiles()[7][7];
		if(tile.getItems().size() != 0) return;

		Box box = new Box(room.getTiles()[7][7]);
		room.getTiles()[7][7].addItem(box);
		room.addItem(box);


		Shoes shoes = new Shoes(null);
		box.getInventory().add(shoes);
	}



	private void genKey(int i){
		Room room = game.getRoomsInGame().get(i);

		if(keyColors.get(0).equals("R")){
			RedKey key  = new RedKey(room.getTiles()[1][5]);
			room.getTiles()[1][5].addItem(key);
			room.addItem(key);
			keyColors.remove(0);
		}
		else if(keyColors.get(0).equals("G")){
			GreenKey key = new GreenKey(room.getTiles()[1][5]);
			room.getTiles()[1][5].addItem(key);
			room.addItem(key);
			keyColors.remove(0);
		}
		else if(keyColors.get(0).equals("Y")){
			YellowKey key = new YellowKey(room.getTiles()[1][5]);
			room.getTiles()[1][5].addItem(key);
			room.addItem(key);
			keyColors.remove(0);
		}
		else if(keyColors.get(0).equals("P")){
			PurpleKey key = new PurpleKey(room.getTiles()[1][5]);
			room.getTiles()[1][5].addItem(key);
			room.addItem(key);
			keyColors.remove(0);
		}
	}

	private void genLight(int i){
		Room room = game.getRoomsInGame().get(i);
		Light light = new Light(room.getTiles()[1][1]);
		room.getTiles()[1][1].addItem(light);
		room.addItem(light);

	}

}