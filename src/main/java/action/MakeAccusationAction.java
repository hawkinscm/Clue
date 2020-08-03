package action;

import gui.HostGUI;

/**
 * Action for informing the Host that an accusation has been made.
 */
public class MakeAccusationAction extends Action<HostGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public MakeAccusationAction() {}
	
	/**
	 * Generates and returns a Make Accusation Action message.
	 * @param playerId id of player making the accusation
	 * @param roomId id of the accused room
	 * @param suspectId id of the accused suspect
	 * @param weaponId id of the accused weapon
	 * @return a Make Accusation Action message
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
		gui.makeAccusation(playerId, roomId, suspectId, weaponId);
		return null;
	}
}
