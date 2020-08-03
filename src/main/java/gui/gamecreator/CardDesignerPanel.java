package gui.gamecreator;

import gui.CustomButton;
import gui.ImageHelper;
import gui.Messenger;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.FileCopyHelper;
import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Panel used to Design the Rooms, Suspects, Weapons, Story, and Cards of a CLUE game.
 */
public class CardDesignerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final int MIN_ROOMS = 6;
	private final int MAX_ROOMS = 12;
	private final int MIN_SUSPECTS = 6;
	private final int MAX_SUSPECTS = 8;
	private final Color[] DEFAULT_COLORS = {Color.decode("0x8B0000"), Color.YELLOW, Color.WHITE, Color.decode("0x006400"), Color.BLUE, Color.decode("0x4B0082"), Color.CYAN, Color.MAGENTA};
	private final int MIN_WEAPONS = 6;
	private final int MAX_WEAPONS = 10;
	
	private static File defaultDirectory = new File("images"); 
	
	private JComboBox<Object[]> numRoomsComboBox;
	private JTextField[] roomTextFields;
	private CustomButton[] roomPictureButtons;
	private CustomButton[] roomTransparentColorButtons;
	private JLabel roomColorTextLabel;
	private JCheckBox displayRoomPicturesOnBoardCheckBox;

	private JComboBox<Object[]> numSuspectsComboBox;
	private JTextField[] suspectTextFields;
	private CustomButton[] suspectPictureButtons;
	private CustomButton[] suspectColorButtons;

	private JComboBox<Object[]> numWeaponsComboBox;
	private JTextField[] weaponTextFields;
	private CustomButton[] weaponPictureButtons;
	
	private int numRooms = 9;
	private Room[] rooms;
	
	private int numSuspects = 6;
	private Suspect[] suspects;
	
	private int numWeapons = 6;
	private Weapon[] weapons;
	
	private CustomButton backgroundImageButton;
	private CustomButton backgroundColorButton;
	private JRadioButton imageOption;
	private String backgroundImageFilename;
	
	private JTextArea storyTextArea;
		
	/**
	 * Creates a new Card Designer Panel.
	 */
	public CardDesignerPanel() {
		rooms = new Room[MAX_ROOMS];
		for (int idx = 0; idx < MAX_ROOMS; idx++)
			rooms[idx] = new Room((idx + 1), "Room #" + (idx + 1));
		
		suspects = new Suspect[MAX_SUSPECTS];
		for (int idx = 0; idx < MAX_SUSPECTS; idx++)
			suspects[idx] = new Suspect((idx + 1), "Suspect #" + (idx + 1), DEFAULT_COLORS[idx]);
		
		weapons = new Weapon[MAX_WEAPONS];
		for (int idx = 0; idx < MAX_WEAPONS; idx++)
			weapons[idx] = new Weapon((idx + 1), "Weapon #" + (idx + 1));
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.NORTH;
		
		add(createNumRoomsPanel(), c);
		
		c.gridy++;
		add(createRoomDetailsPanel(), c);
		
		c.gridy--;
		c.gridx++;
		add(createNumSuspectsPanel(), c);
		
		c.gridy++;
		add(createSuspectDetailsPanel(), c);
		
		c.gridy--;
		c.gridx++;
		add(createNumWeaponsPanel(), c);
		
		c.gridy++;
		add(createWeaponDetailsPanel(), c);
				
		c.gridx = 0;
		c.gridy++;
		c.anchor = GridBagConstraints.CENTER;
		CustomButton pictureHelpButton = new CustomButton("Instructions & Tips For Pictures") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				String message = "<html>" +
								 "The images selected for rooms, suspects, and weapons will be displayed on their corresponding cards.  The images " + "<br>" +
								 "selected for rooms can optionally be displayed on the board for that room during game play.  If you choose to " + "<br>" +
								 "display these on the board there are a few things to note.  Firstly, each room will allow you to select a " + "<br>" +
								 "transparent color.  This color will be transparent on the board so that anywhere this color is found in your " + "<br>" +
								 "image, you will not see it but will see the board underneath it.  This will allow you to use images for rooms " + "<br>" +
								 "that are not rectangular and to display the default image of a passage if you so desire.  Although the images do " + "<br>" +
								 "not need to be exact size, they should be proportionate to the rooms they represent so that the image will not " + "<br>" +
								 "appear squashed or streched.  Lastly, use PNG or GIF images for displaying rooms on the board since JPEGs are " + "<br>" +
								 "lossy and will not keep the transparent colors you create.  Make sure image is transparent on non-room tiles." + "<br>" +
								 "<br>" +
								 "For the background--what is shown where tiles have been removed--you can select a single color or you can select " + "<br>" +
								 "an image.  If you select an image it will use the color as the transparent color similar to what the room images " + "<br>" +
								 "use.  However, if you use an image you must use the transparent color everywhere you do not want the image to " + "<br>" +
								 "show.  The \"background\" image will actually overlay the board and you will not see any of the board except " + "<br>" +
								 "where the tranparent color is on the image.  One of the easiest ways to create such an image is to take a screen- " + "<br>" +
								 "shot of the board when you have finished creating it, open a paint program and paste it in, then replace everything " + "<br>" +
								 "but the removed tiles with the transparent color.  Then you are free to replace what is left with the image(s) you " + "<br>" +
								 "want to show through.  It's a bit tricky but will give the board a more real look than a simple color.  Remember to " + "<br>" +
								 "use PNG or GIF images and not JPEGs.  The image should be transparent on all normal, walkable tiles." +
								 "</html>";
				Messenger.display(message, "Instructions & Tips For Pictures", CardDesignerPanel.this);
			}
		};
		add(pictureHelpButton, c);
		c.anchor = GridBagConstraints.NORTH;
		
		c.gridx++;
		add(createBackgroundPanel(), c);
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0.5;
		String defaultStory = "Mr. Boddy--apparently the victim of foul play--is found dead in one of the rooms of his mansion.";
		storyTextArea = new JTextArea(defaultStory);
		storyTextArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Custom Story"));
		storyTextArea.setLineWrap(true);
		add(storyTextArea, c);
	}
	
	/**
	 * Creates and returns a panel which allows the user to specify the number of rooms to have in the game.
	 * @return a panel which allows the user to specify the number of rooms to have in the game
	 */
	private JPanel createNumRoomsPanel() {
		JPanel numRoomsPanel = new JPanel();
		numRoomsPanel.add(new JLabel("Number of Rooms:"));
		
		LinkedList<Integer> roomOptions = new LinkedList<Integer>();
		for (int option = MIN_ROOMS; option <= MAX_ROOMS; option++)
			roomOptions.add(option);
		numRoomsComboBox = new JComboBox(roomOptions.toArray());
		numRoomsComboBox.setSelectedItem(9);
		numRoomsComboBox.addActionListener(e -> setNumRooms((Integer) ((JComboBox) e.getSource()).getSelectedItem()));
		numRoomsPanel.add(numRoomsComboBox);
		
		return numRoomsPanel;
	}
	
	/**
	 * Creates and returns a panel which allows the user to input the details of each room.
	 * @return a panel which allows the user to input the details of each room
	 */
	private JPanel createRoomDetailsPanel() {
		JPanel roomDetailsPanel = new JPanel(new GridBagLayout());
		roomDetailsPanel.setBorder(BorderFactory.createTitledBorder("Rooms"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 10, 5, 10);
		
		roomTextFields = new JTextField[MAX_ROOMS];
		roomPictureButtons = new CustomButton[MAX_ROOMS];
		roomTransparentColorButtons = new CustomButton[MAX_ROOMS];
		
		roomDetailsPanel.add(new JLabel("Name"), c);
		
		c.gridx++;
		roomDetailsPanel.add(new JLabel("Picture"), c);
		
		c.gridx++;
		roomColorTextLabel = new JLabel("Color");
		roomColorTextLabel.setVisible(false);
		roomDetailsPanel.add(roomColorTextLabel, c);
		
		for(int roomIndex = 0; roomIndex < MAX_ROOMS; roomIndex++) {
			c.gridx = 0;
			c.gridy++;
			
			roomTextFields[roomIndex] = new JTextField(10);
			roomDetailsPanel.add(roomTextFields[roomIndex], c);
			
			c.gridx++;
			final int currentIndex = roomIndex;
			roomPictureButtons[roomIndex] = new CustomButton(ImageHelper.getCamera()) {
				private static final long serialVersionUID = 1L;
				public void buttonClicked() {
					JFileChooser chooser = new JFileChooser(defaultDirectory);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, PNG, & GIF Images", "jpg", "png", "gif");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(this);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	String filename = chooser.getSelectedFile().getName();
				    	try {
				    		filename = FileCopyHelper.copyFile(chooser.getSelectedFile().getAbsolutePath(), "images/" + filename);
				    	}
				    	catch (IOException ex) {
				    		Messenger.error("Failed to copy picture file to game image folder. Picture cannot be used: " + ex.getMessage(), "Image File Copy Error");
				    		return;
				    	}
				    	rooms[currentIndex].setPictureName(filename);
				    	Image resizedImage = ImageHelper.getIcon(filename).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
						setIcon(new ImageIcon(resizedImage));
				    	defaultDirectory = chooser.getSelectedFile().getParentFile();
				    }
				}
			};
			roomPictureButtons[roomIndex].setFocusable(false);
			roomDetailsPanel.add(roomPictureButtons[roomIndex], c);
			
			c.gridx++;
			roomTransparentColorButtons[roomIndex] = new CustomButton(" ") {
				private static final long serialVersionUID = 1L;
				public void buttonClicked() {
					Color newColor = JColorChooser.showDialog(null, "Choose Transparent Color For Room", this.getBackground());
					if (newColor != null)
						this.setBackground(newColor);
				}
			};
			roomTransparentColorButtons[roomIndex].setBackground(Color.WHITE);
			roomTransparentColorButtons[roomIndex].setFocusable(false);
			roomTransparentColorButtons[roomIndex].setVisible(false);
			roomDetailsPanel.add(roomTransparentColorButtons[roomIndex], c);
			
			if (roomIndex >= numRooms) {
				roomTextFields[roomIndex].setVisible(false);
				roomPictureButtons[roomIndex].setVisible(false);
			}
		}
		
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 3;
		displayRoomPicturesOnBoardCheckBox = new JCheckBox("Display Room Pictures On Board");
		displayRoomPicturesOnBoardCheckBox.addActionListener(e -> {
			roomColorTextLabel.setVisible(displayRoomPicturesOnBoardCheckBox.isSelected());
			for (int roomIndex = 0; roomIndex < numRooms; roomIndex++)
				roomTransparentColorButtons[roomIndex].setVisible(displayRoomPicturesOnBoardCheckBox.isSelected());
		});
		roomDetailsPanel.add(displayRoomPicturesOnBoardCheckBox, c);
		
		return roomDetailsPanel;
	}
	
	/**
	 * Creates and returns a panel which allows the user to specify the number of suspects to have in the game.
	 * @return a panel which allows the user to specify the number of suspects to have in the game
	 */
	private JPanel createNumSuspectsPanel() {
		JPanel numSuspectsPanel = new JPanel();
		numSuspectsPanel.add(new JLabel("Number of Suspects:"));
		
		LinkedList<Integer> suspectOptions = new LinkedList<Integer>();
		for (int option = MIN_SUSPECTS; option <= MAX_SUSPECTS; option++)
			suspectOptions.add(option);
		numSuspectsComboBox = new JComboBox(suspectOptions.toArray());
		numSuspectsComboBox.setSelectedItem(6);
		numSuspectsComboBox.addActionListener(e -> setNumSuspects((Integer) ((JComboBox) e.getSource()).getSelectedItem()));
		numSuspectsPanel.add(numSuspectsComboBox);
		
		return numSuspectsPanel;
	}
	
	/**
	 * Creates and returns a panel which allows the user to input the details of each suspect.
	 * @return a panel which allows the user to input the details of each suspect
	 */
	private JPanel createSuspectDetailsPanel() {
		JPanel suspectDetailsPanel = new JPanel(new GridBagLayout());
		suspectDetailsPanel.setBorder(BorderFactory.createTitledBorder("Suspects"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 10, 5, 10);
		
		suspectTextFields = new JTextField[MAX_SUSPECTS];
		suspectColorButtons = new CustomButton[MAX_SUSPECTS];
		suspectPictureButtons = new CustomButton[MAX_SUSPECTS];
		
		suspectDetailsPanel.add(new JLabel("Name"), c);
		
		c.gridx++;
		suspectDetailsPanel.add(new JLabel("Color"), c);
		
		c.gridx++;
		suspectDetailsPanel.add(new JLabel("Picture"), c);
		
		for(int suspectIndex = 0; suspectIndex < MAX_SUSPECTS; suspectIndex++) {
			c.gridx = 0;
			c.gridy++;
			
			suspectTextFields[suspectIndex] = new JTextField(10);
			suspectDetailsPanel.add(suspectTextFields[suspectIndex], c);
			
			c.gridx++;
			suspectColorButtons[suspectIndex] = new CustomButton(" ") {
				private static final long serialVersionUID = 1L;
				public void buttonClicked() {
					Color newColor = JColorChooser.showDialog(null, "Choose Suspect Color", this.getBackground());
					if (newColor != null)
						this.setBackground(newColor);
				}
			};
			suspectColorButtons[suspectIndex].setBackground(DEFAULT_COLORS[suspectIndex]);
			suspectColorButtons[suspectIndex].setFocusable(false);
			suspectDetailsPanel.add(suspectColorButtons[suspectIndex], c);
			
			c.gridx++;
			final int currentIndex = suspectIndex;
			suspectPictureButtons[suspectIndex] = new CustomButton(ImageHelper.getCamera()) {
				private static final long serialVersionUID = 1L;
				public void buttonClicked() {
					JFileChooser chooser = new JFileChooser(defaultDirectory);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, PNG, & GIF Images", "jpg", "png", "gif");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(this);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	String filename = chooser.getSelectedFile().getName();
				    	try {
				    		filename = "images/" + FileCopyHelper.copyFile(chooser.getSelectedFile().getAbsolutePath(), "images/" + filename);
				    	}
				    	catch (IOException ex) {
				    		Messenger.error("Failed to copy picture file to game image folder. Picture cannot be used: " + ex.getMessage(), "Image File Copy Error");
				    		return;
				    	}
				    	suspects[currentIndex].setPictureName(filename);
				    	Image resizedImage = ImageHelper.getIcon(filename).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
						setIcon(new ImageIcon(resizedImage));
				    	defaultDirectory = chooser.getSelectedFile().getParentFile();
				    }
				 
				}
			};
			suspectPictureButtons[suspectIndex].setFocusable(false);
			suspectDetailsPanel.add(suspectPictureButtons[suspectIndex], c);
			
			if (suspectIndex >= numSuspects) {
				suspectTextFields[suspectIndex].setVisible(false);
				suspectPictureButtons[suspectIndex].setVisible(false);
				suspectColorButtons[suspectIndex].setVisible(false);
			}
		}
		
		return suspectDetailsPanel;
	}
	
	/**
	 * Creates and returns a panel which allows the user to specify the number of weapons to have in the game.
	 * @return a panel which allows the user to specify the number of weapons to have in the game
	 */
	private JPanel createNumWeaponsPanel() {
		JPanel numWeaponsPanel = new JPanel();
		numWeaponsPanel.add(new JLabel("Number of Weapons:"));
		
		LinkedList<Integer> weaponOptions = new LinkedList<Integer>();
		for (int option = MIN_WEAPONS; option <= MAX_WEAPONS; option++)
			weaponOptions.add(option);
		numWeaponsComboBox = new JComboBox(weaponOptions.toArray());
		numWeaponsComboBox.setSelectedItem(6);
		numWeaponsComboBox.addActionListener(e -> setNumWeapons((Integer) ((JComboBox) e.getSource()).getSelectedItem()));
		numWeaponsPanel.add(numWeaponsComboBox);
		
		return numWeaponsPanel;
	}
	
	/**
	 * Creates and returns a panel which allows the user to input the details of each weapon.
	 * @return a panel which allows the user to input the details of each weapon
	 */
	private JPanel createWeaponDetailsPanel() {
		JPanel weaponDetailsPanel = new JPanel(new GridBagLayout());
		weaponDetailsPanel.setBorder(BorderFactory.createTitledBorder("Weapons"));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 10, 5, 10);
		
		weaponTextFields = new JTextField[MAX_WEAPONS];
		weaponPictureButtons = new CustomButton[MAX_WEAPONS];
		
		weaponDetailsPanel.add(new JLabel("Name"), c);
		c.gridx++;
		weaponDetailsPanel.add(new JLabel("Picture"), c);
		
		for(int weaponIndex = 0; weaponIndex < MAX_WEAPONS; weaponIndex++) {
			c.gridx = 0;
			c.gridy++;
			
			weaponTextFields[weaponIndex] = new JTextField(10);
			weaponDetailsPanel.add(weaponTextFields[weaponIndex], c);
			
			c.gridx++;
			final int currentIndex = weaponIndex;
			weaponPictureButtons[weaponIndex] = new CustomButton(ImageHelper.getCamera()) {
				private static final long serialVersionUID = 1L;
				public void buttonClicked() {
					JFileChooser chooser = new JFileChooser(defaultDirectory);
					FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, PNG, & GIF Images", "jpg", "png", "gif");
					chooser.setFileFilter(filter);
					int returnVal = chooser.showOpenDialog(this);
				    if (returnVal == JFileChooser.APPROVE_OPTION) {
				    	String filename = chooser.getSelectedFile().getName();
				    	try {
				    		filename = FileCopyHelper.copyFile(chooser.getSelectedFile().getAbsolutePath(), "images/" + filename);
				    	}
				    	catch (IOException ex) {
				    		Messenger.error("Failed to copy picture file to game image folder. Picture cannot be used: " + ex.getMessage(), "Image File Copy Error");
				    		return;
				    	}
				    	weapons[currentIndex].setPictureName(filename);
				    	Image resizedImage = ImageHelper.getIcon(filename).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
						setIcon(new ImageIcon(resizedImage));
				    	defaultDirectory = chooser.getSelectedFile().getParentFile();
				    }
				 
				}
			};
			weaponPictureButtons[weaponIndex].setFocusable(false);
			weaponDetailsPanel.add(weaponPictureButtons[weaponIndex], c);
			
			if (weaponIndex >= numWeapons) {
				weaponTextFields[weaponIndex].setVisible(false);
				weaponPictureButtons[weaponIndex].setVisible(false);
			}
		}
		
		return weaponDetailsPanel;
	}
	
	/**
	 * Creates and returns a panel which allows the user to set the details (picture or color) of the CLUE board background.
	 * @return a panel which allows the user to set the details (picture or color) of the CLUE board background
	 */
	private JPanel createBackgroundPanel() {
		JPanel backgroundPanel = new JPanel();
		backgroundPanel.setBorder(BorderFactory.createTitledBorder("Background Settings"));
		
		backgroundImageFilename = null;
		
		JPanel optionPanel = new JPanel();
		optionPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		ButtonGroup optionGroup = new ButtonGroup();
		JRadioButton colorOption = new JRadioButton("Color");
		colorOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backgroundImageButton.setVisible(false);
			}
		});
		imageOption = new JRadioButton("Image");
		imageOption.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backgroundImageButton.setVisible(true);
			}
		});
		optionGroup.add(colorOption);
		optionGroup.add(imageOption);
		optionPanel.add(colorOption);
		optionPanel.add(imageOption);
		backgroundPanel.add(optionPanel);
		colorOption.setSelected(true);
		
		backgroundImageButton = new CustomButton(ImageHelper.getCamera()) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				JFileChooser chooser = new JFileChooser(defaultDirectory);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG, PNG, & GIF Images", "jpg", "png", "gif");
				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(this);
			    if (returnVal == JFileChooser.APPROVE_OPTION) {
			    	String filename = chooser.getSelectedFile().getName();
			    	try {
			    		filename = FileCopyHelper.copyFile(chooser.getSelectedFile().getAbsolutePath(), "images/" + filename);
			    	}
			    	catch (IOException ex) {
			    		Messenger.error("Failed to copy picture file to game image folder. Picture cannot be used: " + ex.getMessage(), "Image File Copy Error");
			    		return;
			    	}
			    	backgroundImageFilename = filename;
			    	Image resizedImage = ImageHelper.getIcon(filename).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
					setIcon(new ImageIcon(resizedImage));
			    	defaultDirectory = chooser.getSelectedFile().getParentFile();
			    }
			}
		};
		backgroundImageButton.setFocusable(false);
		backgroundImageButton.setVisible(false);
		backgroundPanel.add(backgroundImageButton);
		
		backgroundColorButton = new CustomButton(" ") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				Color newColor = JColorChooser.showDialog(null, "Choose Background Color", this.getBackground());
				if (newColor != null)
					this.setBackground(newColor);
			}
		};
		backgroundColorButton.setBackground(Color.BLACK);
		backgroundColorButton.setFocusable(false);
		backgroundPanel.add(backgroundColorButton);
		
		return backgroundPanel;
	}
	
	/**
	 * Returns the rooms the player created.
	 * @return the rooms the player created
	 */
	public ArrayList<Room> getRooms() {
		ArrayList<Room> roomList = new ArrayList<Room>();
		for(int roomIdx = 0; roomIdx < numRooms; roomIdx++) {
			Room room = rooms[roomIdx];
			String roomName = roomTextFields[roomIdx].getText().trim();
			if (roomName.equals(""))
				roomName = "Room #" + (roomIdx + 1);
			room.setName(roomName);
			if (roomTransparentColorButtons[roomIdx].isVisible())
				room.setTransparentPictureColor(roomTransparentColorButtons[roomIdx].getBackground());
			roomList.add(room);
		}
		return roomList;
	}
	
	/**
	 * Sets the details of each room from a list of rooms.
	 * @param roomList rooms to set
	 */
	public void setRooms(List<Room> roomList) {
		setNumRooms(roomList.size());
		numRoomsComboBox.setSelectedItem(numRooms);
		for(int roomIdx = 0; roomIdx < numRooms; roomIdx++) {
			Room room = roomList.get(roomIdx);
			rooms[roomIdx] = room;
			roomTextFields[roomIdx].setText(room.getName());
			if (room.getPictureName() != null) {
				Image resizedImage = ImageHelper.getIcon(room.getPictureName()).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
				roomPictureButtons[roomIdx].setIcon(new ImageIcon(resizedImage));
			}
			if (room.getTransparentPictureColor() != null)
				roomTransparentColorButtons[roomIdx].setBackground(room.getTransparentPictureColor());
		}
	}

	private void setNumRooms(int newNumRooms) {
		if (newNumRooms == numRooms)
			return;
		
		numRooms = newNumRooms;
		for (int roomIndex = MIN_ROOMS; roomIndex < MAX_ROOMS; roomIndex++) {
			roomTextFields[roomIndex].setVisible(roomIndex < numRooms);
			roomPictureButtons[roomIndex].setVisible(roomIndex < numRooms);
			roomTransparentColorButtons[roomIndex].setVisible(roomIndex < numRooms && displayRoomPicturesOnBoardCheckBox.isSelected());
		}
		
		revalidate();
		repaint();
	}
		
	/**
	 * Returns the suspects the player created.
	 * @return the suspects the player created
	 */
	public ArrayList<Suspect> getSuspects() {
		ArrayList<Suspect> suspectList = new ArrayList<Suspect>();
		for(int suspectIdx = 0; suspectIdx < numSuspects; suspectIdx++) {
			Suspect suspect = suspects[suspectIdx];
			String suspectName = suspectTextFields[suspectIdx].getText().trim();
			if (suspectName.equals(""))
				suspectName = "Suspect #" + (suspectIdx + 1);
			suspect.setName(suspectName);
			suspect.setColor(suspectColorButtons[suspectIdx].getBackground());
			suspectList.add(suspect);
		}
		return suspectList;
	}
	
	/**
	 * Sets the details of each suspect from a list of suspects.
	 * @param suspectList suspects to set
	 */
	public void setSuspects(List<Suspect> suspectList) {
		setNumSuspects(suspectList.size());
		numSuspectsComboBox.setSelectedItem(numSuspects);
		for(int suspectIdx = 0; suspectIdx < numSuspects; suspectIdx++) {
			Suspect suspect = suspectList.get(suspectIdx);
			suspects[suspectIdx] = suspect;
			suspectTextFields[suspectIdx].setText(suspect.getName());
			suspectColorButtons[suspectIdx].setBackground(suspect.getColor());
			if (suspect.getPictureName() != null) {
				Image resizedImage = ImageHelper.getIcon(suspect.getPictureName()).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
				suspectPictureButtons[suspectIdx].setIcon(new ImageIcon(resizedImage));
			}
		}
	}
	
	private void setNumSuspects(int newNumSuspects) {
		if (newNumSuspects == numSuspects)
			return;
		
		numSuspects = newNumSuspects;
		for (int suspectIndex = MIN_SUSPECTS; suspectIndex < MAX_SUSPECTS; suspectIndex++) {
			boolean shouldShow = (suspectIndex < numSuspects);
			suspectTextFields[suspectIndex].setVisible(shouldShow);
			suspectPictureButtons[suspectIndex].setVisible(shouldShow);
			suspectColorButtons[suspectIndex].setVisible(shouldShow);
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Returns the weapons the player created.
	 * @return the weapons the player created
	 */
	public ArrayList<Weapon> getWeapons() {
		ArrayList<Weapon> weaponList = new ArrayList<Weapon>();
		for(int weaponIdx = 0; weaponIdx < numWeapons; weaponIdx++) {
			Weapon weapon = weapons[weaponIdx];
			String weaponName = weaponTextFields[weaponIdx].getText().trim();
			if (weaponName.equals(""))
				weaponName = "Weapon #" + (weaponIdx + 1);
			weapon.setName(weaponName);
			weaponList.add(weapon);
		}
		return weaponList;
	}
	
	/**
	 * Sets the details of each weapon from a list of weapons.
	 * @param weaponList weapons to set
	 */
	public void setWeapons(List<Weapon> weaponList) {
		setNumWeapons(weaponList.size());
		numWeaponsComboBox.setSelectedItem(numWeapons);
		for(int weaponIdx = 0; weaponIdx < numWeapons; weaponIdx++) {
			Weapon weapon = weaponList.get(weaponIdx);
			weapons[weaponIdx] = weapon;
			weaponTextFields[weaponIdx].setText(weapon.getName());
			if (weapon.getPictureName() != null) {
				Image resizedImage = ImageHelper.getIcon(weapon.getPictureName()).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
				weaponPictureButtons[weaponIdx].setIcon(new ImageIcon(resizedImage));
			}
		}
	}
	
	private void setNumWeapons(int newNumWeapons) {
		if (newNumWeapons == numWeapons)
			return;
		
		numWeapons = newNumWeapons;
		for (int weaponIndex = MIN_WEAPONS; weaponIndex < MAX_WEAPONS; weaponIndex++) {
			weaponTextFields[weaponIndex].setVisible(weaponIndex < numWeapons);
			weaponPictureButtons[weaponIndex].setVisible(weaponIndex < numWeapons);
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Returns whether or not the player wants the room pictures displayed on the board.
	 * @return true if the player wants the room pictures displayed on the board; false if the user wants the default blue tiles displayed for rooms.
	 */
	public boolean getDisplayRoomPicturesOnBoard() {
		return displayRoomPicturesOnBoardCheckBox.isSelected();
	}
	
	/**
	 * Sets whether or not the room pictures will be displayed on the board.
	 * @param displayRoomPicturesOnBoard value to set
	 */
	public void setDisplayRoomPicturesOnBoard(boolean displayRoomPicturesOnBoard) {
		displayRoomPicturesOnBoardCheckBox.setSelected(displayRoomPicturesOnBoard);
		roomColorTextLabel.setVisible(displayRoomPicturesOnBoardCheckBox.isSelected());
		for (int roomIndex = 0; roomIndex < numRooms; roomIndex++)
			roomTransparentColorButtons[roomIndex].setVisible(displayRoomPicturesOnBoardCheckBox.isSelected());
	}
	
	/**
	 * Returns the filename of the image to use as the CLUE board background.
	 * @return the filename of the image to use as the CLUE board background
	 */
	public String getBackgroundImageFilename() {
		return imageOption.isSelected() ? backgroundImageFilename : null;
	}
	
	/**
	 * Sets the filename of the image to use as the CLUE board background.
	 * @param filename filename of the image to use as the CLUE board background
	 */
	public void setBackgroundImageFilename(String filename) {
		if (filename == null || filename.isEmpty())
			return;
		
		backgroundImageFilename = filename;
		Image resizedImage = ImageHelper.getIcon(filename).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
		backgroundImageButton.setIcon(new ImageIcon(resizedImage));
		imageOption.setSelected(true);
		backgroundImageButton.setVisible(true);
	}
	
	/**
	 * Returns the color to use as the background color if the background image is not available.
	 * Will be used as the transparent color of the image if the image is available.
	 * @return the color to use as the background color
	 */
	public Color getBackgroundColor() {
		return backgroundColorButton.getBackground();
	}
	
	/**
	 * Sets the color to use as the background color if the background image is not available.
	 * Will be used as the transparent color of the image if the image is available.
	 * @param color the color to set as the background color
	 */
	public void setBackgroundColor(Color color) {
		backgroundColorButton.setBackground(color);
	}
	
	/**
	 * Returns the text of the CLUE game custom story.
	 * @return the text of the CLUE game custom story
	 */
	public String getStory() {
		return storyTextArea.getText();
	}
	
	/**
	 * Sets the text of the CLUE game custom story
	 * @param story story text to set
	 */
	public void setStory(String story) {
		storyTextArea.setText(story);
	}
}
