package hawkinscm.clue.action;

import hawkinscm.clue.gui.DisplayCardDialog;
import hawkinscm.clue.gui.ParticipantGUI;
import hawkinscm.clue.model.Card;

/**
 * Action for displaying a AICard along with a message.
 */
public class DisplayCardAction extends Action<ParticipantGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public DisplayCardAction() {}
	
	/**
	 * Generates and returns a Display AICard Action message using the given data.
	 * @param card to display
	 * @param message to display
	 * @return a Display AICard Action message generated from the given data
	 */
	public String createMessage(Card card, String message) {
		setMessage(this.getClass().getName() + MAIN_DELIM +
				   generateCardMessage(card) + MAIN_DELIM + 
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
		Card card = parseCard(data[0], gui.getRooms(), gui.getSuspects(), gui.getWeapons());
		new DisplayCardDialog(gui, card, data[1]).setVisible(true);
		return null;
	}
}
