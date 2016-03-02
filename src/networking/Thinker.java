package networking;

import gameLogic.Game;

/**
 * Thinker class for AI, so we are able to store different AI and tell them to perform their next move depending on their strategy
 * @author veugeljame
 *
 */
public interface Thinker {

	/**
	 * Think method of which is called with a game object in order to be called when the AI wants to perform their next move
	 * @param game Where to perform the move
	 */
	public void think(Game game);

}
