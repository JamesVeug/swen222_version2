package gameLogic;

import gameLogic.Items.AABattery;
import gameLogic.Items.Box;
import gameLogic.Items.GreenKey;
import gameLogic.Items.LargeBattery;
import gameLogic.Items.Light;
import gameLogic.Items.RedKey;
import gameLogic.Items.ScrapMetal;
import gameLogic.Items.Shoes;
import gameLogic.Items.Water;
import gameLogic.Items.Wire;
import gameLogic.Items.YellowKey;
import gameLogic.Tiles.BlankFloor;
import gameLogic.Tiles.Charger;
import gameLogic.Tiles.Column;
import gameLogic.Tiles.Door;
import gameLogic.Tiles.EmptyTile;
import gameLogic.Tiles.Floor;
import gameLogic.Tiles.GreenDoor;
import gameLogic.Tiles.LightMount;
import gameLogic.Tiles.Powerable;
import gameLogic.Tiles.PoweredDoor;
import gameLogic.Tiles.RedDoor;
import gameLogic.Tiles.Tile2D;
import gameLogic.Tiles.Tree;
import gameLogic.Tiles.Wall;
import gameLogic.Tiles.YellowDoor;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import rendering.Direction;
import networking.Thinker;

public class NewGame {

	public NewGame(Game game){

		// Create rooms
		for( int i = 0; i <= 10; i++ ){
			game.addRoom(makeRoom(game, "/gameLogic/rooms/room_" + i + ".txt"));
		}
		linkRooms(game.getRoomsInGame());

		// Add Decorations
		addDecorations(game, game.getRoomsInGame());

		// Add Items
		addItems(game, game.getRoomsInGame());

		// Add Water
		addWaterToRoom5(game,game.getRoomsInGame());
		addWaterToRoom6(game,game.getRoomsInGame());

	}

	private void addWaterToRoom6(Game game, List<Room> rooms){
		Room room = rooms.get(6);
		Tile2D tile = null;
		Water water = null;

		// Get Charger
		Charger charger = room.getChargers().get(0);

		// Water
		for( int x = 12; x < 15; x++){
			for( int y = 0; y < 15; y++){
				tile = room.getTiles()[y][x];
				water = new Water(tile);
				tile.addItem(water);
				room.addItem(water);
				game.addThinker(water);
			}
		}

		for( int x = 5; x < 15; x++){
			for( int y = 12; y < 15; y++){
				tile = room.getTiles()[y][x];
				water = new Water(tile);
				tile.addItem(water);
				room.addItem(water);
				game.addThinker(water);
			}
		}
		for( int x = 0; x < 6; x++){
			tile = room.getTiles()[12][x];
			water = new Water(tile);
			tile.addItem(water);
			room.addItem(water);
			game.addThinker(water);

			tile = room.getTiles()[14][x];
			water = new Water(tile);
			tile.addItem(water);
			room.addItem(water);
			game.addThinker(water);
		}

		// Get wire to power this water
		water = (Water)room.getTiles()[5][14].getTopItem();
		Wire wire = (Wire)room.getTiles()[5][15].getTopItem();
		wire.setNext(water);
		water.join(wire);

		// Connect room 6 water with room 5's water
		water = (Water)room.getTiles()[14][0].getTopItem();

		Room room5 = rooms.get(5);
		Water water5 = (Water) room5.getTiles()[5][room5.getTiles().length-1].getTopItem();
		water.join(water5);

		water5 = (Water) room5.getTiles()[3][room5.getTiles().length-1].getTopItem();
		water.join(water5);
	}

	private void addWaterToRoom5(Game game, List<Room> rooms){
		Room room = rooms.get(5);
		Tile2D tile = null;
		Water water = null;

		// Water
		for( int x = room.getTiles().length-1; x >= 0; x--){
			tile = room.getTiles()[5][x];
			water = new Water(tile);
			tile.addItem(water);
			room.addItem(water);
			game.addThinker(water);
		}

		//--X
		//--X
		//--X
		//XXX
		for( int x = 0; x < 4; x++){
			// Horizontal
			tile = room.getTiles()[3][room.getTiles()[0].length-x-1];
			water = new Water(tile);
			tile.addItem(water);
			room.addItem(water);
			game.addThinker(water);

			if( x < 4 ){
				tile = room.getTiles()[x][room.getTiles()[0].length-5];
				water = new Water(tile);
				tile.addItem(water);
				room.addItem(water);
				game.addThinker(water);
			}
		}
	}

	private void addDecorations(Game game, List<Room> rooms ) {
		// ScrapMetal
		Room room = rooms.get(0);
		ScrapMetal metal = new ScrapMetal(room.getTiles()[1][3]);
		room.getTiles()[1][3].addItem(metal);
		room.addItem(metal);

		metal = new ScrapMetal(room.getTiles()[2][2]);
		room.getTiles()[2][2].addItem(metal);
		room.addItem(metal);

		/** Room 1 **/

		// Wires
		Room room1 = rooms.get(1);
		Tile2D tile = room1.getTiles()[5][4];
		Wire wire1 = new Wire(tile,null,null, Direction.EAST);
		((Powerable)room1.getTiles()[5][5]).join(wire1);
		tile.addItem(wire1);
		room1.addItem(wire1);
		game.addThinker(wire1);

		tile = room1.getTiles()[5][1];
		Wire wire2 = new Wire(tile,null, (Powerable)room1.getTiles()[5][0], Direction.EAST);
		((Powerable)room1.getTiles()[5][0]).join(wire2);
		tile.addItem(wire2);
		room1.addItem(wire2);
		game.addThinker(wire2);

		tile = room1.getTiles()[1][8];
		Wire wire3 = new Wire(tile,null, null, Direction.NORTH);
		tile.addItem(wire3);
		room1.addItem(wire3);
		game.addThinker(wire3);

		tile = room1.getTiles()[9][8];
		Wire wire4 = new Wire(tile,null, null, Direction.NORTH);
		tile.addItem(wire4);
		room1.addItem(wire4);
		game.addThinker(wire4);

		/** Room 6 */
		Room room6 = rooms.get(6);
		tile = room6.getTiles()[5][16];
		wire1 = new Wire(tile,(Powerable)room6.getTiles()[5][17],null, Direction.EAST);
		((Powerable)room6.getTiles()[5][17]).join(wire1);
		tile.addItem(wire1);
		room6.addItem(wire1);
		game.addThinker(wire1);

		tile = room6.getTiles()[5][15];
		wire2 = new Wire(tile,wire1,null, Direction.EAST);
		wire1.setNext(wire2);
		tile.addItem(wire2);
		room6.addItem(wire2);
		game.addThinker(wire2);
	}

	private void addItems(Game game, List<Room> rooms) {

		// Spawn
		Room room = rooms.get(0);
		Tile2D tile = room.getTiles()[2][5];

		// Box w/shoes
		Box box = new Box(tile);
		box.getInventory().add(new ScrapMetal(null));
		tile.addItem(box);
		room.addItem(box);

		// AABattery
		tile = room.getTiles()[2][4];
		AABattery battery = new AABattery(tile);
		tile.addItem(battery);
		room.addItem(battery);

		// Large Battery
		tile = room.getTiles()[4][4];
		LargeBattery lbat = new LargeBattery(tile);
		tile.addItem(lbat);
		room.addItem(lbat);

		// Room 6
		Room room6 = rooms.get(6);
		tile = room6.getTiles()[5][7];
		RedKey redKey = new RedKey(tile);
		tile.addItem(redKey);
		room6.addItem(redKey);

		tile = room6.getTiles()[9][6];
		ScrapMetal scrap = new ScrapMetal(tile);
		tile.addItem(scrap);
		room6.addItem(scrap);

		tile = room6.getTiles()[5][8];
		scrap = new ScrapMetal(tile);
		tile.addItem(scrap);
		room6.addItem(scrap);

		// Room 7
		Room room7 = rooms.get(7);
		tile = room7.getTiles()[2][1];
		YellowKey yellowKey = new YellowKey(tile);
		tile.addItem(yellowKey);
		room7.addItem(yellowKey);

		// Room 2
		Room room2 = rooms.get(2);
		tile = room2.getTiles()[room2.getTiles().length-2][1];
		GreenKey greenKey = new GreenKey(tile);
		tile.addItem(greenKey);
		room2.addItem(greenKey);

		// Room 4
		// Large Battery
		Room room4 = rooms.get(4);
		tile = room4.getTiles()[5][2];
		box = new Box(tile);
		box.getInventory().add(new LargeBattery(null));
		box.getInventory().add(new ScrapMetal(null));
		box.getInventory().add(new Shoes(null));
		tile.addItem(box);
		room4.addItem(box);
	}

	private void linkRooms(List<Room> rooms){

		// Room 0
		rooms.get(0).getDoors().get(0).setToRoom(rooms.get(9));
		rooms.get(0).getDoors().get(1).setToRoom(rooms.get(10));

		// Room 1
		rooms.get(1).getDoors().get(0).setToRoom(rooms.get(10));
		rooms.get(1).getDoors().get(1).setToRoom(rooms.get(2));

		// Room 2
		rooms.get(2).getDoors().get(0).setToRoom(rooms.get(3));
		rooms.get(2).getDoors().get(1).setToRoom(rooms.get(1));

		// Room 3
		rooms.get(3).getDoors().get(0).setToRoom(rooms.get(4));
		rooms.get(3).getDoors().get(1).setToRoom(rooms.get(2));

		// Room 4
		rooms.get(4).getDoors().get(0).setToRoom(rooms.get(8));
		rooms.get(4).getDoors().get(1).setToRoom(rooms.get(7));
		rooms.get(4).getDoors().get(2).setToRoom(rooms.get(10));
		rooms.get(4).getDoors().get(3).setToRoom(rooms.get(3));

		// Room 5
		rooms.get(5).getDoors().get(0).setToRoom(rooms.get(8));
		rooms.get(5).getDoors().get(1).setToRoom(rooms.get(10));

		// Room 6
		rooms.get(6).getDoors().get(0).setToRoom(rooms.get(10));

		// Room 7
		rooms.get(7).getDoors().get(0).setToRoom(rooms.get(4));

		// Room 8
		rooms.get(8).getDoors().get(0).setToRoom(rooms.get(5));
		rooms.get(8).getDoors().get(1).setToRoom(rooms.get(4));

		// Room 9
		rooms.get(9).getDoors().get(0).setToRoom(rooms.get(0));

		// Room 10
		rooms.get(10).getDoors().get(0).setToRoom(rooms.get(5));
		rooms.get(10).getDoors().get(1).setToRoom(rooms.get(6));
		rooms.get(10).getDoors().get(2).setToRoom(rooms.get(4));
		rooms.get(10).getDoors().get(3).setToRoom(rooms.get(0));
		rooms.get(10).getDoors().get(4).setToRoom(rooms.get(1));
	}

	private Door getNewDoor(int x, int y, int doorCount, String roomName ){
		if (roomName.equals("room0")) {
			if (doorCount == 0) { return new RedDoor(x, y); }
			if (doorCount == 1) { return new Door(x, y); }
		}
		else if (roomName.equals("room1")) {
			if (doorCount == 0) { return new YellowDoor(x, y); }
			if (doorCount == 1) { return new PoweredDoor(x, y); }
		}
		else if (roomName.equals("room2")) {
			if (doorCount == 0) { return new Door(x, y); }
			if (doorCount == 1) { return new Door(x, y); }
		}
		else if (roomName.equals("room3")) {
			if (doorCount == 0) { return new Door(x, y); }
			if (doorCount == 1) { return new Door(x, y); }
		}
		else if (roomName.equals("room4")) {
			if (doorCount == 0) { return new GreenDoor(x, y); }
			if (doorCount == 1) { return new Door(x, y); }
			if (doorCount == 2) { return new Door(x, y); }
			if (doorCount == 3) { return new Door(x, y); }
		}
		else if (roomName.equals("room5")) {
			if (doorCount == 0) { return new Door(x, y); }
			if (doorCount == 1) { return new Door(x, y); }
		}
		else if (roomName.equals("room6")) {
			if (doorCount == 0) { return new Door(x, y); }
		}
		else if (roomName.equals("room7")) {
			if (doorCount == 0) { return new Door(x, y); }
		}
		else if (roomName.equals("room8")) {
			if (doorCount == 0) { return new Door(x, y); }
			if (doorCount == 1) { return new GreenDoor(x, y); }
		}
		else if (roomName.equals("room9")) {
			if (doorCount == 0) { return new RedDoor(x, y); }
		}
		else if (roomName.equals("room10")) {
			if (doorCount == 0) { return new Door(x, y); }
			if (doorCount == 1) { return new Door(x, y); }
			if (doorCount == 2) { return new Door(x, y); }
			if (doorCount == 3) { return new Door(x, y); }
			if (doorCount == 4) { return new YellowDoor(x, y); }
		}
		System.out.println("Null room for " + x + " " + y + " " + doorCount + " " + roomName);
		return null;
	}

	/**
	 * Important to Note that if there is an IO exception thrown in this method, even if it is caught the method will return null.
	 * @param string
	 * @return
	 * @author Leon North and Ryan Griffin
	 */
	private Room makeRoom(Game game, String v){
		int doorCount = 0;

		Scanner scan = new Scanner(NewGame.class.getResourceAsStream(v));

		String tile = null;
		int tileRows = 0;
		int tileCols = 0;
		int tileRowsFinal = 0;

		String roomPlace = scan.next();
		while (scan.hasNext()) { // Initial loop to count tiles for 2d array
									// construction
			tile = scan.next();
			if (tile == null)
				break;
			else if (tile.toUpperCase().equals("E")) {
				tileCols++;
				tileRowsFinal = tileRows;
				tileRows = 0;
			} else {
				tileRows++;
			}
		}

		scan = new Scanner(NewGame.class.getResourceAsStream(v));
		tile = null; // precautionary read reset
		int x = 0;
		int y = 0;

		Tile2D[][] tiles = new Tile2D[tileRowsFinal][tileCols];
		roomPlace = scan.next();
		while (scan.hasNext()) {
			tile = scan.next();
			if (tile == null)
				break;
			else if (tile.toUpperCase().equals("E")) {
				x = 0;
				y++;
				continue;

			} else if (tile.toUpperCase().equals("W")) {
				Tile2D wall = new Wall(x, y);
				tiles[y][x] = wall;

			} else if (tile.toUpperCase().equals("F")) {
				Tile2D floor = new BlankFloor(x, y);
				tiles[y][x] = floor;
			} else if (tile.toUpperCase().equals("X")) {
				Tile2D floor = new Floor(x, y);
				tiles[y][x] = floor;
			} else if (tile.equals("-")) {
				Tile2D floor = new EmptyTile(x, y);
				tiles[y][x] = floor;
			} else if (tile.toUpperCase().equals("D")) {
				tiles[y][x] = getNewDoor(x,y,doorCount,roomPlace);
				doorCount++;
			} else if (tile.toUpperCase().equals("C")) {
				Tile2D column = new Column(x, y);
				tiles[y][x] = column;
			} else if (tile.toUpperCase().equals("T")) {
				Tile2D tree = new Tree(x, y);
				tiles[y][x] = tree;
			} else if (tile.toUpperCase().equals("Z")) {
				Tile2D charger = new Charger(x, y);
				tiles[y][x] = charger;
			} else if (tile.toUpperCase().equals("N")) {
				Tile2D light = new LightMount(x, y);
				tiles[y][x] = light;
			} else if (tile.toUpperCase().equals("M")) {
				LightMount mount = new LightMount(x, y);
				mount.addLight(new Light(null));
				tiles[y][x] = mount;
			}
			else{
				throw new RuntimeException("Unknown Character: " + tile);

			}
			x++;

		}
		Room room = new Room(tiles, null);
		room.setRoomPlace(roomPlace);
		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				tiles[i][j].setRoom(room);
				if (tiles[i][j] instanceof Door)
					room.getDoors().add((Door) tiles[i][j]);
				if (tiles[i][j] instanceof Thinker)
					game.addThinker((Thinker)tiles[i][j]);
				if (tiles[i][j] instanceof Floor)
					room.getFloors().add((Floor) tiles[i][j]);
				if (tiles[i][j] instanceof Wall)
					room.getWalls().add((Wall) tiles[i][j]);
				if (tiles[i][j] instanceof Column)
					room.getColumns().add((Column) tiles[i][j]);
				if (tiles[i][j] instanceof Tree)
					room.getTrees().add((Tree) tiles[i][j]);
				if (tiles[i][j] instanceof Charger)
					room.getChargers().add((Charger) tiles[i][j]);
				if (tiles[i][j] instanceof LightMount && ((LightMount) tiles[i][j]).isMounted()){
					room.addLight((LightMount) tiles[i][j]);
					if( new Random().nextInt(1) == 0 ){
						((LightMount) tiles[i][j]).turnOn();
					}
				}
			}
		}

		return room;
	}
}
