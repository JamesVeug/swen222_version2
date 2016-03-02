package gameLogic.Tiles;

import gameLogic.NonWalkable;

/**
 *
 * @author griffiryan
 *	Wall extends Tile2D - no added functionality - is a impassible tile.
 */
public class Wall extends Tile2D implements NonWalkable{

	public Wall(int xPos, int yPos) {
		super(xPos, yPos);
	}

}
