package hawkinscm.clue.action;

import hawkinscm.clue.gui.HostGUI;

/**
 * Action for allowing a Network player to send the Host a message (which will then be sent to all players).
 */
public class ShareInfoAction extends Action<HostGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ShareInfoAction() {}
	
	/**
	 * Generates and returns a Share Info Action message using the given data.
	 * @param message message to share host and all players
	 * @return a Share Info Action message generated from the given data
	 */
	public String createMessage(String message) {
		setMessage(this.getClass().getName() + MAIN_DELIM + message);
		return getMessage();
	}
	
	@Override
	public Class<HostGUI> getActionTypeClass() {
		return HostGUI.class;
	}
	
	@Override
	public String[] performAction(HostGUI gui) {
		String message = getMessageWithoutClassHeader();
		gui.informAllPlayers(message);
		return null;
	}
}
