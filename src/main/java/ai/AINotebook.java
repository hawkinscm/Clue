package ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.Card;
import model.Player;
import model.PlayerType;
import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Holds the information for what an AI player knows about the suspected rooms, suspects, and weapons.
 */
public class AINotebook {

	private Player aiPlayer;
	private List<Player> playersWithUnknownCards;
	
	private Room roomToAccuse;
	private Suspect suspectToAccuse;
	private Weapon weaponToAccuse;
	
	private HashMap<Room, AINotebookEntry> roomNotebookEntries;
	private HashMap<Suspect, AINotebookEntry> suspectNotebookEntries;
	private HashMap<Weapon, AINotebookEntry> weaponNotebookEntries;
	private List<SuggestedAnswer> suggestedAnswers;
	
	private LinkedList<NewInformation> newInformationList;
	
	/**
	 * Creates a new AI Notebook.
	 * @param player player that this notebook belongs to
	 * @param players all card-holding players in the current game
	 * @param rooms all rooms in the game
	 * @param suspects all suspects in the game
	 * @param weapons all weapons in the game
	 */
	public AINotebook(Player player, List<Player> players, List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons) {
		aiPlayer = player;
		playersWithUnknownCards = new ArrayList<>(players);
		playersWithUnknownCards.remove(aiPlayer);
		newInformationList = new LinkedList<>();
		
		roomToAccuse = null;
		suspectToAccuse = null;
		weaponToAccuse = null;
		
		roomNotebookEntries = new HashMap<>();
		for (Room room : rooms)
			roomNotebookEntries.put(room, new AINotebookEntry(playersWithUnknownCards));
		
		suspectNotebookEntries = new HashMap<>();
		for (Suspect suspect : suspects)
			suspectNotebookEntries.put(suspect, new AINotebookEntry(playersWithUnknownCards));
		
		weaponNotebookEntries = new HashMap<>();
		for (Weapon weapon : weapons)
			weaponNotebookEntries.put(weapon, new AINotebookEntry(playersWithUnknownCards));
		
		suggestedAnswers = new ArrayList<>();
		
		for (Card card : player.getCards()) {
			if (card.getRoom() != null)
				roomNotebookEntries.get(card.getRoom()).setOwner(aiPlayer);
			else if (card.getSuspect() != null)
				suspectNotebookEntries.get(card.getSuspect()).setOwner(aiPlayer);
			else if (card.getWeapon() != null)
				weaponNotebookEntries.get(card.getWeapon()).setOwner(aiPlayer);
		}
	}
	
	/**
	 * Adds a card whose owner is known to the notebook.
	 * @param owner owner of the card
	 * @param card card whose owner is known
	 */
	public void addKnownCard(Player owner, Card card) {
		if (isAlreadyKnown(card))
			return;
		
		newInformationList.add(new NewInformation(owner, card, true));
		evaluateNewInformation();
	}
	
	/**
	 * Adds a information from a suggestion and the player who answered it to the notebook.
	 * @param suggestedAnswer information about a suggested answer
	 */
	public void addSuggestedAnswer(SuggestedAnswer suggestedAnswer) {
		if (aiPlayer.getPlayerType() != PlayerType.COMPUTER_MEDIUM && aiPlayer.getPlayerType() != PlayerType.COMPUTER_HARD)
			return;
		
		Player answerer = suggestedAnswer.getAnswerer();
		
		AINotebookEntry roomEntry = roomNotebookEntries.get(suggestedAnswer.getRoom());
		if (roomEntry.isOwnerKnown() && roomEntry.getOwner() == answerer)
			return;
		else if (!roomEntry.isPossibleOwner(answerer))
			suggestedAnswer.presentNewFact(answerer, new Card(suggestedAnswer.getRoom()), false);
		
		AINotebookEntry suspectEntry = suspectNotebookEntries.get(suggestedAnswer.getSuspect());
		if (suspectEntry.isOwnerKnown() && suspectEntry.getOwner() == answerer)
			return;
		else if (!suspectEntry.isPossibleOwner(answerer))
			suggestedAnswer.presentNewFact(answerer, new Card(suggestedAnswer.getSuspect()), false);
		
		AINotebookEntry weaponEntry = weaponNotebookEntries.get(suggestedAnswer.getWeapon());
		if (weaponEntry.isOwnerKnown() && weaponEntry.getOwner() == answerer)
			return;
		else if (!weaponEntry.isPossibleOwner(answerer))
			suggestedAnswer.presentNewFact(answerer, new Card(suggestedAnswer.getWeapon()), false);
		
		if (suggestedAnswer.isAnswerKnown())
			addKnownCard(suggestedAnswer.getAnswerer(), suggestedAnswer.getAnsweringCard());
		else {
			suggestedAnswers.add(suggestedAnswer);
			countCards();
		}
	}
	
	/**
	 * Adds information about a suggestion that a player could not answer.
	 * @param player player who could not answer
	 * @param room room that the player does not have
	 * @param suspect suspect that the player does not have
	 * @param weapon weapon that the player does not have
	 */
	public void addCannotAnswer(Player player, Room room, Suspect suspect, Weapon weapon) {
		if (roomNotebookEntries.get(room).isPossibleOwner(player))
			newInformationList.add(new NewInformation(player, new Card(room), false));
		if (suspectNotebookEntries.get(suspect).isPossibleOwner(player))
			newInformationList.add(new NewInformation(player, new Card(suspect), false));
		if (weaponNotebookEntries.get(weapon).isPossibleOwner(player))
			newInformationList.add(new NewInformation(player, new Card(weapon), false));
		
		if (!newInformationList.isEmpty())
			evaluateNewInformation();
	}
	
	/**
	 * Evaluates all the new information that has been entered into the notebook.
	 */
	private void evaluateNewInformation() {
		while (!newInformationList.isEmpty()) {
			NewInformation information = newInformationList.remove();
			if (information.hasCard)
				evaluateAsCardOwner(information.player, information.card);
			else
				evaluateAsNotCardOwner(information.player, information.card);
		}
			
		countCards();
	}
	
	/**
	 * Evaluates the new information about a card and the player who owns it.
	 * @param owner player who owns the card
	 * @param card card that the player owns
	 */
	private void evaluateAsCardOwner(Player owner, Card card) {
		if (isAlreadyKnown(card))
			return;
		
		if (card.getRoom() != null)
			roomNotebookEntries.get(card.getRoom()).setOwner(owner);
		else if (card.getSuspect() != null)
			suspectNotebookEntries.get(card.getSuspect()).setOwner(owner);
		else if (card.getWeapon() != null)
			weaponNotebookEntries.get(card.getWeapon()).setOwner(owner);
		
		Iterator<SuggestedAnswer> suggestedAnswerIter = suggestedAnswers.iterator();
		while (suggestedAnswerIter.hasNext()) {
			SuggestedAnswer suggestedAnswer = suggestedAnswerIter.next();
			suggestedAnswer.presentNewFact(owner, card, true);
			if (suggestedAnswer.isAnswerKnown()) {
				suggestedAnswerIter.remove();
				if (suggestedAnswer.getAnswerer() != owner)
					newInformationList.add(new NewInformation(suggestedAnswer.getAnswerer(), suggestedAnswer.getAnsweringCard(), true));
			}
		}
	}
	
	/**
	 * Evaluates the new information about a card that the player does NOT own.
	 * @param player player who doesn't own the card
	 * @param card card the player doesn't own
	 */
	private void evaluateAsNotCardOwner(Player player, Card card) {
		if (card.getRoom() != null) {
			if (roomNotebookEntries.get(card.getRoom()).removePossibleOwner(player)) {
				if (roomNotebookEntries.get(card.getRoom()).isOwnerKnown())
					newInformationList.add(new NewInformation(roomNotebookEntries.get(card.getRoom()).getOwner(), card, true));
			}
			else
				return;
		}
		else if (card.getSuspect() != null) {
			if (suspectNotebookEntries.get(card.getSuspect()).removePossibleOwner(player)) {
				if (suspectNotebookEntries.get(card.getSuspect()).isOwnerKnown())
					newInformationList.add(new NewInformation(suspectNotebookEntries.get(card.getSuspect()).getOwner(), card, true));
			}
			else
				return;
		}
		else if (card.getWeapon() != null) {
			if (weaponNotebookEntries.get(card.getWeapon()).removePossibleOwner(player)) {
				if (weaponNotebookEntries.get(card.getWeapon()).isOwnerKnown())
					newInformationList.add(new NewInformation(weaponNotebookEntries.get(card.getWeapon()).getOwner(), card, true));
			}
			else
				return;
		}
		
		Iterator<SuggestedAnswer> suggestedAnswerIter = suggestedAnswers.iterator();
		while (suggestedAnswerIter.hasNext()) {
			SuggestedAnswer suggestedAnswer = suggestedAnswerIter.next();
			suggestedAnswer.presentNewFact(player, card, false);
			if (suggestedAnswer.isAnswerKnown()) {
				suggestedAnswerIter.remove();
				newInformationList.add(new NewInformation(suggestedAnswer.getAnswerer(), suggestedAnswer.getAnsweringCard(), true));
			}
		}
	}
	
	/**
	 * Whether or not the owner of the card is already been marked in the notebook.
	 * @param card card to evaluate
	 * @return true if the owner of the given card is known
	 */
	private boolean isAlreadyKnown(Card card) {
		if (card.getRoom() != null)
			return (roomNotebookEntries.get(card.getRoom()).isOwnerKnown());
		if (card.getSuspect() != null)
			return (suspectNotebookEntries.get(card.getRoom()).isOwnerKnown());
		if (card.getWeapon() != null)
			return (weaponNotebookEntries.get(card.getWeapon()).isOwnerKnown());
		return false;
	}
	
	/**
	 * Returns whether or not the AI player is ready to accuse based on the info in the notebook. 
	 * @return true if the player knows all 3 cards to accuse
	 */
	public boolean isReadyToAccuse() {
		if (roomToAccuse == null) {
			for (Room room : roomNotebookEntries.keySet()) {
				if (roomNotebookEntries.get(room).isCardToAccuse()) {
					roomToAccuse = room;
					break;
				}
			}
			if (roomToAccuse == null)
				return false;
		}
		
		if (suspectToAccuse == null) {
			for (Suspect suspect : suspectNotebookEntries.keySet()) {
				if (suspectNotebookEntries.get(suspect).isCardToAccuse()) {
					suspectToAccuse = suspect;
					break;
				}
			}
			if (suspectToAccuse == null)
				return false;
		}
		
		if (weaponToAccuse == null) {
			for (Weapon weapon : weaponNotebookEntries.keySet()) {
				if (weaponNotebookEntries.get(weapon).isCardToAccuse()) {
					weaponToAccuse = weapon;
					break;
				}
			}
			if (weaponToAccuse == null)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the room the AI player should accuse.
	 * @return the room the AI player should accuse
	 */
	public Room getRoomToAccuse() {
		return roomToAccuse;
	}

	/**
	 * Returns the suspect the AI player should accuse.
	 * @return the suspect the AI player should accuse
	 */
	public Suspect getSuspectToAccuse() {
		return suspectToAccuse;
	}

	/**
	 * Returns the weapon the AI player should accuse.
	 * @return the weapon the AI player should accuse
	 */
	public Weapon getWeaponToAccuse() {
		return weaponToAccuse;
	}
	
	/**
	 * Evaluates and known information and eliminates players from possible owners of cards if the known cards
	 * for the player have reached the number of cards the player has. 
	 */
	private void countCards() {
		if (aiPlayer.getPlayerType() != PlayerType.COMPUTER_HARD)
			return;
		
		Iterator<Player> playerIter = playersWithUnknownCards.iterator();
		while (playerIter.hasNext()) {
			Player player = playerIter.next();
			int knownCards = 0;
			for (AINotebookEntry notebookEntry : roomNotebookEntries.values()) {
				if (notebookEntry.getOwner() == player)
					knownCards++;
			}
			for (AINotebookEntry notebookEntry : suspectNotebookEntries.values()) {
				if (notebookEntry.getOwner() == player)
					knownCards++;
			}
			for (AINotebookEntry notebookEntry : weaponNotebookEntries.values()) {
				if (notebookEntry.getOwner() == player)
					knownCards++;
			}
			
			if (knownCards < player.getCards().size()) {
				Set<Room> possibleAnsweredRooms = new HashSet<>();
				Set<Suspect> possibleAnsweredSuspects = new HashSet<>();
				Set<Weapon> possibleAnsweredWeapons = new HashSet<>();
				for (SuggestedAnswer suggestedAnswer : suggestedAnswers) {
					if (suggestedAnswer.getAnswerer() == player) {
						if (suggestedAnswer.getRoom() != null)
							possibleAnsweredRooms.add(suggestedAnswer.getRoom());
						if (suggestedAnswer.getSuspect() != null)
							possibleAnsweredSuspects.add(suggestedAnswer.getSuspect());
						if (suggestedAnswer.getWeapon() != null)
							possibleAnsweredWeapons.add(suggestedAnswer.getWeapon());
					}
				}
				int minimumPossiblesCardCount = Math.min(possibleAnsweredRooms.size(), Math.min(possibleAnsweredSuspects.size(), possibleAnsweredWeapons.size()));
				if (knownCards + minimumPossiblesCardCount == player.getCards().size()) {
					playerIter.remove();
					for (Room room : roomNotebookEntries.keySet()) {
						if (!possibleAnsweredRooms.contains(room)) {
							AINotebookEntry notebookEntry = roomNotebookEntries.get(room);
							if (!notebookEntry.isOwnerKnown())
								newInformationList.add(new NewInformation(player, new Card(room), false));
						}
					}
					for (Suspect suspect : suspectNotebookEntries.keySet()) {
						if (!possibleAnsweredSuspects.contains(suspect)) {
							AINotebookEntry notebookEntry = suspectNotebookEntries.get(suspect);
							if (!notebookEntry.isOwnerKnown())
								newInformationList.add(new NewInformation(player, new Card(suspect), false));
						}
					}
					for (Weapon weapon : weaponNotebookEntries.keySet()) {
						if (!possibleAnsweredWeapons.contains(weapon)) {
							AINotebookEntry notebookEntry = weaponNotebookEntries.get(weapon);
							if (!notebookEntry.isOwnerKnown())
								newInformationList.add(new NewInformation(player, new Card(weapon), false));
						}
					}
				}
			}
			else if (knownCards == player.getCards().size()) {
				playerIter.remove();
				for (Room room : roomNotebookEntries.keySet()) {
					AINotebookEntry notebookEntry = roomNotebookEntries.get(room);
					if (!notebookEntry.isOwnerKnown())
						newInformationList.add(new NewInformation(player, new Card(room), false));
				}
				for (Suspect suspect : suspectNotebookEntries.keySet()) {
					AINotebookEntry notebookEntry = suspectNotebookEntries.get(suspect);
					if (!notebookEntry.isOwnerKnown())
						newInformationList.add(new NewInformation(player, new Card(suspect), false));
				}
				for (Weapon weapon : weaponNotebookEntries.keySet()) {
					AINotebookEntry notebookEntry = weaponNotebookEntries.get(weapon);
					if (!notebookEntry.isOwnerKnown())
						newInformationList.add(new NewInformation(player, new Card(weapon), false));
				}
			}
		}
		
		if (!newInformationList.isEmpty())
			evaluateNewInformation();
	}
	
	/**
	 * Container for new information.
	 */
	private class NewInformation {
		public Player player;
		public Card card;
		public boolean hasCard;
		
		/**
		 * Creates a container for new information.
		 * @param player player involved
		 * @param card card involved
		 * @param hasCard whether or not the player owns the card
		 */
		public NewInformation(Player player, Card card, boolean hasCard) {
			this.player = player;
			this.card = card;
			this.hasCard = hasCard;
		}
	}
}
