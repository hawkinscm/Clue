package hawkinscm.clue.action;

import hawkinscm.clue.gui.HostGUI;

/**
 * Action for informing the Host that a Network player has finished his turn.
 */
public class EndTurnAction extends Action<HostGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public EndTurnAction() {}
	
	/**
	 * Generates and returns an End Turn Action message.
	 * @return an End Turn Action message
	 */
	public String createMessage() {
		setMessage(this.getClass().getName() + MAIN_DELIM);
		return getMessage();
	}
	
	@Override
	public Class<HostGUI> getActionTypeClass() {
		return HostGUI.class;
	}
	
	@Override
	public String[] performAction(HostGUI gui) {
		gui.endTurn();
		return null;
	}
}
