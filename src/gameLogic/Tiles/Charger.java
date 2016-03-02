package gameLogic.Tiles;

import gameLogic.Cell;
import gameLogic.Game;
import gameLogic.NonWalkable;
import gameLogic.Recharger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import networking.Thinker;
/**
 *
 * @author griffiryan
 *
 *	The charger class extends the tile class, and when the avatar charges next to it, it increases its battery life.
 */
public class Charger extends Tile2D implements Serializable, NonWalkable, Powerable, Recharger, Thinker{

	private static final long serialVersionUID = -120239992386564353L;

	private List<Powerable> connections = new ArrayList<Powerable>();
	private Cell cell = new Cell(this, 1000, 1000);
	private int rechargeAmount = 5; // Recharge other plugins
	private int restoreAmount = 1; // Recharge THIS battery

	public Charger(int xPos, int yPos){
		super(xPos, yPos);

	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	public void think(Game game) {
		for( Powerable power : connections ){
			// Don't want to be able to recharge our connections OR ourselves
			if( cell.isEmpty() ) return;
			recharge(power.getCell());
		}

		// Regenerate this
		cell.incBattery(restoreAmount);
	}

	@Override
	public boolean join(Powerable power) {
		return connections.add(power);
	}

	@Override
	public void recharge(Cell cell) {
		int restoreAmount = getRechargeAmount();
		this.cell.decBattery(cell.incBattery(restoreAmount));
	}

	@Override
	public int getRechargeAmount() {
		return rechargeAmount;
	}

	@Override
	public boolean disconnect(Powerable power) {
		return connections.remove(power);
	}

}
