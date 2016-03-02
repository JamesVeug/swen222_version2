package networking;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Room;
import gameLogic.Tiles.Tile2D;

import java.util.Random;

import rendering.Direction;

/**
 * Random AI that will Randomly move when there are no enemies around.
 * If there are Avatars near the AI, it will attack that player trying to kill them.
 * @author veugeljame
 *
 */
public class DummyAI extends AI {

	public DummyAI(Room room, String name) {
		super(room, name);
	}

	@Override
	public void think(Game game) {
		
	}
}
