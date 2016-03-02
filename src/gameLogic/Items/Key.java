package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Container;
import gameLogic.Tiles.Tile2D;

import java.awt.Color;

/**
 *
 * @author griffiryan
 * Generilization of the Key class and specialization of the Item class.
 *
 */
public class Key extends Item implements Pickupable{

	private static final String description = "Used to unlock doors of the same color";
	private Container container;

	public Key(Tile2D tile){
		super(tile);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public int getWeight() {
		return 0;
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
}
