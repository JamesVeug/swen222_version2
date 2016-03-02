package gameLogic;

import gameLogic.Items.Item;
import gameLogic.Tiles.Charger;
import gameLogic.Tiles.Floor;
import gameLogic.Tiles.Tile2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import networking.*;

/**
 *
 * @author griffiryan
 * The Game class is used to initialize and store game state information. It utilizes an auxillary class - NewGame to setup game variables, if game loading/saving is not
 * present. The Game class is responsible for maintaining information about players currently playing, and being added to the game. It Stores all game state information -
 * all of the current players and their associated Avatar objects, Rooms in the game, and current active AI units. Typically, a usable game class will have commands sent
 * to it from a server, which will instruct it to move an avatar(moveAvatar(Move move) is called), add a new player to the game (addPlayer() is called), instruct an Avatar
 * to interact with an Item (avatarInteractWithItem()). Any game actions that effect the state of the game need to be communicated to the Game class.
 */
public class Game{

	private List<GameMessageListener> messageListeners;
	private List<Room> roomsInGame;
	private List<Avatar> activeAvatars;
	private List<Avatar> activeAIAvatars;
	private List<AI> activeAI;
	private List<Thinker> thinkers;


	private Score score;

	// Enviroment thread - generate game items
	private Thread environment;

	public Game(){
		this.roomsInGame = new ArrayList<Room>();
		this.messageListeners = new ArrayList<GameMessageListener>();
		this.activeAvatars = new ArrayList<Avatar>();
		this.activeAIAvatars = new ArrayList<Avatar>();
		this.activeAI= new ArrayList<AI>();
		this.thinkers = new ArrayList<Thinker>();
		createNewGame();
		this.score = new Score();

		//TODO FIX ENVIRONMENT
		//TODO FIX ENVIRONMENT
		this.environment = new Environment(this);
		//environment.start();
	}

	public void addRoom(Room room){
		roomsInGame.add(room);
	}

	private void createNewGame(){
		new NewGame(this);
	}

	/**
	 * Searches all ActiveAvatars in the game and returns that avatar if their playername matches the given playername string
	 * @param playerName
	 * @return Avatar that equals the given string
	 */
	public Avatar getAvatar(String playerName){
		for(Avatar avatar: activeAvatars){
			if(playerName.equals(avatar.getPlayerName())){
				return avatar;
			}
		}
		return null;
	}

	/**
	 * Searches all ActiveAvatars in the game and returns that avatar if their playername matches the given playername string
	 * @param playerName
	 * @return Avatar that equals the given string
	 */
	public Avatar getAIAvatar(String playerName){
		for(Avatar avatar: activeAIAvatars){
			if(playerName.equals(avatar.getPlayerName())){
				return avatar;
			}
		}
		return null;
	}

	/**
	 * Server calls addPlayer(playerName) with a name for the new Avatar, this method sets up the avatar and adds it to the game.
	 * @param playerName
	 * @return The room the created avatar starts in (Their home / spawn room)
	 */
	public Room addPlayer(String playerName){
		if(playerName.startsWith("ai")){
			Room room = roomsInGame.get(6);

			int tileX = 1;
			int tileY = 1;
			Tile2D tile = room.getTiles()[tileY][tileX];

			while (tile.getAvatar() != null && tile instanceof Floor ){
				tile = room.getTiles()[tileY+1][tileX+1];
			}
			Avatar avatar = new Avatar(playerName,tile,room,this);
			activeAIAvatars.add(avatar);
			return room;
		}
		else{
			// Player
			Room room = roomsInGame.get(1);
			Tile2D tile = room.getTiles()[2][2];
			Avatar avatar = new Avatar(playerName,tile,room, this);
			activeAvatars.add(avatar);
			thinkers.add(avatar);

			return room;
		}
	}


	/**
	 * Finds the Avatar associated with the given move object, and commands them to perform the move associated with the given move object.
	 * @param move
	 * @return true iff the move is succesful.
	 */
	public boolean moveAvatar(Move move){
		Avatar mover = null;

		for(Avatar avatar : activeAvatars){
			if(avatar.getPlayerName().equals(move.getPlayer().getName())){
				mover = avatar;
			}

		}
		if(mover==null) return false;
		return mover.moveTo(move, this);
	}


	public boolean avatarInteractWithItem(String playerName, Item item){
		for(Avatar avatar : activeAvatars){
			if(avatar.getPlayerName().equals(playerName)){
				return avatar.interact(item);
			}
		}
		return false;
	}

	/**
	 * Return the room in the game that a player with the given playername string has.
	 * @param playerName - player identified with their name string
	 * @return the Room the given player is in
	 */

	public Room getRoom(String playerName){
		for(Room room : roomsInGame){
			for(Avatar player : room.getAvatars()){
				if(player.getPlayerName().equals(playerName)){
					return room;
				}
			}
		}
		return null;
	}

	public boolean setPlayerName(String fromName, String toName){
		for(Avatar avatar : activeAvatars){
			if(avatar.getPlayerName().equals(fromName)){
				avatar.setPlayerName(toName);
				return true;
			}
		}
		return false;
	}

	/**
	 * Servercalls removePlayerFromGame(playerName) if they disconnect. This method finds the avatar in the game with a matching playername, returns all of the items
	 * in their inventory to their starting positions and removes all references to the avatar.
	 * @param playerName
	 * @return true iff the Avatar is successfully removed from the game, and all referenced information is reset.
	 */
	public boolean removePlayerFromGame(String playerName){
		Avatar leaving = null;
		for(Avatar avatar : activeAvatars){
			if(avatar.getPlayerName().equals(playerName)){
				leaving = avatar;
			}
		}
		if(leaving == null){
			System.out.println("No player in game with name: "+playerName);
			return false;
		}
		leaving.dropInventory(leaving.getTile());
		leaving.getCurrentRoom().removeAvatar(leaving);
		leaving.getTile().removeAvatar(leaving);
		score.getScore().remove(leaving.getPlayerName());
		return activeAvatars.remove(leaving);
	}

	public boolean addAI(AI ai){
		activeAI.add(ai);
		return thinkers.add(ai);
	}
	public boolean removeAI(AI ai){
		if( activeAI.remove(ai) )
			return thinkers.remove(ai);
		return false;
	}

	public boolean addThinker(Thinker think){
		return thinkers.add(think);
	}
	public boolean removeThinker(Thinker think){
		return thinkers.remove(think);
	}

	/**
	 * Server thread calls this method to systematically instruct AI to think / generate a movement.
	 * @return an integer - the number of AI in game that think() was called on.
	 */
	public void tickGame(){
		for(int i = 0; i < thinkers.size(); i++){
			Thinker think = thinkers.get(i);
			think.think(this);
		}
	}

	public Room getRoomByName(String roomName){
		for(Room r: roomsInGame){
			if(r.getRoomPlace().equals(roomName)){
				return r;
			}
		}
		return null;
	}

	public List<Room> getRoomsInGame() {
		return roomsInGame;
	}
	public List<Avatar> getActiveAvatars() {
		return activeAvatars;
	}

	public Score getScore(){
		score.updateMap(activeAvatars);
		return score;
	}

	public List<AI> getActiveAI() {
		return activeAI;
	}

	public void notifyMessage(String string) {
		for(GameMessageListener l : messageListeners){
			l.notifyMessage(string);
		}
	}

	public void addMessageListener(GameMessageListener listener) {
		messageListeners.add(listener);
	}
}
