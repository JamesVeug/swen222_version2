package rendering;

import gameLogic.Avatar;
import gameLogic.Cell;
import gameLogic.Lightable;
import gameLogic.Lockable;
import gameLogic.Mountable;
import gameLogic.Room;
import gameLogic.Toggleable;
import gameLogic.Items.Item;
import gameLogic.Items.Light;
import gameLogic.Items.Water;
import gameLogic.Items.Wire;
import gameLogic.Tiles.BlankFloor;
import gameLogic.Tiles.EmptyTile;
import gameLogic.Tiles.Floor;
import gameLogic.Tiles.Powerable;
import gameLogic.Tiles.Tile2D;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;

import GUI.DrawingPanel;

/**
 * This class will draw the location and everything in it.
 *
 * @author Leon North
 *
 */
public class DrawWorld {

	private FloatingPointer floatingPointer;

	private Avatar character; // the main player

	private double scale;
	private int width;
	private int height;
	private Point offset = new Point(690, 220);
	private DrawingPanel panel;
	private boolean rotated90 = false; // used as a cheap way to show room rotation by
							           // flipping the images horizontally.
	private Map<String, BufferedImage> images;
	private Direction direction;

	public DrawWorld(Avatar character, DrawingPanel rendering) {

		floatingPointer = new FloatingPointer();
		this.character = character;
		this.panel = rendering;

		images = MakeImageMap.makeMap();
	}

	/**
	 * This method will be call externally from the UI to draw everything
	 * gameplay related
	 *
	 * @param Graphics g
	 * @param Room room
	 * @param Avatar character
	 * @param String direction
	 */
	public void redraw(Graphics g, Room room, Direction direction, Avatar avatar) {

		this.character= avatar;
		this.direction = direction;

		// set scaling based on frame size
		scale = 80 * (panel.getWidth() / 1280.0);
		width = (int) (1 * scale);
		height = width;

		// set offset based on character position.
		calibrateOffset(direction, room);

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		drawLocation(g, room, direction);
		//drawNight(g);
	}

	/**
	 * Gets tiles from the room provided. Rotates them to the direction
	 * provided. Draws the tiles to the Graphics provided using the placeTile()
	 * method.
	 *
	 * @param Graphics g
	 * @param Room room
	 * @param String direction
	 * @author Leon North
	 */
	private void drawLocation(Graphics g, Room room, Direction direction) {
		// TODO Auto-generated method stub
		Tile2D[][] tiles = room.getTiles().clone();

		// rotate the game the correct number of tiles
		 for (int i = 0; i < direction.ordinal()+3; i++){
		 tiles = rotate90(tiles);
		 }

//		 Temporary code here, this sets the rotated90 and back boolean which
//		 is used to flip images
		 if(Direction.get(direction) == 1 ||Direction.get(direction) == 3){
		 rotated90 = true;
		 }
		 else{
		 rotated90 = false;
		 }

		 List<Lightable> lights = room.getLights();


		for (int i = 0; i < tiles.length; i++) {
			for (int j = 0; j < tiles[i].length; j++) {
				int x = i * width;
				int y = j * height;
				Point point = twoDToIso(new Point(x, y));
				drawTile(g, point, tiles[i][j], lights);
				drawItems(g, point, tiles[i][j], lights);
				drawCharacter(g, point, tiles[i][j], lights);
			}
		}
	}

	//-----------------------Drawing things in the location
	//-----------------------Everything goes to drawObject() to actually draw

	/**
	 * Takes the name of the class and gets drawObject(...) to draw it.
	 *
	 * @param Point pt
	 * @param String tileName
	 * @param Graphics g
	 * @author Leon North
	 * @param lights
	 * @param tiles
	 */
	private void drawTile(Graphics g, Point pt, Tile2D tile, List<Lightable> lights) {


		//pick the animation number
		int tileNum = 0;
		if (rotated90){
			tileNum = 1;
		}

		String tileName = tile.getClass().getSimpleName();

		// Don't draw Empty Tiles
		if( tile instanceof EmptyTile ){
			return;
		}

		// Toggleable tiles have 2 separate states
		if( tile instanceof Mountable && ((Mountable)tile).isMounted()){

			// Use mounting image
			tileNum += 2;
		}
		if( tile instanceof Toggleable && ((Toggleable)tile).turnedOn()){

			// Use mounting image
			tileNum += 2;
		}

		BufferedImage original = images.get(tileName+tileNum);
		if( original == null ){
			System.err.println("Could not find image: " + (tileName+tileNum) );
		}


		// Draw floor if tile is NOT a floor
		if( tile.getClass() != Floor.class && tile.getClass() != BlankFloor.class){
			drawFloor(g, pt, tile, tileNum%2, lights);
		}

		// Draw tile
		drawObject(g, pt, original, 255);

		// Draw Shading
		boolean tileIsHiddenInShadown = drawShading(g, pt, tile.getPos(), tileName+tileNum, lights) == 255;

		// Don't draw extras if it's hidden
		if( tileIsHiddenInShadown ){
			return;
		}

		// Unshaded Extras
		if( tile instanceof Powerable ){
			Cell cell = ((Powerable)tile).getCell();
			if( cell.isEmpty()){
				FloatingNoPower.reDraw(g, pt, width, height, offset);
			}
			else{
				drawPowerable(g, pt, ((Powerable)tile), tileNum, original);
			}
		}
		else if( tile instanceof Lockable && ((Lockable)tile).isLocked()){
			FloatingLock.reDraw(g, pt, width, height, offset);
		}
	}

	private void drawPowerable(Graphics g, Point pt, Powerable power, int tileNum, BufferedImage image){
		int x = pt.x+offset.x-(width/2);
		int y = offset.y + pt.y - (height);

		int health = power.getCell().getBatteryLife();
		int red = 255 - (int)(255*(float)health/(float)power.getCell().getMaxBatteryLife());
		int green = (int)(255*((float)health/(float)power.getCell().getMaxBatteryLife()));
		int blue = 0;

		int barWidth = (int) ((width-4));
		int barHeight = (int) (image.getHeight()*0.01);

		g.setColor(new Color(red,green,blue));
		g.fillRect(x,y, (int)(barWidth*((health*1.0)/(power.getCell().getMaxBatteryLife()*1.0))),barHeight);

		g.setColor(Color.black);
		g.drawRect(x,y, barWidth,barHeight);
	}

	private void drawFloor(Graphics g, Point pt, Tile2D tile, int tileNum, List<Lightable> lights) {
		String floorName = "";
		if( tile instanceof BlankFloor ){
			floorName = "BlankFloor";
		}
		else if( tile instanceof Floor ){
			floorName = "Floor";
		}
		else{
			//System.err.println("Could not find Floor image: " + (tile.getClass().getName()+tileNum) );
			return;
		}

		// Draw Floor
		drawObject(g, pt, images.get(floorName + tileNum), 255);

		// Draw Shading
		drawShading(g, pt, tile.getPos(), floorName+tileNum, lights);
	}

	private int drawShading(Graphics graphics, Point pt, Point inPt, String image, List<Lightable> lights) {
		BufferedImage shadedImage = images.get("shading/" + image);

		// More Precise Shading
		/*BufferedImage shadedImage2 = images.get("shading/" + image);
		int r,g,b,a,col;
		for( int x = 0; x < shadedImage2.getWidth(); x++ ){
			for( int y = 0; y < shadedImage2.getHeight(); y++ ){

				r = (shadedImage2.getRGB(x, y) >> 16) & 0x000000FF;
				g = (shadedImage2.getRGB(x, y) >>8 ) & 0x000000FF;
				b = shadedImage2.getRGB(x, y) & 0x000000F;
				//a = shadedImage2.getAlphaRaster().get// alpha (transparency) component 0...255
				col = (255 << 24) | (r << 16) | (g << 8) | b;
				shadedImage2.setRGB(x, y, col);
				//System.out.println(r + "," + g + "," + b);
			}
		}*/



		int alpha = 255;
		double alphaReduction = 0d;
		double dot = 0d;
		for( Lightable light : lights ){

			// Only use lights that are turned on
			if( !light.turnedOn() ){
				continue;
			}

			// Tiles initial Position
			int x = light.getxPos();
			int y = light.getyPos();

			// Multiplyer for color
			double distance = Math.sqrt((x - inPt.x)*(x - inPt.x) + (y - inPt.y)*(y - inPt.y));
			if( distance > light.getRadius()){
				continue;
			}

			// Get average light
			dot = distance/light.getRadius() * 255;

			// Subtract the lighting
			alphaReduction = (light.getBrightness()-dot);


			// Tile is fully visible
			alpha -= (alphaReduction);
			if(alpha <= 0 ){
				break;
			}
		}


		// Alpha 0 = not seen
		alpha = Math.min(255, alpha);
		alpha = Math.max(0, alpha);

		// Draw shading
		drawObject(graphics, pt, shadedImage, alpha);

		// Return alpha
		return alpha;
	}

	/**
	 * Takes the name of the class and gets drawObject(...) to draw it.
	 *
	 * @param Point pt
	 * @param Tile2D tile
	 * @param Graphics g
	 * @author Leon North
	 * @param tiles
	 * @param lights
	 */
	private void drawCharacter(Graphics g, Point pt, Tile2D tile, List<Lightable> lights) {
		if (tile.getAvatar() == null) return;

		Avatar avatar = tile.getAvatar();

		Point avatarOffset = avatarTilePos(tile);

		pt.y-=(height/2);
		pt.x+=avatarOffset.x;
		pt.y+=avatarOffset.y;

		// Get avatar image
		BufferedImage avatarImage = getAvatarImage(avatar);
		drawObject(g,pt,avatarImage, 255);

		// Draw Shading
		drawShading(g, pt, tile.getPos(), getAvatarImageName(avatar), lights);

		//either draw a floating pointer if avatar is current player
		//or draw the name above the avatar
		if (tile.getAvatar().equals(character))
			floatingPointer.reDraw(g, pt, width, height, offset);
		else if( Point.distance(tile.getAvatar().getTileXPos(), tile.getAvatar().getTileYPos(),
				character.getTileXPos(), character.getTileYPos()) < 4.0d ){

			// Name
			int nameWidth = g.getFontMetrics(g.getFont()).stringWidth(avatar.getPlayerName());
			int nameHeight = g.getFontMetrics(g.getFont()).getHeight();
			int x = pt.x+offset.x-(width/4);
			int y = pt.y+offset.y;

			g.setColor(new Color(0,0,0,64));
			g.fillRect(x-2, y-nameHeight, nameWidth+4, nameHeight);

			g.setColor(Color.BLACK);
			g.drawString(avatar.getPlayerName(), x, y);

			// Health
			x = pt.x+offset.x-(width/2);
			y = offset.y + pt.y - (height*2)-25;
			g.setColor(Color.black);
			g.fillRect(x, y, width, nameHeight/2);

			int health = avatar.getCell().getBatteryLife();
			g.setColor(new Color(10,200,10));
			if (health < (501/4.0)){
				g.setColor(new Color(200,10,10));
			}
			g.fillRect(x+2, y+2, (int) ((width-4)*((health*1.0)/(501*1.0))), (nameHeight/2)-4);
		}
	}

	private BufferedImage getAvatarImage(Avatar avatar){
		return images.get(getAvatarImageName(avatar));
	}

	private Direction calculateFacingDirection(Direction originalDirection){
		return Direction.getRotatedDirection(originalDirection, direction);
	}

	private String getAvatarImageName(Avatar avatar){
		//cases:
		//avatar is ai and charging
		//avatar is ai
		//avatar is other player and charging
		//avatar is other player
		//avatar is current player and charging
		//avatar is current player
		if (avatar.getPlayerName().startsWith("ai") && avatar.getCell().isCharging()){ //AI && charging
			return "AvatarB"+calculateFacingDirection(avatar.getFacingDirection())+"Charging"+avatar.getSpriteIndex();
		}
		else if (avatar.getPlayerName().startsWith("ai")){ //AI
			return "AvatarB"+calculateFacingDirection(avatar.getFacingDirection())+""+avatar.getSpriteIndex();
		}
		else if(avatar.equals(character) && avatar.getCell().isCharging()){ //Avatar = current player and is charging
			return "AvatarA"+calculateFacingDirection(avatar.getFacingDirection())+"Charging"+avatar.getSpriteIndex();
		}
		else if(avatar.equals(character) && avatar.getCell().isCharging()){ //Avatar = current player and NOT charging
			return "AvatarA"+calculateFacingDirection(avatar.getFacingDirection())+""+avatar.getSpriteIndex();
		}
		else if(avatar.getCell().isCharging()){ //Avatar != current player and charging
			String facing = otherAvatarFacing(avatar);
			return "AvatarA"+facing+"Charging"+avatar.getSpriteIndex();
		}
		else{//Avatar != current player and NOT charging
			String facing = otherAvatarFacing(avatar);
			return "AvatarA"+facing+""+avatar.getSpriteIndex();
		}
	}

	/**
	 * This calculates the correct facing direction of other avatars relative to the direction
	 * the current player is facing.
	 * @param avatar : other avatar
	 * @return string : the direction that the avatar is facing
	 */
	private String otherAvatarFacing(Avatar avatar) {
		int avatarFacing = Direction.get(avatar.getFacingDirection());          //gets the direction that the avatar is facing
																				  //relative to the direction they are viewing the world

		int myRenderingDirection = Direction.get(direction);                     // the direction the current avatar is facing  relative
																			     //to the rendering direction
		avatarFacing = (avatarFacing - myRenderingDirection);
		if( avatarFacing < 0 ){
			avatarFacing += 4;
		}

		String facing = Direction.get(avatarFacing).toString();
		return facing;
	}

	/**
	 * Takes the name of the class and gets drawObject(...) to draw it.
	 *
	 * @param Point pt
	 * @param Tile2D tile
	 * @param Graphics g
	 * @author Leon North
	 * @param tiles
	 * @param lights
	 */
	private void drawItems(Graphics g, Point pt, Tile2D tile, List<Lightable> lights) {
		if (tile.getItems() == null) return;

		//pick the animation number
		int tileNum = 0;
		if (rotated90){
			tileNum = 1;
		}

		//for each item in the inventory, get the corresponding image and send it to drawObject() to draw it
		for (int i = 0; i < tile.getItems().size(); i++){
			Item item = tile.getItems().get(i);
			String itemName = item.getClass().getSimpleName();

			// Toggleable items have 2 seperate states
			if( item instanceof Wire ){
				int index = ((Wire)item).getSpriteIndex();
				if( tileNum == 0 && index == 0) tileNum = 0;
				else if( tileNum == 0 && index == 1) tileNum = 1;
				else if( tileNum == 1 && index == 0) tileNum = 1;
				else if( tileNum == 1 && index == 1) tileNum = 0;
			}

			if( item instanceof Toggleable && ((Toggleable)item).turnedOn()){
				tileNum += 2;
			}


			BufferedImage image = images.get(itemName+tileNum);
			if( image == null ){
				throw new RuntimeException("Unknown Item image: " + itemName+tileNum);
			}

			// Draw item
			drawObject(g,pt,image, 255);

			// Draw Shading
			// Get alpha level of item
			boolean tileIsHiddenInShadow = drawShading(g, pt, tile.getPos(), itemName+tileNum, lights) == 255;

			// Don't draw extras if it's hidden
			if( tileIsHiddenInShadow ){
				return;
			}

			if( item instanceof Powerable ){
				Cell cell = ((Powerable)item).getCell();

				if( item instanceof Water ){
					if( !cell.isEmpty() ){
						drawSpark(g, pt, width, height, offset, tileNum);
					}
				}
				else{
					if( cell.isEmpty()){
						FloatingNoPower.reDraw(g, pt, width, height, offset);
					}
					else{
						drawPowerable(g, pt, ((Powerable)item), tileNum, images.get(itemName+tileNum));
					}
				}
			}
		}
	}

	private void drawSpark(Graphics g, Point pt, int width2, int height2,Point offset2, int tileNum) {
		BufferedImage img = MakeImageMap.images.get("Spark" + tileNum);
		if( img == null ){
			throw new RuntimeException("Unknown Item image: " + "Spark" + tileNum);
		}

		int imgHeight = ((int) img.getHeight(null) / 250);

		g.drawImage(img, offset.x + pt.x - width, offset.y + pt.y
				- ((width * imgHeight)), width * 2, height * imgHeight, null);

	}

	/**
	 * Generic drawing method that gets called to draw Tile2D, GameCharacter,
	 * Item. It will draw the image given to it at the point given to the graphics given.
	 *
	 * @param Graphics g
	 * @param Point pt
	 * @param java.net.URL imageURL
	 * @author Leon North
	 */
	private void drawObject(Graphics g, Point pt, BufferedImage img, float alpha) {
		//int imgHeight = ((int) img.getHeight(null) / 250);
		Rectangle2D dim = getDimensions(pt.x,pt.y, img.getHeight(), img.getHeight());


		if( alpha > 0 ){
			Graphics2D g2 = (Graphics2D)g;

			float converted = alpha/255;
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, converted);
			g2.setComposite(ac);
			g.drawImage(img, (int)dim.getMinX(), (int)dim.getMinY(), (int)dim.getWidth(), (int)dim.getHeight(), null);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		}
	}

	private Rectangle2D getDimensions(int x, int y, int w, int h){
		w /= 250;
		h /= 250;
		return new Rectangle2D.Double(offset.x + x - width, offset.y + y
				- ((width * h)), width * 2, height * h);
	}

	/**
	 * Draws a night time mask over the map depending on system time.
	 *
	 * @param g: Graphics object
	 * @author Leon North
	 */
	private void drawNight(Graphics g) {
		long millis = System.currentTimeMillis();
		int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
		int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
		int secondsCycle = seconds % 2;
		int minuteCycle = minutes % 2;

		Graphics2D g2d = (Graphics2D)g;

		BufferedImage img = images.get("Night");

		//check if the avatar is carying a light, if they are, use a lighter image as a night mask
		for(int i = 0; i < character.getInventory().size(); i++){
			if(character.getInventory().get(i) instanceof Light){
				img = images.get("NightLight");
				break;
			}
		}

		//make image transparent varying to the time of day (in meatspace).
		//1 second transition to night, night for 59 seconds, 1 second
		//transition to day, day for 59 seconds, rinse repeat.
		//pick the alpha depending on the previous rule.
		float alpha = 0F;
		if (minuteCycle == 1){
			if (seconds == 0){alpha = (millis %1000)/1000F;}
			else{alpha = 1.0F;}
		}
		else if (minuteCycle == 0){
			if (seconds == 0){alpha = 1.0F -(millis %1000)/1000F;}
			else{alpha = 0.0F;}
		}

		//set an alpha composite on the buffered image to make it transparent.
		int rule = AlphaComposite.SRC_OVER;
		AlphaComposite ac = java.awt.AlphaComposite.getInstance(rule, alpha);
		g2d.setComposite(ac);
		g2d.drawImage(img,0,0,(int)panel.getWidth(), (int)panel.getHeight(), null);
		g2d.setComposite(java.awt.AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
	}

	//-----------------------------------Utilities

	/**
	 * Returns the point within the tile that the avatar is standing
	 * @param tile: The tile the avatar is standing on
	 * @return Point: position the avatar is standing on within the tile
	 * @author Leon North
	 */
	public Point avatarTilePos(Tile2D tile){

		double stepSize = width/100.0; // 100 is the number of positions across a tile.
		Avatar avatar = tile.getAvatar();

		Point avatarPoint = new Point((int)avatar.getTileXPos(), (int)avatar.getTileYPos());

		//rotate the ppoint around to the same direction the location is
		for(int i = 0; i < Direction.get(direction)*3; i++){
			avatarPoint = new Point((100-avatarPoint.y),(avatarPoint.x));
	    }

		//convert point to isometric view
		avatarPoint = twoDToIso(avatarPoint);

		//scale the change to fit with the drawn tile
		avatarPoint.x = (int)(avatarPoint.x * stepSize);
		avatarPoint.y = (int)(avatarPoint.y * stepSize);

		return avatarPoint;
	}

	/**
	 * converts the coordinates to isometric
	 *
	 * @param Point point
	 * @return Point tempPt
	 * @author Leon North
	 */
	private Point twoDToIso(Point point) {
		return twoDToIso(point.x,point.y);
	}

	/**
	 * converts the coordinates to isometric
	 *
	 * @param Point point
	 * @return Point tempPt
	 * @author Leon North
	 */
	public Point twoDToIso(int x, int y) {
		//rotates points by -45 degrees, squeezes the y to 50%
		Point tempPt = new Point(0, 0);
		tempPt.x = x - y;
		tempPt.y = (x + y) / 2;
		return (tempPt);
	}

	/**
	 * converts the coordinates to isometric
	 *
	 * @param Point point
	 * @return Point tempPt
	 * @author James Veugelaers
	 */
	public Point isoToTwoD(int x, int y) {
		//rotates points by 45 degrees, squeezes the y to 50%
		x *= 2;
		x -= y;

		y += x;

		Point tempPt = new Point(0, 0);
		tempPt.y = y / (height*2);
		tempPt.x = x / width;

		return (tempPt);
	}

	public Point getConvertedPoint(int x, int y){
		return isoToTwoD(x,y);
	}

	/**
	 * Rotates the given 2d array 90 degrees
	 *
	 * @param Tile2D [][] tiles
	 * @return Tile2D[][] newTiles
	 * @author Leon North
	 */
	private Tile2D[][] rotate90(Tile2D[][] tiles) {

		//makes a new 2d array, takes each object and puts it into the corresponding
		//position in the new array rotated by 90 degrees
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

	/**
	 * Makes the offset that everything needs to be drawn by to put the current
	 * players avatar in the centre of the screen.
	 *
	 * @param direction
	 * @param room
	 * @author Leon North
	 */
	private void calibrateOffset(Direction direction, Room room) {

		Point tile = null;

		//copy the tiles
		Tile2D[][] tiles= room.getTiles().clone();

		//rotate the copied tiles to the correct direction we are facing
		for (int i = 0; i < Direction.get(direction)+3; i++){//the 3 is to solve a bug that was not resolved but plugged
			 tiles = rotate90(tiles);
		}

		//travers the 2d array to find the tile our avatar is in.
		for(int i = 0; i < tiles[0].length; i++){
			for(int j = 0; j < tiles.length; j++){
				if(tiles[j][i].getAvatar() != null && tiles[j][i].getAvatar().equals(character)){
					tile = new Point(j,i);
					break;
				}
			}
		}

		if( tile == null ){
			System.out.println("Couldn't find Avatar DrawWorld.calibrateOffset");
			return;
		}

		//find the offset for where in the tile the avatar is
		Point avatarOffset = avatarTilePos(tiles[tile.x][tile.y]);

		//scale the offset by the panel width
		tile.x = (tile.x * width);
		tile.y = (tile.y * height);

		//convert the point from 2d to isometric
		tile = twoDToIso(tile);

		//add the avatar offset
		tile.x = tile.x + avatarOffset.x;
		tile.y = tile.y + avatarOffset.y;

		//center the offset
		tile.x = panel.getWidth() - (tile.x + (panel.getWidth() / 2));
		tile.y = (panel.getHeight()/3) - (tile.y - (panel.getHeight() / 5));

		offset = tile;
	}

	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
}
