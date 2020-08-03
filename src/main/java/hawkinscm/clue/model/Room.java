package hawkinscm.clue.model;

import java.awt.Color;

/**
 * Represents a CLUE room.
 */
public class Room {
	private final Color DEFAULT_ROOM_COLOR = Color.decode("0xB0E0E6");
	
	private int id;
	private String name;
	private String pictureName;
	private Color transparentPictureColor;
	
	private Color color;
	
	/**
	 * Creates a new Room
	 * @param id id of the room to set
	 * @param name name of the room to set
	 */
	public Room(int id, String name) {
		setId(id);
		setName(name);
		pictureName = null;
		transparentPictureColor = null;
		
		color = DEFAULT_ROOM_COLOR;
	}
	
	/**
	 * Copy Constructor.
	 * @param room room to copy
	 */
	public Room(Room room) {
		setId(room.id);
		setName(room.name);
		pictureName = room.pictureName;
		transparentPictureColor = room.transparentPictureColor;
		
		color = room.color;
	}
	
	/**
	 * Returns the room's id.
	 * @return the room's id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the room's id.
	 * @param id id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the room's name.
	 * @return the room's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the room's name.
	 * @param name name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the filename of the image to display for this room.
	 * @return the filename of the image to display for this room
	 */
	public String getPictureName() {
		return pictureName;
	}
	
	/**
	 * Sets the filename of the image to display for this room.
	 * @param pictureName the filename of the image to display for this room
	 */
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	
	/**
	 * Returns the color that will be transparent on the image of this room.
	 * @return the color that will be transparent on the image of this room
	 */
	public Color getTransparentPictureColor() {
		return transparentPictureColor;
	}
	
	/**
	 * Sets the color that will be transparent on the image of this room
	 * @param transparentPictureColor color that will be transparent on the image of this room
	 */
	public void setTransparentPictureColor(Color transparentPictureColor) {
		this.transparentPictureColor = transparentPictureColor;
	}
	
	/**
	 * Returns the color used for the background of this room.
	 * @return the color used for the background of this room
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the color used for the background of this room
	 * @param color color used for the background of this room
	 */
	public void setColor(Color color) {
		if (color == null)
			color = DEFAULT_ROOM_COLOR;
		this.color = color;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Room))
			return false;
		
		if (this == obj)
			return true;
		
		Room that = (Room) obj;
		return (this.id == that.id && 
				this.name.equals(that.name) &&
				((this.pictureName == null) ? that.pictureName == null : this.pictureName.equals(that.pictureName)) &&
				((this.transparentPictureColor == null) ? that.transparentPictureColor == null : this.transparentPictureColor.equals(that.transparentPictureColor)));
	}
	
	@Override
	public String toString() {
		return name;
	}
}
