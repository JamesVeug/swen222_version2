package rendering;

/**
 * A Class that acts as a utility.
 * Given w, a, s, d it will return 0, 3, 2, 1 respectively
 * Given North, East, South, West it will return 0, 1, 2, 3 respectively
 * Given 0, 1, 2, 3 it will return North, East, South, West respectively
 *
 * @author Leon North
 *
 */
public enum Direction {

	NORTH,
	EAST,
	SOUTH,
	WEST;

	/**
	 * Given North, East, South, West it will return 0, 1, 2, 3 respectively
	 * @param direction
	 * @return int
	 * @author Leon North
	 */
	public static int get(Direction direction){
		return direction.ordinal();
	}

	/**
	 * Given North, East, South, West it will return 0, 1, 2, 3 respectively
	 * @param direction
	 * @return int
	 * @author Leon North
	 */
	public static String getMove(Direction direction){
		if(direction == NORTH){
			return "W";
		}
		if(direction == WEST){
			return "A";
		}
		if(direction == SOUTH){
			return "S";
		}
		else if(direction == EAST){
			return "D";
		}
		//should never get here
		return "O";
	}

	/**
	 * Given North, East, South, West it will return 0, 1, 2, 3 respectively
	 * @param direction
	 * @return int
	 * @author Leon North
	 */
	public static String getMove(int direction){
		return getMove(Direction.get(direction));
	}

	/**
	 * Given w, a, s, d it will return 0, 3, 2, 1 respectively
	 * @param direction
	 * @return
	 * @author Leon North
	 */
	public static int getKeyDirection(String direction){
		if(direction.equalsIgnoreCase("w")){
			return 0;
		}
		if(direction.equalsIgnoreCase("d")){
			return 1;
		}
		if(direction.equalsIgnoreCase("s")){
			return 2;
		}
		else if(direction.equalsIgnoreCase("a")){
			return 3;
		}
		//should never get here
		return -1;
	}

	public static Direction getRotatedDirection(Direction originalDirection, Direction rotatedDirection){
		int original = originalDirection.ordinal();          //gets the direction that the avatar is facing
		  //relative to the direction they are viewing the world

		int rotated = Direction.get(rotatedDirection);                     // the direction the current avatar is facing  relative
		   //to the rendering direction
		original = (original - rotated);
		if( original < 0 ){
			original += 4;
		}
		return Direction.get(original);
	}

	/**
	 * Given 0, 1, 2, 3 it will return North, East, South, West respectively
	 * @param direction
	 * @return
	 * @author Ryan Gryffin
	 */
	public static Direction get(int direction){
		if (direction == 0){
			return NORTH;
		}
		if (direction == 1){
			return EAST;
		}
		if (direction == 2){
			return SOUTH;
		}
		if (direction == 3){
			return WEST;
		}
		//should never get here
		return null;
	}

	public static Direction get(String action) {
		if( action.equalsIgnoreCase("North")) return NORTH;
		if( action.equalsIgnoreCase("East")) return EAST;
		if( action.equalsIgnoreCase("South")) return SOUTH;
		if( action.equalsIgnoreCase("West")) return WEST;
		return null;
	}

	public String toString(){
		String s = super.toString();
		s = s.toLowerCase();
		s = s.substring(0,1).toUpperCase() + s.substring(1);
		return s;
	}
}
