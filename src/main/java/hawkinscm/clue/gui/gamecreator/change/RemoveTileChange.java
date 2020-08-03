package hawkinscm.clue.gui.gamecreator.change;

import hawkinscm.clue.gui.DisplayTile;

/**
 * Change for removing/hiding a board tiles.
 */
public class RemoveTileChange extends Change {
	
	/**
	 * Creates a new Remove Tile Change
	 */
	public RemoveTileChange() {}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (tile.isRoomTile() || tile.isPassage() || tile.hasSuspect() || 
			tile.isRemovedTile() || changedTiles.contains(tile) || tile.hasDoorConnection())
			return;

		tile.setRemoved();
		super.addChangedTile(tile);
	}
	
	@Override
	public String applyChange() {
		if (changedTiles.isEmpty())
			return "Tiles must be empty non-room tiles, not connected to room doors before they can be removed.";
				
		return null;
	}
	
	@Override
	public void undoChange() {
		for (DisplayTile changedTile : changedTiles)
			changedTile.setUnremoved();
	}
	
	@Override
	public void redoChange() {
		for (DisplayTile changedTile : changedTiles)
			changedTile.setRemoved();
	}
}
