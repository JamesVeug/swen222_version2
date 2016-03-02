package gameLogic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author northleon
 *
 */
public class Score implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = -3510705526938899858L;
	Map<String, Integer> scores;

	public Score() {
		scores = new HashMap<String, Integer>();
	}

	public Map<String, Integer> getScore(){
		return scores;
	}

	public void updateMap(List<Avatar> activeAvatars) {
		scores = new HashMap<String, Integer>();
		for(int i = 0; i < activeAvatars.size(); i++){
			Avatar avatar = activeAvatars.get(i);
			scores.put(avatar.getPlayerName(), avatar.getScore());
		}
	}


	public void setScores(Map<String, Integer> scores) {
		this.scores = scores;
	}

	public Map<String, Integer> getScores() {
		return scores;
	}


}
