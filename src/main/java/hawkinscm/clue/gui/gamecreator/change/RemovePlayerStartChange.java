package hawkinscm.clue.gui.gamecreator.change;

import hawkinscm.clue.model.Suspect;
import hawkinscm.clue.gui.DisplayTile;

/**
 * Change for removing a Player Start from a board tile.
 */
public class RemovePlayerStartChange extends Change {
		
	private DisplayTile clickedTile;
	private Suspect clickedSuspect;
	private DisplayTile.Direction suspectDirection;
	
	/**
	 * Creates a new Remove Player Start Change.
	 */
	public RemovePlayerStartChange() {
		clickedTile = null;
		clickedSuspect = null;
		suspectDirection = null;
	}
	
	@Override
	public String applyChange() {
		if (changedTiles.isEmpty())
			return "No Changes Detected";
		
		if (changedTiles.size() > 1 && changedTiles.getFirst() != changedTiles.getLast())
			return "";
		
		
		clickedSuspect = changedTiles.getFirst().getSuspect();
		if (clickedSuspect == null)
			return "";
		
		clickedTile = changedTiles.getFirst();
		suspectDirection = clickedTile.getSuspectDirection();
		clickedTile.removeSuspects();
				
		return null;
	}
	
	@Override
	public void undoChange() {
		if (clickedTile == null || clickedSuspect == null)
			return;
		
		clickedTile.addSuspect(clickedSuspect, suspectDirection);
	}
	
	@Override
	public void redoChange() {
		if (clickedTile == null)
			return;
		
		clickedTile.removeSuspects();
	}
	
	public Suspect getSuspect() {
		return clickedSuspect;
	}
}
