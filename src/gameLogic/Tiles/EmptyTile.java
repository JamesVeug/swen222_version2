package gameLogic.Tiles;

import gameLogic.NonWalkable;

import java.io.Serializable;
/**
 *
 * @author griffiryan
 * Extends Tile2D - location for avatar to be present on.
 */
public class EmptyTile extends Tile2D implements NonWalkable{


	public EmptyTile(int xPos, int yPos) {
		super(xPos, yPos);
	}

}
