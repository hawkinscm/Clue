package hawkinscm.clue.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;

import hawkinscm.clue.model.Card;

/**
 * Dialog for displaying a single card and a message.
 */
public class DisplayCardDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor for the Display Card Dialog
	 * @param owner parent/owner of this dialog
	 * @param card card to display
	 * @param message message to display
	 */
	public DisplayCardDialog(JFrame owner, Card card, String message) {
		super(owner, "View Card");
		setModal(false);
		
		add(new JLabel(message), c);
		
		c.gridy++;
		add(new CardDisplay(card), c);
		
		refresh();
	}
}
