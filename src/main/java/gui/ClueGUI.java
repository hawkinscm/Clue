package gui;

import gui.gamecreator.GameDesignerDisplay;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import log.ErrorWriter;
import model.Board;
import model.Card;
import model.ClueGameData;
import model.Room;
import model.Suspect;
import model.Weapon;


/**
 * Main GUI for CLUE games.
 */
public abstract class ClueGUI extends JFrame implements Observer {
	private static final long serialVersionUID = 1L;
	
	private final String configFile = "Clue.cfg";
	
	protected JMenuItem newGameMenuItem;
	protected JMenuItem replacePlayerMenuItem;
	protected JMenuItem optionsMenuItem;
	private JMenu infoMenu;
	
	private JLabel messageLabel;
	private JTextArea messageTextArea;
	protected DisplayCardsDialog displayCardsDialog;
	private DisplayBoardPanel boardPanel; 
	private PlayPanel playPanel;
	
	protected String defaultIP;
	protected int numDice;
	protected boolean useFirstInListPlayerStart;
	protected boolean useDefinedSuspectLocations;
	
	protected Board board;
	private List<Room> rooms;
	private List<Suspect> suspects;
	private List<Weapon> weapons;
	private String story;
	private DetectiveNotepadDialog notepad;
	
	protected LinkedHashMap<String, Integer> playerCardCountMap;
	
	protected boolean controlledSuspectTransferred;
	
	protected boolean isGameOver;
	
	/**
	 * Creates a new CLUE GUI and begins a game.
	 */
	public ClueGUI() {
		super("CLUE");
		isGameOver = true;
		numDice = 1;
		useFirstInListPlayerStart = true;
		useDefinedSuspectLocations = true;
		playerCardCountMap = new LinkedHashMap<>();
		
		board = null;
		rooms = null;
		suspects = null;
		weapons = null;
		
		// Changes the default icon displayed in the title bar
		setIconImage(ImageHelper.getMGlass().getImage());
		
		setLocation(0, 0);
		setSize(800, 685);
		setMinimumSize(new Dimension(575, 600));
		setResizable(true);		
		
		// Cause the application to call a special exit method on close
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				exitProgram();
			}			
		});
						
		defaultIP = null;
		loadConfigFile();
				
		try {
			ImageHelper.loadImages();
		}
		catch (Exception ex) {
			Messenger.error(ex, "Unable to read internal JAR image files.", "Corrupted JAR");
			exitProgram();
		}
		catch (Error error) {
			Messenger.error("Unable to read internal JAR image files.", "Corrupted JAR");
			exitProgram();
		}
		
		initializeDisplay();		
		setVisible(true);
	}
	
	/**
	 * Initializes and sets up the GUI.
	 */
	private void initializeDisplay() {		
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 0, 5, 0);
		c.gridx = 0;
		c.gridy = 0;
						
		messageLabel = new JLabel();
		getContentPane().add(messageLabel, c);
		
		createMenuBar();
	}
	
	/**
	 * Initializes and sets up the main menu bar.
	 */
	private void createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
			
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic(KeyEvent.VK_G);
		
		// new game
		newGameMenuItem = new JMenuItem("New Game");
		newGameMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F2, 0));
		
		// replace player
		replacePlayerMenuItem = new JMenuItem("Replace Player");
		replacePlayerMenuItem.setVisible(false);
				
		// view rules
		JMenuItem rulesMenuItem = new JMenuItem("Rules");
		rulesMenuItem.addActionListener(e -> displayRules());
		
		// set options
		optionsMenuItem = new JMenuItem("Options");
		optionsMenuItem.addActionListener(e -> displayOptions());
				
		// quit
		JMenuItem quitMenuItem = new JMenuItem("Quit");
		quitMenuItem.addActionListener(e -> exitProgram());
		
		gameMenu.add(newGameMenuItem);
		gameMenu.add(replacePlayerMenuItem);
		gameMenu.addSeparator();
		gameMenu.add(rulesMenuItem);
		gameMenu.add(optionsMenuItem);
		gameMenu.add(quitMenuItem);
		
		JMenu designMenu = new JMenu("Design");
		designMenu.setMnemonic(KeyEvent.VK_D);
		
		JMenuItem createDesignMenuItem = new JMenuItem("Create Game Design");
		createDesignMenuItem.addActionListener(e -> new GameDesignerDisplay(ClueGUI.this, null).setVisible(true));
		
		JMenuItem editDesignMenuItem = new JMenuItem("Edit Game Design");
		editDesignMenuItem.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser("ccgs");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Custom Clue Games", "ccg");
			chooser.setFileFilter(filter);
			int returnVal = chooser.showOpenDialog(ClueGUI.this);
		    if (returnVal == JFileChooser.APPROVE_OPTION)
				new GameDesignerDisplay(ClueGUI.this, chooser.getSelectedFile()).setVisible(true);
		});
		
		designMenu.add(createDesignMenuItem);
		designMenu.add(editDesignMenuItem);
				
		menuBar.add(gameMenu);
		menuBar.add(designMenu);
		
		setJMenuBar(menuBar);
	}
	
	/**
	 * Loads a new game using the given game data.
	 * @param gameData data to load game with
	 */
	public void loadGame(final ClueGameData gameData) {
		getContentPane().removeAll();
		for (ComponentListener listener : getContentPane().getComponentListeners())
			getContentPane().removeComponentListener(listener);
		
		isGameOver = false;
		controlledSuspectTransferred = true;
		
		notepad = new DetectiveNotepadDialog(this, gameData.getRooms(), gameData.getSuspects(), gameData.getWeapons());
		
		if (infoMenu != null)
			getJMenuBar().remove(infoMenu);
		infoMenu = new JMenu("Info");
		infoMenu.setMnemonic(KeyEvent.VK_I);

		JMenuItem notepadMenuItem = new JMenuItem("Detective Notepad");
		notepadMenuItem.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F1, 0));
		notepadMenuItem.addActionListener(e -> notepad.setVisible(!notepad.isVisible()));
		infoMenu.add(notepadMenuItem);
		
		JMenuItem cardsMenuItem = new JMenuItem("Cards");
		cardsMenuItem.addActionListener(e -> displayControlledPlayerCards());
		infoMenu.add(cardsMenuItem);
		
		JMenuItem cardCountMenuItem = new JMenuItem("All Player Card Counts");
		cardCountMenuItem.addActionListener(e -> {
			StringBuilder message = new StringBuilder("<html>");
			for (String playerName : playerCardCountMap.keySet())
				message.append(playerName).append(": ").append(playerCardCountMap.get(playerName)).append("<br>");
			message.append("</html>");
			Messenger.display(message.toString(), "All Player Card Counts");
		});
		infoMenu.add(cardCountMenuItem);
		
		JMenuItem storyMenuItem = new JMenuItem("Story");
		storyMenuItem.addActionListener(e -> Messenger.display(story, "Game Story", ClueGUI.this));
		infoMenu.add(storyMenuItem);
		
		getJMenuBar().add(infoMenu);
		
		board = gameData.getBoard();
		board.addObserver(this);
		rooms = gameData.getRooms();
		suspects = gameData.getSuspects();
		weapons = gameData.getWeapons();
		story = gameData.getStory();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		messageTextArea = new JTextArea();
		messageTextArea.setLineWrap(true);
		messageTextArea.setWrapStyleWord(true);
		messageTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(messageTextArea);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setMinimumSize(new Dimension(10, 100));
		scrollPane.setPreferredSize(new Dimension(10, 100));
		getContentPane().add(scrollPane, c);
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		
		c.gridy++;
		c.insets = new Insets(0, 0, 0, 0);
		boardPanel = new DisplayBoardPanel(gameData.getBoard(), gameData.getRooms(), gameData.getDisplayRoomPicturesOnBoard(),
				                           gameData.getBackgroundImageFilename(), gameData.getBackgroundColor());
		board.addObserver(boardPanel);
		boardPanel.resizeBoardDisplay(getSize().height - 218, getSize().width + 40);
		getContentPane().add(boardPanel, c);
		getContentPane().addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				if (getContentPane().getHeight() > 50 && getContentPane().getWidth() > 0)
					boardPanel.resizeBoardDisplay(getSize().height - 218, (int)getSize().getWidth() + 40);
				
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
			public void componentShown(ComponentEvent e) {}
		});
		
		c.insets = new Insets(5, 10, 5, 10);
		c.fill = GridBagConstraints.HORIZONTAL;		
		c.gridy++;
		playPanel = new PlayPanel(this, board, getControlledSuspect());
		playPanel.setVisible(false);
		getContentPane().add(playPanel, c);
		
		notepad.setVisible(true);
		
		validate();
		repaint();
	}
	
	/**
	 * Starts the next Player's turn.
	 */
	protected void startPlayerTurn() {
		DisplayTile currentTile = board.getSuspectTile(getControlledSuspect());
		boolean canUseSecretPassage = false;
		if (currentTile.isRoomTile()) {
			for (DisplayTile tile : board.getRoomTiles(currentTile.getRoom())) {
				if (tile.isPassage() && tile.getPassageConnection().isRoomTile()) {
					canUseSecretPassage = true;
					break;
				}
			}
		}
		
		playPanel.display(canUseSecretPassage, controlledSuspectTransferred);
		controlledSuspectTransferred = false;
	}
	
	/**
	 * Enables a player to input that his turn is over.
	 */
	public void enableEndTurn() {
		playPanel.enableEndTurn();
	}
	
	/**
	 * Adds a message to the main message scrolling display.
	 * @param message message to add
	 */
	public void addMessage(String message) {
		if (messageTextArea == null) {
			messageLabel.setText(message);
			return;
		}
		messageTextArea.append("\n" + message);
		if (messageTextArea.getSelectedText() == null)
			messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength());
	}
	
	/**
	 * Notifies all human players of the given display message.
	 * @param displayMessage message to share and display for all users
	 */
	public abstract void informAllPlayers(String displayMessage);
	
	/**
	 * Displays and allows the player to change game options.
	 */
	protected abstract void displayOptions();

	/**
	 * Allows special exit handling to be performed on exit - prompt the user if they want to end the current game.
	 */
	protected abstract void exitProgram();
	
	/**
	 * Called when the controlled player wishes to make a suggestion.
	 * @param suggestedRoom suggested room
	 * @param suggestedSuspect suggested suspect
	 * @param suggestedWeapon suggested weapon
	 */
	public void makeSuggestion(Room suggestedRoom, Suspect suggestedSuspect, Weapon suggestedWeapon) {
		if (board.getSuspectTile(suggestedSuspect).getRoom() != suggestedRoom)
			board.moveSuspectToRoom(suggestedSuspect, suggestedRoom);
		makeSuggestion(getControlledSuspectId(), suggestedRoom, suggestedSuspect, suggestedWeapon);
	}
	
	/**
	 * Called when a player wishes to make a suggestion.
	 * @param playerId player id of the player making the suggestion
	 * @param suggestedRoom suggested room
	 * @param suggestedSuspect suggested suspect
	 * @param suggestedWeapon suggested weapon
	 */
	protected abstract void makeSuggestion(int playerId, Room suggestedRoom, Suspect suggestedSuspect, Weapon suggestedWeapon);
	
	/**
	 * Called when the controlled player wishes to make an accusation.
	 * @param accusedRoom accused room
	 * @param accusedSuspect accused suspect
	 * @param accusedWeapon accused weapon
	 */
	public void makeAccusation(Room accusedRoom, Suspect accusedSuspect, Weapon accusedWeapon) {
		playPanel.setVisible(false);
		makeAccusation(getControlledSuspectId(), accusedRoom, accusedSuspect, accusedWeapon);
	}
	
	/**
	 * Called when the a player wishes to make an accusation.
	 * @param playerId player id of the player making the accusation
	 * @param accusedRoom accused room
	 * @param accusedSuspect accused suspect
	 * @param accusedWeapon accused weapon
	 */
	protected abstract void makeAccusation(int playerId, Room accusedRoom, Suspect accusedSuspect, Weapon accusedWeapon);
	
	/**
	 * Returns the id of the Suspect controlled by this player.
	 */
	protected abstract int getControlledSuspectId();
	
	/**
	 * Returns the suspect controlled by this player.
	 * @return the suspect controlled by this player
	 */
	private Suspect getControlledSuspect() {
		int controlledSuspectId = getControlledSuspectId();
		for (Suspect suspect : suspects)
			if (suspect.getId() == controlledSuspectId)
				return suspect;
		
		return null;
	}
	
	/**
	 * Displays the controlled player's cards.
	 */
	private void displayControlledPlayerCards() {
		if (displayCardsDialog != null)
			displayCardsDialog.setVisible(true);
	}
	
	/**
	 * Returns this player's cards.
	 * @return this player's cards
	 */
	public abstract List<Card> getCards();
	
	/**
	 * Moves the given suspect to the specified board tile.
	 * @param suspectId the id of the suspect to move
	 * @param tilePosition the tile position of the tile where the suspect will be moved
	 * @param responsiblePlayerId the id of the player who caused the suspect to move
	 */
	public void moveSuspectToTile(int suspectId, Board.TilePosition tilePosition, int responsiblePlayerId) {
		Suspect suspect = getSuspect(suspectId);
		DisplayTile currentTile = board.getSuspectTile(suspect);
		DisplayTile newTile = board.getTile(tilePosition);
		
		if (suspectId == getControlledSuspectId() && newTile.isRoomTile() && newTile.getRoom() != currentTile.getRoom())
			controlledSuspectTransferred = true;

		currentTile.removeSuspect(suspect);
		newTile.addSuspect(suspect, board.getDirection(currentTile, newTile));
		
		if (responsiblePlayerId != getControlledSuspectId() && (currentTile.isRoomTile() || newTile.isRoomTile()))
			boardPanel.repaint();
		
		notifyOfSuspectMove(suspect, responsiblePlayerId);
	}
	
	/**
	 * Notifies players that a suspect has moved so their displays can be updated.
	 */
	protected abstract void notifyOfSuspectMove(Suspect suspect, int responsiblePlayerId);
	
	/**
	 * Ends the current player's turn
	 */
	public abstract void endTurn();
	
	/**
	 * Ends the current game.
	 * @param winnerName name of the winner or null if everyone lost
	 * @param caseFileRoom the room where the crime was committed
	 * @param caseFileSuspect the suspect who committed the crime
	 * @param caseFileWeapon the weapon used to commit the crime
	 */
	protected void endGame(final String winnerName, final Room caseFileRoom, final Suspect caseFileSuspect, final Weapon caseFileWeapon) {
		isGameOver = true;
		CustomDialog gameOverDialog = new CustomDialog(this, "Game Over") {
			private static final long serialVersionUID = 1L;
			private boolean initialized = false;
			@Override
			public void setVisible(boolean visible) {
				if (initialized) {
					super.setVisible(visible);
					return;
				}
				c.gridwidth = 3;
				String message;
				if (winnerName == null) {
					message = "Unbelievable! Everyone failed to make a correct accusation. No one wins!";
					addMessage("Game Over.");
				}
				else {
					message = winnerName + " correctly accused the following and Wins!";
					addMessage("Game Over. " + winnerName + " Wins!");
				}
				getContentPane().add(new JLabel(message), c);
				c.gridwidth = 1;
				
				c.gridy++;
				getContentPane().add(new CardDisplay(new Card(caseFileRoom)), c);
				
				c.gridx++;
				getContentPane().add(new CardDisplay(new Card(caseFileSuspect)), c);
				
				c.gridx++;
				getContentPane().add(new CardDisplay(new Card(caseFileWeapon)), c);
				
				initialized = true;
				refresh();
				super.setVisible(visible);
			}
		};
		gameOverDialog.setVisible(true);
	}
	
	/**
	 * Returns the number of dice to roll per turn (controlled by the option settings).
	 * @return the number of dice to roll per turn
	 */
	public int getNumDice() {
		return numDice;
	}
	
	/**
	 * Sets the number of dice to toll per turn.
	 * @param dice number of dice to set
	 */
	public void setNumDice(int dice) {
		numDice = dice;
	}
	
	/**
	 * Returns the Rooms on the board.
	 * @return the Rooms on the board
	 */
	public List<Room> getRooms() {
		return rooms;
	}
	
	/**
	 * Returns the Suspects in the current game.
	 * @return the Suspects in the current game
	 */
	public List<Suspect> getSuspects() {
		return suspects;
	}
	
	/**
	 * Returns the Weapons in the current game.
	 * @return the Weapons in the current game
	 */
	public List<Weapon> getWeapons() {
		return weapons;
	}
	
	/**
	 * Returns the specified Room.
	 * @param roomId id of the Room to return
	 * @return the specified Room
	 */
	protected Room getRoom(int roomId) {
		for (Room room : rooms)
			if (room.getId() == roomId)
				return room;
		
		return null;
	}
	
	/**
	 * Returns the specified Suspect.
	 * @param suspectId id of the Suspect to return
	 * @return the specified Suspect
	 */
	protected Suspect getSuspect(int suspectId) {
		for (Suspect suspect : suspects)
			if (suspect.getId() == suspectId)
				return suspect;
		
		return null;
	}
	
	/**
	 * Returns the specified Weapon.
	 * @param weaponId id of the Weapon to return
	 * @return the specified Weapon
	 */
	protected Weapon getWeapon(int weaponId) {
		for (Weapon weapon : weapons)
			if (weapon.getId() == weaponId)
				return weapon;
		
		return null;
	}

	/**
	 * Displays the rules of the game using the default text file reader.
	 */
	private void displayRules() {
		try {
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().open(new File("rules.txt"));
		}
		catch (IOException ex) {
			Messenger.error(ex.getMessage(), "File Error", this);
		}
	}
		
	/**
	 * Loads the configuration defaults and settings from the config file.
	 */
	private void loadConfigFile() {
		File file = new File(configFile);
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split(";");
					if (tokens.length == 4) {
						try {
							Dimension startSize = new Dimension();
							Point startLocation = getLocation();
							for (String token : tokens) {
								String[] windowParams = token.split(":");
								if (windowParams.length != 2)
									break;

								if (windowParams[0].equalsIgnoreCase("startX"))
									startLocation.x = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startY"))
									startLocation.y = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startWidth"))
									startSize.width = Integer.parseInt(windowParams[1]);
								else if (windowParams[0].equalsIgnoreCase("startHeight"))
									startSize.height = Integer.parseInt(windowParams[1]);
							}							
							setLocation(startLocation);
							if (startSize.width > 20 && startSize.height > 20)
								setSize(startSize);
						}
						catch (NumberFormatException ex) {}
						
						continue;
					}
					
					if (line.startsWith(":HostIP:=")) {
						try {
							defaultIP = line.substring(line.indexOf('=') + 1);
						}
						catch (Exception ex) {}
						
						continue;
					}
										
					if (line.startsWith(":NumDice:=")) {
						try {
							numDice = Integer.parseInt(line.substring(line.indexOf('=') + 1));
						}
						catch (Exception ex) {}
						
						continue;
					}
					
					if (line.startsWith(":UseFirstInListPlayerStart:=")) {
						try {
							useFirstInListPlayerStart = Boolean.parseBoolean(line.substring(line.indexOf('=') + 1));
						}
						catch (Exception ex) {}
						
						continue;
					}
					
					if (line.startsWith(":UseDefinedSuspectLocations:=")) {
						try {
							useDefinedSuspectLocations = Boolean.parseBoolean(line.substring(line.indexOf('=') + 1));
						}
						catch (Exception ex) {}
						
						continue;
					}
				}
				reader.close();
			} catch (IOException ex) {}
		}
	}
	
	/**
	 * Writes the latest configuration defaults and settings to the config file.
	 */
	protected void updateConfigFile() {
		File file = new File(configFile);
		try {
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("startX:" + getLocation().x + ";");
			writer.write("startY:" + getLocation().y + ";");
			writer.write("startWidth:" + getSize().width + ";");
			writer.write("startHeight:" + getSize().height);
			writer.newLine();
			
			if (defaultIP != null) {
				writer.write(":HostIP:=");
				writer.write(defaultIP);
				writer.newLine();
			}
			
			writer.write(":NumDice:=" + numDice);
			writer.newLine();
			
			writer.write(":UseFirstInListPlayerStart:=" + useFirstInListPlayerStart);
			writer.newLine();
			
			writer.write(":UseDefinedSuspectLocations:=" + useDefinedSuspectLocations);
			writer.newLine();
			
			writer.close();
		} catch (IOException ex) {}
	}

	@Override
	public void update(Observable observable, Object obj) {
		if (observable instanceof Board)
			if (obj instanceof Suspect)
				notifyOfSuspectMove((Suspect) obj, getControlledSuspectId());
	}
		
	/**
	 * Main class that is first called and starts running the GUI and thereby the program.
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			ErrorWriter.beginLogging("Clue.main.java.log");
		}
		catch (IOException ex) {
			Messenger.error(ex, "Failed to create/open error main.java.log file", "Log Error");
		}
		
		// Run the GUI in a thread safe environment
		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					String prompt = "Would you like to host or join a CLUE game?";
					ImageIcon icon = ImageHelper.getMGlass();
					
					String[] options = {"Host", "Join", "Exit Program"};
					int result = JOptionPane.showOptionDialog(null, prompt, "Custom CLUE v1.2", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, "Host");
					if (result == JOptionPane.YES_OPTION)
						new HostGUI();
					else if (result == JOptionPane.NO_OPTION)
						new ParticipantGUI();
				}
			}
		);
	}
}