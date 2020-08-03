package hawkinscm.clue.action;

import hawkinscm.clue.gui.ParticipantGUI;

/**
 * Action for allowing a Network player to end his turn.
 */
public class EnableEndTurnAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public EnableEndTurnAction() {}
	
	/**
	 * Generates and returns an Enable End Turn Action message using the given data.
	 * @return an Enable End Turn Action message generated from the given data
	 */
	public String createMessage() {
		setMessage(this.getClass().getName() + MAIN_DELIM);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		gui.enableEndTurn();
		return null;
	}
}
