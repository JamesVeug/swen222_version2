package gameLogic.Tiles;

import gameLogic.NonWalkable;

/**
 *
 * @author griffiryan
 *
 * Extends Tile2D with no added functionality - exists as a non passable unit.
 */
public class Column extends Tile2D implements NonWalkable{

	public Column(int xPos, int yPos) {
		super(xPos, yPos);
	}

}
