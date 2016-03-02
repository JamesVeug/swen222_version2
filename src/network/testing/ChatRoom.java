package network.testing;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import networking.ChatClient;
import networking.ChatMessage;


/**
 * Basic chat room used to demonstrate the networking comparison between a client and server for those wanting to send something over the network.
 * @author veugeljame
 *
 */
public class ChatRoom implements ActionListener{

	// The client of this run that is talking to the server
	private ChatClient client;

	// Server if this chat client wants to start their own public server for everyone to connect to
	private int port = 32768;
	private ChatRoomServer server;
	private String publicIP = "173.255.249.21";

	private JPanel chatHistory;
	private JScrollPane scroll;
	private JTextField message;
	private JButton send;


	private JTextField IPConnection;
	private JLabel connectLabel;
	private JButton connect;
	private JButton connectLocal;
	private JButton startServer;



	public ChatRoom(){

		// Set up a basic client for this ChatRoom
		// Tell this chat room to wait for input from the server that sends data to this client
		client = new ChatClient("BOB - Undefined");

		// Set up interface
		setUpGui();
	}

	private void setUpGui(){
		JFrame frame = new JFrame("Chat Room");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(650,300);
		frame.setLayout(new FlowLayout());

		JLabel yourIP = new JLabel("Your IP: " + client.getIPAddress() );
		IPConnection = new JTextField(publicIP);
		IPConnection.setPreferredSize(new Dimension(110,25));
		IPConnection.addActionListener(this);
		try {
			client.setName( InetAddress.getLocalHost().getHostName() );
		} catch (UnknownHostException e) {e.printStackTrace();}


		connectLabel = new JLabel("Connect To: ");
		connect = new JButton("Connect");
		connect.addActionListener(this);
		connectLocal = new JButton("Local");
		connectLocal.addActionListener(this);
		startServer = new JButton("Start Server");
		startServer.addActionListener(this);

		// Chat Messages display on the board
		chatHistory = new JPanel(){


			@Override
			public void paintComponent(Graphics g){
				super.paintComponent(g);

				// Get the chat history
				ArrayList<ChatMessage> history = client.getChatHistory(10);
				int maxMessages = Math.min(history.size(), 20);

				// Display
				for(int i = history.size()-maxMessages; i < history.size(); i++){
					ChatMessage cm = history.get(i);
					g.setColor(cm.color);
					g.drawString(cm.toString(), 0, (history.size()-i)*10);
				}

				// Scroll to bottom of the page
				int height = getHeight();
		        scroll.getVerticalScrollBar().setValue(height);
			}
		};
		chatHistory.setFocusable(false);
		chatHistory.setBackground(Color.white);
		chatHistory.setLayout(new BoxLayout(chatHistory, BoxLayout.Y_AXIS));
		scroll = new JScrollPane(chatHistory);
		scroll.setPreferredSize(new Dimension(500,200));
		message = new JTextField(15);
		message.addActionListener(this);
		send = new JButton("Send");
		send.addActionListener(this);

		frame.getContentPane().add(yourIP);
		frame.getContentPane().add(connectLabel);
		frame.getContentPane().add(IPConnection);
		frame.getContentPane().add(connect);
		frame.getContentPane().add(connectLocal);
		frame.getContentPane().add(startServer);
		frame.getContentPane().add(scroll);
		frame.getContentPane().add(message);
		frame.getContentPane().add(send);
		frame.setVisible(true);
	}

	/**
	 * Send a chat message to the server
	 * @param chatMessage text a user has entered to send to the everyone to view
	 * @return True if the message was sent and no exceptions were thrown
	 */
	private boolean sendMessageToServer(String chatMessage){

		try {

			// Send the chat message to the server
			if( !client.sendChatMessageToServer(chatMessage) ){
				System.out.println("Didn't send...");
			}
		} catch (IOException e) {

			// Could not send message to the server
			client.appendWarningMessage("Could not send message!\n");
			e.printStackTrace();
			return false;
		}

		// Message sent successfully
		return true;
	}


	@Override
	public void actionPerformed(ActionEvent ae) {

		// Sends a message out to the client
		if( ae.getSource() == message || ae.getSource() == send ){

			// Attempt to send a message to the server
			if( !message.getText().equals("") && sendMessageToServer(message.getText()) ){
				message.setText("");
			}
		}
		else if( ae.getSource() == startServer ){


			if( startServer.getText().equals("Start Server")){

				// Attempt to send a message to the server
				if( server == null ){


					// Start a new server
					server = new ChatRoomServer();

					// Attempt to connect to the server
					if( connectToServer(server.getIPAddress(), port) ){

						// Change button
						startServer.setText("Stop Server");

						// Don't allow us to connect to other servers
						connect.setEnabled(false);
					}
				}
			}
			else{

				// Stop the server
				server.stopServer();
				server = null;

				// Change button
				startServer.setText("Start Server");

				// Allow us to connect to other servers
				connect.setEnabled(true);
			}


		}
		else if( ae.getSource() == connect || ae.getSource() == IPConnection ){

			// Attempt to connect to the server
			connectToServer(IPConnection.getText(), port);
		}
		else if( ae.getSource() == connectLocal || ae.getSource() == IPConnection ){

			// Attempt to connect to the server
			connectToServer(client.getIPAddress(), port);
		}
	}

	/**
	 * Attempt to connect to the server with the given ip and port
	 * @param ip IPAddress of server to connect to
	 * @param port Port
	 */
	public boolean connectToServer(String ip, int port){

		try {
			if( client.connect(ip) ){
				chatHistory.removeAll();
				client.appendWarningMessage("Connected to " + IPConnection.getText() + ":" + port);

				return true;
			}
			else{
				client.appendWarningMessage("Could not connect to " + IPConnection.getText() + ":" + port);
			}


		} catch (UnknownHostException e) {
			client.appendWarningMessage("Could not connect to " + IPConnection.getText() + ":" + port);
			e.printStackTrace();

		} catch (IOException e) {
			client.appendWarningMessage("Could not connect to " + IPConnection.getText() + ":" + port);
			e.printStackTrace();
		}


		// Could not connect
		return false;
	}

	public static void main(String[] args){
		new ChatRoom();
	}
}
