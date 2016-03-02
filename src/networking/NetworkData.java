package networking;

import java.io.Serializable;
import java.util.Calendar;

/**
 *Abstract Data that contains information if the data has been acknowledged from the server and also whe nthe data was created
 * @author veugeljame
 *
 */
public abstract class NetworkData implements Serializable{
	private static final long serialVersionUID = 4448513134913822794L;

	// Check if the data has been acknowledged if sent from a client to the server
	public boolean acknowledged = false;

	// When was this data sent?
	public int hours = 0;
	public int minutes = 0;
	public int seconds = 0;
	public long timeInMillis = 0;


	public NetworkData(){
		this(Calendar.getInstance());
	}

	public NetworkData(Calendar create){
		this.setTime(create);
	}

	/**
	 * Sets the time the object was created
	 * @param s
	 * @param m
	 * @param h
	 */
	public void setTime(Calendar create){
		setTime(create.get(Calendar.HOUR), create.get(Calendar.MINUTE), create.get(Calendar.SECOND), System.currentTimeMillis());
	}

	/**
	 * Gets the time that the packet was sent
	 * @return
	 */
	public String getTime(){
		return (hours + ":" + minutes);
	}

	public void setTime(int hours, int minutes, int seconds, long ms) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.timeInMillis = ms;
	}
}
