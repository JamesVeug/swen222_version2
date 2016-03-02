package gameLogic.Tiles;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Interactable;
import gameLogic.Lockable;
import gameLogic.Room;

import java.awt.Color;
import java.io.Serializable;
import java.util.List;
/**
 *
 * @author griffiryan
 * Door class is an extension of Tile2D, it has identical functionality, but also contains a Room field which represents the room the avatar will travel to if the door is used.
 */
public class Door extends Tile2D implements Interactable, Lockable{

	private Room toRoom;
	private boolean locked = false;

	public Door(int xPos, int yPos) {
		super(xPos, yPos);
		toRoom = null;
	}

	public Room getToRoom() {
		return toRoom;
	}

	public void setToRoom(Room toRoom) {
		this.toRoom = toRoom;
	}

	@Override
	public boolean interactWith(Avatar avatar, Game game) {
		// Unlocked already
		return true;
	}

	@Override
	public boolean isLocked() {
		return locked;
	}

	@Override
	public void lock(){
		locked = true;

	}

	@Override
	public void unlock(){
		locked = false;
	}

	public Door getExitDoor(){
		return toRoom.getDoorLeadingToRoom(getRoom());
	}
}
