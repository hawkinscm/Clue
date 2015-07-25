package action;

import gui.HostGUI;

/**
 * Action for informing the Host that a suggestion has been made.
 */
public class MakeSuggestionAction extends Action<HostGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public MakeSuggestionAction() {}
	
	/**
	 * Generates and returns a Make Suggestion Action message.
	 * @param playerId id of player making the suggestion
	 * @param roomId id of the suggested room
	 * @param suspectId id of the suggested suspect
	 * @param weaponId id of the suggested weapon
	 * @return a Make Suggestion Action message
	 */
	public String createMessage(int playerId, int roomId, int suspectId, int weaponId) {
		setMessage(this.getClass().getName() + MAIN_DELIM +
				   playerId + MAIN_DELIM +
				   roomId + MAIN_DELIM +
				   suspectId + MAIN_DELIM +
				   weaponId + MAIN_DELIM);
		return getMessage();
	}
	
	@Override
	public Class<HostGUI> getActionTypeClass() {
		return HostGUI.class;
	}
	
	@Override
	public String[] performAction(HostGUI gui) {
		String[] data = this.getMessageWithoutClassHeader().split(MAIN_DELIM);
		int playerId = Integer.parseInt(data[0]);
		int roomId = Integer.parseInt(data[1]);
		int suspectId = Integer.parseInt(data[2]);
		int weaponId = Integer.parseInt(data[3]);
		gui.makeSuggestion(playerId, roomId, suspectId, weaponId);
		return null;
	}
}
