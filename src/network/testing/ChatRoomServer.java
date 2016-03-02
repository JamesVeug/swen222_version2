package network.testing;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import networking.ChatMessage;
import networking.ChatServer;
import networking.NetworkObject;

/**
 *
 * @author veugeljame
 *
 */

public class ChatRoomServer extends ChatServer {


	public ChatRoomServer() {
		super();

		// Server listens for input directly to servers terminal Thread
		Thread serverTextBox = new Thread(new ServerTextListener());
		serverTextBox.start();
	}

	/**
	 * The method that is run once the server enters a message in console
	 *
	 * @param message
	 */
	private synchronized void processServerMessage(String message) {

		// Process to everyone
		retrieveObject(new NetworkObject(getIPAddress(), new ChatMessage("~Admin", message, chatMessageColor, true)));
	}

	/**
	 * A class that listens for input from the console and sends the input to
	 * processServerMessage
	 *
	 * @author veugeljame
	 *
	 */
	private class ServerTextListener implements Runnable {
		@Override
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String text;

			while (true) {
				try {
					text = (String) br.readLine();

					if (text != null) {

						processServerMessage(text);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args){
		new ChatRoomServer();
	}
}
