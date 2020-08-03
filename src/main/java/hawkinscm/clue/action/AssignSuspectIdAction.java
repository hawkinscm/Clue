package hawkinscm.clue.action;

import hawkinscm.clue.gui.ParticipantGUI;

/**
 * Action for assigning a suspect id on a Network player.
 */
public class AssignSuspectIdAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public AssignSuspectIdAction() {}
	
	/**
	 * Generates and returns an Assign Suspect Id Action message using the given data.
	 * @param suspectId id of the suspect to assign to the player
	 * @return an Assign Suspect Id Action message generated from the given data
	 */
	public String createMessage(int suspectId) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + suspectId;
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		int suspectId = Integer.parseInt(getMessageWithoutClassHeader());
		gui.setSuspectId(suspectId);
		
		return null;
	}
}
