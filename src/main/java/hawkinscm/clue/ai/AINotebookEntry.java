package hawkinscm.clue.ai;

import java.util.ArrayList;
import java.util.List;

import hawkinscm.clue.model.Player;
import hawkinscm.clue.model.PlayerType;
import hawkinscm.clue.model.Suspect;

/**
 * Represents an entry in the notebook with all the possible Players who may own the associated card.
 */
public class AINotebookEntry {

	public static final Player CASE_FILE_PLAYER = new Player(new Suspect(99, "CASE_FILE", null), PlayerType.NON_PLAYER);
	
	private List<Player> possibleOwners;
	private Player owner;
	
	/**
	 * Creates a new notebook entry with the given players as all possible owners.
	 * @param possibleOwners all possible owners of the associated card
	 */
	public AINotebookEntry(List<Player> possibleOwners) {
		this.possibleOwners = new ArrayList<Player>(possibleOwners);
		this.possibleOwners.add(CASE_FILE_PLAYER);
		owner = null;
	}
	
	/**
	 * Sets the given player as the owner of the associated card.
	 * @param owner the owner of the associated card
	 */
	public void setOwner(Player owner) {
		this.owner = owner;
	}
	
	/**
	 * Returns whether or not the given player is a possible owner of the associated card.
	 * @param player possible owner to check
	 * @return true if the player is a possible owner of the associated card; false otherwise
	 */
	public boolean isPossibleOwner(Player player) {
		return possibleOwners.contains(player);
	}
	
	/**
	 * Removes the player as a possible owner of the associated card.
	 * @param player player to remove
	 * @return true if the player was removed from the list; false if he wasn't in the list
	 */
	public boolean removePossibleOwner(Player player) {
		return possibleOwners.remove(player);
	}
	
	/**
	 * Whether or not this notebook entry is complete and the owner is known.
	 * @return true if the owner is known; false if still under investigation
	 */
	public boolean isOwnerKnown() {
		if (owner == null && possibleOwners.size() == 1)
			owner = possibleOwners.get(0);
			
		return (owner == null);
	}
	
	/**
	 * Returns the owner of the associated card.
	 * @return the owner of the associated card
	 */
	public Player getOwner() {
		if (owner == null && possibleOwners.size() == 1)
			owner = possibleOwners.get(0);
		
		return owner;
	}
	
	/**
	 * Returns whether or not the associated card is the one to accuse.
	 * @return true if the associated card is owned by the CASE_FILE_PLAYER and is the one to accuse.
	 */
	public boolean isCardToAccuse() {
		return (getOwner() == CASE_FILE_PLAYER);
	}
}
