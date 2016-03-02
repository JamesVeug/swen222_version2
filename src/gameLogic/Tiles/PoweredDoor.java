package gameLogic.Tiles;

import java.util.ArrayList;
import java.util.List;

import networking.Thinker;
import gameLogic.Avatar;
import gameLogic.Cell;
import gameLogic.Game;
import gameLogic.Room;
import gameLogic.Toggleable;
import gameLogic.Items.Item;
import gameLogic.Items.RedKey;

/**
*
* @author griffiryan
*
*	RedDoor is a specialization of the Door class.
*/
public class PoweredDoor extends Door implements Powerable, Thinker, Toggleable{

	private static final int USEDPOWER = 1;
	private static final int MAXPOWER = 100;
	private Cell cell = new Cell(this, MAXPOWER, 0);
	private List<Powerable> connections = new ArrayList<Powerable>();
	private boolean toggled = false;
	
	public PoweredDoor(int xPos, int yPos) {
		super(xPos, yPos);
		lock();
	}

	private void setCell(Cell cell) {
		this.cell = cell;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	@Override
	public void think(Game game) {
		if( isLocked() && cell.fullyCharged() ){
			unlock();
			getExitDoor().unlock();
		}
		else if( !isLocked() && cell.isEmpty() ){
			lock();
			getExitDoor().lock();
		}
		
		if( turnedOn() ){
			cell.decBattery(USEDPOWER);
		}	
	}
	
	@Override
	public void setToRoom(Room toRoom) {
		super.setToRoom(toRoom);
		
		Door door = getExitDoor(); 
		if( door != null && door instanceof PoweredDoor ){
			((PoweredDoor)getExitDoor()).setCell(cell);
		}
	}

	@Override
	public boolean join(Powerable power) {
		return connections.add(power);
	}

	@Override
	public boolean disconnect(Powerable power) {
		return connections.remove(power);
	}
	
	@Override
	public void lock(){
		super.lock();
		turnOff();
	}
	
	@Override
	public void unlock(){
		super.unlock();
		turnOn();
	}

	@Override
	public boolean turnedOn() {
		return toggled == true;
	}

	@Override
	public boolean turnedOff() {
		return toggled == false;
	}

	@Override
	public void turnOn() {
		toggled = true;
	}

	@Override
	public void turnOff() {
		toggled = false;
	}

	@Override
	public void toggle() {
		toggled = !toggled;
	}
}
