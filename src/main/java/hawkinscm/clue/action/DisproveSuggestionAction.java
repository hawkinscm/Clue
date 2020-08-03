package hawkinscm.clue.action;

import hawkinscm.clue.gui.HostGUI;
import hawkinscm.clue.model.Card;

/**
 * Action for informing the Host of the results of trying to disprove a suggestion.
 */
public class DisproveSuggestionAction extends Action<HostGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DisproveSuggestionAction() {}

	
	/**
	 * Generates and returns a Disprove Suggestion Action message.
	 * @param suggestingPlayerId id of the player who made the suggestion
	 * @param answeringPlayerId id of the player attempting to disprove the suggestion
	 * @param disprovingCard card that disproves the suggestion (will be null if unable to disprove suggestion)
	 * @param suggestedRoomId id of the suggested room
	 * @param suggestedSuspectId id of the suggested suspect
	 * @param suggestedWeaponId id of the suggested weapon
	 * @return a Disprove Suggestion Action message
	 */
	public String createMessage(int suggestingPlayerId, int answeringPlayerId, Card disprovingCard, int suggestedRoomId, int suggestedSuspectId, int suggestedWeaponId) {
		setMessage(this.getClass().getName() + MAIN_DELIM +
				   suggestingPlayerId + MAIN_DELIM +
				   answeringPlayerId + MAIN_DELIM +
				   generateCardMessage(disprovingCard) + MAIN_DELIM +
				   suggestedRoomId + MAIN_DELIM + 
				   suggestedSuspectId + MAIN_DELIM + 
				   suggestedWeaponId);
		return getMessage();
	}
	
	@Override
	public Class<HostGUI> getActionTypeClass() {
		return HostGUI.class;
	}
	
	@Override
	public String[] performAction(HostGUI gui) {
		String[] data = getMessageWithoutClassHeader().split(MAIN_DELIM);
		int suggestingPlayerId = Integer.parseInt(data[0]);
		int answeringPlayerId = Integer.parseInt(data[1]);
		Card disprovingCard = this.parseCard(data[2], gui.getRooms(), gui.getSuspects(), gui.getWeapons());
		if (!gui.disproveSuggestion(suggestingPlayerId, answeringPlayerId, disprovingCard)) {
			int suggestedRoomId = Integer.parseInt(data[3]);
			int suggestedSuspectId = Integer.parseInt(data[4]);
			int suggestedWeaponId = Integer.parseInt(data[5]);
			gui.requestDisproveSuggestion(suggestingPlayerId, answeringPlayerId, suggestedRoomId, suggestedSuspectId, suggestedWeaponId);
		}
		return null;
	}
}
