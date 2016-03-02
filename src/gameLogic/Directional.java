package gameLogic;

import rendering.Direction;

public interface Directional {
	public Direction getFacingDirection();
	public void setFacingDirection(Direction face);
}
