package hawkinscm.clue.model;

/**
 * Represents a CLUE card (Room, Suspect, or Weapon).
 */
public class Card {
	private Room room;
	private Suspect suspect;
	private Weapon weapon;
	
	/**
	 * Creates a new card using the given room.
	 * @param room room to set on the card
	 */
	public Card(Room room) {
		this.room = room;
		this.suspect = null;
		this.weapon = null;
	}
	
	/**
	 * Creates a new card using the given suspect.
	 * @param suspect suspect to set on the card
	 */
	public Card(Suspect suspect) {
		this.room = null;
		this.suspect = suspect;
		this.weapon = null;
	}
	
	/**
	 * Creates a new card using the given weapon.
	 * @param weapon weapon to set on the card
	 */
	public Card(Weapon weapon) {
		this.room = null;
		this.suspect = null;
		this.weapon = weapon;
	}
	
	/**
	 * Returns the room on this card or null if not a room card.
	 * @return the room on this card or null if not a room card
	 */
	public Room getRoom() {
		return room;
	}

	/**
	 * Returns the suspect on this card or null if not a suspect card.
	 * @return the suspect on this card or null if not a suspect card
	 */
	public Suspect getSuspect() {
		return suspect;
	}

	/**
	 * Returns the weapon on this card or null if not a weapon card.
	 * @return the weapon on this card or null if not a weapon card
	 */
	public Weapon getWeapon() {
		return weapon;
	}
	
	/**
	 * Returns whether or not the room matches the element on this card.
	 * @param room room to check
	 * @return true if the given room matches the room on this card; false if not a room card or the card's room does not match the given room.
	 */
	public boolean matches(Room room) {
		return (this.room == room);
	}

	/**
	 * Returns whether or not the suspect matches the element on this card.
	 * @param suspect suspect to check
	 * @return true if the given suspect matches the suspect on this card; false if not a suspect card or the card's suspect does not match the given suspect.
	 */
	public boolean matches(Suspect suspect) {
		return (this.suspect == suspect);
	}

	/**
	 * Returns whether or not the weapon matches the element on this card.
	 * @param weapon weapon to check
	 * @return true if the given weapon matches the weapon on this card; false if not a weapon card or the card's weapon does not match the given weapon.
	 */
	public boolean matches(Weapon weapon) {
		return (this.weapon == weapon);
	}
	
	@Override
	public String toString() {
		String message = "";
		if (room != null)
			message = room.getName();
		else if (suspect != null)
			message = suspect.getName();
		else if (weapon != null)
			message = weapon.getName();
		message += " Card";
		return message;
	}
}
