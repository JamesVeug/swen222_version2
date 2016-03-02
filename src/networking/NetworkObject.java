package networking;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Object of which is sent through the network containing data on who send the package, what was sent and when it was sent
 * @author veugeljame
 *
 */
public class NetworkObject implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 3965982154655961725L;
	private final NetworkData data; // What's being sent through the network
	private final String IPAddress; // IP of who sent the data

	/**
	 * Creates a new NetworkObject and attaches the IPAddress of the client that sends the data
	 * @param IPAddress IP of the client sending the data
	 * @param name Name of the client sending the data
	 * @param data The data wanting to be sent through the network
	 */
	public NetworkObject(String IPAddress, NetworkData data){
		this.data = data;
		this.IPAddress = IPAddress;
	}

	/**
	 * Creates a new NetworkObject and attaches the IPAddress of the client that sends the data
	 * @param IPAddress IP of the client sending the data
	 * @param name Name of the client sending the data
	 * @param data The data wanting to be sent through the network
	 * @param calendar Date on the calendar for when the object was sent through the network
	 */
	public NetworkObject(String IPAddress, NetworkData data, Calendar calendar){
		this.data = data;
		this.IPAddress = IPAddress;
		this.data.setTime(calendar);
	}

	/**
	 * Creates a new NetworkObject and attaches the IPAddress of the client that sends the data
	 * @param IPAddress IP of the client sending the data
	 * @param name Name of the client sending the data
	 * @param data The data wanting to be sent through the network
	 * @param calendar Date on the calendar for when the object was sent through the network
	 */
	public NetworkObject(String IPAddress, NetworkData data, int hours, int minutes, int seconds, long ms){
		this.data = data;
		this.IPAddress = IPAddress;
		this.data.setTime(hours,minutes,seconds, ms);
	}

	public String toString(){
		return data.toString();
	}

	/**
	 * Gets the data that was sent through the network
	 * @return data the data sent from a server to the given client
	 */
	public NetworkData getData() {
		return data;
	}

	/**
	 * Returns when the message was sent
	 * @return Calendar containing the information of when the data was sent
	 */
	public int getHours() {
		return data.hours;
	}

	/**
	 * Returns when the message was sent
	 * @return Calendar containing the information of when the data was sent
	 */
	public int getMinutes() {
		return data.minutes;
	}

	/**
	 * Returns when the message was sent
	 * @return Calendar containing the information of when the data was sent
	 */
	public int getSeconds() {
		return data.seconds;
	}

	/**
	 * Gets the time that the packet was sent
	 * @return
	 */
	public String getTime(){
		return data.getTime();
	}

	/**
	 * Gets the IPAddress of the client that sent the packet
	 * @return String containing the IP of the sender
	 */
	public String getIPAddress() {
		return IPAddress;
	}

	/**
	 * Time in ms when this object was made
	 * @return representation of the time in ms
	 */
	public long getTimeInMillis() {
		return data.timeInMillis;
	}
}
