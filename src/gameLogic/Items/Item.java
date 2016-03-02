package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Tiles.Tile2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 
 * @author griffiryan Abstract super class to provide methods that must be
 *         implemented for all concrete item classes.
 */
public abstract class Item implements Serializable {

	private static final long serialVersionUID = 2903139965045313571L;

	protected Tile2D startTile;
	protected Tile2D tile;
	protected boolean movable = false;
	protected String itemID = UUID.randomUUID().toString();

	public abstract int getWeight();
	public abstract String getDescription();
	public abstract boolean interactWith(Avatar avatar);
	
	public Item(Tile2D tile) {
		this.tile = tile;
		this.startTile = tile;
		this.movable = true;
	}
	

	public boolean moveItemTo(Tile2D toTile) {
		if(toTile == null) return false;
		this.tile = toTile;
		toTile.addItem(this);
		return true;
	}


	public Tile2D getTile() {
		return this.tile;
	}
	
	public String getItemID() {
		return itemID;
	}

	public Tile2D getStartTile() {
		return startTile;
	}

	public void setStartTile(Tile2D startTile) {
		this.startTile = startTile;
	}

	public void returnToStartPos() {
		tile = startTile;
		if (tile != null)
			tile.addItem(this);

	}

	public int hashCode() {
		return itemID.hashCode();
	}
	
	public void setTile(Tile2D tile) {
		this.tile = tile;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (!itemID.equals(other.itemID))
			return false;
		return true;
	}
}
