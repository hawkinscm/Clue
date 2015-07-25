package gui;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import model.Room;

/**
 * Dialog that allows a player to select a room from a list.
 */
public class SelectRoomDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private Room selectedRoom;
	
	/**
	 * Creates a new Select Room Dialog
	 * @param owner parent/owner of this dialog
	 * @param message message to display
	 * @param rooms rooms to choose from
	 */
	public SelectRoomDialog(JFrame owner, String message, List<Room> rooms) {
		super(owner, "Select A Room");
		
		selectedRoom = null;
		
		add(new JLabel(message), c);
		
		c.gridy++;
		final JComboBox roomComboBox = new JComboBox(rooms.toArray());
		roomComboBox.setSelectedIndex(0);
		add(roomComboBox, c);
		
		c.gridy++;
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				selectedRoom = (Room) roomComboBox.getSelectedItem();
				dispose();
			}
		};
		add(okButton, c);
		
		refresh();
	}
	
	/**
	 * Returns the chosen room.
	 * @return the selected room
	 */
	public Room getSelectedRoom() {
		return selectedRoom;
	}
}
