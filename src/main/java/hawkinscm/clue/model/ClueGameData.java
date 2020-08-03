package hawkinscm.clue.model;

import java.awt.Color;
import java.util.List;

/**
 * Represents the data that make up a CLUE game.
 */
public class ClueGameData {

	private Board board;
	private List<Room> rooms;
	private List<Suspect> suspects;
	private List<Weapon> weapons;
	private String story;
	private boolean displayRoomPictures;
	private String backgroundImageFilename;
	private Color backgroundColor;
	
	/**
	 * Creates a new empty CLUE Game Data object.
	 */
	public ClueGameData() {
		this.board = null;
		this.rooms = null;
		this.suspects = null;
		this.weapons = null;
		this.story = null;
		this.displayRoomPictures = false;
		this.backgroundImageFilename = null;
		this.backgroundColor = null;
	}
	
	/**
	 * Creates a new CLUE Game Data object.
	 * @param board board to set
	 * @param rooms rooms to set
	 * @param suspects suspects to set
	 * @param weapons weapons to set
	 * @param story story to set
	 * @param displayRoomPictures whether or not room pictures will be displayed
	 * @param backgroundImageFilename image filename for the background image
	 * @param backgroundColor color for the background color
	 */
	public ClueGameData(Board board, List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons, String story,
                        boolean displayRoomPictures, String backgroundImageFilename, Color backgroundColor) {
		this.board = board;
		this.rooms = rooms;
		this.suspects = suspects;
		this.weapons = weapons;
		this.story = story;
		this.displayRoomPictures = displayRoomPictures;
		this.backgroundImageFilename = backgroundImageFilename;
		this.backgroundColor = backgroundColor;
	}
	
	/**
	 * Returns the CLUE game board.
	 * @return the CLUE game board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Returns the CLUE game rooms.
	 * @return the CLUE game rooms
	 */
	public List<Room> getRooms() {
		return rooms;
	}

	/**
	 * Returns the CLUE game suspects.
	 * @return the CLUE game suspects
	 */
	public List<Suspect> getSuspects() {
		return suspects;
	}

	/**
	 * Returns the CLUE game weapons.
	 * @return the CLUE game weapons
	 */
	public List<Weapon> getWeapons() {
		return weapons;
	}

	/**
	 * Returns the CLUE game story.
	 * @return the CLUE game story
	 */
	public String getStory() {
		return story;
	}

	/**
	 * Returns whether or not room pictures will be displayed on the board.
	 * @return true if the room pictures will be displayed on the board; false otherwise
	 */
	public boolean getDisplayRoomPicturesOnBoard() {
		return displayRoomPictures;
	}

	/**
	 * Returns the filename of the background image for the board.
	 * @return the filename of the background image for the board
	 */
	public String getBackgroundImageFilename() {
		return backgroundImageFilename;
	}

	/**
	 * Returns the Color for the background of the board.
	 * @return the Color for the background of the board
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	/**
	 * Sets the CLUE game board.
	 * @param board board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	/**
	 * Sets the CLUE game rooms.
	 * @param rooms rooms to set
	 */
	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

	/**
	 * Sets the CLUE game suspects.
	 * @param suspects suspects to set
	 */
	public void setSuspects(List<Suspect> suspects) {
		this.suspects = suspects;
	}

	/**
	 * Sets the CLUE game weapons.
	 * @param weapons weapons to set
	 */
	public void setWeapons(List<Weapon> weapons) {
		this.weapons = weapons;
	}

	/**
	 * Sets the CLUE game story.
	 * @param story story to set
	 */
	public void setStory(String story) {
		this.story = story;
	}

	/**
	 * Sets whether or not room pictures will be displayed on the board.
	 * @param displayRoomPicturesOnBoard sets the value of the display room pictures on the board flag
	 */
	public void setDisplayRoomPicturesOnBoard(boolean displayRoomPicturesOnBoard) {
		this.displayRoomPictures = displayRoomPicturesOnBoard;
	}

	/**
	 * Sets the background image filename for the board.
	 * @param backgroundImageFilename background image filename to set
	 */
	public void setBackgroundImageFilename(String backgroundImageFilename) {
		this.backgroundImageFilename = backgroundImageFilename;
	}

	/**
	 * Sets the background color for the board.
	 * @param backgroundColor background color to set
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
}
