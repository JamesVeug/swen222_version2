package gameLogic.Tiles;

import gameLogic.Cell;

public interface Powerable {
	public Cell getCell();
	public boolean join(Powerable power);
	public boolean disconnect(Powerable power);
}
