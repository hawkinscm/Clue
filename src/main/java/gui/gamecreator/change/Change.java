package gui.gamecreator.change;

import gui.DisplayTile;

import java.util.LinkedList;

/**
 * Represents a Change made to the CLUE board.
 */
public abstract class Change {
	protected LinkedList<DisplayTile> changedTiles;
	
	/**
	 * Creates a new Change.
	 */
	public Change() {
		changedTiles = new LinkedList<>();
	}
	
	/**
	 * Adds a changed tile to the Change.
	 * @param tile tile to add
	 */
	public void addChangedTile(DisplayTile tile) {
		changedTiles.add(tile);
	}
	
	/**
	 * Applies/performs the Change.
	 * @return an error message if the apply failed or null if it succeeded.
	 */
	public abstract String applyChange();
	
	/**
	 * Undoes the Change, restoring the board to what it was before the Change was made.
	 */
	public abstract void undoChange();
	
	/**
	 * Redoes the Change, reapplying the Change after it has been undone.
	 */
	public abstract void redoChange();
}
