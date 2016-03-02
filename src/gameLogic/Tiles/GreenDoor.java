package gameLogic.Tiles;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Items.GreenKey;
import gameLogic.Items.Item;

/**
 *
 * @author griffiryan
 *
 *	GreenDoor is a specialization of the Door class.
 */
public class GreenDoor extends Door  {

	public GreenDoor(int xPos, int yPos) {
		super(xPos, yPos);
		lock();
	}

	@Override
	public boolean interactWith(Avatar avatar, Game game) {
		if( isLocked() ){
			Item key = null;
			for(Item item : avatar.getInventory()){
				if(item instanceof GreenKey){
					unlock();
					getExitDoor().unlock();

					//Remove key
					key = item;
				}
			}

			// Can't unlock
			if( key == null){
				return false;
			}
			else{
				avatar.getInventory().remove(key);
				return true;
			}
		}

		// Unlocked already
		return true;
	}
}
