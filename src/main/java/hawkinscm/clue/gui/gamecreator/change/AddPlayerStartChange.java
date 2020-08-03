package hawkinscm.clue.gui.gamecreator.change;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.model.Suspect;
import hawkinscm.clue.gui.DisplayTile;

/**
 * Change for adding a Player Start to a board tile.
 */
public class AddPlayerStartChange extends Change {
		
	private Board board;
	private Suspect suspect;
	private DisplayTile clickedTile;
	private DisplayTile suspectPreviousTile;
	private DisplayTile.Direction suspectPreviousDirection;
	
	/**
	 * Creates a new Add Player Start Change.
	 * @param board current custom CLUE board
	 * @param suspect player/suspect to add
	 */
	public AddPlayerStartChange(Board board, Suspect suspect) {
		this.board = board;
		this.suspect = suspect;
		clickedTile = null;
		suspectPreviousTile = null;
		suspectPreviousDirection = null;
	}
	
	@Override
	public String applyChange() {
		if (changedTiles.isEmpty() || suspect == null)
			return "No Changes Detected";
		
		if (changedTiles.size() > 1 && changedTiles.getFirst() != changedTiles.getLast())
			return "";
		
		clickedTile = changedTiles.getFirst();
		Suspect clickedSuspect = clickedTile.getSuspect();
		if (clickedTile.isRemovedTile() || clickedTile.isPassage() || (clickedSuspect != null && clickedSuspect != suspect))
			return "Player Start can only be placed on an empty floor tile.";
		
		suspectPreviousTile = board.getSuspectTile(suspect);
		
		if (clickedSuspect == suspect) {
			suspectPreviousDirection = clickedTile.getSuspectDirection();
			clickedTile.rotateSuspectDirection();
		}
		else {
			if (suspectPreviousTile != null)
				suspectPreviousTile.removeSuspects();
			clickedTile.addSuspect(suspect, null);
		}
				
		return null;
	}
	
	@Override
	public void undoChange() {
		if (clickedTile == null || suspect.getColor() == null)
			return;
		
		clickedTile.removeSuspects();
		if (suspectPreviousTile != null)
			suspectPreviousTile.addSuspect(suspect, suspectPreviousDirection);
	}
	
	@Override
	public void redoChange() {
		if (clickedTile == null || suspect.getColor() == null)
			return;
		
		if (suspectPreviousTile != null)
			suspectPreviousTile.removeSuspects();
		DisplayTile.Direction direction = (suspectPreviousDirection == null) ? null : suspectPreviousDirection.rotateClockwise();
		clickedTile.addSuspect(suspect, direction);
	}
	
	/**
	 * Returns the suspect involved in this change.
	 * @return the suspect involved in this change
	 */
	public Suspect getSuspect() {
		return suspect;
	}
}
