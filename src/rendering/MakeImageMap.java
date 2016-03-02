package rendering;

import gameLogic.Avatar;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/**
 * Makes a map of all the images in the game String -> Buffered image
 *
 * @author Leon North
 *
 */
public class MakeImageMap {

	static Map<String, BufferedImage> images = new HashMap<String, BufferedImage>();
	static int avatarAnimations = 4;
	static int tileAnimations = 2;
	static int directions = 4;
	static int pointerAnimations = 6;
	static int lockAnimations = 5;
	static int noPowerAnimations = 6; // TOTAL ANIMATIONS
	static int itemAnimations = 2;

	public MakeImageMap() {

	}

	/**
	 * Makes a map of all the images in the game String -> Buffered image
	 * and returns it
	 *
	 * @return map of String -> BufferedImage
	 * @author Leon North
	 */
	public static Map<String, BufferedImage> makeMap(){
		//Avatars
		for(int j = 0; j < directions; j++){
			for(int i = 0; i < avatarAnimations; i++){
				//Avatar A
				addImageToMap("AvatarA"+Direction.get(j)+""+i);
				//Avatar A Charging
				addImageToMap("AvatarA"+Direction.get(j)+"Charging"+i);

				//Avatar B
				addImageToMap("AvatarB"+Direction.get(j)+""+i);
				//Avatar B Charging
				addImageToMap("AvatarB"+Direction.get(j)+"Charging"+i);
			}
		}
		//Tiles
		for(int i = 0; i < tileAnimations; i++){
			//wall
			addImageToMap("Wall"+i);

			//floor
			addImageToMap("Floor"+i);

			//blankfloor
			addImageToMap("BlankFloor"+i);

			//door
			addImageToMap("Door"+i);

			//Column
			addImageToMap("Column"+i);

			//doors
			addImageToMap("Door"+i);
			addImageToMap("PoweredDoor"+i);
			addImageToMap("PoweredDoor"+(i+2));
			addImageToMap("RedDoor"+i);
			addImageToMap("YellowDoor"+i);
			addImageToMap("GreenDoor"+i);
			addImageToMap("PurpleDoor"+i);

			//tree
			addImageToMap("Tree"+i);

			//water
			addImageToMap("Water"+i);

			//charger
			addImageToMap("Charger"+i);

			// light mount
			addImageToMap("LightMount"+i);

			// Light mount - Mounted
			addImageToMap("LightMount"+(i+2));

			// Light mount - Mounted Animated
			addImageToMap("LightMount"+(i+4));
		}

		//Pointer
		for(int i = 0; i < pointerAnimations; i++){
			addImageToMap("FloatingPointer"+i);
		}

		//Lock
		for(int i = 0; i < lockAnimations; i++){
			//pointer
			addImageToMap("Lock"+i);
		}

		//Lock
		for(int i = 0; i < noPowerAnimations; i++){
			//pointer
			addImageToMap("NoPower"+i);
		}

		//Spark
		addImageToMap("Spark0");
		addImageToMap("Spark1");

		//Items
		for(int i = 0; i < itemAnimations; i++){

			//Battery
			addImageToMap("LargeBattery"+i);

			//Light
			addImageToMap("Light"+i);
			addImageToMap("Light"+(i+2));

			//Shoes
			addImageToMap("Shoes"+i);

			//Box
			addImageToMap("Box"+i);

			//------Keys------
			//RedKey
			addImageToMap("RedKey"+i);

			//GreenKey
			addImageToMap("GreenKey"+i);

			//PurpleKey
			addImageToMap("PurpleKey"+i);

			//GreenKey
			addImageToMap("YellowKey"+i);

			//------Consumable------

			// AA Battery
			addImageToMap("AABattery"+i);


			//------Decorations------
			addImageToMap("ScrapMetal"+i);
			addImageToMap("Wire"+i);
			addImageToMap("Wire"+(i+2));
		}

		//Night time
		addImageToMap("Night");
		addImageToMap("NightLight");

		return images;
	}

	/**
	 * Takes a string and adds an entry to the map by finding the corresponding
	 * file on disk
	 *
	 * @param name
	 * @author Leon North
	 */
	private static void addImageToMap(String name){

		//System.out.println(name);
		java.net.URL imageURL = MakeImageMap.class.getResource(name + ".png");
		java.net.URL imageURLShading = MakeImageMap.class.getResource("shading/" + name + ".png");

		try {
			BufferedImage img = ImageIO.read(imageURL);
			addToMap(name, img);


			img = ImageIO.read(imageURLShading);
			addToMap("shading/" + name, img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addToMap(String name, BufferedImage img){
		if( img != null ){
			images.put(name, img);
		}
	}
}
