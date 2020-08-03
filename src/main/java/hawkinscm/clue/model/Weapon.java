package hawkinscm.clue.model;

/**
 * Represents a CLUE weapon.
 */
public class Weapon {	
	private int id;
	private String name;
	private String pictureName;
	
	/**
	 * Creates a new Weapon
	 * @param id id of the weapon to set
	 * @param name name of the weapon to set
	 */
	public Weapon(int id, String name) {
		setId(id);
		setName(name);
		pictureName = null;
	}
	
	/**
	 * Copy Constructor.
	 * @param weapon weapon to copy
	 */
	public Weapon(Weapon weapon) {
		setId(weapon.id);
		setName(weapon.name);
		pictureName = weapon.pictureName;
	}
	
	/**
	 * Returns the weapon's id.
	 * @return the weapon's id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the weapon's id.
	 * @param id id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the weapon's name.
	 * @return the weapon's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the weapon's name.
	 * @param name name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the filename of the image to display for this weapon.
	 * @return the filename of the image to display for this weapon
	 */
	public String getPictureName() {
		return pictureName;
	}
	
	/**
	 * Sets the filename of the image to display for this weapon.
	 * @param pictureName the filename of the image to display for this weapon
	 */
	public void setPictureName(String pictureName) {
		this.pictureName = pictureName;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Weapon))
			return false;
		
		if (this == obj)
			return true;
		
		Weapon that = (Weapon) obj;
		return (this.id == that.id && 
				this.name.equals(that.name) && 
				((this.pictureName == null) ? that.pictureName == null : this.pictureName.equals(that.pictureName)));
	}
	
	@Override
	public String toString() {
		return name;
	}
}
