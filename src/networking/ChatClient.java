package networking;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.JComponent;

import networking.Client.InputWaiter;
import networking.Server.ClientThread;

public class ChatClient extends Client {

	// Clients sides version of the current chat history
	private ArrayList<ChatMessage> chatHistory = new ArrayList<ChatMessage>();

	// Color of the clients messages
	private Color chatMessageColor = new Color(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255));

	// Thread to check if the server is functional
	private ServerCheckerThread serverChecker = null;

	// When pinging the server, we want to coutn the amoutn of times we have pinged it
	private int failedPings = 0;

	/**
	 * Creates a new GameClient to connect to a server
	 * @param playerName Name of the client
	 */
	public ChatClient(String playerName){
		player = new Player(playerName);
	}

	/**
	 * Starts a thread that will constantly ping the server to see if the socket is still valid
	 */
	public class ServerCheckerThread extends Thread{

			public static final int maxFailPings = 5;
			public static final int pingTime = 3000;
			private boolean running = true;

			@Override
			public void run(){
				while( running ){

					// Sleep 5s
					try { Thread.sleep(pingTime);} catch (InterruptedException e) {}

						ChatMessage ping = new ChatMessage(player.getName(),"/ping everyone",chatMessageColor,true);
						boolean pinged = sendData(ping);

						// Couldn't ping them
						if( !pinged ){
							System.out.println("Could not ping server!");
							failedPings++;

							// Should we disconnect?
							if( isConnected() || failedPings >= maxFailPings ){

								// Can't connect to the server
								disconnect();
								break;
							}
						}
						else{
							// Pinged correctly. Reset their fail count
							failedPings = 0;
						}
					}
			}

			/**
			 * Stops the thread from thinking
			 */
			public void stopRunning(){
				running = false;
			}
	}

	@Override
	public boolean disconnect(){
		if( !super.disconnect() ) return false;

		// Check if we still have a thread checking for a failed server
		// Stop the thread
		if( serverChecker != null ){
			serverChecker.stopRunning();

			try {
				serverChecker.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public synchronized void retrieveObject(NetworkObject data) {

		if( data.getData() instanceof ChatHistory ){
			chatHistory.addAll(((ChatHistory)data.getData()).history);
			return;
		}
		else if( data.getData() instanceof ChatMessage ){
			ChatMessage chatMessage = (ChatMessage) data.getData();

			// Check for commands
			Scanner scan = new Scanner(chatMessage.message);

			// Ping command is received
			if( scan.hasNext("/ping") ){ scan.next();
				if( scan.hasNext(getName()) ){

					// Someone Pinged me
					long delay = (System.currentTimeMillis() - data.getTimeInMillis());
					sendData(new ChatMessage(chatMessage.sendersName + " pinged " + getName() + " at " + delay + "ms",chatMessage.color,true));
				}
				else if( scan.hasNext("everyone") ){
					return;
				}
			}
			else if( scan.hasNext("/name") ){

				if( data.getIPAddress().equals(getIPAddress()) ){

					scan.next(); // /name
					if( scan.hasNext() ){

						// Trim the new name, removing white spaces
						//String newName = scan.nextLine().trim();
						//player.setName(newName);
					}
				}
			}

			// Check if we have just gotten an acknowledgement
			if( chatHistory.contains(chatMessage) ){

				// Acknowledge the message
				chatHistory.get(chatHistory.indexOf(chatMessage)).acknowledged = true;
			}
			else{

				// Save the message
				chatHistory.add(chatMessage);
			}
		}
	}

	/**
	 * Changes the name of the client to be displayed through-out the game
	 * @param name
	 */
	public void setName(String name){
		name.trim(); // Remove spaces

		// Only change name if we are not connectged, otherwise we need permission from the server
		if( !isConnected() ){

			// Set name directly
			player.setName(name);
		}
	}

	/**
	 * Sends the given object to the server that the client is connected to
	 * @param data Object to sent to the server for processing
	 */
	public boolean sendChatMessageToServer(String message) throws IOException{

		// Create a new chatMessage
		ChatMessage chat = new ChatMessage(player.getName(), message, getChatMessageColor());
		return sendChatMessageToServer(chat);
	}

	/**
	 * Sends the given object to the server that the client is connected to
	 * @param data Object to sent to the server for processing
	 */
	public boolean sendChatMessageToServer(ChatMessage chat) throws IOException{

		NetworkObject data = new NetworkObject(getIPAddress(), chat);

		// Client side commands
		if( !new CommandParser().parseCommand(new Scanner(chat.message), data) ){
			return true;
		}

		// Send data to the server
		return sendData(chat);
	}



	/**
	 * Returns a new arrayList containing all the chatMessages up to "size" back
	 * Size indicates how many of the most recent messages to claim
	 * @param size how many messages we should get from our history from the furthest back to the last message
	 */
	public ArrayList<ChatMessage> getChatHistory(int size) {

		size = Math.min(size, chatHistory.size());

		ArrayList<ChatMessage> history = new ArrayList<ChatMessage>();
		for (int i = chatHistory.size()-size; i < chatHistory.size(); i++) {
			history.add(chatHistory.get(i));
		}

		// Send a new ArrayList of the chat messages to the client
		return history;
	}

	/**
	 * Returns a new arrayList containing all the chatMessages up to "size" back
	 * @param size how many messages we should get from our history from the furthest back to the last message
	 */
	public ArrayList<ChatMessage> getChatHistory() {
		// Send a get ArrayList of the chat messages to the client
		return getChatHistory(chatHistory.size());
	}

	/**
	 * Clears all messages from the chat history
	 */
	protected void clearChatHistory() {
		chatHistory.clear();
	}

	/**
	 * Gets the chat history that has been sent to this client
	 * @return String containing chat history
	 */
	public synchronized void appendWarningMessage(String warning){

		// Save the message
		chatHistory.add(new ChatMessage("WARNING",warning, Color.red, true));
	}

	/**
	 * @return the chatMessageColor
	 */
	public Color getChatMessageColor() {
		return chatMessageColor;
	}

	/**
	 * @param chatMessageColor the chatMessageColor to set
	 */
	public void setChatMessageColor(Color chatMessageColor) {
		this.chatMessageColor = chatMessageColor;
	}

	@Override
	public boolean successfullyConnected(String playerName) {
		if( !super.successfullyConnected(playerName) ){
			return false;
		}

		// Reset to default
		clearChatHistory();
		failedPings = 0;

		// Change the name of the player
		player.setName(playerName);

		// Start checking the server
		serverChecker = new ServerCheckerThread();
		serverChecker.start();

		return true;
	}

	/**
	 * Parser for all the commands that the server processes from ChatMessages sent from clients
	 * @author veugeljame
	 *
	 */
	private class CommandParser {

		/**
		 * Process the command in the scanner and see if it contains a command that the server can work with.
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		public boolean parseCommand(Scanner scan, NetworkObject data) {

			if (!scan.hasNext()) {
				return true;
			}

			String command = scan.next();

			// Set something
			if (command.equals("/name")) {
				return parseName(scan, data);
			} else if (command.equals("/clear")){
				return parseClear(scan, data);
			} else if (command.equals("/reconnect")){
				return parseReconnect(scan, data);
			} else if (command.equals("/disconnect")){
				return parseDisconnect(scan, data);
			} else if (command.equals("/ping")) {
				return parsePing(scan, data);
			} else if (command.equals("/ip")) {
				return parseIP(scan, data);
			}else if (command.equals("/chatcolor")){
				return parseChatColor(scan,data);
			}
			return true;
		}

		/**
		 * Reconnects the client to the last server successfully joined
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseReconnect(Scanner scan, NetworkObject data) {
			((ChatMessage)data.getData()).acknowledged = true; // Chat worked
			chatHistory.add((ChatMessage)data.getData());	// Record message

			// Attempt to reconnect
			if(	reconnect(getName()) ){
				appendWarningMessage("Connected successfully to " + connectedIP + " : " + connectedPort);
				return true;
			}
			return true;
		}

		/**
		 * Disconnects the client from the server
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseDisconnect(Scanner scan, NetworkObject data) {
			((ChatMessage)data.getData()).acknowledged = true; // Chat worked
			chatHistory.add((ChatMessage)data.getData());	// Record message
			disconnect(); // Disconnect from server
			return true;
		}

		/**
		 * Clears the chathistory off the clients
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseClear(Scanner scan, NetworkObject data) {
			chatHistory.clear();
			return true;
		}

		/**
		 * Changes the name of the client that sent the data
		 * @param scan Scanner attached to a string/chat message with the next name to be used with the client
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseName(Scanner scan, NetworkObject data) {

			// Check if we REALLY are assigning our name
			if( scan.hasNext() && data.getIPAddress().equals(getIPAddress()) ){
				setName( scan.next() );
			}
			return true;
		}

		/**
		 * Changes the color of the clients chat messages
		 * @param scan Scanner attached to a string/chat message with R G B as 3 integer extensions
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseChatColor(Scanner scan, NetworkObject data){

			if( scan.hasNextInt() ){
				int red,green,blue;

				if( !scan.hasNextInt() ){
					appendWarningMessage("Missing Red EG: /chatColor 0 64 128");
					return false;
				}

				red = scan.nextInt();

				if( !scan.hasNextInt() ){
					appendWarningMessage("Missing Green EG: /chatColor 0 64 128");
					return false;
				}

				green = scan.nextInt();

				if( !scan.hasNextInt() ){
					appendWarningMessage("Missing Blue EG: /chatColor 0 64 128");
					return false;
				}

				blue = scan.nextInt();

				chatMessageColor = new Color(red,green,blue);
				return true;
			}
			return true;
		}

		/**
		 * Client is pinging, so find out what we should ping
		 * @param scan Scanner attached to a string/chat message possible extension of everyone or another clients name/IP
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parsePing(Scanner scan, NetworkObject data) {
			if( scan.hasNext("everyone") ){
				return false;
			}
			return true;
		}

		/**
		 * Displays the clients Ping on their screen
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseIP(Scanner scan, NetworkObject data) {
			chatHistory.add(new ChatMessage("~Admin", "Your IP: " + getIPAddress(), chatMessageColor, true));	// Record message
			return false;
		}


	}
}
