package networking;

import java.io.Serializable;

/**
 * A Player object that contains the name of the player and IP in order to determine who is performing a move on which server
 * @author veugeljame
 *
 */
public class Player implements Serializable {
	private static final long serialVersionUID = 1045665174986227643L;
	private String name = null; // Name of the Player
	private String IPAddress = null; // IP Address of who is controlling this playher

	public Player(){
	}

	public Player(String name){
		this.setName(name);
	}

	public Player(String IP, String name){
		this.setIPAddress(IP);
		this.setName(name);
	}

	/**
	 * Gets the name of the player
	 * @return String containing name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assigns a new name to the player
	 * @param name New name to assign the player
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the IP Address of the player
	 * @return String with IP
	 */
	public String getIPAddress() {
		return IPAddress;
	}

	/**
	 * Assigns a new IP address to the player
	 * @param iPAddress new IP to assign the player
	 */
	public void setIPAddress(String iPAddress) {
		IPAddress = iPAddress;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((IPAddress == null) ? 0 : IPAddress.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Player))
			return false;
		Player other = (Player) obj;
		if (IPAddress == null) {
			if (other.IPAddress != null)
				return false;
		} else if (!IPAddress.equals(other.IPAddress))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
