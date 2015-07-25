package gui;

import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import model.Card;
import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Dialog for attempting to disprove a suggestion by showing one of the suggested objects' cards.
 */
public class DisproveSuggestionDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private Card disprovingCard;
	
	/**
	 * Create a new Disprove Suggestion Dialog
	 * @param gui ClueGUI that is the parent/owner of this dialog
	 * @param suggestingPlayerName name of the player making the suggestion
	 * @param suggestedRoom suggested room
	 * @param suggestedSuspect suggested suspect
	 * @param suggestedWeapon suggested weapon
	 */
	public DisproveSuggestionDialog(ClueGUI gui, String suggestingPlayerName, Room suggestedRoom, Suspect suggestedSuspect, Weapon suggestedWeapon) {
		super(gui, "Disprove Suggestion");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		disprovingCard = null;
		
		ArrayList<Card> disprovingCards = new ArrayList<Card>();
		for (Card card : gui.getCards())
			if (card.matches(suggestedRoom) || card.matches(suggestedSuspect) || card.matches(suggestedWeapon))
				disprovingCards.add(card);
		
		add(new JLabel(suggestingPlayerName + " suggests it was " + suggestedSuspect.getName() +
				       " in the " + suggestedRoom.getName() + " with the " + suggestedWeapon.getName() + "."), c);
		

		if (disprovingCards.isEmpty()) {
			c.gridy++;
			add(new JLabel("You have no cards that disprove this suggestion."), c);
			
			c.gridy++;
			CustomButton okButton = new CustomButton("OK") {
				private static final long serialVersionUID = 1L;
				public void buttonClicked() {
					dispose();
				}
			};
			add(okButton, c);
			
			refresh();
			return;
		}
		
		c.gridy++;
		String message = "You have ";
		if (disprovingCards.size() == 1)
			message += "1 card that disproves";
		else
			message += disprovingCards.size() + " cards that disprove";
		message += " this suggestion.";
		add(new JLabel(message), c);
			
		c.gridy++;
		final JComboBox disprovingCardsComboBox = new JComboBox(disprovingCards.toArray());
		disprovingCardsComboBox.setSelectedIndex(0);
		add(disprovingCardsComboBox, c);
		
		c.gridy++;
		CustomButton disproveButton = new CustomButton("Disprove By Showing Card") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				disprovingCard = (Card) disprovingCardsComboBox.getSelectedItem();
				dispose();
			}
		};
		add(disproveButton, c);
		
		refresh();
	}
	
	/**
	 * Returns the selected Card that disproves the suggestion or null if player was unable to disprove suggestion.
	 * @return the selected Card that disproves the suggestion or null if player was unable to disprove suggestion
	 */
	public Card getDisprovingCard() {
		return disprovingCard;
	}
}
