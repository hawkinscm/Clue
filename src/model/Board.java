package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Observable;

import gui.DisplayTile;
import gui.Messenger;
import gui.DisplayTile.Direction;

/**
 * Represents a CLUE board.
 */
public class Board extends Observable {

	public static final int MIN_HEIGHT = 6;
	public static final int MAX_HEIGHT = 50;
	public static final int MIN_WIDTH = 6;
	public static final int MAX_WIDTH = 50;
	
	private DisplayTile[][] boardTiles;

	private int height;
	private int width;
	
	/**
	 * Creates a new CLUE Board.
	 * @param height height in tiles of the board
	 * @param width width in tiles of the board
	 */
	public Board(int height, int width) {
		boardTiles = new DisplayTile[MAX_HEIGHT][MAX_WIDTH];
		for (int row = 0; row < MAX_HEIGHT; row++)
			for (int col = 0; col < MAX_WIDTH; col++)
				boardTiles[row][col] = new DisplayTile();
		setSize(height, width);
	}
	
	/**
	 * Copy Constructor.
	 * @param board board to copy
	 */
	public Board(Board board) {
		boardTiles = new DisplayTile[MAX_HEIGHT][MAX_WIDTH];
		for (int row = 0; row < MAX_HEIGHT; row++)
			for (int col = 0; col < MAX_WIDTH; col++)
				boardTiles[row][col] = new DisplayTile(board.boardTiles[row][col]);
		
		for (int row = 0; row < MAX_HEIGHT; row++) {
			for (int col = 0; col < MAX_WIDTH; col++) {
				DisplayTile tile = boardTiles[row][col];
				if (tile.getPassageConnection() != null)
					tile.setPassageConnection(getTile(board.getTilePosition(tile.getPassageConnection())), tile.getPassageLetter());
			}
		}
		
		setSize(board.height, board.width);
	}
	
	/**
	 * Returns the height of the board in tiles.
	 * @return the height of the board in tiles
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Returns the width of the board in tiles.
	 * @return the width of the board in tiles
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Sets the size of the board in tiles.
	 * @param height height in tiles to set
	 * @param width width in tiles to set
	 */
	public void setSize(int height, int width) {
		this.height = height;
		this.width = width;
	}
	
	/**
	 * Returns the tile at the specified row and column
	 * @param rowIdx index of the tile's row
	 * @param colIdx index of the tile's column
	 * @return the tile at the specified row and column
	 */
	public DisplayTile getTile(int rowIdx, int colIdx) {
		return boardTiles[rowIdx][colIdx];
	}
	
	/**
	 * Return the tile at the given position.
	 * @param position position (row and column) of the tile
	 * @return the tile at the given position
	 */
	public DisplayTile getTile(TilePosition position) {
		return boardTiles[position.row][position.col];
	}
	
	/**
	 * Returns all the tiles on the board in a list.
	 * @return a list of all tiles on the board
	 */
	public ArrayList<DisplayTile> getTiles() {
		ArrayList<DisplayTile> tiles = new ArrayList<DisplayTile>(height * width);
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++)
				tiles.add(boardTiles[row][col]);
		return tiles;
	}
	
	/**
	 * Returns the tile position of the given tile.
	 * @param tile tile to locate
	 * @return the tile position (row and column) of the given tile
	 */
	public TilePosition getTilePosition(DisplayTile tile) {
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++)
				if (boardTiles[row][col] == tile)
					return new TilePosition(row, col);
				
		return null;
	}
	
	/**
	 * Whether or not this board has the given Room.
	 * @param room room to attempt to find
	 * @return true if the room is found on the board; false otherwise
	 */
	public boolean hasRoom(Room room) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++)
				if (boardTiles[row][col].getRoom() == room)
					return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the tiles that belong to the given room.
	 * @param room room to retrieve the tiles of 
	 * @return the tiles that belong to the given room
	 */
	public LinkedList<DisplayTile> getRoomTiles(Room room) {		
		LinkedList<DisplayTile> roomTiles = new LinkedList<DisplayTile>();
		if (room == null)
			return roomTiles;
		
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				DisplayTile tile = boardTiles[row][col];
				if (tile.getRoom() == room)
					roomTiles.add(tile);
			}
		}
		return roomTiles;
	}
	
	/**
	 * Returns the unblocked room exit tiles.  Exit tiles are those tiles on the other side of each room door.
	 * @param room room to check for exit tiles.
	 * @return the unblocked room exit tiles
	 */
	public List<DisplayTile> getFreeExitTiles(Room room) {
		List<DisplayTile> exitTiles = new LinkedList<DisplayTile>();
		for (DisplayTile roomTile : getRoomTiles(room)) {
			for (Direction direction : Direction.values()) {
				if (roomTile.hasDoor(direction)) {
					DisplayTile exitTile = getAdjacentTile(roomTile, direction);
					boolean isFree;
					if (exitTile.isRoomTile())
						isFree = true;
					else if (exitTile.isRemovedTile())
						isFree = false;
					else if (exitTile.hasSuspect())
						isFree = false;
					else if (exitTile.isPassage() && exitTile.getPassageConnection().hasSuspect())
						isFree = false;
					else
						isFree = true;
					
					if (isFree && !exitTiles.contains(exitTile))
						exitTiles.add(exitTile);
				}
			}
		}
		return exitTiles;
	}
	
	/**
	 * Removes the given room from this board.
	 * @param room room to remove
	 */
	public void removeRoom(Room room) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				DisplayTile tile = boardTiles[row][col];
				if (tile.getRoom() == room) {
					tile.removeRoom();
					for (Direction direction : Direction.values()) {
						if (tile.hasDoor(direction)) {
							DisplayTile adjacentTile = getAdjacentTile(tile, direction);
							if (!adjacentTile.isRoomTile()) {
								tile.removeDoor(direction);
								adjacentTile.removeDoor(direction.getOpposite());
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns the tiles that are adjacent to the given tile.
	 * @param tile tile to get adjacent tiles of
	 * @return the tiles that are adjacent to the given tile
	 */
	public DisplayTile[] getAdjacentTiles(DisplayTile tile) {
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++)
				if (boardTiles[row][col] == tile) {
					DisplayTile[] adjacentTiles = new DisplayTile[4];
					adjacentTiles[Direction.NORTH.getOrdinal()] = (row > 0) ? boardTiles[row - 1][col] : null;
					adjacentTiles[Direction.SOUTH.getOrdinal()] = (row < height - 1) ? boardTiles[row + 1][col] : null;
					adjacentTiles[Direction.WEST.getOrdinal()] = (col > 0) ? boardTiles[row][col - 1] : null;
					adjacentTiles[Direction.EAST.getOrdinal()] = (col < width - 1) ? boardTiles[row][col + 1] : null;
					return adjacentTiles;
				}
		}
		return new DisplayTile[] {null, null, null, null};
	}
	
	/**
	 * Returns the tile that is adjacent to the given tile in the given direction.
	 * @param tile tile to get adjacent tile of
	 * @param direction direction of the tile to return
	 * @return the tile that is adjacent to the given tile in the given direction
	 */
	public DisplayTile getAdjacentTile(DisplayTile tile, Direction direction) {
		return getAdjacentTiles(tile)[direction.getOrdinal()];
	}
	
	/**
	 * Returns the direction of the startTile to the adjacent endTile or null if not adjacent tiles.
	 * @param startTile tile to start at
	 * @param endTile tile to end at
	 * @return the direction of the startTile to the adjacent endTile or null if not adjacent tiles
	 */
	public Direction getDirection(DisplayTile startTile, DisplayTile endTile) {
		for (Direction direction : Direction.values())
			if (getAdjacentTile(startTile, direction) == endTile)
				return direction;
		
		return null;
	}
	
	/**
	 * Returns the tile on this board where the given suspect is located.
	 * @param suspect suspect to find
	 * @return the tile on this board where the given suspect is located or null if not found
	 */
	public DisplayTile getSuspectTile(Suspect suspect) {
		if (suspect == null) 
			return null;
		
		for (int row = 0; row < height; row++)
			for (int col = 0; col < width; col++) {
				DisplayTile currentTile = boardTiles[row][col]; 
				if (currentTile.getSuspects().contains(suspect))
					return currentTile;
			}
		
		return null;
	}
	
	/**
	 * Moves the given suspect to the specified room.
	 * @param suspect suspect to move
	 * @param room room to move the suspect to
	 * @return the room tile where the suspect is now located
	 */
	public DisplayTile moveSuspectToRoom(Suspect suspect, Room room) {
		removeSuspectFromTile(suspect, getSuspectTile(suspect));
		List<DisplayTile> leastOccupiedTiles = new LinkedList<DisplayTile>();
		int leastOccupiedTileSuspectCount = 99;
		for (DisplayTile tile : getRoomTiles(room)) {
			if (tile.isPassage())
				continue;
			
			int suspectCount = tile.getSuspects().size();
			if (suspectCount < leastOccupiedTileSuspectCount) {
				leastOccupiedTileSuspectCount = suspectCount;
				leastOccupiedTiles.clear();
				leastOccupiedTiles.add(tile);
			}
			else if (suspectCount == leastOccupiedTileSuspectCount)
				leastOccupiedTiles.add(tile);
		}
		DisplayTile selectedTile = leastOccupiedTiles.get(Randomizer.getRandom(leastOccupiedTiles.size()));
		selectedTile.addSuspect(suspect, null);
		setChanged();
		notifyObservers(true);
		setChanged();
		notifyObservers(suspect);
		return selectedTile;
	}
	
	/**
	 * Moves the given suspect to the specified tile.
	 * @param suspect suspect to move
	 * @param suspectDirection direction that the suspect will face on the new tile
	 * @param tile tile where the suspect will be moved
	 */
	public void moveSuspectToTile(Suspect suspect, Direction suspectDirection, DisplayTile tile) {
		removeSuspectFromTile(suspect, getSuspectTile(suspect));
		tile.addSuspect(suspect, suspectDirection);
		setChanged();
		notifyObservers(tile.isRoomTile());
		setChanged();
		notifyObservers(suspect);
	}
	
	/**
	 * Removes the given suspect from the specified tile.
	 * @param suspect suspect to remove
	 * @param tile tile from which the suspect will be removed
	 */
	private void removeSuspectFromTile(Suspect suspect, DisplayTile tile) {
		tile.removeSuspect(suspect);
		if (tile.isRoomTile()) {
			setChanged();
			notifyObservers(true);
		}
	}
	
	/**
	 * Returns the (alphabetically) next unused passage letter
	 * @return the (alphabetically) next unused passage letter
	 * @throws NoSuchElementException if all the letters have been used and none are available
	 */
	public char getNextUnusedPassageLetter() throws NoSuchElementException {
		LinkedList<Character> passageLetters = new LinkedList<Character>();
		for (char letter = 'A'; letter <= 'Z'; letter++)
			passageLetters.add(letter);
		
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				DisplayTile currentTile = boardTiles[row][col];
				if (currentTile.isPassage())
					passageLetters.remove((Character) currentTile.getText().charAt(0));
			}
		}
		
		return passageLetters.getFirst();
	}
	
	/**
	 * Validates that this board and the elements on it represent a playable CLUE board.
	 * @param rooms rooms to verify
	 * @param suspects suspects to verify
	 * @return true if this is a valid, playable CLUE board; false otherwise
	 */
	public boolean validate(List<Room> rooms, List<Suspect> suspects) {
		LinkedList<String> invalidMessages = new LinkedList<String>(); 
		for (Room room : rooms) {
			LinkedList<DisplayTile> roomTiles = getRoomTiles(room);
			if (roomTiles.isEmpty()) {
				invalidMessages.add("Room " + room.getId() + " \"" + room.getName() + "\"" + " needs to be added to the board or removed as a room card.");
				continue;
			}
			
			int emptyRoomTiles = 0;
			for (DisplayTile tile : roomTiles) {
				if (!tile.isPassage())
					emptyRoomTiles++;
				else if (tile.getPassageConnection().getRoom() == room) {
					TilePosition position = getTilePosition(tile);
					invalidMessages.add("Passage at row index " + position.row + " and column index " + position.col + 
							            " in the Room \"" + tile.getRoom().getName() + "\" is connected to a passage in the same room.");
				}
			}
			
			if (emptyRoomTiles < 2)
				invalidMessages.add("Room " + room.getId() + " \"" + room.getName() + "\"" + " needs to contain at least 2 empty, non-passage tiles.");
		}
		
		int suspectCount = 0;
		for (Suspect suspect : suspects) {
			if (getSuspectTile(suspect) != null)
				suspectCount++;
		}
		if (suspectCount != 0 && suspectCount != suspects.size())
			invalidMessages.add("The board either needs to have a starting player tile for each Suspect OR no starting player tiles.");
		
		if (invalidMessages.isEmpty())
			return true;
		
		String message = "<html>This is not a completed, playable Clue Board for the following reason(s):";
		for (String invalidMessage : invalidMessages)
			message += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + invalidMessage;
		message += "</html>";
		Messenger.warn(message, "Clue Board Validation");
		return false;
	}
	
	/**
	 * Represents a row and column of a tile on the board.
	 */
	public static class TilePosition {
		public int row;
		public int col;
		
		/**
		 * Creates a new Tile Position
		 * @param row row of the tile (0-based)
		 * @param col column of the tile (0-based)
		 */
		public TilePosition(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
}
