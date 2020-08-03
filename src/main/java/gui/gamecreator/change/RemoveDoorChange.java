package gui.gamecreator.change;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import model.Board;
import gui.DisplayTile;

/**
 * Change for adding a door from the side of a board tile.
 */
public class RemoveDoorChange extends Change {
		
	private Board board;
	private HashMap<DisplayTile, LinkedList<DisplayTile.Direction>> removedTileDoors;
	
	/**
	 * Creates a new Remove Door Change
	 * @param board current custom CLUE board
	 */
	public RemoveDoorChange(Board board) {
		this.board = board;
		removedTileDoors = new HashMap<>();
	}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (changedTiles.isEmpty()) {
			super.addChangedTile(tile);
			return;
		}
		
		DisplayTile lastTile = changedTiles.getLast();
		if (lastTile == tile)
			return;
		
		super.addChangedTile(tile);
		
		DisplayTile[] adjacentTiles = board.getAdjacentTiles(lastTile);
		for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
			if (tile == adjacentTiles[direction.getOrdinal()] && lastTile.hasDoor(direction)) {
				lastTile.removeDoor(direction);
				tile.removeDoor(direction.getOpposite());
				
				LinkedList<DisplayTile.Direction> removedDoors = removedTileDoors.get(lastTile);
				if (removedDoors == null) {
					removedDoors = new LinkedList<>();
					removedTileDoors.put(lastTile, removedDoors);
				}
				removedDoors.add(direction);
				break;
			}
		}
	}
	
	@Override
	public String applyChange() {		
		if (changedTiles.size() == 1)
			return "Right-click and drag through doors to remove them.";
		
		Set<DisplayTile> removedDoorTiles = removedTileDoors.keySet();
		if (removedDoorTiles.isEmpty())
			return "";
		
		return null;
	}
	
	@Override
	public void undoChange() {
		for (DisplayTile removedDoorTile : removedTileDoors.keySet()) {
			for (DisplayTile.Direction doorDirection : removedTileDoors.get(removedDoorTile)) {
				DisplayTile connectedTile = board.getAdjacentTile(removedDoorTile, doorDirection);
				if (removedDoorTile.getRoom() == connectedTile.getRoom())
					continue;
				
				removedDoorTile.addDoor(doorDirection);
				connectedTile.addDoor(doorDirection.getOpposite());
			}				
		}
	}
	
	@Override
	public void redoChange() {
		for (DisplayTile removedDoorTile : removedTileDoors.keySet()) {
			for (DisplayTile.Direction doorDirection : removedTileDoors.get(removedDoorTile)) {
				removedDoorTile.removeDoor(doorDirection);
				board.getAdjacentTile(removedDoorTile, doorDirection).removeDoor(doorDirection.getOpposite());
			}				
		}
	}
}
