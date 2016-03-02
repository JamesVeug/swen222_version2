package networking;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Tiles.Tile2D;
import gameLogic.Room;

import java.awt.Point;
import java.util.Random;
import java.util.Scanner;

import rendering.Direction;

/**
 * Random AI that will Randomly move when there are no enemies around.
 * If there are Avatars near the AI, it will attack that player trying to kill them.
 * @author veugeljame
 *
 */
public class ChaseAI extends AI {

	private static final long THINKDELAY = 500;
	private long NEXTTHINK = System.currentTimeMillis() + THINKDELAY;
	private Avatar avatar = null;

	private Avatar target = null;

	public static final int SIGHT_WIDTH = 2;
	public static final int SIGHT_DISTANCE = 4;

	public ChaseAI(Room room, String name) {
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


		// Turn off charge
		if( avatar.getCell().isCharging() ){
			avatar.moveTo(new Move(getPlayer(), "O", "Stop"), game);
		}
		// Check for target
		else if( hasTarget() ){
			System.out.println("Enemy: (" + target.getTile().getxPos() + "," + target.getTile().getyPos() + ")");
			System.out.println("Avatar: (" + avatar.getTile().getxPos() + "," + avatar.getTile().getyPos() + ")");


			if( !canSeeTarget(target) ){
				System.out.println("Can't see him???");

				if( !isDirectionTarget(target) ){
					System.out.println("	Direction target");
					faceAvatar(target);
					return;
				}
				else{
					System.out.println("	LOST TARGET");
					target = null;
					return;
				}
			}
			else if( getNextTile() == target.getTile() ){
				System.out.println("ATTACKING");

				//Turn on charge
				avatar.moveTo(new Move(getPlayer(), "O", "Start"), game);
			}
			else if( moveTowardsTarget(game) ){
				System.out.println("	MOVED!");
			}
			else{
				System.out.println("	Did not move");
			}

		}
		else{
			if( foundTarget() ){
				faceAvatar(target);
				System.out.println("FOUND TARGET: " + target.getPlayerName());
			}
			else{
				moveInRandomDirection(game);
			}
		}

		// Remember when we need to think next
		NEXTTHINK = System.currentTimeMillis() + THINKDELAY;
	}

	private boolean moveTowardsTarget(Game game) {

		boolean movedDirection = avatar.moveTo(new Move(getPlayer(), getMovementKey(avatar.getFacingDirection()), ""), game);
		if( !movedDirection ){
			float angle = getTargetAngle(target);
			//Direction Direction = getDirectionDirection(angle);
			Direction Direction = avatar.getFacingDirection();

			//Alternative direciton
			if( angle >= 0 && angle <= 90 ){
				if( Direction == Direction.NORTH ){
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.WEST), ""), game);
				}
				else{
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.NORTH), ""), game);
				}
			}
			else if( angle >= 90 && angle <= 180 ){
				if( Direction == Direction.WEST ){
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.SOUTH), ""), game);
				}
				else{
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.WEST), ""), game);
				}
			}
			else if( angle >= 180 && angle <= 270 ){
				if( Direction == Direction.SOUTH ){
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.EAST), ""), game);
				}
				else{
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.SOUTH), ""), game);
				}
			}
			else if( angle >= 270 ){
				if( Direction == Direction.EAST ){
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.NORTH), ""), game);
				}
				else{
					return avatar.moveTo(new Move(getPlayer(), getMovementKey(Direction.EAST), ""), game);
				}
			}
		}

		// Moved
		return true;
	}

	public boolean hasTarget(){
		return target != null;
	}

	public boolean foundTarget(){

		Tile2D[][] tiles = room.getTiles();

		int scanWidth = avatar.getFacingDirection() == Direction.EAST || avatar.getFacingDirection() == Direction.WEST ? SIGHT_DISTANCE : SIGHT_WIDTH;
		int scanX = (int)avatar.getTileXPos() - scanWidth;

		int scanHeight = scanX == SIGHT_DISTANCE ? SIGHT_WIDTH : SIGHT_DISTANCE;
		int scanY = (int)avatar.getTileYPos() - scanHeight;


		int startX = (int)Math.max(avatar.getTile().getxPos()-SIGHT_DISTANCE, 0);
		int startY = (int)Math.max(avatar.getTile().getyPos()-SIGHT_DISTANCE, 0);
		int endX = (int)Math.min(avatar.getTile().getxPos()+SIGHT_WIDTH, tiles.length);
		int endY = (int)Math.min(avatar.getTile().getyPos()+SIGHT_DISTANCE, tiles.length);

		// NORTH
		for( int x = Math.min(startX, endX); x <= Math.max(endX, startX); x++){
			for( int y = Math.min(startY,endY); y <= Math.max(endY, startY); y++){

				// Check for avatar
				//TODO JUST UNCOMMENTED
				if( tiles[y][x].getAvatar() != null && tiles[y][x].getAvatar() != avatar ){

					// We we see it?
					//if( canSeeTarget(tiles[y][x].getAvatar()) ){
						target = tiles[y][x].getAvatar();
						System.out.println("X");
						return true;
					//}
				}
				System.out.print("-");
			}
			System.out.println();
		}

		// Couldn't see
		return false;
	}

	public boolean canSeeTarget(Avatar suspect){

		// Not in the same room
		if( !room.getAvatars().contains(suspect) ){
			return false;
		}

		// Direction
		if( !isDirectionTarget(suspect)){
			System.out.println("Direction the wrong direction!");
			return false;
		}

		// Within distance
		double distance = Point.distance(suspect.getTile().getxPos(), suspect.getTile().getyPos(), avatar.getTile().getxPos(), avatar.getTile().getyPos());
		return( distance <= SIGHT_DISTANCE );
	}

	public Direction getDirectionDirection(float angle){
		if( angle >= 45 && angle <= 135 ){
			return Direction.SOUTH;
		}
		else if( angle >= 135 && angle <= 225 ){
			return Direction.EAST;
		}
		else if( angle >= 225 && angle <= 315 ){
			return Direction.NORTH;
		}
		else if( angle >= 315 || angle <= 45 ){
			return Direction.WEST;
		}

		return null;
	}

	public boolean isDirectionTarget(Avatar suspect){
		if( suspect == null ){
			return false;
		}

		// NORTH
		float angle = getTargetAngle(suspect);
		Direction Direction = getDirectionDirection(angle);
		System.out.println("Found: " + suspect.getPlayerName());
		System.out.println("Me: " + avatar.getPlayerName());

		System.out.println("D: " + avatar.getFacingDirection());
		System.out.println("A: " + angle);


		return avatar.getFacingDirection() == Direction;
	}

	/**
	 * Face an avatar that is near us if there is one
	 * @param game
	 * @return True if Direction an avatar
	 */
	public void faceAvatar(Avatar suspect){
		System.out.println("Direction AVATAR");

		float angle = getTargetAngle(suspect);
		Direction Direction = getDirectionDirection(angle);
		avatar.setFacingDirection(Direction);
	}

	/**
	 * Gets the tile in front of the direction that we are Direction
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
				//avatar.moveTo(new Move(getPlayer(), direction));
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

	/**
	 * Returns the opposite direction of any outcome from getRandomDirection
	 * @param direction What to get the inverse of
	 * @return Inverse of direction
	 */
	public String getMovementKey(Direction Direction){
		switch(Direction){
		case NORTH:
			return "W";
		case EAST:
			return "D";
		case SOUTH:
			return "S";
		}

		return "A";
	}

	public float getTargetAngle(Avatar suspect){
		int suspectX = suspect.getTile().getxPos();
		int suspectY = suspect.getTile().getyPos();
		int avatarX = avatar.getTile().getxPos();
		int avatarY = avatar.getTile().getyPos();
		return getAngle(avatarX, avatarY, suspectX, suspectY);
	}

	public float getAngle(int x, int y, int tX, int tY) {
	    float angle = (float) Math.toDegrees(Math.atan2(tY - y, tX - x));

	    if(angle < 0){
	        angle += 360;
	    }

	    return angle;
	}
}
