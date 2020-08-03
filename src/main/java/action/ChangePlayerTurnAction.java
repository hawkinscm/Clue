package action;

import gui.ParticipantGUI;

/**
 * Action for notifying Network players that it is now a new player's turn. 
 */
public class ChangePlayerTurnAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangePlayerTurnAction() {}
	
	/**
	 * Generates and returns a Change Player Turn Action message using the given data.
	 * @param playerId id of the player/suspect whose turn it now is
	 * @param message changed player turn message to display
	 * @return a Change Player Turn Action message generated from the given data
	 */
	public String createMessage(int playerId, String message) {
		setMessage(this.getClass().getName() + MAIN_DELIM + 
				   playerId + MAIN_DELIM +
				   message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] data = getMessageWithoutClassHeader().split(MAIN_DELIM);
		int playerId = Integer.parseInt(data[0]);
		gui.beginPlayerTurn(playerId);
		gui.addMessage(data[1]);
		return null;
	}
}
