package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Container;
import gameLogic.Tiles.Tile2D;

public class ScrapMetal extends Item implements Pickupable{
	
	private static final String description = "No Description";
	private Container container;
	
	public ScrapMetal(Tile2D tile) {
		super(tile);
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean interactWith(Avatar avatar) {
		return false;
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
		return true;
	}

	@Override
	public boolean isPickedUp() {
		return container != null;
	}

	@Override
	public boolean drop(Avatar avatar) {
		return true;
	}

}
