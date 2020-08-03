package gui;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Dialog that allows a player to make an Accusation.
 */
public class AccuseDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	public AccuseDialog(final ClueGUI gui) {
		super(gui, "Make An Accusation");

		add(new JLabel("<html> WARNING: ACCUSING WILL BE YOUR FINAL TURN OF THE GAME! This is NOT Make a Suggestion.<br><br>" +
				"If your Accusation is correct, you win! But be warned: if you are wrong you will lose and will no longer be able<br>" +
				"to take any turns the rest of the game, except to disprove suggestions made by other players with your cards.</html>"), c);

		c.gridy++;
		final RoomSuspectWeaponSelectionPanel selectionPanel = new RoomSuspectWeaponSelectionPanel(gui.getRooms(), gui.getSuspects(), gui.getWeapons());
		add(selectionPanel, c);

		c.gridy++;
		JPanel buttonPanel = new JPanel();

		CustomButton accuseButton = new CustomButton("Make Accusation") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				Room selectedRoom = selectionPanel.getSelectedRoom();
				Suspect selectedSuspect = selectionPanel.getSelectedSuspect();
				Weapon selectedWeapon = selectionPanel.getSelectedWeapon();
				if (selectedRoom == null || selectedSuspect == null || selectedWeapon == null)
					return;

				String message = "If you are wrong you will lose the game!  Are you sure you want to accuse?";
				int result = JOptionPane.showConfirmDialog(this, message, "ACCUSE WARNING", JOptionPane.YES_NO_CANCEL_OPTION);
				if (result != JOptionPane.YES_OPTION)
					return;

				gui.makeAccusation(selectedRoom, selectedSuspect, selectedWeapon);
				dispose();
			}
		};
		buttonPanel.add(accuseButton);

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
}
