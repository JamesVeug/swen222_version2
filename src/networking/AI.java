package networking;

import java.net.InetAddress;
import java.net.UnknownHostException;

import gameLogic.Room;

/**
 * Super class of Ai what enables all AI to contain a room and players
 * @author veugeljame
 *
 */
public abstract class AI implements Thinker{

	protected Room room;
	protected Player player;

	public AI(Room room, String name){
		this.room = room;

		String IPAddress = "Unknown";
		try { IPAddress = InetAddress.getLocalHost().getHostAddress(); } catch (UnknownHostException e) {}
		this.player = new Player(IPAddress, name);
	}

	/**
	 *Gets the room that the AI is currently in
	 * @return Room object
	 */
	public Room getRoom(){
		return room;
	}

	/**
	 * Gets the Player that the AI is controlling
	 * @return Player object
	 */
	public Player getPlayer(){
		return player;
	}


	/**
	 *Gets the local IP of the AI
	 * @return String of the AI's IP
	 */
	public String getIPAddress(){
		return player.getIPAddress();
	}
}
