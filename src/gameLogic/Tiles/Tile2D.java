package gameLogic.Tiles;

import gameLogic.Avatar;
import gameLogic.Room;
import gameLogic.Items.Item;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author griffiryan
 *
 * Tile2D is a generalization of any location in the playing world. It can hold avatars and items.
 * A Tile2D operates as a graph structure, with access to its neighbouring tiles.
 */
public class Tile2D implements Serializable, Cloneable{


	private static final long serialVersionUID = 111202619281809955L;

	private Point point;
	private Room room;

	private List <Item> itemsOnTile;

	// One character per Tile at any given time
	private Avatar avatarOnTile;


	public Tile2D(int xPos, int yPos) {
		point = new Point(xPos,yPos);

		this.itemsOnTile = new ArrayList<Item>();
		this.avatarOnTile = null;
	}
	
	public Tile2D(Point point2) {
		this(point2.x,point2.y);
	}

	@Override
	public Tile2D clone(){
		Tile2D clone = new Tile2D(point);
		return clone;
		
	}



	/**
	 *  items on tile will be returned with the lowest weights first
	 * @param item - the Item object to be added to the tile
	 */
	public void addItem(Item item){
		itemsOnTile.add(item);
		Collections.sort(itemsOnTile,new Comparator<Item>() {
			@Override
			public int compare(Item item1, Item item2) {
				if(item1.getWeight()<item2.getWeight()) return -1;
				else if (item1.getWeight()>item2.getWeight()) return 1;
				else return 0;
			}
		});
	}


	/**
	 * GRAPH STRUCTURE
	 */


	public Tile2D getTileRight(){
		if(this.point.x <= 0) return null;
		Tile2D newTile = room.getTiles()[point.y][point.x-1];
		return newTile==null ? this : newTile;
	}

	public Tile2D getTileLeft(){
		if(this.point.x >= room.getTiles().length-1) return null;
		Tile2D newTile = room.getTiles()[point.y][point.x+1];
		return newTile==null ? this : newTile;
	}

	public Tile2D getTileUp(){
		if(this.point.y <= 0) return null;
		Tile2D newTile =  room.getTiles()[point.y-1][point.x];
		return newTile==null ? this : newTile;
	}

	public Tile2D getTileDown(){
		if(this.point.y >= room.getTiles().length-1) return null;
		Tile2D newTile = room.getTiles()[point.y+1][point.x];
		return newTile==null ? this : newTile;
	}

	public boolean removeItem(Item item){
		return itemsOnTile.remove(item);
	}

	public Item getTopItem(){
		if(itemsOnTile.size()==0) return null;
		return itemsOnTile.get(0);
	}

	public boolean itemOnTile(){
		if(avatarOnTile != null) return false;
		return true;
	}

	public List<Item> getItems() {
		return itemsOnTile;
	}

	public void removeAvatar(Avatar player) {
		if (avatarOnTile == null) return;
		if(avatarOnTile.equals(player)){
			avatarOnTile = null;
		}
	}

	public void addAvatar(Avatar player) {
		avatarOnTile = player;
	}

	public int getxPos() {
		return point.x;
	}
	
	public Point getPos() {
		return point;
	}

	public void setxPos(int xPos) {
		this.point.x = xPos;
	}

	public int getyPos() {
		return point.y;
	}

	public void setyPos(int yPos) {
		this.point.y = yPos;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setItems(List<Item> itemsOnTile) {
		this.itemsOnTile = itemsOnTile;
	}

	public Avatar getAvatar() {
		return avatarOnTile;
	}

	public void setAvatarOnTile(Avatar avatarOnTile) {
		this.avatarOnTile = avatarOnTile;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + point.x;
		result = prime * result + point.y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tile2D other = (Tile2D) obj;
		if (point.x != other.point.x)
			return false;
		if (point.y != other.point.y)
			return false;
		return true;
	}


}
