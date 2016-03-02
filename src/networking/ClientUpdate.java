package networking;

import gameLogic.Room;
import gameLogic.Score;

/**
 * Object sent through the network that contains the current up-to-date information
 * Sent via the server to the clients
 * @author veugeljame
 *
 */
public class ClientUpdate extends NetworkData {
	private static final long serialVersionUID = 8175985451592995007L;

	// New room to be sent to clients
	public final Room updatedRoom;

	// Game Score according to the server
	public final Score score;
	public final NotificationHistory messageHistory;

	public ClientUpdate(Room room, Score score, NotificationHistory notificationHistory){
		this.updatedRoom = room;
		this.score = score;
		this.messageHistory = notificationHistory;
	}

}
