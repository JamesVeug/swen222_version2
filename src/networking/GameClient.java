package networking;

import gameLogic.Avatar;
import gameLogic.Room;
import gameLogic.Score;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JComponent;

import networking.ChatClient.ServerCheckerThread;

/**
 *Chat Client that deals with the main aspects of the Chat program when it comes to the client
 * @author veugeljame
 *
 */
public class GameClient extends ChatClient {

	// Client side of the game
	private Room clientRoom = null;

	// Clients version of the score
	private Score clientScore = null;

	// Game messages
	private List<ChatMessage> notificationHistory = null;

	/**
	 * Constructor for a GameClient to specify what it's characteristics are. And how it will appear when being drawn
	 * @param playerName Name of the player
	 * @param clientImage Where to draw the Room given
	 */
	public GameClient(String playerName) {
		super(playerName);
	}

	/**
	 * Constructor that allows a client to be created then will connect it directly to a server
	 * @param playerName What to call the player
	 * @param clientImage Where to draw the game on
	 * @param serverIP IP Of the server to join
	 * @param serverPort Port of the server to join
	 */
	public GameClient(String playerName, String serverIP) throws UnknownHostException, IOException{
		this(playerName);
		connect(serverIP);
	}

	/**
	 * Returns the client side version of the room that the player is currently in
	 * @return Room containing the player and al lthe rooms contents
	 */
	public synchronized Room getRoom(){
		return clientRoom;
	}

	/**
	 * Gets the clients current up-to-date version of the score
	 * @return Score object
	 */
	public synchronized Score getScore(){
		return clientScore;
	}

	/**
	 * Returns the Avatar accociated with the current player
	 * @return
	 */
	public Avatar getAvatar(){

		Room room = clientRoom;
		if( room != null ){
			Avatar avatar = clientRoom.getAvatar(getName());
			return avatar;
		}

		// Get avatar according to our name
		return null;
	}

	/**
	 * Sends the given object to the server that the client is connected to
	 * @param data Object to sent to the server for processing
	 */
	public boolean sendMoveToServer(Move interaction) throws IOException{

		/*Avatar avatar = getAvatar();
		if( !avatar.moveTo(interaction) ){
			return false;
		}*/

		// Send data to the server
		return super.sendData(interaction);
	}

	@Override
	public synchronized void retrieveObject(NetworkObject data) {
		super.retrieveObject(data);

		// Check what type of data we were sent
		if( data.getData() instanceof ClientUpdate ){

			// We were sent a move
			retrievedUpdate((ClientUpdate)data.getData());
		}
	}

	@Override
	public boolean successfullyConnected(String playerName) {
		if( !super.successfullyConnected(playerName) ){
			return false;
		}

		// Reset to default
		clientRoom = null;
		clientScore = null;

		// Correct Connection
		return true;
	}

	/**
	 * Process the supplied chat message sent from the server to this client.
	 * @param sendersIPAddress IP of the senders IP Address
	 * @param chatMessage Message that was sent from the given IP
	 */
	public synchronized void retrievedUpdate(ClientUpdate update){

		//Save the newly updated room
		clientRoom = update.updatedRoom;
		clientScore = update.score;
		notificationHistory = update.messageHistory.history;
	}

	public List<ChatMessage> getNotifications() {
		return notificationHistory;
	}
}
