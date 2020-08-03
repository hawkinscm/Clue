package hawkinscm.clue.action;

import hawkinscm.clue.gui.ParticipantGUI;

/**
 * Action for requesting that a Network player disprove the suggestion that was made.
 */
public class RequestDisproveSuggestionAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public RequestDisproveSuggestionAction() {}
	
	/**
	 * Generates and returns a Request Disprove Suggestion Action message.
	 * @param suggestingPlayerId id of player who made the suggestion
	 * @param suggestedRoomId id of the suggested room
	 * @param suggestedSuspectId id of the suggested suspect
	 * @param suggestedWeaponId id of the suggested room
	 * @return a Request Disprove Suggestion Action weapon
	 */
	public String createMessage(int suggestingPlayerId, int suggestedRoomId, int suggestedSuspectId, int suggestedWeaponId) {
		setMessage(this.getClass().getName() + MAIN_DELIM +
			       suggestingPlayerId + MAIN_DELIM +
			       suggestedRoomId + MAIN_DELIM +
			       suggestedSuspectId + MAIN_DELIM +
			       suggestedWeaponId);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] data = getMessageWithoutClassHeader().split(MAIN_DELIM);
		int suggestingPlayerId = Integer.parseInt(data[0]);
		int suggestedRoomId = Integer.parseInt(data[1]);
		int suggestedSuspectId = Integer.parseInt(data[2]);
		int suggestedWeaponId = Integer.parseInt(data[3]);
		gui.disproveSuggestion(suggestingPlayerId, suggestedRoomId, suggestedSuspectId, suggestedWeaponId);
		return null;
	}
}
