package gui.gamecreator.change;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;

import model.Board;
import model.Room;
import gui.DisplayTile;

/**
 * Change for assigning tiles to a room.
 */
public class AddRoomChange extends Change {
		
	private Board board;
	private Room room;
	
	private RoomTileList linkedTiles;
	private ArrayList<DisplayTile> unlinkedTiles;
	private HashMap<DisplayTile, LinkedList<DisplayTile.Direction>> removedTileDoors;
	
	/**
	 * Creates a new Add Room Change.
	 * @param board current custom CLUE board
	 * @param room room to add tiles to
	 */
	public AddRoomChange(Board board, Room room) {
		this.board = board;
		this.room = room;
		
		linkedTiles = new RoomTileList(board.getRoomTiles(room));
		unlinkedTiles = new ArrayList<DisplayTile>();
		removedTileDoors = new HashMap<DisplayTile, LinkedList<DisplayTile.Direction>>();
	}
	
	@Override
	public void addChangedTile(DisplayTile tile) {
		if (tile.isRoomTile() || tile.isRemovedTile())
			return;

		super.addChangedTile(tile);
		
		if (linkedTiles.isEmpty())
			linkedTiles.add(tile);
		else {
			for (DisplayTile adjacentTile : board.getAdjacentTiles(tile))
				if (adjacentTile != null && linkedTiles.contains(adjacentTile)) {
					linkedTiles.add(tile);					
					
					return;
				}
			
			unlinkedTiles.add(tile);
			if (!tile.hasSuspect())
				tile.setBackground(Color.RED);
		}	
	}
	
	@Override
	public String applyChange() {
		if (!unlinkedTiles.isEmpty()) {
			for (DisplayTile unlinkedTile : unlinkedTiles) {
				unlinkedTile.removeRoom();
				changedTiles.remove(unlinkedTile);
			}
		}
		
		if (changedTiles.isEmpty()) {
			if (unlinkedTiles.isEmpty())
				return "Tiles must be non-removed, non-room tiles before they can be added to a room.";
			else
				return "Tiles must be adjacent to other room tiles in order to add to the SELECTED room: use lower-left toolbar to select a room.";
		}
		
		for (DisplayTile roomTile : linkedTiles) {
			int[] borderThicknesses = new int[] {2, 2, 2, 2};
			DisplayTile[] adjacentTiles = board.getAdjacentTiles(roomTile);
			for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
				DisplayTile adjacentTile = adjacentTiles[direction.getOrdinal()];
				if (adjacentTile != null && linkedTiles.contains(adjacentTile)) {
					borderThicknesses[direction.getOrdinal()] = 0;
					if (roomTile.hasDoor(direction)) {
						roomTile.removeDoor(direction);
						adjacentTile.removeDoor(direction.getOpposite());
						
						LinkedList<DisplayTile.Direction> removedDoors = removedTileDoors.get(roomTile);
						if (removedDoors == null) {
							removedDoors = new LinkedList<DisplayTile.Direction>();
							removedTileDoors.put(roomTile, removedDoors);
						}
						removedDoors.add(direction);
					}
				}
			}			
			roomTile.setBorder(BorderFactory.createMatteBorder(borderThicknesses[DisplayTile.Direction.NORTH.getOrdinal()],
															   borderThicknesses[DisplayTile.Direction.WEST.getOrdinal()], 
															   borderThicknesses[DisplayTile.Direction.SOUTH.getOrdinal()], 
															   borderThicknesses[DisplayTile.Direction.EAST.getOrdinal()],
															   Color.BLACK));
		}
				
		return null;
	}
	
	@Override
	public void undoChange() {
		if (changedTiles.isEmpty())
			return;
		
		for (DisplayTile roomTile : changedTiles) {
			roomTile.removeRoom();
			roomTile.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}
		
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
		if (changedTiles.isEmpty())
			return;
		
		for (DisplayTile roomTile : changedTiles)
			roomTile.setRoom(room);
		
		for (DisplayTile roomTile : linkedTiles) {
			DisplayTile[] adjacentTiles = board.getAdjacentTiles(roomTile);
			DisplayTile northTile = adjacentTiles[DisplayTile.Direction.NORTH.getOrdinal()];
			DisplayTile westTile = adjacentTiles[DisplayTile.Direction.WEST.getOrdinal()];
			DisplayTile southTile = adjacentTiles[DisplayTile.Direction.SOUTH.getOrdinal()];
			DisplayTile eastTile = adjacentTiles[DisplayTile.Direction.EAST.getOrdinal()];
			
			int topBorderThickness = (northTile == null || !linkedTiles.contains(northTile)) ? 2 : 0;
			int leftBorderThickness = (westTile == null || !linkedTiles.contains(westTile)) ? 2 : 0;
			int bottomBorderThickness = (southTile == null || !linkedTiles.contains(southTile)) ? 2 : 0;
			int rightBorderThickness = (eastTile == null || !linkedTiles.contains(eastTile)) ? 2 : 0;
			roomTile.setBorder(BorderFactory.createMatteBorder(topBorderThickness, leftBorderThickness, bottomBorderThickness, rightBorderThickness, Color.BLACK));
		}
		
		for (DisplayTile removedDoorTile : removedTileDoors.keySet()) {
			for (DisplayTile.Direction doorDirection : removedTileDoors.get(removedDoorTile)) {
				removedDoorTile.removeDoor(doorDirection);
				board.getAdjacentTile(removedDoorTile, doorDirection).removeDoor(doorDirection.getOpposite());
			}				
		}
	}
	
	/**
	 * Returns the room involved in this change.
	 * @return the room involved in this change
	 */
	public Room getRoom() {
		return room;
	}
	
	/**
	 * List for containing/handling lists of linked and unlinked tiles.
	 */
	private class RoomTileList extends LinkedList<DisplayTile> {
		private static final long serialVersionUID = 1L;
		
		/**
		 * Creates a new Room Tile List.
		 * @param list list of linked tiles to initialize with
		 */
		public RoomTileList(List<DisplayTile> list) {
			super(list);
		}
		
		@Override
		public boolean add(DisplayTile tile) {
			tile.setRoom(room);
			
			LinkedList<DisplayTile> toLinkTiles = new LinkedList<DisplayTile>();
			
			Iterator<DisplayTile> unlinkedTileIter = unlinkedTiles.iterator();
			while (unlinkedTileIter.hasNext()) {
				DisplayTile unlinkedTile = unlinkedTileIter.next();
				for (DisplayTile adjacentTile : board.getAdjacentTiles(unlinkedTile)) {
					if (adjacentTile == tile) {
						unlinkedTileIter.remove();
						toLinkTiles.add(unlinkedTile);
						break;
					}
				}
			}
			
			for (DisplayTile toLinkTile : toLinkTiles)
				linkedTiles.add(toLinkTile);
			
			return super.add(tile);
		}
	}
}
