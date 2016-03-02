package GUI;

import gameLogic.Avatar;
import gameLogic.Game;
import gameLogic.Room;
import gameLogic.Score;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Sound.SoundController;
import networking.ChaseAI;
import networking.DummyAI;
import networking.GameClient;
import networking.GameServer;
import networking.RandomAI;
import networking.Player;
import rendering.Direction;
import rendering.DrawChat;
import rendering.DrawCompass;
import rendering.DrawHealth;
import rendering.DrawInventory;
import rendering.DrawMiniMap;
import rendering.DrawWorld;
import rendering.ScoreBoard;

/**
 *
 * @author Daphne Wang and Leon North
 *
 */
public class DrawingPanel extends JPanel {

	private String spectate;
	private Game menuGame;

	private Direction direction = Direction.NORTH;
	private KeyBoard keyboard = new KeyBoard(this);
	private ButtonController buttonController = new ButtonController(this);
	private DrawWorld dw; // this draws all the game-stuff: avatars items etc
	private DrawChat chat;
	private DrawCompass compass;
	private DrawInventory inventory;
	private DrawMiniMap map;
	private DrawHealth health;
	private ScoreBoard score;

	private WindowFrame wf;

	private Point mousePoint = new Point(0,0);
	private Handler handler = new Handler();

	Map<Integer,String> moveCommands = new HashMap<Integer, String>(){{
		put(KeyEvent.VK_W, "W");
		put(KeyEvent.VK_A, "A");
		put(KeyEvent.VK_S, "S");
		put(KeyEvent.VK_D, "D");
	}};

	Image splashImage;
	Image helpImage;

	boolean chatOpen = false;
	boolean scoreBoardOpen = false;
	boolean showInventory = false;
	private String currentMessage = "";

	private GameServer gs;
	private GameClient gc;

	public States menuState = States.MainMenu;

	public enum States {
			MainMenu,
			Start,
			Join,
			Load,
			Help,
			Quit,
			Game;
			public static States toggleMenu(States state1){
				return state1 == Game ? MainMenu : Game;
			}
	};

	public DrawingPanel(WindowFrame frame){
		addKeyListener(handler);
		addMouseListener(handler);
		addMouseMotionListener(handler);
		setFocusable(true);

		this.wf = frame;

		try {
			splashImage = ImageIO.read(DrawingPanel.class.getResource("startMenuImages/splash.png"));
			helpImage = ImageIO.read(DrawingPanel.class.getResource("helpImage/helpmenu.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		gc = new GameClient("James");
		setUpMenuGame();


		// Draws consistently to display the new messages
		Thread drawThread = new Thread(){
			@Override
			public void run(){

				// Draw every 30ms
				while( true ){

					if( !isInGame() && menuGame != null ){

						// Move AI
						menuGame.tickGame();

						// Add AI
						if( menuGame.getActiveAI().size() < 2 ){
							String name = "ai" + menuGame.getActiveAI().size();
							Room room = menuGame.addPlayer(name);

							if( room != null ){
								menuGame.addAI(new DummyAI(room, name));
							}
						}

						// Change spectator
						if( menuGame.getAIAvatar(spectate).getCell().getBatteryLife() <= 0 ){
							int current = Integer.valueOf(spectate.charAt(2));
							current = ((current + 1) %menuGame.getActiveAI().size());
							spectate = "ai" + current;
						}
					}

					// Tell the component to repaint
					repaint();

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		};
		drawThread.start();
	}

	public void setUpMenuGame(){
		menuGame = new Game();

		spectate = "ai0";
		Room room = menuGame.addPlayer(spectate);
		menuGame.addAI(new DummyAI(room, spectate));

		dw = new DrawWorld(menuGame.getAIAvatar(spectate), this);
	}

	public void startGame(){
		gs = new GameServer();

		try {
			gc.connect(gs);
			joinGame();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void joinGame(){

		dw = new DrawWorld(gc.getAvatar(), this);
		chat = new DrawChat(this);
		compass = new DrawCompass(this);
		inventory = new DrawInventory(this);
		map = new DrawMiniMap(this, gc.getAvatar());
		health = new DrawHealth(this);
		score = new ScoreBoard(this);

		menuState = States.Game;
	}

	private void drawGame(Graphics g){
		Room room = gc.getRoom();
		Avatar avatar = gc.getAvatar();

		dw.redraw(g, room, direction, avatar);
		compass.redraw(g, direction);
		map.redraw(g, room, direction);
		health.redraw(g, avatar);

		if( scoreBoardOpen ){
			score.redraw(g, gc.getScore());
		}
		if( showInventory ){
			inventory.redraw(g, mousePoint, avatar.getInventory());
		}

		chat.redraw(g, gc.getChatHistory(), gc.getNotifications(), currentMessage);
	}

	private void drawMenuGame(Graphics g){
		if( menuGame == null ){
			System.out.println("Null");
			return;
		}


		Room arena = menuGame.getRoomsInGame().get(0);
		Avatar avatar = menuGame.getAIAvatar("ai0");

		dw.redraw(g, arena, direction, avatar);
	}



	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if( isInGame() ){
			drawGame(g);

			// fade
			if( menuState != States.Game ){
				g.setColor(new Color(0,0,0,128));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		}
		else if( menuState != States.Help ){
			// Draw game
			drawMenuGame(g);

			g.setColor(new Color(0,0,0,128));
			g.fillRect(0, 0, getWidth(), getHeight());

			// Draw Splash image for main menu
			g.drawImage(splashImage, 0, 0, getWidth(), getHeight(), null);
		}

		// Main Menu
		if( menuState == States.Help ){
			g.drawImage(helpImage,0,0,getWidth(),getHeight(), null);
		}

		// Draw all buttons
		buttonController.drawButtons(g,mousePoint);
	}


	public class Handler implements KeyListener, MouseListener, MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent arg0) {
			mousePoint = arg0.getPoint();

			if( showInventory )inventory.mouseDragged(arg0);
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			mousePoint = arg0.getPoint();
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent mouse) {
			buttonController.buttonPressed(mouse.getPoint());
			if( showInventory )inventory.mousePressed(mouse);
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			buttonController.buttonReleased(arg0.getPoint());
			if( showInventory )inventory.mouseReleased(arg0);
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			keyboard.keyPressed(arg0);

		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			keyboard.keyReleased(arg0);

		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

	}




	public boolean isChatMode() {
		return chatOpen;
	}

	public void setChatMode(boolean b) {
		chatOpen = b;
	}

	public boolean isScoreBoard() {
		return scoreBoardOpen;
	}

	public void setScoreBoard(boolean b) {
		this.scoreBoardOpen = b;
	}

	public void addToCurrentMessage(String string) {
		currentMessage = currentMessage + string;
	}


	public void setCurrentMessage(String string) {
		currentMessage = string;
	}

	public GameClient getGameClient() {
		return gc;
	}

	public GameServer getGameServer(){
		return gs;
	}

	public String getCurrentMessage() {
		return currentMessage;
	}

	public int getDirection() {
		return Direction.get(direction);
	}

	public void setDirection(int i) {
		direction = Direction.get(i);
	}

	public States getState() {
		return menuState;
	}

	public void setState(States game) {
		menuState = game;
	}

	public boolean isInGame() {
		return gc != null && gc.isConnected();
	}

	public void disconnect() {
		if( gc != null ){
			gc.disconnect();
		}

		if( gs != null ){
			gs.stopServer();
		}
		menuState = States.MainMenu;
	}

	public void join() {
		menuState = States.Join;

	}

	public void load() {
		menuState = States.Load;
	}

	public void quit() {
		disconnect();
		this.wf.dispose();
		menuGame = null;
	}

	public void help() {
		menuState = States.Help;
	}

	public void connect(String IP) {
		if( menuState == States.Join ){
			try {
				if( gc.connect(IP) ){
					joinGame();
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public DrawInventory getInventory() {
		return inventory;
	}

	public DrawWorld getDrawWorld() {
		return dw;
	}

	public boolean showInventory() {
		return showInventory;
	}

	public void setShowInventory(boolean b) {
		showInventory = b;
	}
}
