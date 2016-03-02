package networking;

import java.awt.Color;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *Basic abstract server class that contains the required features of a server alogn with features of admins, pinging and more.
 * @author veugeljame
 *
 */
public abstract class Server implements Runnable{

	// Clients Currently listening in on the server
	protected ArrayList<ClientThread> clients = new ArrayList<ClientThread>();

	// ArrayList of client names to check for duplicate names
	protected Map<String, String> clientNameToIP = new HashMap<String, String>();

	// List of admin IP addresses
	protected ArrayList<String> adminIPs = new ArrayList<String>();

	private final int port = 32768;
	private ServerSocket serverSocket;
	private String IPAddress;


	/**
	 * Sets up a basic server that addresses it's own IPAddress, adds the localIP to be an admin
	 */
	protected Server() {

		try {
			IPAddress = InetAddress.getLocalHost().getHostAddress();

			// Save the localhost as an admin
			adminIPs.add(IPAddress);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		// Start listening for clients!
		Thread myThread = new Thread(this);
		myThread.start();

		// What happens when shut down
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run(){
				stopServer();
			}
		});
	}

	/**
	 * Clients interact with server
	 */
	public void run() {


		// Try and start a new server off the default port
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e1) {
			System.out.println("Unable to start new server\n\tLocal server already running");
			return;
		}

		// Working port
		try {

			System.out.print("Servers IP: " + IPAddress + " : " + port + "\n");
			System.out.print("Waiting for clients...\n");

			// Keep the server constantly running
			while (serverSocket != null && !serverSocket.isClosed()) {

				// Someone connects to the server
				Socket clientSocket = serverSocket.accept();

				// Create a thread for each client
				new ClientThread(clientSocket);
			}
		} catch ( SocketException e ){

		} catch (Exception e) {
			System.out.print("\n====\n" + e.getMessage() + "\n====\n");
			e.printStackTrace();
		}finally {

			// Turn the server off
			shutDownServer();
		}
	}

	/**
	 * Gets the name for the player. Also does a check if it's already contained, if it is then the name is given a suffix
	 * @param name Name to be checked for in the server
	 * @param clientIP
	 * @return New Name to be assigned to the player
	 */
	private String getNewPlayerName(String name, ClientThread client) {

		// See if this name already exists
		// Also if it's not a rejoining player
		if( client != null && clientNameToIP.containsKey(name) && !clientNameToIP.get(name).equals(client.getIPAddress()) ){

			// Change the name a suffix
			int i = 1;
			while(clientNameToIP.keySet().contains(name + "(" + i + ")")){
				i++;
			}

			name = (name + "(" + i + ")");
		}

		// Record new name
		clientNameToIP.put(name, client.getIPAddress());

		// Return new name for assigning it to the new Client
		return name;
	}

	/**
	 * Removes the client at the given location from our list of clients
	 *
	 * @param client
	 *            index to remove a client from
	 * @param reconnecting
	 */
	public synchronized boolean removeClient(ClientThread client, boolean reconnecting) {

		//int index = clients.indexOf(client);
		boolean removed = clients.remove(client);
		if( !removed ){
			System.out.println("Couldn't remove!");
		}
		else if( clients.contains(client) ){
			System.out.println("STILL IN CLIENTSSSSSSSSS!");
		}

		// Stop the thread
		client.stopClient();

		// Check if this client has disconnected
		if ( !reconnecting ) {
			messageAllClients(client.getPlayerName() + " has Disconnected.", client);

			// Remove the clients name
			clientNameToIP.remove(client.getPlayerName());
			System.out.println(client.getPlayerName() + " has Disconnected.");
			return true;
		}
		return false;
	}



	/**
	 * Sends the given message to all clients on the server, provided they aren't listen in "exceptions"
	 * @param data Data to send to all the clients on the server
	 * @param exceptions Clients to not send the data to
	 */
	public void sendToAllClients(NetworkData data, ClientThread... exceptions) {

		for (int i = 0; i < clients.size(); i++) {

			ClientThread client = clients.get(i);

			// Check if we shouldn't send it to this client
			if( exceptions != null && exceptions.length > 0){
				for( ClientThread ex : exceptions ){
					if( ex.equals(client) ){
						continue;
					}
				}
			}

			// Send the data to this client
			client.sendData(data);
		}
	}

	/**
	 * Sends the given message to all clients on the server, provided they aren't listen in "exceptions"
	 * @param data Data to send to all the clients on the server
	 * @param exceptions Clients to not send the data to
	 */
	public void sendToAllClients(NetworkObject data, ClientThread... exceptions) {

		for (int i = 0; i < clients.size(); i++) {

			ClientThread client = clients.get(i);

			// Check if we shouldn't send it to this client
			if( exceptions != null && exceptions.length > 0){
				for( ClientThread ex : exceptions ){
					if( ex.equals(client) ){
						continue;
					}
				}
			}

			// Send the data to this client
			client.sendData(data);
		}

	}

	/**
	 * Sends the given NetworkData to the client with the given IP
	 * @param clientIP Who to send the IP to
	 * @param data What to send to the given client
	 * @return True if send successfully
	 */
	protected boolean sendToClient(String clientIP, NetworkData data) {

		// Iterate
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getIPAddress().equals(clientIP)) {
				return clients.get(i).sendData(data);
			}
		}

		// Couldn't send
		return false;
	}

	/**
	 * Gets a client from the server with the given name if there is any.
	 * @param name name of the client to get from the server
	 * @return Client with the given name, or Null
	 */
	protected ClientThread getClientFromName(String name) {

		// Iterate
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getPlayerName().equals(name)) {
				return clients.get(i);
			}
		}

		// Don't have one
		return null;
	}

	/**
	 * Gets a client from the server with the given IP if there is any.
	 * @param IP IP of the client to get from the server
	 * @return Client with the given IP, or Null
	 */
	protected ClientThread getClientFromIP(String IP) {

		// Iterate
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getIPAddress().equals(IP)) {
				return clients.get(i);
			}
		}

		// Don't have one
		return null;
	}

	/**
	 * Gets the IPAddress of the server for others to connect to
	 * @return String containing IPAddress
	 */
	public String getIPAddress() {
		return IPAddress;
	}

	/**
	 * Gets the port that the server is currently running off
	 * @return int containing the Port that the server is running off
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Stops the server and all clientsThreads from running
	 */
	public boolean stopServer() {

		if( serverSocket != null && !serverSocket.isClosed() ){
			try {
				serverSocket.close();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Shuts down the sever by closing all sockets associated with all clients, then closing all threads
	 */
	private void shutDownServer(){

		// Close our clients
		while( !clients.isEmpty() ){
			removeClient(clients.get(0),false);
		}

		System.out.print("WARNING: The Server has closed\n");
	}

	/**
	 * Checks if the given IP is an admin for the server or not
	 * @param IP IPAddress to check if the server has registered them as an admin
	 * @return True if the IP is an admin
	 */
	protected boolean isAdmin(String IP){
		return adminIPs.contains(IP);
	}

	/**
	 * How we are going to process the object received from a client.
	 * @param data Information sent from the client
	 */
	public abstract void retrieveObject(NetworkObject data);

	/**
	 * What to do when a new client that has never joined the server before, joins the server
	 * @param client Client that has joined the thread
	 */
	public abstract boolean newClientConnection(ClientThread client);

	/**
	 * The client that has rejoined the server
	 * @param client Client that has rejoined
	 */
	public abstract boolean clientRejoins(ClientThread client);

	/**
	 * The server wants to message all clients
	 * @param message MEssage to sent tp clients
	 */
	public abstract void messageAllClients(String message, ClientThread... exceptions);

	/**
	 * Servers wants to message a single client with the given message
	 * @param message Message to sent to client
	 * @param client Who to sent the message to
	 */
	public abstract void messageClient(String message, ClientThread client);

	/**
	 * A Thread relating to a specific client. Acts as a listener for incoming data, and allows sending to the client with this Thread
	 * @author veugeljame
	 *
	 */
	class ClientThread extends Thread {

		// Player object to use according to the client
		private final Player player;

		// Socket to send backs to and receive from the client
		private Socket socket;

		/**
		 * Starts a clientThread that listens to the given socket, and relates the client to the given player object.
		 * @param socket Socket to listen and send to
		 * @param player Details on the user
		 */
		public ClientThread(Socket socket, Player player){
			this.socket = socket;
			this.player = player;
		}

		/**
		 * STEPS:
		 * #1 Send IP of socket to Client
		 * #2 Recieve Name of Client
		 * #3 Send New Name to Client
		 * @param socket Socket to listen and send to
		 * @param player Details on the user
		 */
		public ClientThread(Socket socket){
			this.socket = socket;
			this.player = new Player(socket.getInetAddress().getHostAddress(), "Client(" + clients.size() + ")");

			// Send SocketIP To client
			sendData(getIPAddress());

			// Wait for their public name to be sent through
			ObjectInputStream input;
			try {

				// Get Name from client
				input = new ObjectInputStream(socket.getInputStream());
				String name = getNewPlayerName((String) input.readObject(), this);

				// Change name of player
				setPlayerName(name);

				// Send name To client
				sendData(name);

				// See if this is a new client
				ClientThread previousClient = getClientFromIP(getIPAddress());
				if (previousClient == null) {

					// Tell our sub server that we have a new clinet
					if( !newClientConnection(this) ){

						// Close the client
						socket.close();
						return;
					}
				}
				else{
					// Remove the old connection
					removeClient(previousClient,true);

					// We have rejoined
					if( !clientRejoins(this) ){

						// Close the client
						socket.close();
						return;
					}
				}

				// Add the client to our list
				clients.add(this);
				start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/**
		 * Checks if the client is connected to a server
		 * @return True if socket is valid
		 */
		public boolean isConnected(){
			return socket != null && !socket.isClosed();
		}

		/**
		 * Gets the IP address of the player
		 * @return String with the players IP
		 */
		public String getIPAddress() {
			return player.getIPAddress();
		}

		/**
		 * Waits for input from this client and sends it to the server for processing
		 */
		public void run() {

			while (socket != null && !socket.isClosed()) {

				// Receive Input
				try {

					// Get Message
					ObjectInputStream input;
					try {
						input = new ObjectInputStream(socket.getInputStream());
					} catch (IOException e) {
						continue;
					} catch (NullPointerException e){
						continue;
					}

					// Get the data from what was sent through the network
					NetworkObject data = null;
					try{
						data = (NetworkObject)input.readObject();
					} catch(StreamCorruptedException e){
						e.printStackTrace();
					}
					data.getData().acknowledged = true; // We received the data

					// Sent data to our subclass for processing
					retrieveObject(data);

				} catch(InvalidClassException e){
					System.out.println("Incoming object must be NetworkObject: " + e.classname);
					e.printStackTrace();
				}catch (SocketException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * Stops the clientThread from running
		 */
		public void stopClient(){
			if( socket != null && !socket.isClosed()){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socket = null;
		}

		/**
		 * Sends the data directly to the client
		 * @param data To be sent to the client
		 * @return True if the data was sent without fault
		 */
		public synchronized boolean sendData(NetworkObject data) {

			// Check if we have a connection
			if( socket != null && !socket.isClosed() ){
				try {

					// Send to server
					ObjectOutputStream outputStream;
					try{
						outputStream = new ObjectOutputStream(ClientThread.this.socket.getOutputStream());
					}catch(SocketException e){
						return false;
					}

					// Get packet to send
					try{
						outputStream.writeObject(data);
					}catch(SocketException e){
						return false;
					}catch(NotSerializableException e){
						System.out.println("Something is not Serializable, and can not be sent in object:\n" + data);
						e.printStackTrace();
					}
					outputStream.flush();

					return true;
				}catch(SocketException e){
					e.printStackTrace();
				}
				catch(StreamCorruptedException e){
					e.printStackTrace();
				}

				catch(IOException e){
					e.printStackTrace();
				}
			}

			// Could not send
			return false;
		}

		private boolean sendData(Object data){
			// Check if we have a connection
			if( socket != null && !socket.isClosed() ){
				try {

					// Send to server
					ObjectOutputStream outputStream;
					try{
						outputStream = new ObjectOutputStream(ClientThread.this.socket.getOutputStream());
						outputStream.writeObject(data);
					}catch(SocketException e){
						return false;
					}
					outputStream.flush();

					return true;
				}catch(SocketException e){
					e.printStackTrace();
				}
				catch(StreamCorruptedException e){
					e.printStackTrace();
				}
				catch(NotSerializableException e){
					System.out.println("Something is not Serializable, and can not be sent in object:\n" + data);
					e.printStackTrace();
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}

			// Could not send
			return false;
		}

		/**
		 * Sends the data directly to the client
		 * @param data To be sent to the client
		 * @return True if the data was sent without fault
		 */
		public boolean sendData(NetworkData data) {
			return sendData(new NetworkObject(IPAddress, data));
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((player == null) ? 0 : player.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof ClientThread))
				return false;
			ClientThread other = (ClientThread) obj;
			if (player == null) {
				if (other.player != null)
					return false;
			} else if (!player.equals(other.player))
				return false;
			return true;
		}

		/**
		 * Gets the name of the player that's associated with the player.
		 * @return String with the players name
		 */
		public String getPlayerName(){
			return player.getName();
		}

		/**
		 * Assigns a new name for the player
		 * @param name New name to assign the player
		 */
		public void setPlayerName(String name){
			player.setName(name);
		}
	}
}
