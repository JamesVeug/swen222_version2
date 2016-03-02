package gameLogic;

import gameLogic.Items.Item;
import gameLogic.Tiles.Tile2D;

import java.util.List;

public interface Container {
	public Tile2D getTile();
	public List<Item> getInventory();
	public boolean hasItems();
	public boolean isFull();
	public int maxInventorySize();
}
