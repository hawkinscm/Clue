package hawkinscm.clue.ai;

import hawkinscm.clue.gui.DisplayTile;
import hawkinscm.clue.gui.DisplayTile.Direction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.model.Room;

/**
 * Handler for exploring AI travel around the board (from room to room).
 */
public class TravelHandler {
	/**
	 * Returns a map from every accessible room to the shortest path the player can currently take to get to that room.
	 * @param board Clue Board
	 * @param startTile start tile to explore out from (where the player currently is)
	 * @return a map from every accessible room to the shortest path the player can currently take to get to that room
	 */
	public static void getShortestPathsToRooms(Board board, DisplayTile startTile) {
		HashMap<Room, List<DisplayTile>> shortestPathToRoomsMap = new HashMap<Room, List<DisplayTile>>();
		HashMap<DisplayTile, DisplayTile> chainedTiles = new HashMap<DisplayTile, DisplayTile>();
		chainedTiles.put(startTile, null);
		LinkedList<DisplayTile> tilesToExplore = new LinkedList<DisplayTile>();
		
		addUnexploredAdjacentTiles(board, startTile, chainedTiles, tilesToExplore);
		while (!tilesToExplore.isEmpty()) {
			DisplayTile currentTile = tilesToExplore.remove();
			if (currentTile.isRoomTile()) {
				LinkedList<DisplayTile> path = new LinkedList<DisplayTile>();
				DisplayTile tile = currentTile;
				do {
					path.addLast(tile);
					tile = chainedTiles.get(tile);
				}
				while (tile != startTile);
	
				Room room = currentTile.getRoom();
				if (!shortestPathToRoomsMap.containsKey(room) || path.size() < shortestPathToRoomsMap.get(room).size())
					shortestPathToRoomsMap.put(room, path);
			}
			else
				addUnexploredAdjacentTiles(board, currentTile, chainedTiles, tilesToExplore);
		}
	}
	
	/**
	 * Adds the list of the unblocked and unexplored adjacent tiles next to the given tile to the list of tiles to explore.
	 * @param board Clue Board
	 * @param startTile center tile to start from
	 * @param chainedTiles tiles that have been explored and chained in shortest paths
	 * @param tilesToExplore queue of tiles to explore in FIFO order
	 */
	private static void addUnexploredAdjacentTiles(Board board, DisplayTile startTile, HashMap<DisplayTile, DisplayTile> chainedTiles, LinkedList<DisplayTile> tilesToExplore) {
		if (startTile.isRoomTile()) {
			for (DisplayTile exitTile : board.getFreeExitTiles(startTile.getRoom())) 
				addUnexploredTile(exitTile, chainedTiles, tilesToExplore);
		}
		else {
			for (Direction direction : DisplayTile.Direction.values()) {
				DisplayTile adjacentTile = board.getAdjacentTile(startTile, direction);
				if (isTileAvailable(adjacentTile, direction, chainedTiles))
					addUnexploredTile(adjacentTile, chainedTiles, tilesToExplore);
			}
		}
	}
	
	/**
	 * Adds the tile as an unexplored tile.
	 * @param tile tile to add
	 * @param chainedTiles tiles marked as chained and explored
	 * @param tilesToExplore list of unexplored tiles to add to
	 */
	private static void addUnexploredTile(DisplayTile tile, HashMap<DisplayTile, DisplayTile> chainedTiles, LinkedList<DisplayTile> tilesToExplore) {
		if (!tile.isRoomTile() && tile.isPassage()) {
			chainedTiles.put(tile, null);
			tile = tile.getPassageConnection();
		}
		tilesToExplore.add(tile);
	}
			
	/**
	 * Returns whether or not the given tile is available to be arrived at and explored.
	 * @param tile tile to examine
	 * @param fromDirection the direction from which we attempting to move
	 * @param chainedTiles tiles that have been explored and chained in shortest paths
	 * @return true if the tile is unblocked and has not yet been explored and chained; false otherwise
	*/
	private static boolean isTileAvailable(DisplayTile tile, Direction fromDirection, HashMap<DisplayTile, DisplayTile> chainedTiles) {
		if (tile == null || chainedTiles.containsKey(tile))
			return false;
		
		if (tile.isRoomTile() && !tile.hasDoor(fromDirection))
			return false;
			
		if (tile.hasSuspect() || tile.isRemovedTile())
			return false;
		
		if (tile.isPassage() && tile.getPassageConnection().hasSuspect())
			return false;
		
		return true;
	}
}
