package networking;

import gameLogic.Game;
import gameLogic.GameMessageListener;
import gameLogic.Room;
import gameLogic.Score;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *Game server class of which handles all the processing of the game on a server side level.
 *If a change is made to any clients information, then it is changed on all clients levels.
 * @author veugeljame
 *
 */
public class GameServer extends ChatServer implements GameMessageListener {

	// Game that all players are playing off
	private Game gameServer;
	private ArrayList<ChatMessage> gameMessageHistory = new ArrayList<ChatMessage>();

	/**
	 * Creates a new Game Server that will load a map from the file given.
	 * Waits for input from the console to run commands.
	 * @param gameToLoad MapFile to load
	 */
	public GameServer() {
		super();

		// Create a new game
		gameServer = new Game();
		gameServer.addMessageListener(this);

		// Server listens for input directly to servers terminal Thread
		Thread serverTextBox = new Thread(new ServerTextListener());
		serverTextBox.start();

		Thread tickThread = new Thread(new TickThread());
		tickThread.start();
		System.out.println("GameServer - constructor end reached");
	}

	/**
	 * Updates all clients with a new room according to the state of the game logic
	 */
	private synchronized void updateAllClients(){

			// Make sure we have a server to update the clients with
			if( gameServer == null ){
				return;
			}

			Score currentScore = gameServer.getScore();
			NotificationHistory nH = new NotificationHistory(gameMessageHistory,true);

			// Sent the room of the client to all it's clients
			for (int i = 0; i < clients.size(); i++) {

				// Get each of our clients, and the room they are in
				ClientThread client = clients.get(i);
				Room room = gameServer.getRoom(client.getPlayerName());

				// Send the new room to the player
				client.sendData(new ClientUpdate(room, currentScore, nH));
			}
	}

	/**
	 * Creates a new RandomAI and adds it to the game
	 * @return True if the AI was added successfully
	 */
	private boolean createAI(){
		int activeAI = gameServer.getActiveAI().size();

		// Max of 10 bots
		if(activeAI >= 10){
			return false;
		}

		// Create a new ai Player
		String botName = ("ai" + activeAI).trim();
		Room room = gameServer.addPlayer(botName);

		// Get if the AI was given a room
		if( room == null ){
			return false;
		}

		// Create a random AI
		AI ai = new ChaseAI(room, botName);

		// Attempt to add it to the gamelogic
		if( !gameServer.addAI(ai) ){
			return false;
		}

		// Tell everyone a bot has joined the game
		messageAllClients(botName + " has Joined the Game.");

		// Tell console
		System.out.println(botName + " has Joined the Game.");

		return true;
	}

	@Override
	public synchronized void retrieveObject(NetworkObject data) {
		if( data.getData() instanceof ChatMessage ){
			ChatMessage cm = (ChatMessage)data.getData();

			// Check for a command ONLY used by the GameServer
			if( cm.message.startsWith("/addbot") ){

				// Only admins can close the server
				if( isAdmin(data.getIPAddress()) ){

					if( !createAI() ){
						System.out.println("Didn't add AI!");
					}

					// Confirmation
					getClientFromIP(data.getIPAddress()).sendData(data);
					return;
				}
			}
		}

		// Tell chatMessage
		super.retrieveObject(data);

		// Determine what to do with each of the different types of objects sent from clients
		if( data.getData() instanceof Move ){
			Move move = (Move)data.getData();

			// A move performed by a client
			processMove(move, data);
		}

	}

	/**
	 *
	 * @param move Move wanting to be performed by a client
	 * @param data NetworkObject sent through the network
	 */
	private synchronized void processMove(Move move, NetworkObject data){

		// Move the players Avatar
		gameServer.moveAvatar(move);
	}

	/**
	 * A class that listens for input from the console and sends the input to
	 * processServerMessage
	 *
	 * @author veugeljame
	 *
	 */
	private class ServerTextListener implements Runnable {
		@Override
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String text;

			while (true) {
				try {
					text = (String) br.readLine();

					if (text != null) {

						// Process to everyone
						retrieveObject(new NetworkObject(getIPAddress(), new ChatMessage("~Admin", text, chatMessageColor, true)));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean newClientConnection(ClientThread cl) {
		if( !super.newClientConnection(cl) ){
			return false;
		}


		// Set new players current room
		Room currentRoom = gameServer.addPlayer(cl.getPlayerName());

		// Send the room back to the client
		if( currentRoom != null ){
			return cl.sendData(new ClientUpdate(currentRoom, gameServer.getScore(), new NotificationHistory(gameMessageHistory,true)));
		}

		// Couldn't send new room
		return false;
	}

	@Override
	public boolean clientRejoins(ClientThread cl) {
		if( !super.clientRejoins(cl) ){
			return false;
		}

		// Try and send a room to the client
		Room currentRoom = gameServer.getRoom(cl.getPlayerName());
		if( currentRoom != null ){
			// Send new room to client
			return cl.sendData(new ClientUpdate(currentRoom, gameServer.getScore(), new NotificationHistory(gameMessageHistory,true)));
		}

		// Couldn't join the client
		return false;
	}

	/**
	 * Removes the client at the given location from our list of clients
	 *
	 * @param client
	 *            index to remove a client from
	 * @param reconnecting
	 */
	@Override
	public synchronized boolean removeClient(ClientThread client, boolean reconnecting) {
		if( !super.removeClient(client, reconnecting) ){
			System.out.println("Couldn't remove from Server");
			return false;
		}

		if ( !reconnecting ) {
			if( !gameServer.removePlayerFromGame(client.getPlayerName()) ){
				System.out.println("Couldn't remove from GameLogic!");
				return false;
			}
		}
		return true;
	}

	public static void main(String[] args) {
		new GameServer();
	}

	private class TickThread implements Runnable{

		@Override
		public void run(){
			try {
				while(true){
					gameServer.tickGame();
					updateAllClients();

					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void notifyMessage(String message) {
		ChatMessage cm = new ChatMessage(message,Color.yellow,true);
		//gameMessageHistory.add(cm);
		//chatHistory.add(cm);

	};
}
