package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Panel for allowing a player to choose one room, one suspect, and one weapon.
 */
public class RoomSuspectWeaponSelectionPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private ButtonGroup roomGroup;
	private ButtonGroup suspectGroup;
	private ButtonGroup weaponGroup;
	
	private List<Room> rooms;
	private List<Suspect> suspects;
	private List<Weapon> weapons;
	
	/**
	 * Creates a new Room-Suspect-Weapon Selection Panel
	 * @param rooms rooms to choose from (may be null)
	 * @param suspects suspects to choose from (may be null)
	 * @param weapons (weapons to choose from (may be null)
	 */
	public RoomSuspectWeaponSelectionPanel(List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons) {
		this.rooms = rooms;
		this.suspects = suspects;
		this.weapons = weapons;
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 10, 2, 10);
		c.anchor = GridBagConstraints.WEST;
		
		roomGroup = new ButtonGroup();
		for (Room room : rooms) {
			JRadioButton roomOption = new JRadioButton(room.getName());
			roomGroup.add(roomOption);
			add(roomOption, c);
			c.gridy++;
		}
		
		c.gridx++;
		c.gridy = 0;
		suspectGroup = new ButtonGroup();
		for (Suspect suspect : suspects) {
			JRadioButton suspectOption = new JRadioButton(suspect.getName());
			suspectGroup.add(suspectOption);
			add(suspectOption, c);
			c.gridy++;
		}
		
		c.gridx++;
		c.gridy = 0;
		weaponGroup = new ButtonGroup();
		for (Weapon weapon : weapons) {
			JRadioButton weaponOption = new JRadioButton(weapon.getName());
			weaponGroup.add(weaponOption);
			add(weaponOption, c);
			c.gridy++;
		}
	}
	
	/**
	 * Returns the chosen room.
	 * @return the selected room
	 */
	public Room getSelectedRoom() {
		Enumeration<AbstractButton> radioButtons = roomGroup.getElements();
		for (int buttonIdx = 0; buttonIdx < roomGroup.getButtonCount(); buttonIdx++)
			if (radioButtons.nextElement().isSelected())
				return rooms.get(buttonIdx);

		return null;
	}
	
	/**
	 * Returns the chosen suspect.
	 * @return the selected suspect
	 */
	public Suspect getSelectedSuspect() {
		Enumeration<AbstractButton> radioButtons = suspectGroup.getElements();
		for (int buttonIdx = 0; buttonIdx < suspectGroup.getButtonCount(); buttonIdx++)
			if (radioButtons.nextElement().isSelected())
				return suspects.get(buttonIdx);

		return null;
	}
	
	/**
	 * Returns the chosen weapon.
	 * @return the selected weapon
	 */
	public Weapon getSelectedWeapon() {
		Enumeration<AbstractButton> radioButtons = weaponGroup.getElements();
		for (int buttonIdx = 0; buttonIdx < weaponGroup.getButtonCount(); buttonIdx++)
			if (radioButtons.nextElement().isSelected())
				return weapons.get(buttonIdx);

		return null;
	}
}
