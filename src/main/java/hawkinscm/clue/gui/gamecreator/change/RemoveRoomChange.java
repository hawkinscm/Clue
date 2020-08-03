package hawkinscm.clue.gui.gamecreator.change;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.BorderFactory;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.model.Room;
import hawkinscm.clue.gui.DisplayTile;

/**
 * Change for clearing tiles from a room.
 */
public class RemoveRoomChange extends Change {
		
	private Board board;
	private Room room;
	
	private LinkedList<DisplayTile> roomTiles;
	private boolean areAllTilesConnected;
	private HashMap<DisplayTile, ArrayList<DisplayTile.Direction>> removedTileDoors;
	
	/**
	 * Creates a new Remove Room Change.
	 * @param board current custom CLUE board
	 * @param room room that tiles will be removed from
	 */
	public RemoveRoomChange(Board board, Room room) {
		this.board = board;
		this.room = room;
		
		roomTiles = board.getRoomTiles(room);
		areAllTilesConnected = true;
		removedTileDoors = new HashMap<>();
	}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (tile.getRoom() != room)
			return;

		tile.removeRoom();
		removedTileDoors.put(tile, new ArrayList<>());
		for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
			if (tile.hasDoor(direction)) {
				DisplayTile connectedTile = board.getAdjacentTile(tile, direction);
				if (!connectedTile.isRoomTile()) {
					tile.removeDoor(direction);
					connectedTile.removeDoor(direction.getOpposite());
					removedTileDoors.get(tile).add(direction);
				}
			}
		}
		roomTiles.remove(tile);
		super.addChangedTile(tile);
		
		LinkedList<LinkedList<DisplayTile>> roomGroups = new LinkedList<>();
		LinkedList<DisplayTile> biggestGroup = null;
		LinkedList<DisplayTile> checkedRoomTiles = new LinkedList<>();
		for (DisplayTile roomTile : roomTiles) {
			if (checkedRoomTiles.contains(roomTile))
				continue;
			
			LinkedList<DisplayTile> roomGroup = new LinkedList<>();
			addRoomAndConnectedRoomTiles(roomGroup, roomTile);
			roomGroups.add(roomGroup);
			if (biggestGroup == null || roomGroup.size() > biggestGroup.size())
				biggestGroup = roomGroup;

			checkedRoomTiles.addAll(roomGroup);			
		}
		
		areAllTilesConnected = (roomGroups.size() <= 1);
					
		for (LinkedList<DisplayTile> roomGroup : roomGroups) {
			if (roomGroup == biggestGroup) {
				for (DisplayTile roomTile : roomGroup)
					roomTile.setRoom(room);
			}
			else {
				for (DisplayTile roomTile : roomGroup)
					roomTile.setBackground(Color.RED);
			}
		}
	}
	
	/**
	 * Recursive method for handling groups of linked and unlinked room tiles.
	 * @param roomGroup group of tiles that are linked
	 * @param roomTile tile to add to the group
	 */
	private void addRoomAndConnectedRoomTiles(LinkedList<DisplayTile> roomGroup, DisplayTile roomTile) {
		roomGroup.add(roomTile);
		for (DisplayTile adjacentTile : board.getAdjacentTiles(roomTile))
			if (adjacentTile != null && adjacentTile.getRoom() == room && !roomGroup.contains(adjacentTile))
				addRoomAndConnectedRoomTiles(roomGroup, adjacentTile);
	}
	
	@Override
	public String applyChange() {
		if (changedTiles.isEmpty())
			return "";
		
		if (!areAllTilesConnected) {
			for (DisplayTile roomTile : roomTiles)
				roomTile.setRoom(room);
			for (DisplayTile changedTile : changedTiles) {
				changedTile.setRoom(room);
				for (DisplayTile.Direction doorDirection : removedTileDoors.get(changedTile)) {
					DisplayTile connectedTile = board.getAdjacentTile(changedTile, doorDirection);
					if (connectedTile != null) {
						changedTile.addDoor(doorDirection);
						connectedTile.addDoor(doorDirection.getOpposite());
					}
				}
			}
			changedTiles.clear();
			
			updateRoomWalls();
			return "Room tile removal failed because it left room tiles disconnected from other room tiles.";
		}
		
		updateRoomWalls();
				
		return null;
	}
	
	@Override
	public void undoChange() {
		if (changedTiles.isEmpty())
			return;
		
		for (DisplayTile changedTile : changedTiles) {
			changedTile.setRoom(room);
			for (DisplayTile.Direction doorDirection : removedTileDoors.get(changedTile)) {
				DisplayTile connectedTile = board.getAdjacentTile(changedTile, doorDirection);
				if (connectedTile != null) {
					changedTile.addDoor(doorDirection);
					connectedTile.addDoor(doorDirection.getOpposite());
				}
			}
		}
			
		updateRoomWalls();
	}
	
	@Override
	public void redoChange() {
		if (changedTiles.isEmpty())
			return;
		
		for (DisplayTile changedTile : changedTiles) {
			changedTile.removeRoom();
			for (DisplayTile.Direction doorDirection : removedTileDoors.get(changedTile)) {
				DisplayTile connectedTile = board.getAdjacentTile(changedTile, doorDirection);
				if (connectedTile != null) {
					changedTile.removeDoor(doorDirection);
					connectedTile.removeDoor(doorDirection.getOpposite());
				}
			}
		}
			
		updateRoomWalls();
	}
	
	/**
	 * Returns the room involving in this Change.
	 * @return the room involving in this Change
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * Updates the display of the room, placing walls only on the room perimeter.
	 */
	private void updateRoomWalls() {		
		LinkedList<DisplayTile> roomTiles = board.getRoomTiles(room);
		for (DisplayTile roomTile : roomTiles) {
			DisplayTile[] adjacentTiles = board.getAdjacentTiles(roomTile);
			DisplayTile northTile = adjacentTiles[DisplayTile.Direction.NORTH.getOrdinal()];
			DisplayTile westTile = adjacentTiles[DisplayTile.Direction.WEST.getOrdinal()];
			DisplayTile southTile = adjacentTiles[DisplayTile.Direction.SOUTH.getOrdinal()];
			DisplayTile eastTile = adjacentTiles[DisplayTile.Direction.EAST.getOrdinal()];
			
			int topBorderThickness = (northTile == null || !roomTiles.contains(northTile)) ? 2 : 0;
			int leftBorderThickness = (westTile == null || !roomTiles.contains(westTile)) ? 2 : 0;
			int bottomBorderThickness = (southTile == null || !roomTiles.contains(southTile)) ? 2 : 0;
			int rightBorderThickness = (eastTile == null || !roomTiles.contains(eastTile)) ? 2 : 0;
			roomTile.setBorder(BorderFactory.createMatteBorder(topBorderThickness, leftBorderThickness, bottomBorderThickness, rightBorderThickness, Color.BLACK));
		}
	}
}
