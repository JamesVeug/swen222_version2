package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Cell;
import gameLogic.Game;
import gameLogic.Tiles.Powerable;
import gameLogic.Tiles.Tile2D;

import java.util.HashSet;
import java.util.Set;

import networking.Thinker;
import Sound.SoundController;

public class Water extends Item implements Powerable, Thinker{
	private HashSet<Powerable> connections = new HashSet<Powerable>();
	private Cell cell = new Cell(this, 50, 0);

	private long lastChargeTime = 0;
	private static final int CHARGETIME = 50;

	private static final String SOUND_ZAP = "zap2.wav";

	public Water(Tile2D tile) {
		super(tile);
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public String getDescription() {
		return "Water";
	}

	@Override
	public boolean interactWith(Avatar avatar) {
		return true;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	public boolean join(Powerable power) {
		System.out.println("JOINED======");
		return connections.add(power);
	}

	@Override
	public boolean disconnect(Powerable power) {
		return connections.remove(power);
	}

	@Override
	public void think(Game game) {
		// Zap avatars on tile
		if( !cell.isEmpty() && tile != null && tile.getAvatar() != null ){
			tile.getAvatar().getCell().decBattery(cell.getBatteryLife());
			SoundController.play(SOUND_ZAP);
		}

		// Should we remove the power in ther water?
		if( !cell.isEmpty() && !isCharging() ){

			// Decrease Life
			cell.decBattery(1);
		}
	}

	/**
	 * Give power to this section of water, and distribute it to it's neighbours
	 * @param power
	 */
	public void distributeEnergy(int power){
		if( power == 0 ) return;

		Set<Cell> cellList = new HashSet<Cell>();
		distributeEnergyRec(power, tile, cellList);
	}

	/**
	 * Children
	 * @param power
	 * @param tile
	 * @param charged
	 */
	private void distributeEnergyRec(int power, Tile2D tile, Set<Cell> charged){

		// Find the water on tile
		// Power the cell in it
		for( Item item : tile.getItems() ){
			if( item instanceof Water ){
				Water water =  (Water)item;
				if( charged.contains(water.getCell()) ){
					return;
				}

				// Record cell
				charged.add(water.getCell());

				// Power Cell
				water.getCell().incBattery(power);

				// Record time powered
				water.recordChargingTime();

				// Children
				if( tile.getTileUp() != null ) distributeEnergyRec(power, tile.getTileUp(), charged);
				if( tile.getTileLeft() != null ) distributeEnergyRec(power, tile.getTileLeft(), charged);
				if( tile.getTileRight() != null ) distributeEnergyRec(power, tile.getTileRight(), charged);
				if( tile.getTileDown() != null ) distributeEnergyRec(power, tile.getTileDown(), charged);

				// Connections
				for( Powerable powerable : water.getConnections() ){
					// Connecting waters from different rooms
					if( powerable instanceof Water ){
						distributeEnergyRec(power, ((Water) powerable).getTile(), charged);
					}
				}
				return;
			}
		}
	}

	private HashSet<Powerable> getConnections() {
		return connections;
	}

	public boolean isCharging(){
		return System.currentTimeMillis() < (lastChargeTime + CHARGETIME);
	}

	public void recordChargingTime(){
		lastChargeTime = System.currentTimeMillis();
	}
}
