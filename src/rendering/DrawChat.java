package rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import networking.ChatMessage;
import GUI.DrawingPanel;

/**
 * Draws the chatroom to the panel
 *
 * @author Leon North
 *
 */
public class DrawChat {

	private DrawingPanel panel;
	private static final int offset = 30;
	private int fontSize = 0;

	public DrawChat(DrawingPanel panel){
		this.panel = panel;
	}

	/**
	 * Draws the chatroom
	 *
	 * @param g : Graphics objects
	 * @param chatMessages : List of chatMessage objects
	 * @param currentMessage : the current message being created
	 */
	public void redraw(Graphics g, List<ChatMessage> chatMessages, List<ChatMessage> notifications, String currentMessage){
		fontSize = (int)((panel.getWidth() / 128)*2);

		if( panel.isChatMode() ){
			g.setColor(new Color(0f, 0f, 0f, 0.7f));
			g.fillRect(0, 0, panel.getWidth(), panel.getHeight());

			drawMessages(g, chatMessages);

			int x = offset;
			int y = panel.getHeight() - offset - fontSize;

			g.setColor(Color.WHITE);
			g.drawString(currentMessage, x, y);
		}
		else{
			drawMessages(g, notifications);
		}
	}

	public void drawMessages(Graphics g, List<ChatMessage> chatMessages){
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

		for (int i = 0; i < chatMessages.size(); i++){
			g.setColor(chatMessages.get(i).color);
			String nameAndMessage = chatMessages.get(i).toString();
			int y = (fontSize * i)+offset;
			int x = 0 + offset;
			g.drawString(nameAndMessage, x, y);
		}
	}
}
