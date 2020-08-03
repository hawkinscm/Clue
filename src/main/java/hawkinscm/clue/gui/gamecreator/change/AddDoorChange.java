package hawkinscm.clue.gui.gamecreator.change;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.gui.DisplayTile;

/**
 * Change for adding a door to the side of a board tile.
 */
public class AddDoorChange extends Change {
		
	private Board board;
	DisplayTile startTile;
	DisplayTile endTile;
	DisplayTile.Direction doorDirection;
	
	/**
	 * Creates a new Add Door Change.
	 * @param board current custom CLUE board
	 */
	public AddDoorChange(Board board) {
		this.board = board;
		startTile = null;
		endTile = null;
		doorDirection = null;
	}
		
	@Override
	public String applyChange() {
		if (changedTiles.isEmpty())
			return "No changes detected";
		
		startTile = changedTiles.getFirst();
		endTile = startTile;
		for (DisplayTile changedTile : changedTiles) {
			if (changedTile != startTile) {
				endTile = changedTile;
				break;
			}
		}
		
		if (startTile.getRoom() == endTile.getRoom())
			return "Door can only be placed on an existing room wall.";
		
		if (startTile.isRemovedTile() || endTile.isRemovedTile())
			return "Door cannot be placed next to a removed tile.";
		
		DisplayTile[] adjacentTiles = board.getAdjacentTiles(startTile);
		for (DisplayTile.Direction direction : DisplayTile.Direction.values())
			if (endTile == adjacentTiles[direction.getOrdinal()])
				doorDirection = direction;
		
		if (doorDirection == null) 
			return "Door can only be placed on an existing room wall.";
		
		if (startTile.hasDoor(doorDirection))
			return "";
		
		startTile.addDoor(doorDirection);
		endTile.addDoor(doorDirection.getOpposite());
		
		return null;
	}
	
	@Override
	public void undoChange() {
		if (startTile == null || endTile == null || doorDirection == null)
			return;
		
		startTile.removeDoor(doorDirection);
		endTile.removeDoor(doorDirection.getOpposite());
	}
	
	@Override
	public void redoChange() {		
		if (startTile == null || endTile == null || doorDirection == null)
			return;
		
		startTile.addDoor(doorDirection);
		endTile.addDoor(doorDirection.getOpposite());
	}
}
