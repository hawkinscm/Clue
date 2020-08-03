
package hawkinscm.clue.gui;

import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import hawkinscm.clue.model.Player;
import hawkinscm.clue.model.PlayerType;

/**
 * Dialog used for changing a player's type: 
 *   set a computer player to a different AI level,
 *   set an alibi player to be a computer player or network player,
 *   set a network player to be a computer player or alibi player,
 *   set a computer player to be a network player or alibi player
 */
public class ChangePlayerTypeDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;
	
	private Player changedPlayer;
	private PlayerType oldPlayerType;
	
	/**
	 * Constructor for a Change Player Dialog
	 * @param owner that called/owns this dialog
	 * @param player player whose type must be changed
	 */
	public ChangePlayerTypeDialog(JFrame owner, Player player) {
		this(owner, player, null);
	}
	
	/**
	 * Constructor for a Change Player Dialog
	 * @param owner that called/owns this dialog
	 * @param players list of all players of which the user can pick any but the host to change their type
	 */
	public ChangePlayerTypeDialog(JFrame owner, List<Player> players) {
		this(owner, null, players);
	}
	
	/**
	 * private constructor for a Change Player Dialog
	 * @param owner that called/owns this dialog
	 * @param player player to replace; if player is not null, it will ask the user which one to replace him with
	 * @param players list of all players of which the user can pick any but the host or a non-player and change their type
	 */
	private ChangePlayerTypeDialog(JFrame owner, Player player, List<Player> players) {
		super(owner, "Replace Player");
		
		final JComboBox<Player> playerComboBox;
		String prompt = "Select ";
		if (player != null) {
			playerComboBox = new JComboBox<>();
			playerComboBox.addItem(player);
		}
		else {
			prompt += "a player and ";
			playerComboBox = new JComboBox(players.toArray());
			for (Player currentPlayer : players)
				if (currentPlayer.getPlayerType() == PlayerType.HOST || currentPlayer.getPlayerType() == PlayerType.NON_PLAYER)
					playerComboBox.removeItem(currentPlayer);
		}		
		prompt += " a player type to change the player to.";
		playerComboBox.setSelectedIndex(0);
		
		c.gridwidth = 2;
		getContentPane().add(new JLabel(prompt), c);
		c.gridwidth = 1;
		
		c.gridy++;
		getContentPane().add(playerComboBox, c);
		
		c.gridx++;
		final JComboBox<PlayerType> playerTypeComboBox = new JComboBox<>(PlayerType.values());
		playerTypeComboBox.removeItem(PlayerType.HOST);
		int toFix;
		playerTypeComboBox.removeItem(PlayerType.NETWORK);
		playerTypeComboBox.removeItem(PlayerType.NON_PLAYER);
		if (player != null)
			playerTypeComboBox.removeItem(player.getPlayerType());
		playerTypeComboBox.setSelectedItem(0);
		getContentPane().add(playerTypeComboBox, c);
		
		c.anchor = GridBagConstraints.EAST;
		c.gridy++;
		CustomButton okButton = new CustomButton("OK") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				changedPlayer = (Player) playerComboBox.getSelectedItem();
				oldPlayerType = changedPlayer.getPlayerType();
				PlayerType newPlayerType = (PlayerType) playerTypeComboBox.getSelectedItem();
				if (oldPlayerType == newPlayerType)
					changedPlayer = null;
				else
					changedPlayer.setPlayerType(newPlayerType);
				dispose();
			}			
		};
		getContentPane().add(okButton, c);
		
		refresh();
	}
	
	/**
	 * Returns the player whose type was changed.
	 * @return the player whose type was changed; null if player's type didn't change
	 */
	public Player getChangedPlayer() {
		return changedPlayer;
	}
	
	/**
	 * Returns the type that the player was before the change.
	 * @return the type that the player was before the change
	 */
	public PlayerType getOldPlayerType() {
		return oldPlayerType;
	}
}
