package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Container;

public interface Pickupable {
	public void setContainer(Container container);
	public Container getContainer();
	public boolean pickUp(Avatar avatar);
	public boolean isPickedUp();
	public boolean drop(Avatar avatar);
}
