package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Card;

/**
 * Panel for displaying a card.
 */
public class CardDisplay extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public final static int CARD_WIDTH = 189;
	public final static int CARD_HEIGHT = 264;
	
	private final Color BACKGROUND_COLOR = Color.WHITE;
	
	private Card card;
		
	/**
	 * Creates new card display for the given card.
	 * @param card card to display
	 */
	public CardDisplay(Card card) {
		this.card = card;
		
		initialize();
	}

	/**
	 * Initialize Card Display
	 */
	private void initialize() {
		setLayout(new GridBagLayout());
		setBackground(BACKGROUND_COLOR);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0,0,0,0);
		setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		ImageIcon icon = null;
		if (card.getRoom() != null) {
			add(new CardLabel("ROOM", 6), c);
			c.gridy++;
			int fontSize = (card.getRoom().getName().length() <= 20) ? 5 : ((card.getRoom().getName().length() <= 26) ? 4 : 3);
			add(new CardLabel(card.getRoom().getName(), fontSize), c);
			icon = ImageHelper.getTransparentIcon(card.getRoom().getPictureName(), card.getRoom().getTransparentPictureColor());
			if (icon == null)
				icon = ImageHelper.getGenericRoom();
		}
		else if (card.getSuspect() != null) {
			add(new CardLabel("SUSPECT", 6), c);
			c.gridy++;
			int fontSize = (card.getSuspect().getName().length() <= 20) ? 5 : ((card.getSuspect().getName().length() <= 26) ? 4 : 3);
			add(new CardLabel(card.getSuspect().getName(), fontSize), c);
			icon = ImageHelper.getIcon(card.getSuspect().getPictureName());
			if (icon == null)
				icon = ImageHelper.getGenericSuspect();
		}
		else if (card.getWeapon() != null) {
			add(new CardLabel("WEAPON", 6), c);
			c.gridy++;
			int fontSize = (card.getWeapon().getName().length() <= 20) ? 5 : ((card.getWeapon().getName().length() <= 26) ? 4 : 3);
			add(new CardLabel(card.getWeapon().getName(), fontSize), c);
			icon = ImageHelper.getIcon(card.getWeapon().getPictureName());
			if (icon == null)
				icon = ImageHelper.getGenericWeapon();
		}

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridy++;
		add(new CardLabel(getResizedIcon(icon)), c);
	}
	
	/**
	 * Resizes the card's image proportionately to fit the card and returns the image icon.
	 * @param icon image icon to resize
	 * @return the resized card image icon
	 */
	public ImageIcon getResizedIcon(ImageIcon icon) {
		double width = CARD_WIDTH - 20;
		double height = CARD_HEIGHT - 100;
		double widthAdjustment = width / (double)icon.getIconWidth();
		double heightAdjustment = height / (double)icon.getIconHeight();
		double sizeAdjustment = Math.min(heightAdjustment, widthAdjustment);

		int newWidth = (int)((double)icon.getIconWidth() * sizeAdjustment);
		int newHeight = (int)((double)icon.getIconHeight() * sizeAdjustment);
		Image resizedImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(resizedImage);
	}
	
	/**
	 * JLabel for correctly displaying text and image on card.
	 */
	private class CardLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		
		public CardLabel(String text, int fontSize) {
			super("<html><center><font size=\"" + fontSize + "\">" + text + "</font></center></html>");
			setup();
		}
		
		public CardLabel(ImageIcon icon) {
			super(icon);
			setup();
		}
		
		private void setup() {
			setOpaque(true);
			setBackground(BACKGROUND_COLOR);
		}
	}
}
