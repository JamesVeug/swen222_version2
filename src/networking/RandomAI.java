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
public class RandomAI extends AI {

	private static final long THINKDELAY = 500;
	private long NEXTTHINK = System.currentTimeMillis() + THINKDELAY;
	private Avatar avatar = null;

	public RandomAI(Room room, String name) {
		super(room, name);

		// Find the avatar in the room
		for(int y = 0; y < room.getTiles().length; y++){
			for(int x = 0; x < room.getTiles()[0].length; x++){
				Tile2D tile = room.getTiles()[y][x];
				if( tile.getAvatar() != null && tile.getAvatar().getPlayerName().equals(player.getName()) ){
					avatar = tile.getAvatar();
					y = Integer.MAX_VALUE-20; // HACK
					x = Integer.MAX_VALUE-20; // HACK
				}
			}
		}
	}

	@Override
	public void think(Game game) {

		// Only think if we need to
		if( NEXTTHINK > System.currentTimeMillis() ){
			return;
		}

		// Should we attack or move in a random direciton?
		if( avatar.getCell().isCharging() ){
			//System.out.println("Charging off");
			avatar.moveTo(new Move(getPlayer(), "O", "Stop"), game);
		}
		else if( facingAvatar(game) ){

			// Turn on charge mode
			avatar.moveTo(new Move(getPlayer(), "O", "Start"), game);
			//System.out.println("Charging on");
		}
		else if( faceAvatarNearUs(game)){
			//System.out.println("Facing a player");
		}
		else{
			//System.out.println("Moving");
			moveInRandomDirection(game);
		}

		// Remember when we need to think next
		NEXTTHINK = System.currentTimeMillis() + THINKDELAY;
	}

	/**
	 * Can we Attack someone in front of us?
	 * @param game Game to call objects on
	 * @return True if we can attack someone
	 */
	public boolean facingAvatar(Game game){

		Tile2D tile = getNextTile();
		if(tile != null && tile.getAvatar() != null && tile.getAvatar() != avatar ){
			return true;
		}
		return false;
	}

	/**
	 * Face an avatar that is near us if there is one
	 * @param game
	 * @return True if facing an avatar
	 */
	public boolean faceAvatarNearUs(Game game){
		Tile2D tile = avatar.getTile();
		if( tile.getTileUp() != null && tile.getTileUp().getAvatar() != null && tile.getTileUp().getAvatar() != avatar ){
			avatar.setFacingDirection(Direction.NORTH);
			return true;
		}
		else if( tile.getTileLeft() != null && tile.getTileLeft().getAvatar() != null && tile.getTileLeft().getAvatar() != avatar ){
			avatar.setFacingDirection(Direction.WEST);
			return true;
		}
		else if( tile.getTileRight() != null && tile.getTileRight().getAvatar() != null && tile.getTileRight().getAvatar() != avatar ){
			avatar.setFacingDirection(Direction.EAST);
			return true;
		}
		else if( tile.getTileDown() != null && tile.getTileDown().getAvatar() != null && tile.getTileDown().getAvatar() != avatar ){
			avatar.setFacingDirection(Direction.SOUTH);
			return true;
		}

		// No one is around us
		return false;
	}

	/**
	 * Gets the tile in front of the direction that we are facing
	 * @return
	 */
	public Tile2D getNextTile(){
		switch(avatar.getFacingDirection()){
			case NORTH:
				return avatar.getTile().getTileUp();
			case EAST:
				return avatar.getTile().getTileRight();
			case SOUTH:
				return avatar.getTile().getTileDown();
			default:
				return avatar.getTile().getTileLeft();
		}
	}

	/**
	 * Move the AI in a random direction
	 * @param game Which game to call on the player
	 */
	public void moveInRandomDirection(Game game){
		int nextMoveDirection = new Random().nextInt(4);
		String direction = "";
		for(int counter = 0; counter < 4; counter++ ){

			// First letter of the string to move
			direction = getMovementKey(nextMoveDirection);

			// Attempt to move
			if( avatar.moveTo(new Move(getPlayer(), direction, ""), game) ){
				//avatar.moveTo(new Move(getPlayer(), direction, Direction.get(0)));
				//System.out.println("\t" + direction);
				break;
			}

			// Rotate clockwise
			nextMoveDirection = getRotatedDirection(nextMoveDirection);
		}
	}

	/**
	 * Get the next position when rotating clockwise
	 * @param direction Get next direciton from this
	 * @return 0 - 3 inclusive
	 */
	public int getRotatedDirection(int direction){
		return (direction+1)%4;
	}

	/**
	 * Returns the opposite direction of any outcome from getRandomDirection
	 * @param direction What to get the inverse of
	 * @return Inverse of direction
	 */
	public String getMovementKey(int direction){
		switch(direction){
		case 0:
			return "W";
		case 1:
			return "D";
		case 2:
			return "S";
		}

		return "A";
	}
}
