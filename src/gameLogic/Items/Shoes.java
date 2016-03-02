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
 * Shoes class extends Item and overrides all of its methods. The shoes function is to increase the avatars speed if the avatar contains a Shoes item in their inventory.
 */
public class Shoes extends Item implements Pickupable {

	private Container container;
	public static final int EXTRA_SPEED = 5;
	public static final String description = "Increases movement speed by " + EXTRA_SPEED;

	public Shoes(Tile2D tile) {
		super(tile);
	}

	@Override
	public String getDescription() {
		return description;
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
	public boolean interactWith(Avatar avatar) {
		return false;
	}

	@Override
	public void returnToStartPos() {
		tile = startTile;
		if(tile == null) return;
		else tile.addItem(this);
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
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean pickUp(Avatar avatar) {
		avatar.setStepAmount(EXTRA_SPEED + avatar.getStepAmount());
		return true;
	}

	@Override
	public boolean isPickedUp() {
		return container != null;
	}

	@Override
	public boolean drop(Avatar avatar) {
		avatar.setStepAmount(avatar.getStepAmount() - EXTRA_SPEED);
		return true;
	}

}
