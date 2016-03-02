package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Container;
import gameLogic.Tiles.Tile2D;

import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author griffiryan
 *
 * The Box class is an Extension of Item, and overrides all of its methods.
 */
public class Box extends Item implements Container, Pickupable{

	// The primary function of a Box object is to contain other Items.
	private List <Item> inventory;
	private Container container;

	public Box(Tile2D tile) {
		super(tile);
		this.inventory = new ArrayList<Item>();
	}

	@Override
	public String getDescription() {
		return "'Mystery'";
	}

	@Override
	public Tile2D getTile() {
		return tile;
	}

	@Override
	public boolean moveItemTo(Tile2D toTile) {
		if(toTile == null) return false;
		this.tile = toTile;
		toTile.addItem(this);
		return true;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public void returnToStartPos() {
		tile = startTile;
		tile.addItem(this);

	}


	public List<Item> getInventory() {
		return inventory;
	}

	public void setContains(List<Item> contains) {
		this.inventory = contains;
	}
	
	@Override
	public boolean interactWith(Avatar avatar) {
		return false;
	}
	
	public boolean pickUp(Avatar avatar) {
		return true;
	}
	
	public boolean drop(Avatar avatar){
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Item other = (Item) obj;
		if (itemID != other.itemID) return false;
		return true;
	}

	@Override
	public boolean hasItems() {
		return !inventory.isEmpty();
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean isPickedUp() {
		return container != null;
	}

	@Override
	public boolean isFull() {
		return inventory.size() == maxInventorySize();
	}

	@Override
	public int maxInventorySize() {
		return 1;
	}
}
