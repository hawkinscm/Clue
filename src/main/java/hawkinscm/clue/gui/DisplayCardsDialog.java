package hawkinscm.clue.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import hawkinscm.clue.model.Card;

/**
 * Dialog for displaying a player's cards.
 */
public class DisplayCardsDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private static final int MAX_ROW_CARDS = 3;
	
	/**
	 * Constructor for creating a Display Cards Dialog
	 * @param owner parent/owner of this dialog
	 * @param cards cards to display
	 */
	public DisplayCardsDialog(JFrame owner, List<Card> cards) {
		super(owner, "Player Cards");
		setModal(false);
		setResizable(true);
		
		if (cards.isEmpty())
			return;
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy((cards.size() > MAX_ROW_CARDS) ? JScrollPane.VERTICAL_SCROLLBAR_ALWAYS : JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel cardPanel = new JPanel();
		int numRows = ((cards.size() - 1) / MAX_ROW_CARDS) + 1;
		cardPanel.setLayout(new GridLayout(numRows, MAX_ROW_CARDS, 5, 5));
		for (Card card : cards)
			cardPanel.add(new CardDisplay(card));
		
		scrollPane.getViewport().add(cardPanel);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		getContentPane().add(scrollPane, c);
		
		int numRowCards = (cards.size() >= MAX_ROW_CARDS) ? (cards.size() == 4) ? 2 : MAX_ROW_CARDS : cards.size();
		final int width = numRowCards * (CardDisplay.CARD_WIDTH + 30);
		setSize(new Dimension(width, CardDisplay.CARD_HEIGHT + 50 + ((cards.size() > MAX_ROW_CARDS) ? 30 : 0)));  
		setMinimumSize(new Dimension(width, 10));
		setLocationRelativeTo(owner);
		
		final int maxHeight = (CardDisplay.CARD_HEIGHT + 50) * numRows;
		addComponentListener(new ComponentListener() { 
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				setSize(new Dimension(width, Math.min(getSize().height, maxHeight)));
			}
			public void componentShown(ComponentEvent e) {}
        }); 
	}
}
