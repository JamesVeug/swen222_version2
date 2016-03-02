package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Cell;
import gameLogic.Container;
import gameLogic.Tiles.Tile2D;

public class CellRecharger extends Item implements Consumable, Pickupable {
	
	private int rechargeAmount;
	private Container container;

	public CellRecharger(Tile2D tile, int rechargeAmount) {
		super(tile);
		this.rechargeAmount = rechargeAmount;
	}

	@Override
	public String getDescription() {
		return "Restores " + rechargeAmount + " energy.";
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public boolean consume(Avatar avatar) {
		
		Cell cell = avatar.getCell();
		if( cell.isCharging() ){
			return false;
		}
		else if( cell.fullyCharged() ){
			return false;
		}
		
		cell.incBattery(rechargeAmount);
		return true;
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
