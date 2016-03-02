package gameLogic.Tiles;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Interactable;
import gameLogic.Lightable;
import gameLogic.Mountable;
import gameLogic.NonWalkable;
import gameLogic.Toggleable;
import gameLogic.Items.Item;
import gameLogic.Items.Light;

public class LightMount extends BlankFloor implements Lightable, Toggleable, Mountable<Light>, Interactable, NonWalkable{

	private Light light = null;

	public LightMount(int xPos, int yPos) {
		super(xPos, yPos);
	}

	public void addLight(Light light) {
		this.light = light;
	}

	public Light getLight() {
		return light;
	}

	public Light removeLight(){
		if( light == null ){
			return null;
		}

		// Remove from room
		getRoom().removeLight(light);

		// Remove from mount
		Light temp = light;
		light = null;

		return temp;
	}

	@Override
	public float getR() {
		return light != null ? light.getR() : 0f;
	}

	@Override
	public float getG() {
		return light != null ? light.getG() : 0f;
	}

	@Override
	public float getB() {
		return light != null ? light.getB() : 0f;
	}

	@Override
	public double getBrightness() {
		return light != null ? light.getBrightness() : 0;
	}

	@Override
	public int getRadius() {
		return light != null ? light.getRadius() : 0;
	}

	@Override
	public boolean turnedOn() {
		return light != null ? light.turnedOn() : false;
	}

	@Override
	public boolean turnedOff() {
		return light != null ? light.turnedOff() : true;
	}

	@Override
	public void turnOn() {
		if( light != null ){
			light.turnOn();
		}
	}

	@Override
	public void turnOff() {
		if( light != null ){
			light.turnOff();
		}
	}

	@Override
	public void toggle() {
		if( light != null ){
			light.toggle();
		}
	}

	@Override
	public Light getMounted() {
		return light;
	}

	@Override
	public boolean unmount(Avatar avatar, Game game) {
		if( !avatar.pickupItem(light, game) ){
			return false;
		}

		if( light == null){
			return false;
		}

		if( !light.pickUp(avatar) ){
			return false;
		}

		// Remove our reference to the light
		light = null;
		return true;
	}

	@Override
	public boolean mount(Light t) {
		if( light != null ){
			return false;
		}

		light = t;
		return true;
	}

	@Override
	public boolean isMounted() {
		return light != null;
	}

	@Override
	public boolean interactWith(Avatar avatar, Game game) {
		if( isMounted() ){
			return unmount(avatar, game);
		}
		else{
			// Mount again
			for( int i = 0; i < avatar.getInventory().size(); i++ ){
				Item item = avatar.getInventory().get(i);
				if( item instanceof Light ){

					if( mount((Light)item) ){
						avatar.removeItemFromInventory(item);

						if( ((Light)item).turnedOn() ){
							avatar.removeLight((Light)item);
						}
					}
				}

			}
			return false;
		}
	}

	@Override
	public boolean canMount(Object object) {
		return object.getClass() == Light.class;
	}
}