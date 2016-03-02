package gameLogic.Tiles;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Items.Item;
import gameLogic.Items.RedKey;

/**
*
* @author griffiryan
*
*	RedDoor is a specialization of the Door class.
*/
public class RedDoor extends Door {

	public RedDoor(int xPos, int yPos) {
		super(xPos, yPos);
		lock();
	}

	@Override
	public boolean interactWith(Avatar avatar, Game game) {
		if( isLocked() ){
			Item key = null;
			for(Item item : avatar.getInventory()){
				if(item instanceof RedKey){
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
