package gui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import model.Card;
import model.Player;
import model.Randomizer;

/**
 * Dialog for handling the extra cards that remain after the deal cards are distributed evenly.
 */
public class ExtraCardsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Extra Cards Dialog
	 * @param parent parent/owner of this dialog
	 * @param players players in this CLUE game
	 * @param extraCards the cards left over after the deck was evenly distributed to each player.
	 */
	public ExtraCardsDialog(JFrame parent, final List<Player> players, final List<Card> extraCards) {
		super(parent, "Extra Cards");
		
		final List<Player> cardHoldingPlayers = new ArrayList<Player>();
		for (Player player : players)
			if (player.isCardHoldingPlayer())
				cardHoldingPlayers.add(player);
		
		final List<Player> remainingPlayers = new ArrayList<Player>(cardHoldingPlayers.size());
		remainingPlayers.addAll(cardHoldingPlayers);
		
		c.gridwidth = 2;
		final JLabel promptLabel = new JLabel("There are " + extraCards.size() + " extra cards remaining.");
		getContentPane().add(promptLabel, c);
		c.gridwidth = 1;
		
		c.gridy++;
		JPanel pickPlayerPanel = new JPanel(new GridLayout(cardHoldingPlayers.size(), 1, 5, 5));
		final ButtonGroup playerGroup = new ButtonGroup();
		for (Player player : cardHoldingPlayers) {
			JRadioButton playerOption = new JRadioButton(player.getName());
			playerGroup.add(playerOption);
			pickPlayerPanel.add(playerOption);
		}
		getContentPane().add(pickPlayerPanel, c);
		
		c.gridx++;
		CustomButton giveCardButton = new CustomButton("Give Card") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				Enumeration<AbstractButton> radioButtons = playerGroup.getElements();
				for (int buttonIdx = 0; buttonIdx < playerGroup.getButtonCount(); buttonIdx++) {
					if (radioButtons.nextElement().isSelected()) {
						Player player = cardHoldingPlayers.get(buttonIdx);
						player.getCards().add(extraCards.remove(0));
						remainingPlayers.remove(player);
						break;
					}
				}
				
				if (extraCards.isEmpty())
					dispose();
				else
					promptLabel.setText("There are " + extraCards.size() + " extra cards remaining.");
			}
		};
		getContentPane().add(giveCardButton, c);
				
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		CustomButton randomlyDistributeButton = new CustomButton("Randomly Distribute Remaining") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				String info = "<html>";
				while (extraCards.size() > 0) {
					Player player = remainingPlayers.remove(Randomizer.getRandom(remainingPlayers.size()));
					player.getCards().add(extraCards.remove(0));
					info += player.getName() + " received a card." + "<br>";
				}
				info += "</html>";
				Messenger.display(info, "Randomly Distributed Remaining");
				dispose();
			}
		};
		getContentPane().add(randomlyDistributeButton, c);
		c.gridwidth = 1;
		
		refresh();
	}
}
