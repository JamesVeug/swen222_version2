package gameLogic;

import gameLogic.Tiles.Powerable;

public interface Recharger extends Powerable{
	public int getRechargeAmount();
	public void recharge(Cell cell);
}
