package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Represents a CLUE player.  
 */
public class Player extends Observable {

	private Suspect suspect;
	private PlayerType playerType;
	
	private boolean failedAccuse;
	
	private List<Card> cards;
	
	
	/**
	 * Creates a new CLUE player with a name and player type.
	 * @param suspect suspected player
	 * @param type type of player
	 */
	public Player(Suspect suspect, PlayerType type) {
		this.suspect = suspect;
		playerType = type;
		
		failedAccuse = false;
		
		cards = new LinkedList<>();
	}
	
	@Override
	public String toString() {
		return suspect.getName();
	}
	
	/**
	 * Returns the player's name.
	 * @return the player's name
	 */
	public String getName() {
		return suspect.getName();
	}
	
	/**
	 * Returns the player's type.
	 * @return returns the player's type
	 */
	public PlayerType getPlayerType() {
		return playerType;
	}
	
	/**
	 * Returns the unique id of the player.
	 * @return the unique id of the player
	 */
	public int getId() {
		return suspect.getId();
	}
	
	/**
	 * Sets the player's type to the given type.
	 * @param type player type to set
	 */
	public void setPlayerType(PlayerType type) {
		if (playerType == type)
			return;
		
		playerType = type;
		setChanged();
		notifyObservers(type);
	}
	
	/**
	 * Returns whether or not the player is a computer.
	 * @return true if the player is a computer; false, otherwise
	 */
	public boolean isComputer() {
		int todo; // AI
		/*if (playerType == PlayerType.COMPUTER_EASY)
			return true;
		else if (playerType == PlayerType.COMPUTER_MEDIUM)
			return true;
		else if (playerType == PlayerType.COMPUTER_HARD)
			return true;*/
		
		return false;
	}
	
	/**
	 * Returns whether or not a player will take normal turns.
	 * @return true if the player will take normal turns; false if the player takes no turns or only takes turns in disproving other players' suggestions.
	 */
	public boolean isNoTurnPlayer() {
		return (playerType == PlayerType.ALIBI_ONLY || playerType == PlayerType.NON_PLAYER || failedAccuse);
	}
	
	/**
	 * Return whether or not the given player has cards.
	 * @return true if the given player has cards; false otherwise
	 */
	public boolean isCardHoldingPlayer() {
		return (playerType != PlayerType.NON_PLAYER);
	}
	
	/**
	 * Marks that this player made an accusation and failed to guess it correctly.
	 */
	public void failedAccuse() {
		failedAccuse = true;
	}

	/**
	 * Sets the player's cards.
	 * @param cards card to set
	 */
	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	/**
	 * Returns the player's cards.
	 * @return the player's cards
	 */
	public List<Card> getCards() {
		return cards;
	}
}
