package action;

import gui.ParticipantGUI;

/**
 * Action for updating game options. 
 */
public class ChangeOptionsAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public ChangeOptionsAction() {}
	
	/**
	 * Generates and returns a Change Options Action message using the given data.
	 * @param message message to send and display
	 * @return a Change Options Action message generated from the given data
	 */
	public String createMessage(int numDice) {
		setMessage(this.getClass().getName() + MAIN_DELIM + numDice);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		int numDice = Integer.parseInt(getMessageWithoutClassHeader());
		gui.setNumDice(numDice);
		return null;
	}
}
