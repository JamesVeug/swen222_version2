package gameLogic.Items;

import gameLogic.Avatar;
import gameLogic.Container;
import gameLogic.Lightable;
import gameLogic.Toggleable;
import gameLogic.Tiles.Tile2D;

/**
 *
 * @author Ryan Griffin and Leon North
 * Avatars that contain a light object in their inventory can see better during the night cycle.
 */
public class Light extends Item implements Lightable, Toggleable, Pickupable{

	// Lighting
	private float Red = 255;
	private float Green = 255;
	private float Blue = 255;
	private double Brightness = 255;
	private int radius = 5;
	private boolean turnedOn = false;
	private Container container;
	private static final String description = "Mountable light that projects light";

	public Light(Tile2D tile) {
		super(tile);
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public Tile2D getTile() {
		return tile;
	}

	@Override
	public boolean moveItemTo(Tile2D toTile) {
		if(toTile == null) return false;
		this.tile = toTile;
		toTile.addItem(this);
		return true;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public boolean interactWith(Avatar avatar) {
		return false;
	}

	public boolean drop(Avatar avatar){

		if( tile != null ){
			// Change the room if the light is on
			tile.getRoom().addLight(this);
		}
		else if( container != null){
			// Change the room if the light is on
			container.getTile().getRoom().addLight(this);
		}

		avatar.removeLight(this);

		turnOff();
		return true;
	}

	public boolean pickUp(Avatar avatar){

		if( turnedOn() ){
			turnOff();
		}

		avatar.addLight(this);

		// Remove light from room
		avatar.getTile().getRoom().removeLight(this);

		return true;
	}

	@Override
	public void returnToStartPos() {
		tile = startTile;
		tile.addItem(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Item other = (Item) obj;
		if (itemID != other.itemID) return false;
		return true;
	}

	@Override
	public float getR() {
		return Red;
	}

	@Override
	public float getG() {
		return Green;
	}

	@Override
	public float getB() {
		return turnedOn ? Blue : 0;
	}

	public double getBrightness() {
		return Brightness;
	}

	public void setBrightness(double brightness) {
		Brightness = brightness;
	}

	@Override
	public int getRadius() {
		return radius;
	}
	@Override
	public int getxPos() {
		return tile != null ? tile.getxPos() : getContainer().getTile().getxPos();
	}
	@Override
	public int getyPos() {
		return tile != null ? tile.getyPos() : getContainer().getTile().getyPos();
	}

	@Override
	public boolean turnedOn() {
		return turnedOn;
	}

	@Override
	public void turnOff() {
		if( container != null && container instanceof Avatar ){
			((Avatar)container).removeLight(this);
		}
		turnedOn = false;
	}

	@Override
	public void toggle() {
		if( turnedOn ){
			turnOff();
		}
		else{
			turnOn();
		}
	}

	@Override
	public void turnOn() {
		Container container = getContainer();
		if( container != null && container instanceof Avatar){
			((Avatar)container).addLight(this);
		}
		turnedOn = true;
	}

	@Override
	public boolean turnedOff() {
		return !turnedOn;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean isPickedUp() {
		return container != null;
	}
}
