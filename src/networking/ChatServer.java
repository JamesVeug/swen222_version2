package networking;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

import networking.Server.ClientThread;

public class ChatServer extends Server {

	// Color of the chat messages sent from the server
	protected static final Color chatMessageColor = Color.red;

	// Total chat history from all clients and the server
	protected Stack<ChatMessage> chatHistory = new Stack<ChatMessage>();

	// Pings from Name to how many times we haven't been able to ping them
	private HashMap<String, Integer> failedPings = new HashMap<String, Integer>();

	// All commands for every client to use
	private static final ArrayList<ChatMessage> clientCommandList = new ArrayList<ChatMessage>(){{
		add(new ChatMessage("~Admin","List of Possible Commands:", chatMessageColor,true));
		add(new ChatMessage("","/ip -> Displays your ping on the screen", chatMessageColor,true));
		add(new ChatMessage("","/ping -> Checks how fast your connection currently is", chatMessageColor,true));
		add(new ChatMessage("","/get history 'number' -> Sends back the chat history up to 'number' of most recent messages", chatMessageColor,true));
		add(new ChatMessage("","/admins -> lists the IP's of the admins", chatMessageColor,true));
		add(new ChatMessage("","/players -> lists all the players in the game", chatMessageColor,true));
		add(new ChatMessage("","/chatcolor r g b -> changes the color of your chat messages", chatMessageColor,true));
		add(new ChatMessage("","/clear -> clears all messages off the screen", chatMessageColor,true));
		add(new ChatMessage("","/disconnect -> disconnects from the server", chatMessageColor,true));
		add(new ChatMessage("","/reconnect -> reconnects to the previous server was was successfully connected", chatMessageColor,true));
		add(new ChatMessage("","/kill -> Kills your avatar", chatMessageColor,true));
		add(new ChatMessage("","/hurt 'integer' -> Deals x amount of damage to your avatar", chatMessageColor,true));

		// Removed due to being too slow
		//commandList.add(new ChatMessage("","/name 'string' -> changes your name", chatMessageColor,true));
	}};

	// Commands for admins
	private static final ArrayList<ChatMessage> adminCommandList = new ArrayList<ChatMessage>(){{
		add(new ChatMessage("","- ADMIN COMMANDS -", chatMessageColor,true));
		add(new ChatMessage("","/close -> closes the server", chatMessageColor,true));
		add(new ChatMessage("","/addbot -> adds a random bot to the server", chatMessageColor,true));
		add(new ChatMessage("","/kick 'name' -> kicks a player from the server", chatMessageColor,true));
	}};

	public ChatServer(){
		Thread clientChecker = new Thread(){

			final int maxFailPings = 5;
			final int pingTime = 5000;

			@Override
			public void run(){
				while( true ){

					// Sleep 5s
					try { Thread.sleep(pingTime);} catch (InterruptedException e) {}

					// Ping all
					for (int i = 0; i < clients.size(); i++) {

						ClientThread client = clients.get(i);
						NetworkObject ping = new NetworkObject(getIPAddress(), new ChatMessage("~Admin","/ping everyone",chatMessageColor,true));
						int failCount = failedPings.containsKey(client.getPlayerName()) ? failedPings.get(client.getPlayerName()) : 0;
						boolean pinged = pingClient(client.getPlayerName(), ping);

						// Couldn't ping them
						if( !pinged ){

							// Should we remove them?
							if( failCount >= maxFailPings ){
								if( removeClient(client, false) ){
									i--;
								}
							}
							else{
								// Update their counter
								failedPings.put(client.getPlayerName(),failCount+1);
							}
						}
						else{
							// Pinged correctly. Reset their fail count
							if( failCount > 0 ){
								failedPings.put(client.getPlayerName(),0);
							}
						}
					}
				}
			}
		};
		clientChecker.start();
	}

	@Override
	public void retrieveObject(NetworkObject data) {

		// Can we process a chat message?
		if( data.getData() instanceof ChatMessage ){
			ChatMessage cm = (ChatMessage)data.getData();

			// Process a command if we wrote one and display the message
			if( !processCommand(cm.message, data) ){

				// Send the data back to the client
				ClientThread sender = getClientFromIP(data.getIPAddress());
				if( sender != null ){
					sender.sendData(data);
				}

				// Don't do anything else
				return;
			}

			// Save the message
			chatHistory.add(cm);

			// Send it to all our clients
			sendToAllClients(data);
		}

	}

	/**
	 * Sends the chat history to the client that send the network object
	 * Size indicates how many of the most recent messages to claim
	 * @param clientIP Who we should send the history to
	 * @param size how many messages we should get from our history from the furthest back to the last message
	 */
	private synchronized boolean sendHistoryToClient(String clientIP, int size) {

		// Make sure we don't get a size greater than the list
		size = Math.min(size, chatHistory.size());

		ArrayList<ChatMessage> history = new ArrayList<ChatMessage>();
		for (int i = chatHistory.size()-size; i < chatHistory.size(); i++) {
			history.add(chatHistory.get(i));
		}

		// Send a new ArrayList of the chat messages to the client
		return sendToClient(clientIP, new ChatHistory(history,true));
	}

	/**
	 * Processes the message in the string provided and returns a boolean if it
	 * were processed or not
	 *
	 * @param command
	 *            Command to process
	 * @param data
	 *            packet that was sent to the server
	 * @return true or false if the command was processed
	 */
	private synchronized boolean processCommand(String command,NetworkObject data) {

		Scanner scan = new Scanner(command);
		return new CommandParser().parseCommand(scan, data);
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

				return false;//parseName(scan, data);
			} else if (command.equals("/get")) {
				return parseGet(scan, data);
			} else if (command.equals("/ping")) {
				return parsePing(scan, data);
			} else if (command.equals("/close")){
				return parseClose(scan,data);
			} else if (command.equals("/admins")){
				return parseAdmins(scan,data);
			} else if (command.equals("/players")){
				return parsePlayers(scan,data);
			} else if( command.equals("/help")){
				return parseHelp(scan,data);
			} else if( command.equals("/kick")){
				return parseKick(scan,data);
			} 
			

			// Unknown command
			return true;
		}

		/**
		 * ADMIN COMMAND
		 * Close the server and disconnect all clients
		 * @param scan Scanner with no more unsused text
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseClose(Scanner scan, NetworkObject data) {

			// Only admins can close the server
			if( !isAdmin(data.getIPAddress()) ){
				return true;
			}

			stopServer();
			return false;
		}

		/**
		 * ADMIN COMMAND
		 * Kicks a player fro mthe server
		 * @param scan Scanner with the following being a players name
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseKick(Scanner scan, NetworkObject data) {

			// Only admins can close the server
			if( !isAdmin(data.getIPAddress()) ){
				return true;
			}

			// Get name
			if( !scan.hasNext() ){
				return true;
			}

			String name = scan.next();
			ClientThread client = getClientFromName(name);
			boolean reconnecting = false;

			if( scan.hasNext() ){
				reconnecting = scan.nextBoolean();
			}

			// Remove the client from the server
			removeClient(client,reconnecting);
			return false;
		}

		/**
		 * Changes the name of the client that sent the data
		 * @param scan Scanner attached to a string/chat message with the next name to be used with the client
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseName(Scanner scan, NetworkObject data) {

			// Check for name
			if (!scan.hasNext()) {
				return true;
			}

			// Get next command that SHOULD be a name
			ClientThread client = getClientFromIP(data.getIPAddress());

			// Check for failed client check
			if (client == null) {
				return true;
			}

			// Check for next name
			if (!scan.hasNext()) {
				return true;
			}

			String givenName = scan.nextLine().trim();

			// Check if the given name is
			if( getClientFromName(givenName) != null ){
				client.sendData(new ChatMessage("~Admin","You can not change your name to '" + givenName + "'",chatMessageColor));
				return false;
			}
			else{

				// set "name" name worked
				retrieveObject(new NetworkObject(getIPAddress(), new ChatMessage(client.getPlayerName()
						+ " has changed their name to " + givenName,chatMessageColor, true)));

				client.setPlayerName(givenName);
			}

			return true;
		}

		/**
		 * Get to retrieve some data from the server
		 * @param scan Scanner attached to a string/chat message with a command on what to get
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseGet(Scanner scan, NetworkObject data) {

			// Check for name
			if (!scan.hasNext()) {
				return true;
			}

			// What do we want to get?
			String token = scan.next();

			// Check for failed client check
			if (token.equals("history")) {

				return parseHistory(scan,data);
			}

			return true;
		}

		/**
		 * Sends the chat history to the client that sent the data
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseHistory(Scanner scan, NetworkObject data){
			int size = chatHistory.size();

			// size of history if we were supplied one
			if( scan.hasNextInt() ){
				size = scan.nextInt();
			}

			// Never go out of bounds
			size = Math.min(chatHistory.size(),size);


			// Send history back to the client
			sendHistoryToClient(data.getIPAddress(), size);

			return false;
		}

		/**
		 * Client is pinging, so find out what we should ping
		 * @param scan Scanner attached to a string/chat message possible extension of everyone or another clients name/IP
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parsePing(Scanner scan, NetworkObject data) {

			// Send history back to the client
			long delay = ping(scan, data);

			// Display the ping if it's a valid ping
			if( delay != -1 ){
				System.out.println(((ChatMessage)data.getData()).sendersName + " pinged the server at " + delay + "ms");
				return true;
			}

			return false;
		}

		/**
		 * Sends a list of the IP's that are admins on the server
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parseAdmins(Scanner scan, NetworkObject data) {

			ArrayList<ChatMessage> adminList = new ArrayList<ChatMessage>();
			adminList.add(new ChatMessage("~Admin","List of active Admins", chatMessageColor, true));

			for( int i = 0; i < clients.size(); i++ ){
				ClientThread t = clients.get(i);
				if( isAdmin(t.getIPAddress()) ){
					adminList.add(new ChatMessage("\t\t",t.getPlayerName(), chatMessageColor, true));
				}
			}

			return sendToClient( data.getIPAddress(), new ChatHistory(adminList, true) );
		}

		/**
		 * Sends a list of the IP's that are admins on the server
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if the the text should be displayed and sent to it's clients
		 */
		private boolean parsePlayers(Scanner scan, NetworkObject data) {

			// Send history back to the client
			String playerList = "List of Players:\n";
			for(String name : clientNameToIP.keySet()){
				playerList = playerList + name + "\n";
			}

			sendToClient(data.getIPAddress(), new ChatMessage("~Admin",playerList, chatMessageColor, true));

			return false;
		}

		/**
		 * Displays all the possible commands that can be send form a client to the server
		 * @param scan Scanner attached to a string/chat message
		 * @param data Information sent with the text
		 * @return True if commands were sent to the client
		 */
		private boolean parseHelp(Scanner scan, NetworkObject data) {

			// Sent client commands
			if( !sendToClient(data.getIPAddress(), new ChatHistory(clientCommandList, true)) ){

				// Didn't send
				return false;
			}

			// Sent admin commands if the requester is an admin
			if( isAdmin(data.getIPAddress()) ){

				if( !sendToClient(data.getIPAddress(), new ChatHistory(adminCommandList, true)) ){

					// Didn't send
					return false;
				}
			}

			// Sent successfully
			return true;
		}
	}

	/**
	 * Server was pinged by a client
	 * @param scan2
	 *
	 * @param clientIP
	 *            IP of who wants the history sent to them
	 */
	protected synchronized long ping(Scanner scan, NetworkObject data) {

		// See if the ping has a destination
		if( scan.hasNext() && !scan.hasNext(getIPAddress()) ){

			// Ping all clients?
			if( scan.hasNext("all") ){
				for (int i = 0; i < clients.size(); i++) {
					ClientThread client = clients.get(i);
					pingClient(client.getPlayerName(), data);
				}
			}
			else if( !scan.hasNext("everyone") ){

				// Where is the message going to?
				pingClient(scan.nextLine().trim(),data);
			}
			return -1;
		}
		return pingServer(data.getIPAddress(), data.getTimeInMillis());
	}

	/**
	 * Try and ping a client with the given IP or Name
	 * @param whoToPing Name or IP of the client
	 * @param data
	 * @return
	 */
	protected synchronized boolean pingClient(String whoToPing, NetworkObject data){

		// Get who pinged the server
		ClientThread to = getClientFromName(whoToPing);

		// Check if we can find the client via name instead
		if( to == null ){
			to = getClientFromIP(whoToPing);
			if( to == null ){
				return false;
			}
		}

		return to.sendData(data);
	}

	/**
	 * Someone pinged the server
	 * @param whoPingedMe IP or name of who pinged the server
	 * @param sentMilliSeconds ms's of when the gile was sent
	 * @return Delay between the sending of the ping, and receiving of the ping
	 */
	protected synchronized long pingServer(String whoPingedMe, long sentMilliSeconds){
		long delay = Math.abs(System.currentTimeMillis() - sentMilliSeconds);

		// Get who pinged the server
		ClientThread client = getClientFromIP(whoPingedMe);

		// Check client
		if (client == null) {
			if( !whoPingedMe.equals(getIPAddress()) ){
				System.out.println("Pinged by unknown client " + whoPingedMe);
			}

		}
		else{
			// Send the ping back to the client
			client.sendData(new NetworkObject(getIPAddress(), new ChatMessage("~Admin", "Ping: " + delay + "ms", chatMessageColor, true)));
		}

		return delay;
	}

	@Override
	public boolean newClientConnection(ClientThread cl) {

		// Tell everyone the new client has joined the server
		messageAllClients(cl.getPlayerName() + " has Connected.", cl);

		// Display welcome message for the new client
		messageClient("Welcome Message::" + "\nType /help for commands", cl);

		// Tell console this client connected
		System.out.println(cl.getPlayerName() + " has Connected.");

		// Add new failed Ping
		failedPings.put(cl.getPlayerName(), 0);
		return true;
	}

	@Override
	public boolean clientRejoins(ClientThread cl) {

		// Tell everyone the new client has joined the server
		messageAllClients(cl.getPlayerName() + " has Reconnected.", cl);

		// Tell console this client connected
		System.out.println(cl.getPlayerName() + " has Reconnected.");

		// Reset current FailedPing
		failedPings.put(cl.getPlayerName(), 0);
		return true;
	}

	@Override
	public void messageAllClients(String message, ClientThread... exceptions) {
		sendToAllClients(new ChatMessage("~Admin",message, this.chatMessageColor, true), exceptions);
	}

	@Override
	public void messageClient(String message, ClientThread client) {
		client.sendData(new ChatMessage("~Admin",message, chatMessageColor, true));
	}
}
