package gui;

import gui.ImageHelper.ImageSize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

import model.Room;
import model.Suspect;
	
/**
 * Handles the display of a single board tile.
 */
public class DisplayTile extends JButton {
	private static final long serialVersionUID = 1L;
	
	private static boolean mouseDown = false;

	private final Color DEFAULT_TILE_COLOR = Color.decode("0xFFBF00");
	private final Color BACKGROUND_COLOR = Color.decode("" + javax.swing.UIManager.getColor("Panel.background").getRGB());
	
	private Room room;
	private boolean[] hasDoor;
	private boolean isRemovedTile;
	private DisplayTile passageConnection;
	private LinkedList<Suspect> suspects;
	private Direction suspectDirection;
	
	/**
	 * Direction for each of the four main compass points to handle data that needs to point a certain direction or point to a tile side.
	 */
	public enum Direction {
		NORTH,
		EAST,
		SOUTH,
		WEST;
		
		/**
		 * Returns the integer value for this direction.
		 * @return the integer value for this direction
		 */
		public int getOrdinal() {
			switch (this) {
				case NORTH : return 0;
				case EAST  : return 1;
				case SOUTH : return 2;
				case WEST  : return 3;
				default	   : return -1;
			}
		}
		
		/**
		 * Returns the opposite direction of this direction.
		 * @return the opposite direction of this direction
		 */
		public Direction getOpposite() {
			switch (this) {
				case NORTH : return SOUTH;
				case EAST  : return WEST;
				case SOUTH : return NORTH;
				case WEST  : return EAST;
				default	   : return null;
			}
		}
		
		/**
		 * Returns the direction at a 90 degree angle from this direction.
		 * @return the direction at a 90 degree angle from this direction
		 */
		public Direction rotateClockwise() {
			switch (this) {
				case NORTH : return EAST;
				case EAST  : return SOUTH;
				case SOUTH : return WEST;
				case WEST  : return NORTH;
				default	   : return null;
			}
		}
	};
	
	/**
	 * Constructor for a Display Tile.
	 */
	public DisplayTile() {
		room = null;
		hasDoor = new boolean[] {false, false, false, false};
		isRemovedTile = false;
		passageConnection = null;
		suspects = new LinkedList<Suspect>();
		suspectDirection = null;
		
		setBackground(DEFAULT_TILE_COLOR);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setFocusable(false);
		setHorizontalTextPosition(JButton.CENTER);
		setForeground(Color.WHITE);
		
		addMouseListener(new MouseListener() {
			private boolean selected = false;
			private Border restoreBorder;
			private Border connectedRestoreBorder;
			
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {
				if (!mouseDown && isPassage()) {
					restoreBorder = getBorder();
					setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
					connectedRestoreBorder = passageConnection.getBorder(); 
					passageConnection.setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
					selected = true;
				}
			}
			public void mouseExited(MouseEvent e) {
				if (selected) {
					setBorder(restoreBorder);
					passageConnection.setBorder(connectedRestoreBorder);
					selected = false;
				}
			}
			public void mousePressed(MouseEvent e) {
				mouseDown = true;
				if (selected) {
					setBorder(restoreBorder);
					passageConnection.setBorder(connectedRestoreBorder);
					selected = false;
				}
			}
			public void mouseReleased(MouseEvent e) {
				mouseDown = false;
			}
		});
	}
	
	/**
	 * Copy Constructor for a Display Tile.
	 * @param tile tile to copy
	 */
	public DisplayTile(DisplayTile tile) {
		this();
		if (tile.room != null)
			setRoom(tile.room);
		hasDoor = tile.hasDoor;
		if (tile.isRemovedTile)
			setRemoved();
		if (tile.passageConnection != null)
			setPassageConnection(tile.passageConnection, tile.getText().charAt(0));
		if (tile.suspects != null)
			for (Suspect suspect : tile.getSuspects())
				addSuspect(suspect, tile.getSuspectDirection());
	}
	
	@Override
	public void setPreferredSize(Dimension preferredSize) {
		super.setPreferredSize(preferredSize);
				
		if (getIcon() instanceof ImageIcon) {
			if (hasSuspect())
				setIconByType(ImageHelper.ImageType.FOOTSTEPS);
			else
				setIconByType(ImageHelper.getIconType(getIcon()));
		}
	}
	
	/**
	 * Sets the displays icon according to the given type.
	 * @param type type of icon to build and display
	 */
	public void setIconByType(ImageHelper.ImageType type) {
		if (type == null) {
			setIcon(null);
			return;
		}
		
		int side = getPreferredSize().width;
		ImageHelper.ImageSize newSize = (side > 35) ? ImageSize.LARGE : ((side > 19) ? ImageSize.MEDIUM : ImageSize.SMALL);
		setFont(new Font(getFont().getName(), getFont().getStyle(), (newSize == ImageSize.LARGE) ? 16 : 8));
		setIcon(ImageHelper.getIcon(type, newSize));
		if (hasSuspect() && suspectDirection != null)
			setIconDirection(suspectDirection);
	}
	
	/**
	 * Returns whether or not this is an empty tile with nothing on it.
	 * @return true if this is an empty tile (not a room, no doors, not removed, no secret passage, and no suspects); false otherwise
	 */
	public boolean isFreeTile() {
		return (room == null && !hasDoorConnection() && !isRemovedTile && passageConnection == null && !hasSuspect());
	}
	
	/**
	 * Assigns this tile to be part of the given room.
	 * @param room room to assign this tile to
	 */
	public void setRoom(Room room) {
		if (!hasSuspect()) {
			setBackground(room.getColor());
			setToolTipText(room.getId() + ": " + room.getName());
		}
		this.room = room;
	}
	
	/**
	 * Removes this tile from being a room tile.
	 */
	public void removeRoom() {
		if (!hasSuspect()) {
			setBackground(DEFAULT_TILE_COLOR);
			setToolTipText(null);
		}
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		room = null;
	}
	
	/**
	 * Returns whether or not this tile is part of a room.
	 * @return true if this is a room tile; false otherwise
	 */
	public boolean isRoomTile() {
		return (room != null);
	}
	
	/**
	 * Returns the room this tile is part of or null if not assigned to a room.
	 * @return the room this tile is assigned to or null
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * Adds a door to the side of this tile specified.
	 * @param direction side of the tile to add a door to
	 */
	public void addDoor(Direction direction) {
		hasDoor[direction.getOrdinal()] = true;
		repaint();
	}
	
	/**
	 * Removes a door from the side of the tile specified.
	 * @param direction side of the tile to remove the door from.
	 */
	public void removeDoor(Direction direction) {
		if (hasDoor(direction)) {
			hasDoor[direction.getOrdinal()] = false;
			repaint();
		}
	}
	
	/**
	 * Returns whether or not this tile has a door on the specified side.
	 * @param direction side of the tile to query for a door
	 * @return true if the tile has a door in the given direction; false otherwise
	 */
	public boolean hasDoor(Direction direction) {
		return hasDoor[direction.getOrdinal()];
	}
	
	/**
	 * Returns whether or not this tile has any doors on it.
	 * @return true if this tile has a door in any direction; false otherwise
	 */
	public boolean hasDoorConnection() {
		for (int hasDoorIdx = 0; hasDoorIdx < hasDoor.length; hasDoorIdx++)
			if (hasDoor[hasDoorIdx])
				return true;
		
		return false;
	}
	
	/**
	 * Marks this tile as removed and hides it.
	 */
	public void setRemoved() {
		setBackground(BACKGROUND_COLOR);
		setBorder(null);
		isRemovedTile = true;
	}
	
	/**
	 * Unmarks this tile as removed and displays it.
	 */
	public void setUnremoved() {
		setBackground(DEFAULT_TILE_COLOR);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		isRemovedTile = false;
	}
	
	/**
	 * Returns whether or not this tile has been marked as removed.
	 * @return true if this tile has been marked as removed; false otherwise
	 */
	public boolean isRemovedTile() {
		return isRemovedTile;
	}
	
	/**
	 * Adds a passage from this tile to the given tile.
	 * @param tile tile to connect to
	 * @param letter letter assigned to this passage
	 */
	public void setPassageConnection(DisplayTile tile, char letter) {
		if (tile == null) {
			removePassageConnection();
			return;
		}
		
		passageConnection = tile;
		if (!hasSuspect()) {
			setIconByType(ImageHelper.ImageType.PASSAGE);
			setText(Character.toString(letter));
		}
		setName("Secret Passage " + letter);
	}
	
	/**
	 * Removes the passage connection from this tile.
	 */
	public void removePassageConnection() {
		passageConnection = null;
		if (!hasSuspect())
			setIcon(null);
		setText("");
		setName(null);
	}
	
	/**
	 * Returns the tile this passage is connected to or null if not connected.
	 * @return the tile this passage is connected to or null if not a passage
	 */
	public DisplayTile getPassageConnection() {
		return passageConnection;
	}
	
	/**
	 * Returns whether or not this tile has a passage connection.
	 * @return true if this tile has a passage connection; false otherwise
	 */
	public boolean isPassage() {
		return (passageConnection != null);
	}
	
	/**
	 * Returns the letter assigned to this passage connection.
	 * @return the letter assigned to this passage connection or a space if not connected or assigned a letter
	 */
	public char getPassageLetter() {
		String text = getText();
		if (text == null || text.isEmpty()) {
			String name = getName();
			if (name != null && !name.isEmpty())
				return name.charAt(name.length() - 1);
			else
				return ' ';
		}
		
		return text.charAt(0);
	}
	
	/**
	 * Adds the given suspect to this tile. WARNING: Don't add more than four suspects
	 * @param suspect suspect to add
	 * @param direction direction this suspect will face
	 */
	public void addSuspect(Suspect suspect, Direction direction) {
		if (direction == null)
			direction = Direction.NORTH;
		
		if (!suspects.contains(suspect)) {
			suspects.add(suspect);
			suspectDirection = direction;
			if (getText() != null && !getText().isEmpty())
				setText("");
		}
		String suspectNames = "<html>";
		for (Suspect tileSuspect : suspects)
			suspectNames += tileSuspect.getName() + "<br>";
		suspectNames += "</html>";
		setToolTipText(suspectNames);
		setBackground((suspect.getColor() == null) ? DEFAULT_TILE_COLOR : suspect.getColor());
		setIconByType(ImageHelper.ImageType.FOOTSTEPS);
	}
	
	/**
	 * Removes the given suspect from this tile.
	 * @param suspect suspect to remove from this tile
	 */
	public void removeSuspect(Suspect suspect) {
		if (!suspects.contains(suspect))
			return;
		
		if (suspects.size() == 1) {
			removeSuspects();
			return;
		}
		
		suspects.remove(suspect);
		String suspectNames = "<html>";
		for (Suspect tileSuspect : suspects)
			suspectNames += tileSuspect.getName() + "<br>";
		suspectNames += "</html>";
		setToolTipText(suspectNames);
		Suspect lastSuspectAdded = suspects.getLast();
		setBackground((lastSuspectAdded.getColor() == null) ? DEFAULT_TILE_COLOR : lastSuspectAdded.getColor());
	}
	
	/**
	 * Removes all suspects from this tile.
	 */
	public void removeSuspects() {
		suspects.clear();
		suspectDirection = null;
		setToolTipText(isRoomTile() ? (room.getId() + ": " + room.getName()) : null);
		setBackground(isRoomTile() ? room.getColor() : DEFAULT_TILE_COLOR);
		if (isPassage()) {
			setIconByType(ImageHelper.ImageType.PASSAGE);
			setText(Character.toString(getName().charAt(getName().length() - 1)));
		}
		else
			setIcon(null);
	}
	
	/**
	 * Returns whether or not this tile has any suspects on it.
	 * @return true if this tile has at least one suspect; false otherwise
	 */
	public boolean hasSuspect() {
		return (!suspects.isEmpty());
	}
	
	/**
	 * Returns the first suspect found on this display tile.
	 * @return the first suspect found on this display tile or null if none found
	 */
	public Suspect getSuspect() {
		return (suspects.isEmpty()) ? null : suspects.getFirst();
	}
	
	/**
	 * Returns the list of suspects on this tile.
	 * @return the list of suspects on this tile
	 */
	public List<Suspect> getSuspects() {
		return suspects;
	}
	
	/**
	 * Returns the direction the first added suspect is facing.
	 * @return the direction the first added suspect is facing
	 */
	public Direction getSuspectDirection() {
		return suspectDirection;
	}
	
	/**
	 * Turns the direction the suspect is facing by 90 degrees (clockwise).
	 */
	public void rotateSuspectDirection() {
		if (!hasSuspect() || suspectDirection == null || getIcon() == null)
			return;
		
		suspectDirection = suspectDirection.rotateClockwise();
		
		ImageIcon newImage = (ImageIcon) getIcon();
		final int width = newImage.getIconWidth();
        final int height = newImage.getIconHeight();
		BufferedImage rotatedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = rotatedImage.createGraphics();   
        g.rotate(Math.toRadians(90), width/2, height/2);   
        g.drawImage(newImage.getImage(), 0, 0, null);
        setIcon(new ImageIcon(rotatedImage));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(getBackground());
		int erasePosition = isRoomTile() ? 1 : 0;
		int tileSquareSize = getWidth();
		int doorFrameLength = tileSquareSize / 4 + 1;
		int endDoorFrameStart = tileSquareSize - 1 - doorFrameLength;
		int doorWidth = tileSquareSize - 1 - erasePosition;
		if (hasDoor[Direction.NORTH.getOrdinal()])
			g.drawLine(doorFrameLength, erasePosition, endDoorFrameStart, erasePosition);
		if (hasDoor[Direction.SOUTH.getOrdinal()])
			g.drawLine(doorFrameLength, doorWidth, endDoorFrameStart, doorWidth);
		if (hasDoor[Direction.WEST.getOrdinal()])
			g.drawLine(erasePosition, doorFrameLength, erasePosition, endDoorFrameStart);
		if (hasDoor[Direction.EAST.getOrdinal()])
			g.drawLine(doorWidth, doorFrameLength, doorWidth, endDoorFrameStart);
	}

	/**
	 * Sets the display for the icon to face/point the given direction
	 * @param direction direction for the icont to face/point
	 */
    private void setIconDirection(Direction direction) {
		if (direction == Direction.SOUTH)
			setIcon(ImageHelper.rotateIcon((ImageIcon) getIcon(), 180));
		else if (direction == Direction.EAST)
			setIcon(ImageHelper.rotateIcon((ImageIcon) getIcon(), 90));
		else if (direction == Direction.WEST)
			setIcon(ImageHelper.rotateIcon((ImageIcon) getIcon(), 270));
    }  
}