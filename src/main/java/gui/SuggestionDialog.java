package gui;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Dialog that allows a player to make a suggestion by using the room he is in and having him select the suspect and room of his choice. 
 */
public class SuggestionDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private boolean madeSuggestion;
	
	/**
	 * Creates a new Suggestion Dialog.
	 * @param gui ClueGUI that owns this dialog
	 * @param room room that the player is currently in
	 */
	public SuggestionDialog(final ClueGUI gui, final Room room) {
		super(gui, "Make A Suggestion");
		
		madeSuggestion = false;
		
		add(new JLabel("<html>Select a weapon and suspect, along with the " + room.getName() + " you are currently in, to suggest as part of the crime.<br>" +
				       "The suspect will be moved to the room and each player will, in turn, try to prove you wrong with a card he/she holds.<br>" +
				       "As soon as one player shows you a card it will prove your suggestion false and no other cards will be shown to you.</html>"), c);
		
		c.gridy++;
		final RoomSuspectWeaponSelectionPanel selectionPanel = new RoomSuspectWeaponSelectionPanel(new ArrayList<>(), gui.getSuspects(), gui.getWeapons());
		add(selectionPanel, c);
		
		c.gridy++;
		JPanel buttonPanel = new JPanel();
		
		CustomButton suggestButton = new CustomButton("Make Suggestion") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				Suspect selectedSuspect = selectionPanel.getSelectedSuspect();
				Weapon selectedWeapon = selectionPanel.getSelectedWeapon();
				if (selectedSuspect == null || selectedWeapon == null)
					return;
				
				gui.makeSuggestion(room, selectedSuspect, selectedWeapon);
				madeSuggestion = true;
				dispose();
			}
		};
		buttonPanel.add(suggestButton);
		
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				dispose();
			}
		};
		buttonPanel.add(cancelButton);
		
		add(buttonPanel, c);
		
		refresh();
	}
	
	/**
	 * Returns whether or not the player made a suggestion.
	 * @return true if the player made a suggestion; false otherwise
	 */
	public boolean madeSuggestion() {
		return madeSuggestion;
	}
}
