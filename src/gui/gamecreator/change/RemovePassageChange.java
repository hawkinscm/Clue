package gui.gamecreator.change;

import model.Board;
import gui.DisplayTile;

/**
 * Change for removing a passage between 2 board tiles.
 */
public class RemovePassageChange extends Change {
		
	private Board board;
	private DisplayTile startPassageTile;
	private DisplayTile connectedTile;
	
	/**
	 * Creates a new Remove Passage Change.
	 * @param board current custom CLUE board
	 */
	public RemovePassageChange(Board board) {
		this.board = board;
		startPassageTile = null;
		connectedTile = null;
	}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (changedTiles.isEmpty()) {
			if (tile.isPassage()) {
				startPassageTile = tile;
				connectedTile = tile.getPassageConnection();
				startPassageTile.removePassageConnection();
				connectedTile.removePassageConnection();
			}
		}
		super.addChangedTile(tile);
	}
	
	@Override
	public String applyChange() {
		if (startPassageTile == null)
			return "";
		
		return null;
	}
	
	@Override
	public void undoChange() {
		if (startPassageTile == null)
			return;
	
		char passageLetter = board.getNextUnusedPassageLetter();
		startPassageTile.setPassageConnection(connectedTile, passageLetter);
		connectedTile.setPassageConnection(startPassageTile, passageLetter);
	}
	
	@Override
	public void redoChange() {
		if (startPassageTile == null)
			return;
		
		startPassageTile.removePassageConnection();
		connectedTile.removePassageConnection();
	}
}
