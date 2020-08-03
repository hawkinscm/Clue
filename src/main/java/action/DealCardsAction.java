package action;

import gui.ParticipantGUI;
import model.Card;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Action for dealing cards to a Network player and informing that player of how many cards each player was dealt.
 */
public class DealCardsAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DealCardsAction() {}
	
	/**
	 * Generates and returns a Deal Cards Action message using the given data.
	 * @param cards list of cards that the player has been dealt
	 * @param playerCardCountMap map of player names to how many cards they each have
	 * @return a Change Player Turn Action message generated from the given data
	 */
	public String createMessage(List<Card> cards, LinkedHashMap<String, Integer> playerCardCountMap) {
		StringBuilder message = new StringBuilder(this.getClass().getName());
		message.append(MAIN_DELIM);
		message.append(cards.size()).append(MAIN_DELIM);
		for (Card card : cards)
			message.append(generateCardMessage(card)).append(MAIN_DELIM);
		for (String playerName : playerCardCountMap.keySet())
			message.append(playerName).append(INNER_DELIM).append(playerCardCountMap.get(playerName)).append(MAIN_DELIM);
		setMessage(message.toString());
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] data = getMessageWithoutClassHeader().split(MAIN_DELIM);
		
		int cardCount = Integer.parseInt(data[0]);
		List<Card> cards = new ArrayList<>();
		for (int dataIdx = 1; dataIdx <= cardCount; dataIdx++)
			cards.add(parseCard(data[dataIdx], gui.getRooms(), gui.getSuspects(), gui.getWeapons()));
		gui.setCards(cards);
		
		LinkedHashMap<String, Integer> playerCardCountMap = new LinkedHashMap<>();
		for (int dataIdx = cardCount + 1; dataIdx < data.length; dataIdx++) {
			String[] playerCardCountData = data[dataIdx].split(INNER_DELIM);
			playerCardCountMap.put(playerCardCountData[0], Integer.parseInt(playerCardCountData[1]));
		}
		gui.setPlayerCardCountMap(playerCardCountMap);
		return null;
	}
}
