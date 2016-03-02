package gameLogic.Items;

import rendering.Direction;
import Sound.SoundController;
import gameLogic.Avatar;
import gameLogic.Cell;
import gameLogic.Container;
import gameLogic.Directional;
import gameLogic.Game;
import gameLogic.Toggleable;
import gameLogic.Tiles.Powerable;
import gameLogic.Tiles.Tile2D;
import networking.Thinker;

public class Wire extends Item implements Toggleable, Powerable, Thinker, Pickupable, Rotatable, Directional{

	public static final String description = "Passes power from one Powerable to another.";
	private static final int MAXCHARGE = 10;
	private static final int RESTOREAMOUNT = 1;
	private boolean toggled = false;
	private Powerable previous = null;
	private Powerable next = null;
	private Cell cell = new Cell(this, MAXCHARGE, 0);
	private Direction direction;
	private Container container;


	public Wire(Tile2D tile, Powerable previous, Powerable next, Direction direction) {
		super(tile);
		this.previous = previous;
		this.next = next;
		this.direction = direction;
	}

	@Override
	public int getWeight() {
		return 0;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean interactWith(Avatar avatar) {
		if( toggled ){
			avatar.getCell().decBattery(5);
		}
		return true;
	}

	@Override
	public boolean drop(Avatar avatar){

		Tile2D[][] tiles = avatar.getCurrentRoom().getTiles();
		int x = tile.getxPos();
		int y = tile.getyPos();

		if( direction == Direction.NORTH || direction == Direction.SOUTH ){

			// Assign Next
			if( tiles[y+1][x] instanceof Powerable ){
				// Found Previous tile
				previous = (Powerable)tiles[y+1][x];
				previous.join(this);
			}
			else{
				// Found Previous item
				for( Item item : tiles[y+1][x].getItems() ){
					if( item instanceof Powerable ){
						previous = (Powerable)item;
						if( item instanceof Wire && ((Wire)item).getNext() == null ){
							((Wire)item).setNext(this);
						}
						break;
					}
				}
			}

			// Assign Previous
			if( tiles[y-1][x] instanceof Powerable ){
				// Found NEXT tile
				next = (Powerable)tiles[y-1][x];
				next.join(this);
			}
			else{
				// Found NEXT item
				for( Item item : tiles[y-1][x].getItems() ){
					if( item instanceof Powerable ){
						next = (Powerable)item;
						if( item instanceof Wire && ((Wire)item).getPrevious() == null ){
							((Wire)item).setPrevious(this);
						}
						break;
					}
				}
			}
		}
		else{

			// Assign Next
			if( tiles[y][x+1] instanceof Powerable ){
				// Found Previous tile
				previous = (Powerable)tiles[y][x+1];
				previous.join(this);
			}
			else{
				// Found Previous item
				for( Item item : tiles[y][x+1].getItems() ){
					if( item instanceof Powerable ){
						previous = (Powerable)item;
						if( item instanceof Wire && ((Wire)item).getNext() == null ){
							((Wire)item).setNext(this);
						}
						break;
					}
				}
			}

			// Assign Previous
			if( tiles[y][x-1] instanceof Powerable ){
				// Found NEXT tile
				next = (Powerable)tiles[y][x-1];
				next.join(this);
			}
			else{
				// Found NEXT item
				for( Item item : tiles[y][x-1].getItems() ){
					if( item instanceof Powerable ){
						next = (Powerable)item;
						if( item instanceof Wire && ((Wire)item).getPrevious() == null ){
							((Wire)item).setPrevious(this);
						}
						break;
					}
				}
			}
		}

		return true;
	}

	@Override
	public boolean pickUp(Avatar avatar){

		if( toggled ){
			toggled = false;
			avatar.getCell().decBattery(cell.getBatteryLife());
			cell.setBatteryLife(0);
			SoundController.play("zap2.wav");
		}

		if( previous != null ){
			if( previous instanceof Wire ){
				((Wire)previous).setNext(null);
			}
			else{
				previous.disconnect(this);
			}
			previous = null;
		}

		if( next != null ){
			if( next instanceof Wire ){
				((Wire)next).setPrevious(null);
			}
			else{
				next.disconnect(this);
			}
			next = null;
		}

		return true;
	}

	public void setPrevious(Powerable previous) {
		this.previous = previous;
	}

	@Override
	public boolean turnedOn() {
		return toggled;
	}

	@Override
	public boolean turnedOff() {
		return !toggled;
	}

	@Override
	public void turnOn() {
		toggled = true;

	}

	@Override
	public void turnOff() {
		toggled = false;
	}

	@Override
	public void toggle() {
		toggled = !toggled;
	}

	@Override
	public Cell getCell() {
		return cell;
	}

	public int getSpriteIndex(){
		return direction == Direction.NORTH || direction == Direction.SOUTH ? 1 : 0;
	}

	@Override
	public void think(Game game) {
		if( !toggled && cell.getBatteryLife() == MAXCHARGE ){
			turnOn();
		}
		else if( toggled && next != null ){

			if( !next.getCell().fullyCharged() ){

				if( cell.getBatteryLife() > RESTOREAMOUNT ){

					// Distribute
					if( next instanceof Water ){
						((Water) next).distributeEnergy(RESTOREAMOUNT);
					}
					else{
						next.getCell().incBattery(RESTOREAMOUNT);
					}

					// Lose battery
					cell.decBattery(RESTOREAMOUNT);

				} else {
					turnOff();
				}
			}
		}
	}

	@Override
	public boolean join(Powerable power) {
		return false;
	}

	@Override
	public boolean disconnect(Powerable power) {
		return false;
	}

	public void setNext(Powerable next) {
		this.next = next;
	}

	public Powerable getNext(){
		return next;
	}

	public Powerable getPrevious(){
		return previous;
	}

	@Override
	public void rotate() {
		direction = Direction.getRotatedDirection(direction, Direction.EAST);

	}

	@Override
	public Container getContainer() {
		return container;
	}

	@Override
	public boolean isPickedUp() {
		return container != null;
	}

	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public Direction getFacingDirection() {
		return direction;
	}

	@Override
	public void setFacingDirection(Direction face) {
		this.direction = face;

	}
}
