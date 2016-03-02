package networking;

/**
 * Interaction associated when a Client has requested a move on their side and is sent through to the gamelogic for processing
 * @author veugeljame
 *
 */
public class Move extends NetworkData {

	/**
	 *
	 */
	private static final long serialVersionUID = 5277768659149235735L;

	public final String command;
	public final String action;
	public final Player player;
	public final int index;

	public Move(Player player, String command, String action){
		this.command = command;
		this.action = action;
		this.player = player;
		index = 0;
	}

	public Move(Player player, String command, String action, int index){
		this.command = command;
		this.action = action;
		this.player = player;
		this.index = index;
	}


	/**
	 * What to do when the move is performed
	 * @return Interaction
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * What to do when the move is performed
	 * @return Interaction
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Gets the player that is performing this move
	 * @return Player object of who to move
	 */
	public Player getPlayer(){
		return player;
	}

	@Override
	public String toString(){
		return player.getName() + " performs " + command + (!action.equals("") ? " with " + action : "");
	}

	public int getIndex() {
		return index;
	}

}
