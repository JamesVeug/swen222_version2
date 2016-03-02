package gameLogic;

import gameLogic.Tiles.Powerable;

import java.io.Serializable;

/**
 *
 * @author griffiryan
 *
 *	The Cell class is directly attached to an avatar an represents an Avatars battery.
 *	The cells charging can be toggle be toggled on off, and can increment and decremtent battery life.
 */
public class Cell implements Serializable {

	private static final long serialVersionUID = 7833445205893995697L;

	private int maxBatteryLife;
	private int batteryLife;

	private Powerable holder;

	private boolean charging;

	public Cell(Powerable holder, int maxBatteryLife, int batteryLife){
		this.holder = holder;
		this.setMaxBatteryLife(maxBatteryLife);
		this.batteryLife = batteryLife;
		this.charging = false;

	}

	public int decBattery(int amount){
		return incBattery(-amount);
	}

	public int incBattery(int amount){

		int oldLife = batteryLife;
		oldLife = Math.min(getMaxBatteryLife(), Math.max(0, batteryLife + amount));

		int restoreAmount = Math.abs(batteryLife - oldLife);
		batteryLife = oldLife;
		return restoreAmount;
	}

	public int getBatteryLife(){
		return batteryLife;
	}

	public boolean isCharging(){
		return charging;
	}

	public void setCharging(boolean charging){
		this.charging = charging;

	}

	public void setBatteryLife(int batteryLife) {
		this.batteryLife = batteryLife;
	}

	public void takeHit(int damage) {
		batteryLife -= damage;

	}

	public boolean fullyCharged() {
		return batteryLife == getMaxBatteryLife();
	}

	public boolean isEmpty() {
		return batteryLife <= 0;
	}

	public int getMaxBatteryLife() {
		return maxBatteryLife;
	}

	public void setMaxBatteryLife(int maxBatteryLife) {
		this.maxBatteryLife = maxBatteryLife;
	}


}
