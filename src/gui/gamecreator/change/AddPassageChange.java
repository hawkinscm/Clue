package gui.gamecreator.change;

import java.awt.Color;
import java.util.NoSuchElementException;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import model.Board;
import gui.DisplayTile;
import gui.ImageHelper;

/**
 * Change for adding/editing a passage between 2 board tiles.
 */
public class AddPassageChange extends Change {
		
	private Board board;
	private DisplayTile startTile;
	private DisplayTile endTile;
	private DisplayTile previouslyConnectedTile;
	
	private Border restoreBorder;
	
	/**
	 * Creates a new Add Passage Change.
	 * @param board current custom CLUE board
	 */
	public AddPassageChange(Board board) {
		this.board = board;
		startTile = null;
		endTile = null;
		previouslyConnectedTile = null;
		
		restoreBorder = null;
	}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (changedTiles.isEmpty()) {
			super.addChangedTile(tile);
			
			restoreBorder = tile.getBorder();
			if (!tile.isRemovedTile() && !tile.hasSuspect()) {
				startTile = tile;
				endTile = startTile;
				startTile.setBorder(BorderFactory.createLineBorder(Color.RED, 3));

				if (startTile.isPassage()) 
					startTile.getPassageConnection().setIcon(null);
				else
					startTile.setIconByType(ImageHelper.ImageType.PASSAGE);
			}
			return;
		}
		
		if (startTile == null)
			return;			
		
		if (endTile != null && endTile != startTile)
			endTile.setIcon(null);
		changedTiles.removeLast().setBorder(restoreBorder);
		
		super.addChangedTile(tile);
		
		restoreBorder = tile.getBorder();
		if (tile == startTile) {
			tile.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
			endTile = startTile;
		}
		else {
			if (tile.isRemovedTile() || tile.hasSuspect() || tile.isPassage()) {
				endTile = null;
				tile.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
			}
			else {
				endTile = tile;
				tile.setIconByType(ImageHelper.ImageType.PASSAGE);
				tile.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
			}
		}
	}
	
	@Override
	public String applyChange() {		
		if (startTile == null)
			return "Cannot start a passage on a removed tile or a player start tile.";
		
		if (startTile.isPassage())
			startTile.getPassageConnection().setIconByType(ImageHelper.ImageType.PASSAGE);
		else
			startTile.setIcon(null);
		changedTiles.getLast().setBorder(restoreBorder);
		
		if (endTile == null)
			return "Cannot end a passage on a removed tile, a player start tile, or a secret passage";
		
		if (startTile == endTile)
			return "Left-click and drag the mouse to create a passage.";
		
		previouslyConnectedTile = startTile.getPassageConnection();
		if (previouslyConnectedTile != null)
			previouslyConnectedTile.removePassageConnection();
		
		try {
			char passageLetter = startTile.isPassage() ? startTile.getText().charAt(0) : board.getNextUnusedPassageLetter();
			startTile.setPassageConnection(endTile, passageLetter);
			endTile.setPassageConnection(startTile, passageLetter);
		}
		catch (NoSuchElementException ex) {
			startTile.setIcon(null);
			endTile.setIcon(null);
			startTile = null;
			endTile = null;
			return "You've reached the maximum number of passages allowed and cannot add any more.";
		}
		
		return null;
	}
	
	@Override
	public void undoChange() {
		if (startTile == null || endTile == null || startTile == endTile)
			return;

		if (previouslyConnectedTile == null) {
			startTile.removePassageConnection();
			endTile.removePassageConnection();
		}
		else {
			endTile.removePassageConnection();
			char passageLetter = startTile.getText().charAt(0);
			startTile.setPassageConnection(previouslyConnectedTile, passageLetter);
			previouslyConnectedTile.setPassageConnection(startTile, passageLetter);
		}
	}
	
	@Override
	public void redoChange() {
		if (startTile == null || endTile == null || startTile == endTile)
			return;
		
		if (previouslyConnectedTile != null)
			previouslyConnectedTile.removePassageConnection();
		
		char passageLetter = startTile.isPassage() ? startTile.getText().charAt(0) : board.getNextUnusedPassageLetter();
		startTile.setPassageConnection(endTile, passageLetter);
		endTile.setPassageConnection(startTile, passageLetter);
	}
}
