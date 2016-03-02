package networking;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Stack;

/**
 * A Notification History that contains a stack of ChatMessages sent through the network from the game to the clients
 * @author veugeljame
 *
 */
public class NotificationHistory extends NetworkData {
	/**
	 *
	 */
	private static final long serialVersionUID = -9162015465291895550L;
	public final ArrayList<ChatMessage> history;

	public NotificationHistory(ArrayList<ChatMessage> history, boolean acknowledged){
		this.history = history;
		this.acknowledged = acknowledged;
	}
}
