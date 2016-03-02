package gameLogic;


import gameLogic.Items.Consumable;
import gameLogic.Items.Item;
import gameLogic.Items.Light;
import gameLogic.Items.Pickupable;
import gameLogic.Items.Rotatable;
import gameLogic.Items.Shoes;
import gameLogic.Items.YellowKey;
import gameLogic.Tiles.Charger;
import gameLogic.Tiles.Door;
import gameLogic.Tiles.Floor;
import gameLogic.Tiles.Powerable;
import gameLogic.Tiles.Tile2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Sound.SoundController;
import networking.Move;
import networking.Thinker;
import rendering.Direction;

public class Avatar implements Serializable, Lightable, Powerable, Thinker, Container, Directional{

	private static final long serialVersionUID = 4723069455200795911L;

	private String playerName;
	private Tile2D tile;
	private Room room;
	private Direction facing;
	private Move currentMove;

	// Lighting
	private float red = 255;
	private float green = 0;
	private float blue = 0;
	private float brightness = 255;
	private int radius = 4;
	private boolean turnedOn = true;
	private ArrayList<Lightable> lights = new ArrayList<Lightable>();

	// Avatar's coordinates relative to the tile - local.
	private double tileXPos, tileYPos;


	// Tile dimensions
	private final double tileWidth = 100;
	private final double tileHeight = 100;

	// Tile Centers
	private final double tileXCenter = tileWidth / 2 ;
	private final double tileYCenter = tileHeight / 2;


	//If Avatar moves to adjacent tile their local tile coordinates are set with these fields
	private final double tileMinPos = 1;
	private final double tileMaxPos = 100;

	// The amount the Avatar moves with each key press
	private int stepAmount = 16;


	// While the sprite is animating, spriteIndex will hold the index to the current frame to be displayed for the animation.
	private int spriteIndex;

	//Is this Avatar object a Player, or an AI
	private boolean isAI;

	// Locations for when Avatar dies, can be returned to here.
	private Tile2D startTile;
	private Room startRoom;

	// The avatars current kill score
	private int score;

	// To tell the avatar that killed you to increment their score, also if multiple people hitting you, the score goes to the person who hit you last.
	private Avatar lastHit;

	private int maxCharge = 500;
	private int damage = 125;

	private Cell cell;

	private int maxInventorySize = 12;
	private List <Item> inventory;

	private static final String DROP = "drop.wav";
	private static final String PICKUP = "pickup.wav";
	private static final String LIGHT_SWITCH = "lightswitch.wav";
	private static final String DOOR_UNLOCK= "doorunlock.wav";

	public Avatar(String name, Tile2D tile, Room room, Game game){
		this.playerName = name;
		this.startTile = tile;
		this.startRoom = room;

		updateLocations(tile, room);

		this.inventory = new ArrayList<Item>();
		inventory.add(new YellowKey(null));

		this.facing = Direction.NORTH;

		this.cell = new Cell(this, maxCharge, maxCharge);

		// Avatars relative tile coordinates are initalized to the center of the tile
		this.tileXPos = (tileWidth/2);
		this.tileYPos = (tileHeight/2);

		// Avatars initial sprite image is the 0th element in the animation sequence
		this.spriteIndex = 0;

		if(name.startsWith("ai")){
			stepAmount = 102;
			this.isAI=true;
		}

		this.score = 0;
		this.lastHit = null;
	}
	/**
	 * Every time an avatar move is performed, this method is called to update the avatars current room and tile postitions.
	 * @param tile the new Tile position of the avatar.
	 * @param room the new Room of the avatar.
	 */
	public void updateLocations(Tile2D tile, Room room) {

		updateTile(tile);
		updateRoom(room);
	}

	private void updateTile(Tile2D newTile){
		if(newTile.equals(tile)) return;
		if(tile != null) tile.removeAvatar(this);
		newTile.addAvatar(this);
		tile = newTile;
	}

	private void updateRoom(Room newRoom){
		if(newRoom.equals(room)) return;
		if(room != null)	room.removeAvatar(this);
		newRoom.addAvatar(this);
		room = newRoom;
	}

	public boolean interact(Item item){
		return item.interactWith(this);
	}

	/**
	 *  Move the item from the avatars inventory
	 * @param move
	 * @param game
	 * @return true iff the item is removed succesfully
	 */
	public boolean dropItem(Move move, Game game){
		Tile2D dropTile = null;
		// Find a Tile close to the avatar that the item can be dropped on
		Direction dir = Direction.get(move.getAction());
		if(facing == Direction.NORTH && tile.getTileUp() != tile ) dropTile = tile.getTileUp();
		else if(facing == Direction.WEST && tile.getTileLeft() != tile ) dropTile = tile.getTileLeft();
		else if(facing == Direction.EAST && tile.getTileRight() != tile ) dropTile = tile.getTileRight();
		else if (facing == Direction.SOUTH && tile.getTileDown() != tile ) dropTile = tile.getTileDown();
		else return false;


		int remove = move.getIndex();
		if(remove >= inventory.size()) return false;
		Item item = inventory.get(remove);
		if( dropTile instanceof NonWalkable && (
				(dropTile instanceof Mountable) &&
				!((Mountable)dropTile).canMount(item) )){
			return false;
		}

		// Remove item form inventory
		removeItemFromInventory(item);

		if( item instanceof Directional ){
			((Directional) item).setFacingDirection(dir);
		}

		if( dropTile instanceof Mountable ){
			((Mountable) dropTile).mount(item);
		}
		else{
			// Move to the tile
			item.moveItemTo(dropTile);
		}


		// Tell item that it's been dropped
		((Pickupable)item).drop(this);
		return true;
	}


	public boolean openItem(Move move, Game game){
		Item item = inventory.get(move.getIndex());

		// We can only open items that are a container
		if( !(item instanceof Container) ){
			return false;
		}

		Container box = (Container)item;
		if( !box.hasItems() ){
			game.notifyMessage("Theres nothing in here!");
			return false;
		}
		if( isFull() ){
			game.notifyMessage("Inventory is full!");
			return false;
		}

		// Take items out
		Item inner = null;
		while(!box.getInventory().isEmpty() && !this.isFull() ){
			inner = box.getInventory().get(0);
			box.getInventory().remove(inner);
			this.pickupItem(inner, game);
		}

		// Opened and took out all items
		return true;
	}

	public boolean useItem(Move move, Game game){
		Item item = inventory.get(move.getIndex());
		if( !(item instanceof Consumable) ){
			return false;
		}

		Consumable battery = (Consumable)item;
		if(!battery.consume(this)){
			return false;
		}

		// Remove from inventory
		getInventory().remove(move.getIndex());

		// Consumed
		return true;
	}

	public boolean toggleItem(Move move, Game game){
		Item item = inventory.get(move.getIndex());
		if( !(item instanceof Toggleable) ){
			return false;
		}
		Toggleable battery = (Toggleable)item;
		battery.toggle();

		// SOUNDS
		if( item instanceof Light ){
			SoundController.play(LIGHT_SWITCH);
		}

		return true;
	}

	public boolean pickupItem(Tile2D pickupTile, Game game){

		// Make sure we are trying to pick an item up off a walkable space
		if( pickupTile instanceof NonWalkable ){
			return false;
		}

		Item item = null;
		if( !pickupTile.getItems().isEmpty() ){
			for( int i = 0; i < pickupTile.getItems().size(); i++){

				// Pick the item up?
				if( pickupTile.getItems().get(i) instanceof Pickupable ){
					item = pickupTile.getItems().get(i);
					break;
				}
			}
		}

		return pickupItem(item, game);
	}

	public boolean pickupItem(Item item, Game game){
		if( item == null || !(item instanceof Pickupable)){
			return false;
		}
		else if( this.isFull() ){
			return false; 	//no space left in inventory
		}

		// Remove item from tile
		if(item.getTile() != null){
			System.out.println("!+ NULL");
			if( !item.getTile().removeItem(item) ){
				game.notifyMessage("Could not remove item " + item.getDescription() + " from tile at " + item.getTile().getPos());
			}

			// Picked up items don't have a tile
			item.setTile(null);
		}

		((Pickupable)item).setContainer(this);
		addItemToInventory(item);
		return ((Pickupable)item).pickUp(this);
	}

	public boolean unmountItem(Move move, Game game){
		Item item = inventory.get(move.getIndex());
		if(item instanceof Mountable){
			Mountable battery = (Mountable)item;
			battery.unmount(this, game);
			return true;
		}



		return false;
	}

	public boolean rotateItem(Move move, Game game){
		Item item = inventory.get(move.getIndex());
		if(item instanceof Rotatable){
			Rotatable battery = (Rotatable)item;
			battery.rotate();
			return true;
		}



		return false;
	}

	public boolean interact(Move move, Game game){
		Tile2D tile = getFacingTile();
		if( tile == null ){
			return false;
		}

		// Check for items first
		if( pickupItem(tile, game) ){
			return true;
		}

		// No items interacted with
		if( tile instanceof Interactable ){

			boolean interacted = ((Interactable)tile).interactWith(this, game);
			if( interacted ){

				if( tile instanceof Lockable ){
					SoundController.play(DOOR_UNLOCK);
				}
			}
		}
		return false;
	}

	private void moveAvatar(Move move, Game game){
		updateFacing(move.getCommand());

		Tile2D newPosition = findTile(move);
		if(newPosition == null ) return;
		if(newPosition instanceof NonWalkable) return;
		if(newPosition.getAvatar() != null) return;


		if(newPosition instanceof Lockable && ((Lockable)newPosition).isLocked()){
			return;
		}
		if( newPosition instanceof Door ){

			Room room  = ((Door)newPosition).getToRoom();
			Door door = room.getDoorLeadingToRoom(getTile().getRoom());
			updateLocations(door, room);
		}
		else{
			updateLocations(newPosition,room);
		}

		// Check items for pickup
		if( !tile.getItems().isEmpty() ){

			int size = tile.getItems().size();
			for( int i = 0; i < size; i++){
				interact(tile.getItems().get(i));
				if( tile.getItems().size() != size ){
					size = tile.getItems().size();
					i--;
				}
			}
		}

		if(cell.isCharging()){
			useCell(newPosition, game);
		}
		else{
			cell.decBattery(1);
		}

		animation();
	}

	/**
	 * Tick method to perform every 30ms
	 */
	public void think(Game game){

		// Kill us if we need to
		if(cell.getBatteryLife()<=0){
			die(game);
		}

		// Recharge battery
		if( getCell().isCharging() ){

			if( getFacingTile() instanceof Recharger ){
				Recharger charger = ((Recharger)getFacingTile());
				charger.recharge(getCell());
			}
		}
		// Move Avatar
		else if( currentMove != null ){
			moveAvatar(currentMove, game);
		}
	}

	/**
	 * Move position is calculated using the give Move object. The moveTo() method finds the new position and checks to see if the avatar can move there.
	 * @param move
	 * @param game
	 * @return true if the move is completed succesfully
	 */
	public synchronized boolean moveTo(Move move, Game game){
		if(move.getCommand() == null) return false;
		if(move.getCommand().equalsIgnoreCase("drop")) return dropItem(move, game);
		if(move.getCommand().equalsIgnoreCase("open")) return openItem(move, game);
		if(move.getCommand().equalsIgnoreCase("toggle")) return toggleItem(move, game);
		if(move.getCommand().equalsIgnoreCase("use")) return useItem(move, game);
		if(move.getCommand().equalsIgnoreCase("unmount")) return unmountItem(move, game);
		if(move.getCommand().equalsIgnoreCase("interact")) return interact(move, game);
		if(move.getCommand().equalsIgnoreCase("rotate")) return rotateItem(move, game);

		if(move.getCommand().equals("O")){
			cell.setCharging(!cell.isCharging());
			useCell(getFacingTile(), game);
			return false;
		}

		// Record movement
		if( currentMove != null && currentMove.getCommand().equals(move.getCommand()) && move.getAction().equals("Stop") ){
			currentMove = null;
		}
		else if( move.getAction().equals("Start") ){
			currentMove = move;
		}

		return true;
	}


	/**
	 * @author Ryan Griffin and Leon North
	 * @return
	 */
	private void useCell(Tile2D target, Game game){

		if(target instanceof Recharger){
			((Recharger)target).recharge(cell);;getCell().incBattery(5);
		}
		else if( attack(target, game) == 1 ){
			getCell().decBattery(5);
		}

	}

	/**
	 * @author Ryan Griffin and Leon North
	 * @param target
	 * @return
	 */
	private int attack(Tile2D target, Game game){
		if(target.getAvatar() == null){
			return 0;
		}

		Avatar enemy = target.getAvatar();
		if(enemy != null){// && enemy1 != null && enemy2.equals(enemy1)){
			System.out.println(enemy);
			enemy.takeDamage(damage, game);
			enemy.setLastHit(this);
		}
		return 1;
	}

	/**
	 * @author Ryan Griffin and Leon North
	 * @param damage
	 */
	public void takeDamage(int damage, Game game){
		cell.takeHit(damage);
		//cell.decExtraBattery();
		if(cell.getBatteryLife()<=0){
			die(game);
		}
	}

	private void die(Game game){
		score--;
		game.notifyMessage(""+playerName+" died! Score is now: "+ score);
		if(lastHit != null){
			lastHit.addKill(game);
		}
		reset();
	}

	private void reset(){
		cell.setBatteryLife(maxCharge);
		dropInventory(tile);
		updateLocations(startTile,startRoom);
		lastHit = null;
	}

	public void dropInventory(Tile2D tile){

		Random random = new Random();
		int x,y;
		while( !inventory.isEmpty() ){
			x = tile.getxPos() + random.nextInt(2) - 1;
			y = tile.getyPos() + random.nextInt(2) - 1;
			if( validTile(x,y) ){
				inventory.remove(0).moveItemTo(room.getTiles()[y][x]);
			}
		}
	}

	public boolean validTile( int x, int y){
		return x >= 0 && x < room.getTiles()[0].length &&
				y >= 0 && y < room.getTiles().length;
	}

	public void addKill(Game game){
		score++;
		//TODO GET NOTIFICATIONS WORKING WITHOUT GAME PARAMETER
		//TODO GET NOTIFICATIONS WORKING WITHOUT GAME PARAMETER
		//TODO GET NOTIFICATIONS WORKING WITHOUT GAME PARAMETER
		game.notifyMessage(playerName + " got a kill! Score is now: "+ score);
	}




	public Tile2D getFacingTile(){
		int change = facing.ordinal();
		Tile2D target = null;
		if(change == 0) target = tile.getTileUp();
		else if(change == 1) target = tile.getTileRight();
		else if(change == 2) target = tile.getTileDown();
		else if(change == 3) target = tile.getTileLeft();
		return target;
	}


	private Tile2D findTile(Move move){
		int change = facing.ordinal();
		if(change == 0) return moveUp(tile.getTileUp());
		else if(change == 1) return moveRight(tile.getTileRight());
		else if(change == 2) return moveDown(tile.getTileDown());
		else if(change == 3) return moveLeft(tile.getTileLeft());

		throw new RuntimeException ("Unknown change: " + change);
	}

	private void animation(){
		spriteIndex++;
		spriteIndex = spriteIndex % 4;
	}


	private Tile2D moveUp(Tile2D tileUp){
		tileYPos-=stepAmount;

		if(tileYPos<tileMinPos){
			if( tileUp instanceof NonWalkable ){
				tileYPos+=stepAmount;
				return null;
			}
			tileYPos = tileMaxPos;
			return tileUp;
		}
		else{
			return tile;

		}
	}

	private Tile2D moveDown(Tile2D tileDown){
		tileYPos+=stepAmount;

		if(tileYPos>tileHeight){
			if( tileDown instanceof NonWalkable ){
				tileYPos-=stepAmount;
				return null;
			}
			tileYPos = tileMinPos;
			return tileDown;
		}
		else{
			return tile;
		}
	}

	private Tile2D moveLeft(Tile2D tileLeft){
		tileXPos-=stepAmount;
		if(tileXPos<tileMinPos){

			if( tileLeft instanceof NonWalkable ){
				tileXPos+=stepAmount;
				return null;
			}
			tileXPos = tileMaxPos;
			return tileLeft;
		}
		else{
			return tile;
		}
	}

	private Tile2D moveRight(Tile2D tileRight){
		tileXPos+=stepAmount;
		if(tileXPos>tileWidth){

			if( tileRight instanceof NonWalkable ){
				tileXPos-=stepAmount;
				return null;
			}
			tileXPos = tileMinPos;
			return tileRight;
		}
		else{
			return tile;
		}
	}

	private void updateFacing(String dirKey){
		if(dirKey.toLowerCase().equals("w")) facing = Direction.NORTH;
		else if(dirKey.toLowerCase().equals("d")) facing = Direction.EAST;
		else if(dirKey.toLowerCase().equals("s")) facing = Direction.SOUTH;
		else if(dirKey.toLowerCase().equals("a")) facing = Direction.WEST;
	}

	private void centerAvatar(){
		tileXPos = tileXCenter;
		tileYPos = tileYCenter;
	}

	public void setLastHit(Avatar lastHit){
		this.lastHit = lastHit;
	}

	public void setPlayerName(String name) {
		this.playerName = name;
	}

	public String getPlayerName(){
		return playerName;
	}

	public Tile2D getTile() {
		return tile;
	}

	public Room getCurrentRoom() {
		return room;
	}

	public List<Item> getInventory() {
		return inventory;
	}

	public boolean addItemToInventory(Item item) {
		if( inventory.size() >= maxInventorySize ){
			return false;
		}
		SoundController.play(PICKUP);
		return inventory.add(item);
	}

	public boolean removeItemFromInventory(Item item) {
		if( inventory.remove(item) ){
			SoundController.play(DROP);
			return true;
		}
		return false;
	}

	public void setCurrentRoom(Room currentRoom) {
		this.room = currentRoom;
	}

	public Direction getFacingDirection(){
		return facing;
	}

	public void setFacingDirection(Direction f){
		facing = f;
	}

	public void setCurrentTile(Tile2D currentTile) {
		this.tile = currentTile;
	}

	public double getTileXPos(){
		return tileXPos;
	}

	public double getTileYPos(){
		return tileYPos;
	}

	public int getSpriteIndex(){
		return spriteIndex;
	}

	public Cell getCell() {
		return cell;
	}

	public void setCell(Cell cell) {
		this.cell = cell;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean getAI() {
		return isAI;
	}

	public void setAI(boolean isAI) {
		this.isAI = isAI;
	}

	public Tile2D getStartTile() {
		return startTile;
	}

	public void setStartTile(Tile2D startTile) {
		this.startTile = startTile;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((playerName == null) ? 0 : playerName.hashCode());
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
		Avatar other = (Avatar) obj;
		if (playerName == null) {
			if (other.playerName != null)
				return false;
		} else if (!playerName.equals(other.playerName))
			return false;
		return true;
	}

	public void setStepAmount(int i) {
		this.stepAmount = i;

	}

	public int getStepAmount(){
		return stepAmount;
	}

	@Override
	public float getR() {
		float fullRed = red;
		for(int i = 0; i < lights.size(); i++){
			fullRed += lights.get(i).getR();
		}
		return Math.min(255, fullRed);
	}

	@Override
	public float getG() {
		float fullGreen = green;
		for(int i = 0; i < lights.size(); i++){
			fullGreen += lights.get(i).getG();
		}
		return Math.min(255, fullGreen);
	}

	@Override
	public float getB() {
		float fullBlue = blue;
		for(int i = 0; i < lights.size(); i++){
			fullBlue += lights.get(i).getB();
		}
		return Math.min(255, fullBlue);
	}

	public double getBrightness() {
		float fullBrightness = brightness;
		for(int i = 0; i < lights.size(); i++){
			fullBrightness += lights.get(i).getBrightness();
		}
		return Math.min(255, fullBrightness);
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
	}
	public void addLight(Lightable light) {
		lights.remove(light);
		radius++;
	}

	public void removeLight(Lightable light) {
		lights.add(light);
		radius--;
	}

	@Override
	public int getRadius() {
		return radius;
	}
	@Override
	public int getxPos() {
		return getTile().getxPos();
	}
	@Override
	public int getyPos() {
		return getTile().getyPos();
	}
	@Override
	public boolean turnedOn() {
		return turnedOn;
	}

	public int getMaxInventorySize(){
		return maxInventorySize;
	}
	@Override
	public boolean join(Powerable power) {
		return false;
	}
	@Override
	public boolean disconnect(Powerable power) {
		return false;
	}
	@Override
	public boolean hasItems() {
		return inventory.isEmpty();
	}
	@Override
	public boolean isFull() {
		return inventory.size() == maxInventorySize();
	}

	@Override
	public int maxInventorySize() {
		return maxInventorySize;
	}
}
