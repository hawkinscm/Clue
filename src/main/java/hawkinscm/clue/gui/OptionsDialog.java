package hawkinscm.clue.gui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

/**
 * Dialog for allowing player to see and set game options.
 */
public class OptionsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private JRadioButton inOrderStartOption;
	private JRadioButton definedLocationOption;
	
	private JComboBox numDiceComboBox;
	
	/**
	 * Creates a new Options Dialog
	 * @param owner the frame that created/owns this dialog
	 * @param numDice the currently set number of dice to use
	 * @param useFirstInListPlayerStart whether or not it is currently set to select the starting player in list order
	 * @param useDefinedSuspectLocation whether or not it is currently set to start suspects in their defined locations
	 */
	public OptionsDialog(JFrame owner, final int numDice, final boolean useFirstInListPlayerStart, final boolean useDefinedSuspectLocation) {
		super(owner, "Game Options");

		JPanel dicePanel = new JPanel();
		dicePanel.add(new JLabel("Number of Dice:"));
		
		numDiceComboBox = new JComboBox(new Integer[] {1, 2, 3});
		numDiceComboBox.setSelectedIndex(numDice - 1);
		dicePanel.add(numDiceComboBox);
		getContentPane().add(dicePanel, c);
		
		c.gridy++;
		JPanel startingPlayerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		startingPlayerPanel.setBorder(BorderFactory.createTitledBorder("Starting Player Selection"));
		
		ButtonGroup startingPlayerButtonGroup = new ButtonGroup();
		
		inOrderStartOption = new JRadioButton("First In List (Example: Miss Scarlet)");
		inOrderStartOption.setSelected(useFirstInListPlayerStart);
		startingPlayerButtonGroup.add(inOrderStartOption);
		startingPlayerPanel.add(inOrderStartOption);
		
		JRadioButton randomStartOption = new JRadioButton("Randomly Selected");
		randomStartOption.setSelected(!useFirstInListPlayerStart);
		startingPlayerButtonGroup.add(randomStartOption);
		startingPlayerPanel.add(randomStartOption);
		
		add(startingPlayerPanel, c);
		
		c.gridy++;
		JPanel startingLocationPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		startingLocationPanel.setBorder(BorderFactory.createTitledBorder("Starting Suspect Placement"));
		
		ButtonGroup startingLocationButtonGroup = new ButtonGroup();
		
		definedLocationOption = new JRadioButton("Defined Location On Board");
		definedLocationOption.setSelected(useDefinedSuspectLocation);
		startingLocationButtonGroup.add(definedLocationOption);
		startingLocationPanel.add(definedLocationOption);
		
		JRadioButton randomLocationOption = new JRadioButton("Randomly Placed");
		randomLocationOption.setSelected(!useDefinedSuspectLocation);
		startingLocationButtonGroup.add(randomLocationOption);
		startingLocationPanel.add(randomLocationOption);
		
		add(startingLocationPanel, c);
		
		c.gridy++;
		JPanel buttonPanel = new JPanel();
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;

			public void buttonClicked() {
				dispose();
			}
		};
		buttonPanel.add(okButton);
		
		CustomButton cancelButton = new CustomButton("Cancel") {
			private static final long serialVersionUID = 1L;

			public void buttonClicked() {
				numDiceComboBox.setSelectedIndex(numDice - 1);
				inOrderStartOption.setSelected(useFirstInListPlayerStart);
				definedLocationOption.setSelected(useDefinedSuspectLocation);
				dispose();
			}			
		};
		buttonPanel.add(cancelButton);		
		getContentPane().add(buttonPanel, c);
		
		refresh();
	}
	
	/**
	 * Returns the number of dice the user would like each player to have per turn.
	 * @return the number of dice the user would like each player to have per turn
	 */
	public int getNumberOfDice() {
		return (Integer) numDiceComboBox.getSelectedItem();
	}
	
	/**
	 * Returns whether or not the user wants to start the game with the first Suspect in the list.
	 * @return true if the user wants to start the game with the first Suspect in the list; false if he wants the first player randomly decided
	 */
	public boolean useFirstInListPlayerStart() {
		return inOrderStartOption.isSelected();
	}
	
	/**
	 * Returns whether or not the user wants the Suspects to start in their defined locations (if available)
	 * @return true if the user wants the Suspects to start in their defined locations; false if he wants the Suspect starting locations randomly chosen
	 */
	public boolean useDefinedSuspectLocation() {
		return definedLocationOption.isSelected();
	}
}
