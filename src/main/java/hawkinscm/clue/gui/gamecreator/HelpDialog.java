package hawkinscm.clue.gui.gamecreator;

import hawkinscm.clue.gui.CustomDialog;
import hawkinscm.clue.gui.ImageHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * Dialog for displaying instructions/tips/help on creating custom CLUE games.
 */
public class HelpDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new Help Dialog.
	 * @param owner parent/owner of this dialog
	 */
	public HelpDialog(JDialog owner) {
		super(owner, "Clue Board Creation Help");		
		setModal(false);		
		
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.5;
		c.insets = new Insets(0, 0, 0, 0);
		c.anchor = GridBagConstraints.NORTHWEST;
		
		c.weightx = 0;		
		JLabel toolHeaderLabel = new JLabel("TOOL");
		toolHeaderLabel.setHorizontalAlignment(JLabel.CENTER);
		toolHeaderLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		toolHeaderLabel.setOpaque(true);
		toolHeaderLabel.setBackground(Color.CYAN);
		add(toolHeaderLabel, c);
		
		c.gridy++;
		add(createIconLabel(" Edit Room ", ImageHelper.getIcon(ImageHelper.ImageType.ROOM, ImageHelper.ImageSize.MEDIUM)), c);
		
		c.gridy++;
		add(createIconLabel(" Edit Room Doors ", ImageHelper.getIcon(ImageHelper.ImageType.DOOR, ImageHelper.ImageSize.MEDIUM)), c);
		
		c.gridy++;
		add(createIconLabel(" Remove Tiles ", ImageHelper.getIcon(ImageHelper.ImageType.REMOVE_TILE, ImageHelper.ImageSize.MEDIUM)), c);
		
		c.gridy++;
		add(createIconLabel(" Edit Passages ", ImageHelper.getIcon(ImageHelper.ImageType.PASSAGE, ImageHelper.ImageSize.MEDIUM)), c);
		
		c.gridy++;
		add(createIconLabel(" Edit Player Starts ", ImageHelper.getIcon(ImageHelper.ImageType.FOOTSTEPS, ImageHelper.ImageSize.MEDIUM)), c);
		
		c.gridy++;
		add(createIconLabel(" Undo ", ImageHelper.getUndo()), c);
		
		c.gridy++;
		add(createIconLabel(" Redo ", ImageHelper.getRedo()), c);
		
		c.gridy++;
		add(createIconLabel(" Save Changes ", ImageHelper.getDisk()), c);
		
		c.gridy++;
		add(createIconLabel(" Validate ", ImageHelper.getCheck()), c);
		
		c.gridy = 0;
		c.gridx = 1;
		c.weightx = 0.5;
		JLabel descriptionHeaderLabel = new JLabel("DESCRIPTION");
		descriptionHeaderLabel.setHorizontalAlignment(JLabel.CENTER);
		descriptionHeaderLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		descriptionHeaderLabel.setOpaque(true);
		descriptionHeaderLabel.setBackground(Color.CYAN);
		add(descriptionHeaderLabel, c);
		
		c.gridy++;
		String roomText = "<html>" + 
						  "Use the Edit Room tool to create rooms, add tiles to existing rooms, and remove tiles from rooms. " +
						  "Use the Room Selection toolbar found on the left-hand side of the board creation window under the standard " +
						  "toolbar to select a room to edit.  Left-click to add tiles to the board for that room.  Right-click to " +
						  "remove tiles for the selected room.  You can also click and drag to add or remove multiple tiles.  Room " +
						  "tiles must be connected by at least one adjacent tile.  You cannot place a room tile on top of another room " +
						  "tile or on top of a removed tile unless you first clear that tile." +
						  "</html>";
		
		add(createTextLabel(roomText), c);
		
		c.gridy++;
		String doorText = "<html>" + 
		  				  "Use the Edit Room Door tool to add doors to and remove doors from existing rooms.  Press and hold the mouse " +
		  				  "button down on one side of a room wall, then release the mouse button on the other side of a wall to edit a " +
		  				  "door.  Use the left mouse button to add doors.  Use the right mouse button to remove doors.  Doors cannot " +
		  				  "open out onto removed tiles." +
						  "</html>";
		add(createTextLabel(doorText), c);
		
		c.gridy++;
		String removeTileText = "<html>" + 
		  						"Use the Remove Tile tool to remove tiles from the board or to restore them once they have been removed. " +
		  						"Left-click to remove tiles from the board.  Right-click to restore tiles that have been removed.  You " +
		  						"can also click and drag to remove or restore multiple tiles.  You cannot remove a tile that is part of " +
		  						"a room or has a passage, a door, or a player start unless you first clear the tile of these elements." +
		  						"</html>";
		add(createTextLabel(removeTileText), c);
		
		c.gridy++;
		String passageText = "<html>" + 
							 "Use the Passage tool to create, alter, or remove secret passages and stairways.  To create a passage, press " +
							 "and hold down the left mouse button on the tile where you would like to start the passage, then release it on " +
							 "the tile where you want to end it. To alter a passage, press and hold down the left mouse button on the passage " +
							 "end you would like to keep then release the mouse button on a new tile where you want it to end.  To remove a " +
							 "passage and its connection, right-click on either end of the passage.  Passages cannot be placed on removed " +
							 "tiles, player start tiles, or other passages unless these elements are first removed." +
							 "</html>";
		add(createTextLabel(passageText), c);
		
		c.gridy++;
		String playerStartText = "<html>" + 
		 						 "Use the Player Start tool to add or remove player start tiles.  These are the tiles where suspects will be " +
		 						 "placed at the beginning of each game.  These are not required and if not included, suspects/players will be " +
		 						 "positioned randomly at the beginning of each game.  To select a suspect for player start tile editing, use " +
		 						 "the Suspect Selection toolbar found on the left-hand side of the board creation window under the standard " +
		 						 "toolbar.  Left-click on a tile to add a start tile for the selected Suspect.  If the start tile for that " +
		 						 "suspect is already on the board, it will be moved to the clicked tile.  Right-click on a start tile to " +
		 						 "remove it from the board.  Start tiles cannot be placed on removed tiles, passages, or other suspect start " +
		 						 "tiles unless you first remove those elements. Left-click again on a player start tile to change the image direction." +
		 						 "</html>";
		add(createTextLabel(playerStartText), c);
		
		c.gridy++;
		String undoText = "<html>" + 
		 				  "Use the Undo button to undo the last change made.  The only changes you will not be able to undo are changes to " +
		 				  "the names or sizes of rooms and suspects (which can only be done from the Cards Tab).  Ctrl+Z is the keyboard " +
		 				  "shortcut for undo." +
		 				  "</html>";
		add(createTextLabel(undoText), c);
		
		c.gridy++;
		String redoText = "<html>" + 
		  				  "Use the Redo button to redo the last change undone.  Ctrl+Y is the keyboard shortcut for redo." +
		  				  "</html>";
		add(createTextLabel(redoText), c);
		
		c.gridy++;
		String saveText = "<html>" + 
		  				  "Use the Save Changes button to write your created Clue game to a file.  This will be disabled while no board changes " +
		  				  "are detected since the last save." +
		  				  "</html>";
		add(createTextLabel(saveText), c);
		
		c.gridy++;
		String validateText = "<html>" + 
		  					  "Use the Validate button to verify that this is a completed, playable Clue game.  If there is anything that needs " +
		  					  "to be corrected or completed, a message will display with this information.  The following will be checked: Rooms " +
		  					  "must have at least 2 non-passage spaces; the number of rooms on the board must match the number specified on the " +
		  					  "Cards Tab; the number of player start tiles must match the number specified on the Cards Tab OR there must not be " +
		  					  "any at all; and passage connections that connect to each other cannot be in the same room." +
		  					  "</html>";
		add(createTextLabel(validateText), c);
		
		setPreferredSize(new Dimension(700, 800));
		refresh();
	}
	
	/**
	 * Creates and returns a custom formatted label with the given text and icon.
	 * @param text text for the label
	 * @param icon image icon for the label
	 * @return a custom formatted label with the given text and icon
	 */
	private JLabel createIconLabel(String text, Icon icon) {
		JLabel iconLabel = new JLabel(text, icon, JLabel.CENTER);
		iconLabel.setHorizontalTextPosition(JLabel.CENTER);
		iconLabel.setVerticalTextPosition(JLabel.BOTTOM);
		iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		iconLabel.setOpaque(true);
		iconLabel.setBackground(Color.WHITE);
		return iconLabel;
	}
	
	/**
	 * Creates and returns a custom formatted label with the given text.
	 * @param text text for the label
	 * @return a custom formatted label with the given text
	 */
	private JLabel createTextLabel(String text) {
		JLabel iconLabel = new JLabel(text);
		iconLabel.setVerticalAlignment(JLabel.TOP);
		iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		iconLabel.setOpaque(true);
		iconLabel.setBackground(Color.WHITE);
		return iconLabel;
	}
}
