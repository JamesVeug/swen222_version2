package networking;
import java.awt.Color;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.ConnectException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayDeque;

/**
 *Abstract Client class that holds all the required features when connecting, sending and receiving data from/to the server
 * @author veugeljame
 *
 */
public abstract class Client{

	// Current connected server
	private Socket socket;
	protected String connectedIP = "null";
	protected int connectedPort = -1;

	// Which player the gameclient is controlling
	protected Player player;

	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;

	private InputWaiter inputThread;

	private final int port = 32768;

	/**
	 * Creates a new Client object with a link to who is waiting for information from the server
	 * @param listener Who is waiting for information from the server
	 */
	public Client(){

		player = new Player();
		try {
			player.setIPAddress( Inet6Address.getLocalHost().getHostAddress() );

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the IP Address that this client is currently connected to
	 * @return String containing the connected IP Address
	 */
	public String getConnectedIPAddress(){
		return connectedIP;
	}

	/**
	 * Returns the IP Address of the player according to the server they are connected to
	 * @return String containing the connected IP Address
	 */
	public String getIPAddress(){
		return player.getIPAddress();
	}

	/**
	 * Attempts to connect to the given Server
	 * @param IPAddress IPAddress of the connection to connect as
	 * @return True if connection worked, otherwise a exception gets thrown
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public boolean connect(Server server) throws UnknownHostException, IOException{
		return connect(server.getIPAddress(), getName(), server.getPort());
	}

	/**
	 * Attempts to connect to the given IPAddress and port number of the server using the Clients name
	 * @param IPAddress IPAddress of the connection to connect as
	 * @param port Default: 32768
	 * @return True if connection worked, otherwise a exception gets thrown
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public boolean connect(String IPAddress) throws UnknownHostException, IOException{
		return connect(IPAddress, player.getName(), getPort());
	}

	/**
	 * Attempts to connect to the given IPAddress and port number of the
	 * @param IPAddress IPAddress of the connection to connect as
	 * @param playerName Identification of this client
	 * @param port Default: 32768
	 * @return True if connection worked, otherwise a exception gets thrown
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public boolean connect(String IPAddress, String playerName, int port) throws UnknownHostException, IOException{

		// Disconnect from the server
		disconnect();

		// Attempt Connection
		Socket temp;
		try{
			temp = new Socket(IPAddress,port);
		}catch( ConnectException e ){
			return false;
		}


		try {
			// Wait for new IP
			ObjectInputStream in = new ObjectInputStream(temp.getInputStream());
			player.setIPAddress((String)in.readObject());

			// Give the server our name
			ObjectOutputStream out = new ObjectOutputStream(temp.getOutputStream());
			out.writeObject(playerName);
			out.flush();

			// Receive name from server
			in = new ObjectInputStream(temp.getInputStream());
			String newName = (String)in.readObject();

			// Assign new name
			player.setName(newName);

			// Start our new socket
			inputThread = new InputWaiter();
			inputThread.start();

			// Record server
			connectedIP = IPAddress;
			connectedPort = port;

			// Listen on this socket now
			socket = temp;

			// Perform our setup since we connected to a server
			successfullyConnected(playerName);

			// Successful connection
			return true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Disconnects from the current server
	 * @return
	 */
	public boolean disconnect(){

		// Check if we have a current Socket, close if we do
		if( socket != null ){

			// Close socket first
			if( !socket.isClosed() ){
				try {
					socket.close();
				} catch (IOException e) { return false;}
			}

			// Don't have a socket to use now
			socket = null;
		}

		//Wait for us to stop listening to the current socket
		if( inputThread != null ){
			try {
				inputThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Closed
		System.out.println("Disconnected from Server");
		return true;
	}

	/**
	 * Attemps to reconnect to the current server using the given name
	 * @return true if connected, false if not.
	 */
	public boolean reconnect(String name){
		try {
			return connect(connectedIP, name, connectedPort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Stores the given packet in a queue for sending once we have a socket
	 * @param data Object to sent to the server for processing
	 */
	protected boolean sendData(NetworkData data){

		// Data stored successfully
		return sendData(new NetworkObject(getIPAddress(), data));
	}

	/**
	 * Stores the given packet in a queue for sending once we have a socket
	 * @param data Object to sent to the server for processing
	 */
	protected boolean sendData(NetworkObject data){


		// Check if we have a connection
		if( socket == null || socket.isClosed() ){
			return false;
		}

		// Send to server
		try {
			outputStream = new ObjectOutputStream(socket.getOutputStream());

			// Get packet to send
			outputStream.writeObject(data);
			outputStream.flush();

			// Send to client for client sided review
			retrieveObject(data);

			return true;
		} catch(NotSerializableException e){
			System.out.println("Client: Something is not Serializable, and can not be sent in object:\n" + data);
		}catch(SocketException e){
			return false;
		}catch (IOException e) {
			return false;
		}

		return false;
	}

	public class InputWaiter extends Thread{
		@Override
		public void run() {
			try{

				NetworkObject data = null;

				while( socket != null && !socket.isClosed() ){

					// Wait for text
					try{
						inputStream = new ObjectInputStream(socket.getInputStream());
					}catch(IOException e ){ continue; }
					catch(NullPointerException e ){ continue; }
					try{

						// Get data sent to us
						data = (NetworkObject)inputStream.readObject();

						// Send object to client that is waiting for data
						retrieveObject(data);

					}catch(SocketException e){
						continue;
					}catch(StreamCorruptedException e){
						continue;
					}
				}
			}
			catch(SocketException e){
				e.printStackTrace();
			}
			catch(StreamCorruptedException e){
				e.printStackTrace();
			}
			catch(IOException e){
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			finally{
				if( socket != null && !socket.isClosed() ){
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				retrieveObject(new NetworkObject(getIPAddress(), new ChatMessage("You have been Disconnected", Color.black, true)));
			}
		}
	}

	/**
	 * Checks if the client is connected to a server
	 * @return True if socket is valid
	 */
	public boolean isConnected(){

		// Check for valid socket
		return socket != null && !socket.isClosed();
	}
	/**
	 * Returns the name placed as this client
	 * @return
	 */
	public String getName() {
		return player.getName();
	}

	/**
	 * Returns the player accociated with this client
	 * @return Player object containing name and IP
	 */
	public Player getPlayer(){
		return player;
	}

	/**
	 * Gets the port that will be connecting to a server
	 * @return int of the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * What's called once we successfully connect to a server
	 * @param playerName name of our player that we are connecting with
	 */
	public boolean successfullyConnected(String playerName){
		System.out.println("Connected to: " + connectedIP);
		return true;
	}


	/**
	 * Servers send out objects as data to all their clients.
	 * This method is called once a single object is sent to a client to be handled by who is listening.
	 * Method MUST be synchronized
	 * @param data The data sent from a server to be handled
	 * @return
	 */
	public abstract void retrieveObject(NetworkObject data);
}