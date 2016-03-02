package rendering;

import gameLogic.Avatar;
import gameLogic.NonWalkable;
import gameLogic.Room;
import gameLogic.Items.Water;
import gameLogic.Tiles.BlankFloor;
import gameLogic.Tiles.Charger;
import gameLogic.Tiles.Door;
import gameLogic.Tiles.EmptyTile;
import gameLogic.Tiles.Floor;
import gameLogic.Tiles.GreenDoor;
import gameLogic.Tiles.PurpleDoor;
import gameLogic.Tiles.RedDoor;
import gameLogic.Tiles.Tile2D;
import gameLogic.Tiles.YellowDoor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Draws a minimap on the panel
 *
 * @author Leon North
 *
 */
public class DrawMiniMap {

	private JPanel panel;
	private Avatar charac;
	private double width;
	private double height;
	private double buffer;
	private double cellHeight;
	private double cellWidth;
	private static final double STARTWIDTH = 1280;
	private static final double STARTMAPHEIGHT = 135;
	private static final int NUMCARDS = 5;
	

	public DrawMiniMap(JPanel panel, Avatar charac) {
		this.panel = panel;
		this.charac = charac;

	}

	/**
	 * Draws a mini map on the panel object.
	 * @param g
	 * @param room
	 * @param direction
	 */
	public void redraw(Graphics g, Room room, Direction direction){
		width = ((panel.getWidth() / STARTWIDTH) * STARTMAPHEIGHT);
		height = width;
		buffer = (width / STARTMAPHEIGHT);
		int x = panel.getWidth() - (int) width;
		int y = 0;

		cellHeight = (height / room.getTiles()[0].length);
		cellWidth = (width / room.getTiles().length);

		//clone the tiles so we don't modify the game logics tiles
		Tile2D[][] tiles = room.getTiles().clone();

		//rotate the tiles to up is facing the top right of the screen
		for (int i = 0; i < Direction.get(direction)+3; i++){
			tiles = rotate90(tiles);
		}

		//for each tile, set the color and draw it.
		for (int i = 0; i < tiles.length; i++ ){
			for(int j = 0; j < tiles[i].length; j++){
				g.setColor(chooseColor(tiles[i][j]));
				g.fillRect((int)(x+(i*cellWidth)), (int)(y+(j*cellHeight)), (int)(cellWidth-buffer), (int)(cellHeight-buffer));
			}
		}
		
		// Name
		int fontSize = (int)((panel.getWidth() / 128)*2);
		g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));

		g.setColor(Color.WHITE);
		g.drawString(room.getRoomPlace(), x, (int) (y+STARTMAPHEIGHT+fontSize));
	}

	/**
	 * Returns a color depending on the tile that it is given.
	 * Doors get a special color.
	 * Avatars are Red, solid red if they are the player too.
	 * Floor tiles are drawn pale transparent.
	 * Other tiles are a darker transparent.
	 * @param tile
	 * @return
	 */
	private Color chooseColor(Tile2D tile) {
		Color color = null;

		//Avatars are normally only on a floor tile.
		//If the avatar = the players avater, draw it solid red,
		//otherwise draw it transparent red.
		//doors get there own special color
		if (tile instanceof Door){
			if( tile instanceof YellowDoor) color = new Color(1.0f, 1.0f, 0.0f, 0.5f);
			else if( tile instanceof RedDoor) color = new Color(1.0f, 0.0f, 0.0f, 0.5f); 
			else if( tile instanceof PurpleDoor) color = new Color(0.5f, 0.0f, 1.0f, 0.5f); 
			else if( tile instanceof GreenDoor) color = new Color(0.0f, 1.0f, 0.0f, 0.5f); 
			else color = new Color(0.0f, 0.0f, 1.0f, 0.5f); 
		}
		//other obstacles such as columns, trees, walls get drawn
		//the same darker color
		else if( tile instanceof EmptyTile ){
			color = new Color(0.1f, 0.2f, 0.1f, 0.0f);
		}
		else if( tile instanceof Charger ){
			color = new Color(1.0f, 1.0f, 0f, 0.4f);
		}
		else if (!(tile instanceof NonWalkable)) {
			if (tile.getAvatar() != null) {
				if (tile.getAvatar().equals(charac)) {
					color = new Color(1.0f, 0.0f, 0.0f);
				} else {
					color = new Color(0.5f, 0.1f, 0.1f, 0.5f);
				}
			}
			//if the floor tile has no avatar just draw a normal color
			else {
				
				// Check for water
				for( int i = 0; i < tile.getItems().size(); i++ ){
					if( tile.getItems().get(i) instanceof Water ){
						return color = new Color(0f,0f,1f,0.4f);
					}
				}
				
				// Different floor classes
				if( tile.getClass() == Floor.class ){
					color = new Color(0.4f, 0.4f, 0.4f, 0.4f);
				}
				else if(  tile.getClass() == BlankFloor.class ){
					color = new Color(0.5f, 0.5f, 0.5f, 0.5f);
				}
			}
		}
		else{
			color = new Color(0.1f, 0.2f, 0.1f, 0.5f);
		}
		return color;
	}

	/**
	 * Takes a 2d array and returns a new 2d array rotated 90 degrees left.
	 * @param tiles: 2d array of tile2d objects
	 * @return 2d array of tile2d objects
	 * @author Leon North
	 */
	private Tile2D[][] rotate90(Tile2D[][] tiles) {
	    int width = tiles.length;
	    int height = tiles[0].length;
	    Tile2D[][] newTiles = new Tile2D[height][width];
	    for (int i = 0; i < height; ++i) {
	        for (int j = 0; j < width; ++j) {
	            newTiles[i][j] = tiles[width - j - 1][i];
	        }
	    }
	    return newTiles;
	}

}
