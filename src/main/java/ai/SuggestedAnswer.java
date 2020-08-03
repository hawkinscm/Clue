package ai;

import model.Card;
import model.Player;
import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Represents a AICard answer given by another player to answer a suggestion.  The AICard used to answer will be one of three.
 */
public class SuggestedAnswer {

	private Player answerer;
	private Room room;
	private Suspect suspect;
	private Weapon weapon;
	
	/**
	 * Creates a new Suggested Answer with the player who answered and the three cards he may have shown as the answer.
	 * @param answerer player who answered
	 * @param room room he may have answered with
	 * @param suspect suspect he may have answered with
	 * @param weapon weapon he may have answered with
	 */
	public SuggestedAnswer(Player answerer, Room room, Suspect suspect, Weapon weapon) {
		this.answerer = answerer;
		this.room = room;
		this.suspect = suspect;
		this.weapon = weapon;
	}
	
	/**
	 * Returns the answerer of the suggestion.
	 * @return the answerer of the suggestion
	 */
	public Player getAnswerer() {
		return answerer;
	}
	
	/**
	 * Returns the room the answerer may have answered with.
	 * @return the room the answerer may have answered with
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * Returns the suspect the answerer may have answered with.
	 * @return the suspect the answerer may have answered with
	 */
	public Suspect getSuspect() {
		return suspect;
	}
	
	/**
	 * Returns the weapon the answerer may have answered with.
	 * @return the weapon the answerer may have answered with
	 */
	public Weapon getWeapon() {
		return weapon;
	}
	
	/**
	 * Determines if the given card owned the given player helps determine which card was used to answer the suggestion.
	 * @param player the owner of the given card
	 * @param card card now known to be owned by the given player
	 */
	public void presentNewFact(Player player, Card card, boolean hasCard) {
		if ((!hasCard && player == answerer) || (hasCard && player != answerer)) {
			if (player == answerer) {
				if (room != null && card.matches(room))
					room = null;
				else if (suspect != null && card.matches(suspect))
					suspect = null;
				else if (weapon != null && card.matches(weapon))
					weapon = null;
			}
		}
		else if (hasCard) {
			if (room != null && card.matches(room)) {
				suspect = null;
				weapon = null;
			}
			else if (suspect != null && card.matches(suspect)) {
				room = null;
				weapon = null;
			}
			else if (weapon != null && card.matches(weapon)) {
				room = null;
				suspect = null;
			}
		}
	}
	
	/**
	 * Returns true if it is known which card the player answered with.
	 * @return true if is is known which card the player answered with
	 */
	public boolean isAnswerKnown() {
		int possibles = 3;
		if (room == null) possibles--;
		if (suspect == null) possibles--;
		if (weapon == null) possibles--;
		return (possibles == 1);
	}
	
	/**
	 * Returns the card the player answered with.
	 * @return the card the player answered with, or null if not yet known.
	 */
	public Card getAnsweringCard() {
		if (!isAnswerKnown())
			return null;
		
		if (room != null)
			return new Card(room);
		else if (suspect != null)
			return new Card(suspect);
		else
			return new Card(weapon);
	}
}
