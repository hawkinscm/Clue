package ai;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Card;
import model.Player;
import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Handler for AI.
 */
public class AIManager {
	private List<Player> players;
	private List<Room> rooms;
	private List<Suspect> suspects;
	private List<Weapon> weapons;
	
	private LinkedList<SuggestionResult> suggestionRecord;
	private ArrayList<AINotebook> playerNotebooks;

	
	/**
	 * Creates a new AI Manager.
	 * @param players all card-holding players in the current game
	 * @param rooms all rooms in the game
	 * @param suspects all suspects in the game
	 * @param weapons all weapons in the game
	 */
	public AIManager(List<Player> players, List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons) {
		this.players = players;
		this.rooms = rooms;
		this.suspects = suspects;
		this.weapons = weapons;
		
		playerNotebooks = new ArrayList<AINotebook>();
		suggestionRecord = new LinkedList<SuggestionResult>();
	}
	
	// todo
	public void initializeAIPlayer(Player player) {
		int todo; // call from replace player
		playerNotebooks.add(new AINotebook(player, players, rooms, suspects, weapons));
		buildDetectiveNotebookFromLatestSuggestions(player);
	}
	
	/**
	 * Adds a single suggestion record.
	 * @param suggester the person who made a suggestion
	 * @param responder the person who answered the suggestion (will be null if no one could help)
	 * @param card card that was shown by the responder (will be null if responder is null)
	 */
	public void addSuggesionResult(Player suggester, Player responder, Card card) {
		suggestionRecord.add(new SuggestionResult(suggester, responder, card));
	}
	
	/**
	 * Handles a turn for a computer player.
	 * todo
	 */
	public void takeTurn(Player player) {
		int todo1;
		// based on ai evaluate suggestions made since last turn by other players
		// if known accuse
		
		// if in room, can suggest, and good to suggest, stay there
        // else move to room if can
		int todo;
		//HashMap<Room, List<DisplayTile>> shortestPathToRoomsMap = TravelHandler.getShortestPathsToRooms(board, startTile);
		// make sure to include checks for inner room passages
		// check range of current roll and number of turns estimated to get to a certain room
		
		// easy - only know cards seen
		// medium - keep list of what cards a person has at least one of, based on any suggestion
		// hard - like medium but plus card counting (when all of a persons cards are known or a set of defintes and possibles is known, mark all others as not held by person)
		
		int tocheck; // make sure if someone falsely accuses, that the data gets added, processed by the manager and players
		
		// make suggestion if can
		// if known accuse
		
		// end turn
	}
	
	private void buildDetectiveNotebookFromLatestSuggestions(Player player) {
		
	}
}
