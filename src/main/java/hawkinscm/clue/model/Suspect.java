package hawkinscm.clue.model;

import java.awt.Color;

/**
 * Represents a CLUE suspect.
 */
public class Suspect {
	private int id;
	private String name;
	private Color color;
	private String pictureName;

	/**
	 * Creates a new Suspect
	 * @param id id of the suspect to set
	 * @param name name of the suspect to set
	 * @param color color of the suspect to set
	 */
	public Suspect(int id, String name, Color color) {
		setId(id);
		setName(name);
		setColor(color);
		pictureName = null;
	}
	
	/**
	 * Copy Constructor.
	 * @param suspect suspect to copy
	 */
	public Suspect(Suspect suspect) {
		setId(suspect.id);
		setName(suspect.name);
		color = suspect.color;
		pictureName = suspect.pictureName;
	}
	
	/**
	 * Returns the suspect's id.
	 * @return the suspect's id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the suspect's id.
	 * @param id id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the suspect's name.
	 * @return the suspect's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the suspect's name.
	 * @param name name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the suspect's color.
	 * @return the suspect's color
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Sets the suspect's color
	 * @param color color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * Returns the filename of the image to display for this suspect.
	 * @return the filename of the image to display for this suspect
	 */
	public String getPictureName() {
		return pictureName;
	}
	
	/**
	 * Sets the filename of the image to display for this suspect.
	 * @param pictureName the filename of the image to display for this suspect
	 */
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Suspect))
			return false;
		
		if (this == obj)
			return true;
		
		Suspect that = (Suspect) obj;
		return (this.id == that.id && 
				this.name.equals(that.name) &&
				this.color.equals(that.color) && 
				((this.pictureName == null) ? that.pictureName == null : this.pictureName.equals(that.pictureName)));
	}
	
	@Override
	public String toString() {
		return name;
	}
}
