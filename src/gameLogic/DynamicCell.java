package gameLogic;

import gameLogic.Tiles.Powerable;

/**
 * Cell that increases it's max when the life has gone over it's limit
 * @author James Veug
 *
 */
public class DynamicCell extends Cell {

	public DynamicCell(Powerable holder, int maxBatteryLife, int batteryLife) {
		super(holder, maxBatteryLife, batteryLife);
	}

	@Override
	public int incBattery(int amount){
		int oldLife = getBatteryLife() + amount;
		if( oldLife > getMaxBatteryLife() ){
			setMaxBatteryLife(oldLife);
		}
		
		if( oldLife < 0 ){
			oldLife = 0;
		}

		int restoreAmount = Math.abs(getBatteryLife() - oldLife);
		setBatteryLife(oldLife);
		return restoreAmount;
	}
}
