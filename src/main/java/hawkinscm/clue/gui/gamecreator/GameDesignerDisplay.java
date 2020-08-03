package hawkinscm.clue.gui.gamecreator;

import hawkinscm.clue.gui.CustomDialog;
import hawkinscm.clue.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * Main dialog used for designing a custom CLUE game. 
 */
public class GameDesignerDisplay extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private static final int HEIGHT = 800;
	private static final int WIDTH = 800;
	
	private CardDesignerPanel cardCreationPanel;
	private BoardDesignerPanel boardCreationPanel;
	
	private File selectedGameFile;
	
	private ArrayList<Room> lastSavedRooms;
	private ArrayList<Suspect> lastSavedSuspects;
	private ArrayList<Weapon> lastSavedWeapons;
	private boolean lastSavedDisplayRoomPicturesOnBoard;
	private String lastSavedBackgroundImageFilename;
	private Color lastSavedBackgroundColor;
	private String lastSavedStory;
	
	/**
	 * Creates a new Game Designer Display.
	 * @param owner parent/owner of this dialog
	 * @param selectedGameFile file to load a custom CLUE game (ccg) from; will use default/empty game if null or invalid
	 */
	public GameDesignerDisplay(JFrame owner, File selectedGameFile) {
		super(owner, "CLUE Game Creator");
		this.selectedGameFile = selectedGameFile;
		setLayout(new GridLayout(1, 1));
		setResizable(true);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				if (unsavedChanges()) {
					int choice = JOptionPane.showConfirmDialog(GameDesignerDisplay.this, "Save Changes?", "Close CLUE Game Creator", 
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (choice == JOptionPane.YES_OPTION)
						saveChanges();
					else if (choice != JOptionPane.NO_OPTION)
						return;
				}
				
				dispose();
			}			
		});
		
		JTabbedPane tabbedPane = new JTabbedPane();
		getContentPane().add(tabbedPane);

		cardCreationPanel = new CardDesignerPanel();
		boardCreationPanel = new BoardDesignerPanel(this);
		
		ToolTipManager.sharedInstance().registerComponent(boardCreationPanel);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		
		tabbedPane.addTab("Cards", cardCreationPanel);
		tabbedPane.addTab("Board", boardCreationPanel);
		
		tabbedPane.addChangeListener(e -> {
			boardCreationPanel.setRooms(cardCreationPanel.getRooms());
			boardCreationPanel.setSuspects(cardCreationPanel.getSuspects());
		});
		
		boardCreationPanel.setRooms(cardCreationPanel.getRooms());
		boardCreationPanel.setSuspects(cardCreationPanel.getSuspects());
		
		loadGameDesign();
		
		lastSavedRooms = new ArrayList<>();
		for (Room room : cardCreationPanel.getRooms())
			lastSavedRooms.add(new Room(room));
		lastSavedSuspects = new ArrayList<>();
		for (Suspect suspect : cardCreationPanel.getSuspects())
			lastSavedSuspects.add(new Suspect(suspect));
		lastSavedWeapons = new ArrayList<>();
		for (Weapon weapon : cardCreationPanel.getWeapons())
			lastSavedWeapons.add(new Weapon(weapon));
		lastSavedDisplayRoomPicturesOnBoard = cardCreationPanel.getDisplayRoomPicturesOnBoard();
		lastSavedBackgroundImageFilename = cardCreationPanel.getBackgroundImageFilename();
		lastSavedBackgroundColor = cardCreationPanel.getBackgroundColor();
		lastSavedStory = cardCreationPanel.getStory();
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		refresh();
	}
	
	/**
	 * Whether or not room pictures should be displayed on the board.
	 * @return true if room pictures should be displayed on the board; false if the default blue tiles should be displayed.
	 */
	public boolean displayRoomPicturesOnBoard() {
		return cardCreationPanel.getDisplayRoomPicturesOnBoard();
	}
	
	/**
	 * Return the filename of the background image.
	 * @return the filename of the background image; may be null
	 */
	public String getBackgroundImageFilename() {
		return cardCreationPanel.getBackgroundImageFilename();
	}
	
	/**
	 * Return the color to use as the background color or transparent color of the background filename if it is specified.
	 * @return the color to use as the background color
	 */
	public Color getBackgroundColor() {
		return cardCreationPanel.getBackgroundColor();
	}
	
	/**
	 * Returns whether or not there is anything different since the last time the custom CLUE game was saved.
	 * @return true if there is anything different since the last time the custom CLUE game was saved; false otherwise
	 */
	private boolean unsavedChanges() {
		if (boardCreationPanel.unsavedChanges())
			return true;
		
		ArrayList<Room> currentRooms = cardCreationPanel.getRooms(); 
		if (lastSavedRooms.size() != currentRooms.size())
			return true;		
		for (int index = 0; index < currentRooms.size(); index++) {
			if (!lastSavedRooms.get(index).equals(currentRooms.get(index)))
				return true;
		}
		
		ArrayList<Suspect> currentSuspects = cardCreationPanel.getSuspects(); 
		if (lastSavedSuspects.size() != currentSuspects.size())
			return true;		
		for (int index = 0; index < currentSuspects.size(); index++) {
			if (!lastSavedSuspects.get(index).equals(currentSuspects.get(index)))
				return true;
		}
		
		ArrayList<Weapon> currentWeapons = cardCreationPanel.getWeapons(); 
		if (lastSavedWeapons.size() != currentWeapons.size())
			return true;		
		for (int index = 0; index < currentWeapons.size(); index++) {
			if (!lastSavedWeapons.get(index).equals(currentWeapons.get(index)))
				return true;
		}
		
		if (lastSavedDisplayRoomPicturesOnBoard != cardCreationPanel.getDisplayRoomPicturesOnBoard())
			return true;
		
		if (lastSavedBackgroundImageFilename == null) {
			if (cardCreationPanel.getBackgroundImageFilename() != null)
				return true;
		}
		else if (!lastSavedBackgroundImageFilename.equals(cardCreationPanel.getBackgroundImageFilename()))
			return true;
		
		if (!lastSavedBackgroundColor.equals(cardCreationPanel.getBackgroundColor()))
			return true;
		
		return (!lastSavedStory.equals(cardCreationPanel.getStory()));
	}
	
	/**
	 * Saves the current custom CLUE game to file.
	 */
	public void saveChanges() {
		ClueGameData gameData = new ClueGameData();
		gameData.setBoard(boardCreationPanel.getBoard());
		gameData.setRooms(cardCreationPanel.getRooms());
		gameData.setSuspects(cardCreationPanel.getSuspects());
		gameData.setWeapons(cardCreationPanel.getWeapons());
		gameData.setStory(cardCreationPanel.getStory());
		gameData.setDisplayRoomPicturesOnBoard(cardCreationPanel.getDisplayRoomPicturesOnBoard());
		gameData.setBackgroundImageFilename(cardCreationPanel.getBackgroundImageFilename());
		gameData.setBackgroundColor(cardCreationPanel.getBackgroundColor());
		File savedFile = GameFileIOHandler.writeToFile(selectedGameFile, gameData);
		if (savedFile != null) {
			selectedGameFile = savedFile;
			
			lastSavedRooms = new ArrayList<>();
			for (Room room : gameData.getRooms())
				lastSavedRooms.add(new Room(room));
			lastSavedSuspects = new ArrayList<>();
			for (Suspect suspect : gameData.getSuspects())
				lastSavedSuspects.add(new Suspect(suspect));
			lastSavedWeapons = new ArrayList<>();
			for (Weapon weapon : gameData.getWeapons())
				lastSavedWeapons.add(new Weapon(weapon));
			lastSavedDisplayRoomPicturesOnBoard = gameData.getDisplayRoomPicturesOnBoard();
			lastSavedBackgroundImageFilename = gameData.getBackgroundImageFilename();
			lastSavedBackgroundColor = gameData.getBackgroundColor();
			lastSavedStory = gameData.getStory();
		} 
	}
	
	/**
	 * Loads a custom CLUE game from file.
	 */
	private void loadGameDesign() {
		if (selectedGameFile == null)
			return;
		
		ClueGameData gameData = GameFileIOHandler.readFromFile(selectedGameFile);
		if (gameData != null) {
			cardCreationPanel.setDisplayRoomPicturesOnBoard(gameData.getDisplayRoomPicturesOnBoard());
			cardCreationPanel.setRooms(gameData.getRooms());
			cardCreationPanel.setSuspects(gameData.getSuspects());
			cardCreationPanel.setWeapons(gameData.getWeapons());
			cardCreationPanel.setBackgroundImageFilename(gameData.getBackgroundImageFilename());
			cardCreationPanel.setBackgroundColor(gameData.getBackgroundColor());
			boardCreationPanel.loadBoard(gameData.getBoard(), gameData.getRooms(), gameData.getSuspects());
			cardCreationPanel.setStory(gameData.getStory());
		}
		else
			selectedGameFile = null;
	}
}
