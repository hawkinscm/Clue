package gui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Helper for creating, storing, and altering images
 */
public class ImageHelper {
	
	/**
	 * Enumeration of common CLUE resizable image types.
	 */
	public enum ImageType {
		ROOM,
		DOOR,
		REMOVE_TILE,
		PASSAGE,
		FOOTSTEPS;
		
		public int asInt() {
			switch (this) {
				case ROOM :        return 0;
				case DOOR :        return 1;
				case REMOVE_TILE : return 2;
				case PASSAGE :     return 3;
				case FOOTSTEPS :   return 4;
			}
			return 0;
		}
		
		public String getTypeName() {
			switch (this) {
				case ROOM :        return "room";
				case DOOR :        return "door";
				case REMOVE_TILE : return "remove_tile";
				case PASSAGE :     return "passage";
				case FOOTSTEPS :   return "footsteps";
			}
			return null;
		}
		
		public static int size() {
			return values().length;
		}
		
		public static ImageType getType(int intValue) {
			if (intValue < 0 || intValue >= size())
				return null;
			
			return values()[intValue];
		}
	}
	
	/**
	 * Enumeration of image sizes.
	 */
	public enum ImageSize {
		LARGE,
		MEDIUM,
		SMALL;
		
		public int asInt() {
			switch (this) {
				case LARGE :        return 0;
				case MEDIUM :       return 1;
				case SMALL : 		return 2;
			}
			return 0;
		}
		
		public static int size() {
			return values().length;
		}
	}
	
	private static final String imagePath = "images/";
	
	private static ImageIcon mglassIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "mglass.png", Color.WHITE));
	private static ImageIcon cameraIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "camera.jpg", Color.WHITE));
	private static ImageIcon undoIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "undo.png", Color.WHITE));
	private static ImageIcon redoIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "redo.png", Color.WHITE));
	private static ImageIcon diskIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "disk.png", Color.WHITE));
	private static ImageIcon checkIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "check.png", Color.WHITE));
	private static ImageIcon eyeIcon = new ImageIcon(makeColorTransparent(ClueGUI.class, imagePath + "eye.png", Color.WHITE));
	
	private static ImageIcon emptyCrossboxIcon = new ImageIcon(getImage(ClueGUI.class, imagePath + "crossbox_empty.png"));
	private static ImageIcon crossboxIcon = new ImageIcon(getImage(ClueGUI.class, imagePath + "crossbox.png"));
	
	private static ImageIcon[] diceIcons = new ImageIcon[6];
	static {
		for (int die = 1; die <= 6; die++)
			diceIcons[die - 1] = new ImageIcon(getImage(ClueGUI.class, imagePath + "die" + die + ".png"));
	}
	
	private static ImageIcon genericRoomIcon = new ImageIcon(getImage(ClueGUI.class, imagePath + "generic_room.png"));
	private static ImageIcon genericSuspectIcon = new ImageIcon(getImage(ClueGUI.class, imagePath + "generic_suspect.jpg"));
	private static ImageIcon genericWeaponIcon = new ImageIcon(getImage(ClueGUI.class, imagePath + "generic_weapon.jpg"));
	
	private static ImageIcon[][] typedAndSizedIcons = new ImageIcon[ImageType.size()][ImageSize.size()];
	private static Cursor[][] typedAndSizedCursors = new Cursor[ImageType.size()][ImageSize.size()];
	
	static {		
		for (ImageType type : ImageType.values()) {
			int typeInt = type.asInt();
			String filenameStart = imagePath + type.getTypeName();
			ImageIcon standard = new ImageIcon(makeColorTransparent(ClueGUI.class, filenameStart + ".png", Color.WHITE));
			ImageIcon small = new ImageIcon(makeColorTransparent(ClueGUI.class, filenameStart + "_small.png", Color.WHITE));
			typedAndSizedIcons[typeInt][ImageSize.LARGE.asInt()] = new ImageIcon(standard.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
			typedAndSizedIcons[typeInt][ImageSize.MEDIUM.asInt()] = standard;
			typedAndSizedIcons[typeInt][ImageSize.SMALL.asInt()] = small;
			
			typedAndSizedCursors[typeInt][ImageSize.LARGE.asInt()] = createCursor(standard, true);
			typedAndSizedCursors[typeInt][ImageSize.MEDIUM.asInt()] = createCursor(standard, false);
			typedAndSizedCursors[typeInt][ImageSize.SMALL.asInt()] = createCursor(small, false);
		}
	}
	
	/**
	 * Method that will trigger the building of all static images.  
	 * This can be done at the beginning of the program to specifically catch any image build areas from the start.
	 */
	public static void loadImages() {}
	
	/**
	 * Returns the magnifying glass image icon.
	 * @return the magnifying glass image icon
	 */
	public static ImageIcon getMGlass() {
		return mglassIcon;
	}
	
	/**
	 * Returns the camera image icon.
	 * @return the camera image icon
	 */
	public static ImageIcon getCamera() {
		return cameraIcon;
	}
	
	/**
	 * Returns the undo image icon.
	 * @return the undo image icon
	 */
	public static ImageIcon getUndo() {
		return undoIcon;
	}

	/**
	 * Returns the redo image icon.
	 * @return the redo image icon
	 */
	public static ImageIcon getRedo() {
		return redoIcon;
	}

	/**
	 * Returns the disk image icon.
	 * @return the disk image icon
	 */
	public static ImageIcon getDisk() {
		return diskIcon;
	}

	/**
	 * Returns the check image icon.
	 * @return the check image icon
	 */
	public static ImageIcon getCheck() {
		return checkIcon;
	}

	/**
	 * Returns the eye image icon.
	 * @return the eye image icon
	 */
	public static ImageIcon getEye() {
		return eyeIcon;
	}

	/**
	 * Returns the empty crossbox (empty checkbox) image icon.
	 * @return the empty crossbox image icon
	 */
	public static ImageIcon getEmptyCrossbox() {
		return emptyCrossboxIcon;
	}

	/**
	 * Returns the crossbox (checkbox that has an X rather than a check) image icon.
	 * @return the crossbox image icon
	 */
	public static ImageIcon getCrossbox() {
		return crossboxIcon;
	}

	/**
	 * Returns a die (dice) image icon of the given number.
	 * @return a die (dice) image icon of the given number
	 */
	public static ImageIcon getDie(int die) {
		return diceIcons[die - 1];
	}
	
	/**
	 * Returns the generic room image icon - default icon if room image is invalid or not set.
	 * @return the generic room image icon
	 */
	public static ImageIcon getGenericRoom() {
		return genericRoomIcon;
	}
	
	/**
	 * Returns the generic suspect image icon - default icon if suspect image is invalid or not set.
	 * @return the generic suspect image icon
	 */
	public static ImageIcon getGenericSuspect() {
		return genericSuspectIcon;
	}
	
	/**
	 * Returns the generic weapon image icon - default icon if weapon image is invalid or not set.
	 * @return the generic weapon image icon
	 */
	public static ImageIcon getGenericWeapon() {
		return genericWeaponIcon;
	}
	
	/**
	 * Returns an image icon of the given type and size. 
	 * @param type type of image icon to return
	 * @param size size of image icon to return
	 * @return an image icon of the given type and size
	 */
	public static ImageIcon getIcon(ImageType type, ImageSize size) {
		return typedAndSizedIcons[type.asInt()][size.asInt()];
	}
	
	/**
	 * Returns the type of the given icon or null if isn't an icon of a standard type.
	 * @param icon image icon to get the type of
	 * @return the type of the given icon or null if isn't an icon of a standard type
	 */
	public static ImageType getIconType(Icon icon) {
		for (int typeInt = 0; typeInt < ImageType.size(); typeInt++)
			for (int sizeInt = 0; sizeInt < ImageSize.size(); sizeInt++)
				if (icon == typedAndSizedIcons[typeInt][sizeInt])
					return ImageType.getType(typeInt);
		
		return null;
	}
	
	/**
	 * Returns the image icon at the given filename.
	 * @param filename name of the image file
	 * @return the image icon at the given filename
	 */
	public static ImageIcon getIcon(String filename) {
		try {
			File file = new File(imagePath + filename);
			if (!file.exists())
				return null;

			return new ImageIcon(imagePath + filename);
		}
		catch (Exception e) { return null; }
	}
	
	/**
	 * Returns a buffered image of the given internal file.
	 * @param guiClass internal GUI class that is used to specify the location of the image
	 * @param filename name of the internal image file
	 * @return a buffered image of the given internal file
	 */
	public static BufferedImage getImage(Class<?> guiClass, String filename) {
		try { 
        	return ImageIO.read(guiClass.getResource(filename));   
        } 
        catch (Exception e) { return null; }
	}
	
	/**
	 * Returns an image icon at the given filename with the specified color made transparent.
	 * @param filename name of the image file
	 * @param transparentColor color to make transparent
	 * @return an image icon at the given filename with the specified color made transparent
	 */
	public static ImageIcon getTransparentIcon(String filename, Color transparentColor) {
		try {
			return new ImageIcon(makeColorTransparent(imagePath + filename, transparentColor));
		}
		catch (Exception e) { return null; }
	}
	
	public static Cursor getCursor(ImageType type, ImageSize size) {
		return typedAndSizedCursors[type.asInt()][size.asInt()];
	}
	
	/**
	 * Returns a buffered image at the given filename with the specified color made transparent.
	 * @param filename name of the image file
	 * @param color color to make transparent
	 * @return a buffered image at the given filename with the specified color made transparent
	 */
	public static BufferedImage makeColorTransparent(String filename, Color color) {
        try { 
        	return makeColorTransparent(ImageIO.read(new File(filename)), color);   
        } 
        catch (Exception e) { return null; }
	}
	
	/**
	 * Returns a buffered image of the given internal file with the specified color made transparent.
	 * @param guiClass internal GUI class that is used to specify the location of the image
	 * @param filename name of the internal image file
	 * @param color color to make transparent
	 * @return a buffered image of the given internal file with the specified color made transparent
	 */
	public static BufferedImage makeColorTransparent(Class<?> guiClass, String filename, Color color) {
        try { 
        	return makeColorTransparent(ImageIO.read(guiClass.getResource(filename)), color);   
        } 
        catch (Exception e) { Messenger.error(e.getMessage(), filename); return null; } 
	}
	
	/**
	 * Returns the given buffered image with the specified color made transparent.
	 * @param image buffered image to copy and alter
	 * @param color color to make transparent
	 * @return the given buffered image with the specified color made transparent
	 */
	public static BufferedImage makeColorTransparent(BufferedImage image, Color color) {        
        final int width = image.getWidth();
        final int height = image.getHeight();
		BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = transparentImage.createGraphics();   
		g.setComposite(AlphaComposite.Src);   
		g.drawImage(image, null, 0, 0);   
		g.dispose();   
		for(int i = 0; i < height; i++) {   
			for(int j = 0; j < width; j++) {   
				if(transparentImage.getRGB(j, i) == Color.WHITE.getRGB()) {   
					transparentImage.setRGB(j, i, 0x8F1C1C);   
				}   
			}   
		}
		return transparentImage;
	}
	
	/**
	 * Rotates an image by the given degree (clockwise) and returns it.
	 * @param icon image icon to copy, rotate, and return
	 * @param rotateAngle the degree/angle to rotate by (clockwise)
	 * @return an image by the given degree and returns it
	 */
	public static ImageIcon rotateIcon(ImageIcon icon, int rotateAngle) {
		final int width = icon.getIconWidth();
        final int height = icon.getIconHeight();
		BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = rotatedImage.createGraphics();   
        g.rotate(Math.toRadians(rotateAngle), width/2.0, height/2.0);
        g.drawImage(icon.getImage(), 0, 0, null);
        return new ImageIcon(rotatedImage);
	}
	
	/**
	 * Creates and returns a cursor from the given image.
	 * @param icon image icon to return as a cursor
	 * @param stretchIcon whether or not the image icon should be stretched to fill a 32x32 cursor image or if it should leave it the same size in the cursor's upper-left corner. 
	 * @return a cursor from the given image
	 */
	public static Cursor createCursor(ImageIcon icon, boolean stretchIcon) {
		Image cursorImage;
		if (stretchIcon)
			cursorImage = icon.getImage();
		else {
			cursorImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB); 
			Graphics g = cursorImage.getGraphics(); 
			g.drawImage(icon.getImage(), 0, 0, null);
		}
		
		int iconMiddle = icon.getIconWidth() / (stretchIcon ? 1 : 2);
		Point cursorHotspot = new Point(iconMiddle, iconMiddle);
		return Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, cursorHotspot, null);
	}
}
