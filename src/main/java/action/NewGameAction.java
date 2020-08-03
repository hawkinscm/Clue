package action;

import gui.ParticipantGUI;
import model.GameFileIOHandler;

/**
 * Action for informing the Network player of a new game with all the game data.
 */
public class NewGameAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public NewGameAction() {}
	
	/**
	 * Generates and returns a New Game Action message using the given data.
	 * @param ccgXML XML text of the CLUE game data
	 * @return a New Game Action message generated from the given data
	 */
	public String createMessage(String ccgXML) {
		String message = this.getClass().getName();
		message += MAIN_DELIM + ccgXML;
		message = message.replace('\n', ' ').replace('\r', ' ');
		
		setMessage(message);
		return getMessage();
	}
	
	@Override
	public Class<ParticipantGUI> getActionTypeClass() {
		return ParticipantGUI.class;
	}
	
	@Override
	public String[] performAction(ParticipantGUI gui) {
		String ccgXML = getMessageWithoutClassHeader();
		gui.loadGame(GameFileIOHandler.readFromString(ccgXML));
		
		return null;
	}
}
