package rendering;

import gameLogic.Toggleable;
import gameLogic.Items.Item;
import gameLogic.Items.Wire;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Draws the inventory (Max 5 Items) onto the bottom of the panel.
 *
 * @author Leon North
 *
 */
public class DrawInventory implements MouseListener, MouseMotionListener{
	private JPanel panel;
	private int xMaxInventorySize = 4;
	private int yMaxInventorySize = 4;
	private int cellLip = 20; // Distance between each cell
	private int cellWidth = 20;
	private int cellHeight = 20;
	private int width;
	private int height;
	private int headerheight;

	private int yOffset = 0;
	private int xOffset = 0;
	private static final int DEFAULT_HEADER_HIGHT = 20;
	private static final int DEFAULT_CELLWIDTH = 80;
	private static final int DEFAULT_CELLHEIGHT = 80;
	private static final int DEFAULT_CELLLIP = 10;
	private static final Color HEADER_COLOR = new Color(50,50,50);
	private static final Color INVENTORY_BACKGROUND_COLOR = new Color(25,25,25);
	private static final Color CELL_COLOR = new Color(50,50,50);
	private static final Color HIGHLIGHTED_CELL_COLOR = new Color(40,40,40);
	
	private static Point lastDragPoint = null;
	private static Point position = new Point(0,0);

	public DrawInventory(JPanel panel) {
		this.panel = panel;
	}

	/**
	 *
	 * @param g: Graphics object
	 * @param inventory: list of inventory objects
	 * @param direction: String of the direction
	 * @author Leon North
	 */
	public void redraw(Graphics g, Point mousePoint, List<Item> inventory){
		if( inventory == null ) return;
	
		cellWidth = (int)((panel.getWidth() / 1280.0)*DEFAULT_CELLWIDTH);
		cellHeight = (int)((panel.getWidth() / 1280.0)*DEFAULT_CELLHEIGHT);
		cellLip = (int)((panel.getWidth() / 1280.0)*DEFAULT_CELLLIP);
		headerheight = (int)((panel.getWidth() / 1280.0)*DEFAULT_HEADER_HIGHT);
		
		float widthScaler = (DEFAULT_CELLWIDTH+(DEFAULT_CELLLIP+1)) * xMaxInventorySize;
		width = (int) ((panel.getWidth() / 1280.0) * widthScaler);
		height = (int) (width);
		
		position.x = xOffset + cellLip;
		position.y = yOffset + headerheight + cellLip;
		
		// Draw Header for inventory
		drawHeader(g);
		
		
		// Draw border for inventory
		g.setColor(INVENTORY_BACKGROUND_COLOR);
		g.fillRect(position.x-cellLip, position.y-cellLip, width, height);
		
		// Item from the inventory
		Item item = null;
		int cellIndex = -1;
		for (int index = 0; index < xMaxInventorySize * yMaxInventorySize; index++) {
				
				// Get item from inventory
				item = inventory.size() > index ? item = inventory.get(index) : null;
				
				// Draw cell in visual inventory
				boolean highlighted = drawCell(g, index, mousePoint, item);
				if( highlighted ){
					cellIndex = index;
				}
		}
		
		// Draw Description
		drawItemDescription(g, inventory, cellIndex);
	}
	
	public void drawItemDescription(Graphics g, List<Item> inventory, int cellIndex){
		if( cellIndex == -1 || cellIndex >= inventory.size() ){
			return;
		}
		
		Item item = inventory.get(cellIndex);
		String className = item.getClass().getSimpleName();
		String description = item.getDescription();
		
		int fontSize = (int)((panel.getWidth() / 128)*2);
		Font font = new Font("TimesRoman", Font.PLAIN, fontSize);
		g.setFont(font);
		
		Rectangle2D nameBounds = g.getFontMetrics().getStringBounds(className, g);
		Rectangle2D descriptionBounds = g.getFontMetrics().getStringBounds(description, g);
		int x = getCellX(cellIndex);
		int y = getCellY(cellIndex) + (int)(descriptionBounds.getHeight()/2) + cellHeight;

		g.setColor(Color.WHITE);
		// Name
		g.drawString(className, x - (int)(nameBounds.getWidth()/2) + (cellWidth/2), getCellY(cellIndex));
		
		// Description
		g.drawString(description, x - (int)(descriptionBounds.getWidth()/2) + (cellWidth/2), y);
	}
	
	public void drawHeader(Graphics g){
		g.setColor(HEADER_COLOR);
		g.fillRect(position.x-cellLip, position.y-cellLip-headerheight, width, headerheight);
		
		g.setColor(Color.black);
		g.drawRect(position.x-cellLip, position.y-cellLip-headerheight, width, headerheight);
	}

	private boolean isOnHeader(Point point) {
		return point.x >= position.x-cellLip && point.x <= position.x-cellLip + width && 
			   point.y >= position.y-cellLip - headerheight && point.y <= position.y-cellLip;
	}
	
	public boolean drawCell(Graphics g, int index, Point mousePoint, Item item){
		boolean highlighted = false;
		int cellX = getCellX(index);
		int cellY = getCellY(index);
		
		// Draw
		if( findCellIndex(mousePoint.x,mousePoint.y) == index ){
			g.setColor(HIGHLIGHTED_CELL_COLOR);
			highlighted = true;
		}
		else{
			g.setColor(CELL_COLOR);
		}
		
		g.fillRect(cellX, cellY, cellWidth, cellHeight);
		
		// Draw item
		drawInvItem(g, cellX, cellY, item);
		
		return highlighted;
	}
	
	
	
	public int getCellX(int cell){
		int x = cell % xMaxInventorySize;
		int cellX = 0;
		cellX += x * cellWidth; // Position
		cellX += position.x; // Offset from left side of the window
		cellX += x * cellLip; // Lip distance between cells
		return cellX;
	}
	
	public int getCellY(int cell){
		int y = ( cell / xMaxInventorySize ) % yMaxInventorySize;
		int cellY = 0;
		cellY += y * cellHeight;
		cellY += position.y;
		cellY += y * cellLip; 
		return cellY;//width + width*cell + buffer + buffer*cell;
	}
	
	public int getCellWidth(){
		return cellWidth;
	}
	
	public int getCellHeight(){
		return cellHeight;
	}

	/**
	 * Draws items in the appropriate boxes.
	 * @param g: Graphics
	 * @param i: Position in the inventory
	 * @param inventory: List of inventory objects
	 * @author Leon North
	 */
	private void drawInvItem(Graphics g, int cellX, int cellY, Item item) {
		if( item == null ){
			return;
		}
		
		
		String itemName = item.getClass().getSimpleName();
		int itemNum = 0;
		
		if( item instanceof Wire ){
			int index = ((Wire)item).getSpriteIndex();
			if( itemNum == 0 && index == 0) itemNum = 0;
			else if( itemNum == 0 && index == 1) itemNum = 1;
			else if( itemNum == 1 && index == 0) itemNum = 1;
			else if( itemNum == 1 && index == 1) itemNum = 0;
		}
		
		if( item instanceof Toggleable && ((Toggleable)item).turnedOn() ){
			itemNum += 2;
		}
		
		URL imageURL = DrawInventory.class.getResource(itemName+itemNum+".png");
		if(imageURL == null){
			System.out.println("could not find image on disk: "+ itemName);
		}
		
		
		try {
			BufferedImage img = ImageIO.read(imageURL);
			g.drawImage(img, cellX, cellY, cellWidth, cellHeight, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Get the cell that the x,y are on. cellWidth + cellLip are treated as ONE cell
	 * @param x
	 * @param y
	 * @return -1 if not found.
	 */
	public int findCellIndex(int x, int y){
		if( position == null || 
		    x <= position.x || x >= (position.x+width-cellLip) ||
			y <= position.y || y >= (position.y+height-cellLip) ){
			return -1;
		}
		
		int cellX = 0;
		int cellY = 0;
		
		// Check for a cell
		x -= position.x; // Offset from left side of the window
		y -= position.y;
		
		// Check if mouse is on the border
		cellX = x % (cellWidth+cellLip);
		cellY = y % (cellHeight+cellLip);
		if( cellX > cellWidth || cellY > cellHeight){
			return -1;
		}
		
		// Get the cell
		cellX = x / (cellWidth+cellLip);
		cellY = y / (cellHeight+cellLip);
		
		int index = cellX + cellY*xMaxInventorySize;
		return index;
	}

	/**
	 * @return the position.x
	 */
	public Point getPosition() {
		return position;
	}

	/**
	 * @param position.x the position.x to set
	 */
	public void setX(int x) {
		position.x = x;
	}

	/**
	 * @param position.y the position.y to set
	 */
	public void setY(int y) {
		position.y = y;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if( lastDragPoint != null ){
			xOffset = xOffset + event.getX() - lastDragPoint.x;
			yOffset = yOffset + event.getY() - lastDragPoint.y;
			
			lastDragPoint = event.getPoint();

		}		
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if( isOnHeader(event.getPoint()) ){
			lastDragPoint = event.getPoint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if( lastDragPoint != null ){
			lastDragPoint = null;
		}
	}
}
