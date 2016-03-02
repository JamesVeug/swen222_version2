package gameLogic;

import gameLogic.Items.Item;
import gameLogic.Tiles.Charger;
import gameLogic.Tiles.Column;
import gameLogic.Tiles.Door;
import gameLogic.Tiles.Floor;
import gameLogic.Tiles.Tile2D;
import gameLogic.Tiles.Tree;
import gameLogic.Tiles.Wall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import networking.Thinker;

/**
 *
 * @author griffiryan
 *
 * The Room class exists to contain Collections of tiles, items and The Avatars that are currently in them.
 */
public class Room implements Serializable {


	private static final long serialVersionUID = -6429120453292131025L;

	private Tile2D[][] tiles;
	private List<Door> doors;
	private List<Floor> floors;
	private List<Wall> walls;
	private List<Column> columns;
	private List<Tree> trees;
	private List<Charger> chargers;


	private List <Item> items;
	private List<Avatar> avatars;
	private List<Lightable> lights;

	private String roomPlace;



	public Room(Tile2D[][] tiles, List<Item> items) {
		this.tiles = tiles;
		this.items = items;


		this.doors = new ArrayList<Door>();
		this.floors = new ArrayList<Floor>();
		this.walls = new ArrayList<Wall>();
		this.columns = new ArrayList<Column>();
		this.items = new ArrayList<Item>();
		this.avatars = new ArrayList<Avatar>();
		this.trees = new ArrayList<Tree>();
		this.chargers = new ArrayList<Charger>();
		this.lights = new ArrayList<Lightable>();

		this.roomPlace = "NULL";

	}

	public String getRoomPlace() {
		return roomPlace;
	}

	public void setRoomPlace(String roomPlace) {
		this.roomPlace = roomPlace;
	}

	public Avatar getAvatar(String playerName){
		for(Avatar avatar: avatars){
			if(playerName.equals(avatar.getPlayerName())){
				return avatar;
			}
		}
		return null;
	}


	public void removeAvatar(Avatar avatar){
		avatars.remove(avatar);
		lights.remove(avatar);
	}

	public void addAvatar(Avatar avatar){
		avatars.add(avatar);
		addLight(avatar);
	}

	public void addItem(Item item){
		items.add(item);
		
		if( item instanceof Lightable){
			addLight((Lightable)item);
		}
	}
	
	public List<Lightable> getLights(){
		return lights;
	}
	
	public boolean addLight(Lightable light){
		if( light == null ){
			throw new RuntimeException("Attempting to add null light");
		}
		return lights.add(light);
	}
	
	public boolean removeLight(Lightable light){
		return lights.remove(light);
	}


	public Item getItemAt(int x, int y){
		
		try{
			Item item = tiles[x][y].getTopItem();
			return item;
		}catch(ArrayIndexOutOfBoundsException e){}
		return null;
	}

	public Tile2D[][] getTiles(){
		return tiles;
	}

	public List<Avatar> getAvatars() {
		return avatars;
	}

	public void setAvatars(List<Avatar> avatars) {
		this.avatars = avatars;
	}

	public void setDoors(List<Door> roomDoors) {
		doors = roomDoors;
	}

	public void setFloors(List<Floor> roomFloors) {
		floors = roomFloors;
	}


	public void setWalls(List<Wall> roomWalls) {
		walls = roomWalls;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public List<Door> getDoors() {
		return doors;
	}
	
	public Door getDoorLeadingToRoom(Room room){
		for(Door d : doors){
			Room toRoom = d.getToRoom();
			if( toRoom != null && toRoom.getRoomPlace().equals(room.getRoomPlace())){
				return d;
			}
		}
		
		return null;
	}

	public List<Floor> getFloors() {
		return floors;
	}


	public List<Wall> getWalls() {
		return walls;
	}

	public void setTiles(Tile2D[][] tiles) {
		this.tiles = tiles;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((avatars == null) ? 0 : avatars.hashCode());
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
		Room other = (Room) obj;
		if (avatars == null) {
			if (other.avatars != null)
				return false;
		} else if (!avatars.equals(other.avatars))
			return false;
		if (doors == null) {
			if (other.doors != null)
				return false;
		} else if (!doors.equals(other.doors))
			return false;
		if (floors == null) {
			if (other.floors != null)
				return false;
		} else if (!floors.equals(other.floors))
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		if (!Arrays.deepEquals(tiles, other.tiles))
			return false;
		if (walls == null) {
			if (other.walls != null)
				return false;
		} else if (!walls.equals(other.walls))
			return false;
		return true;
	}

	public void tiles2DSet(int y, int x, Tile2D t){
		t.setRoom(this);
		tiles[y][x] = t;
	}
	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Tree> getTrees() {
		return trees;
	}

	public void setTrees(List<Tree> trees) {
		this.trees = trees;
	}

	public List<Charger> getChargers() {
		return chargers;
	}

	public void setChargers(List<Charger> chargers) {
		this.chargers = chargers;
	}
	
	@Override
	public String toString(){
		return roomPlace;
	}
}
