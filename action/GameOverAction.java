package action;

import gui.ParticipantGUI;

/**
 * Action for informing a Network player that the game is over and displaying the results.
 */
public class GameOverAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public GameOverAction() {}
	
	/**
	 * Generates and returns a Game Over Action message.
	 * @param winnerName name of the player who made a correct accusation and won OR null if everyone lost
	 * @param roomId id of the room where the crime was committed
	 * @param suspectId id of the suspect who committed the crime
	 * @param weaponId id of the weapon used to commit the crime
	 * @return a Game Over Action message
	 */
	public String createMessage(String winnerName, int roomId, int suspectId, int weaponId) {
		setMessage(this.getClass().getName() + MAIN_DELIM +
				   ((winnerName == null) ? "null" : winnerName) + MAIN_DELIM +
				   roomId + MAIN_DELIM +
				   suspectId + MAIN_DELIM +
				   weaponId + MAIN_DELIM);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String[] data = this.getMessageWithoutClassHeader().split(MAIN_DELIM);
		String winnerName = (data[0].equals("null")) ? null : data[0];
		int roomId = Integer.parseInt(data[1]);
		int suspectId = Integer.parseInt(data[2]);
		int weaponId = Integer.parseInt(data[3]);
		gui.endGame(winnerName, roomId, suspectId, weaponId);
		return null;
	}
}
