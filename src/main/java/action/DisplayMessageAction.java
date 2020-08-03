package action;

import gui.ParticipantGUI;

/**
 * Action for displaying a message on a Network player's GUI. 
 */
public class DisplayMessageAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DisplayMessageAction() {}
	
	/**
	 * Generates and returns a Display Message Action message using the given data.
	 * @param message message to send and display
	 * @return a Display Message Action message generated from the given data
	 */
	public String createMessage(String message) {
		setMessage(this.getClass().getName() + MAIN_DELIM + message.replace('\n', MAIN_DELIM.charAt(0)));
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String message = getMessageWithoutClassHeader().replace(MAIN_DELIM.charAt(0), '\n');
		gui.addMessage(message);
		return null;
	}
}
