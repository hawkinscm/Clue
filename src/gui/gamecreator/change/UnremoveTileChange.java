package gui.gamecreator.change;

import gui.DisplayTile;

/**
 * Change for unremoving/unhiding board tiles.
 */
public class UnremoveTileChange extends Change {
	
	/**
	 * Creates a new Unremove Tile Change
	 */
	public UnremoveTileChange() {}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (!tile.isRemovedTile())
			return;

		tile.setUnremoved();
		super.addChangedTile(tile);
	}
	
	@Override
	public String applyChange() {
		if (changedTiles.isEmpty())
			return "";
				
		return null;
	}
	
	@Override
	public void undoChange() {
		for (DisplayTile changedTile : changedTiles)
			changedTile.setRemoved();
	}
	
	@Override
	public void redoChange() {
		for (DisplayTile changedTile : changedTiles)
			changedTile.setUnremoved();
	}
}
