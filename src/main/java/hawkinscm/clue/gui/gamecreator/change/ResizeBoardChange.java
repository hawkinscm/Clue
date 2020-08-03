package hawkinscm.clue.gui.gamecreator.change;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.model.Room;
import hawkinscm.clue.model.Suspect;
import hawkinscm.clue.gui.DisplayTile;
import hawkinscm.clue.gui.gamecreator.BoardDesignerPanel;

/**
 * Change for the resizing of the board.
 * This Change will have sub-changes for every edited tile that is no longer on the board.
 */
public class ResizeBoardChange extends Change {
		
	private BoardDesignerPanel boardCreationPanel;
	private Board board;
	private int oldHeight;
	private int oldWidth;
	private int newHeight;
	private int newWidth;
	private LinkedList<Change> changes;
	
	/**
	 * Creates a new Resize Board Change
	 * @param panel Board Designer Panel where the board is being edited and displayed
	 * @param board current custom CLUE board
	 * @param newHeight new height of the board in tiles
	 * @param newWidth new width of the board in tiles
	 */
	public ResizeBoardChange(BoardDesignerPanel panel, Board board, int newHeight, int newWidth) {
		this.board = board;
		boardCreationPanel = panel;
		oldHeight = board.getHeight();
		oldWidth = board.getWidth();
		this.newHeight = newHeight;
		this.newWidth = newWidth;
		changes = new LinkedList<>();
	}
		
	@Override
	public String applyChange() {
		LinkedList<DisplayTile> hiddenTiles = new LinkedList<>();
		for (int row = 0; row < newHeight; row++) {
			for (int col = newWidth; col < oldWidth; col++)
				hiddenTiles.add(board.getTile(row, col));
		}
		for (int row = newHeight; row < oldHeight; row++) {
			for (int col = 0; col < oldWidth; col++)
				hiddenTiles.add(board.getTile(row, col));
		}
		
		HashMap<Room, RemoveRoomChange> removeRoomChanges = new HashMap<>();
		for (DisplayTile hiddenTile : hiddenTiles) {
			Change change = null;
			if (hiddenTile.hasSuspect())
				change = new RemovePlayerStartChange();
			else if (hiddenTile.isRemovedTile())
				change = new UnremoveTileChange();
			else if (hiddenTile.isPassage())
				change = new RemovePassageChange(board);						
			if (change != null) {
				change.addChangedTile(hiddenTile);
				change.applyChange();
				changes.add(change);
			}
						
			if (hiddenTile.isRoomTile()) {
				Room room = hiddenTile.getRoom();
				RemoveRoomChange removeRoomChange = removeRoomChanges.get(room);
				if (removeRoomChange == null) {
					removeRoomChange = new RemoveRoomChange(board, room);
					removeRoomChanges.put(room, removeRoomChange);
				}
				removeRoomChange.addChangedTile(hiddenTile);
			}
						
			for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
				if (hiddenTile.hasDoor(direction)) {
					change = new RemoveDoorChange(board);
					change.addChangedTile(hiddenTile);
					change.addChangedTile(board.getAdjacentTile(hiddenTile, direction));
					change.applyChange();
					changes.add(change);
				}
			}
		}
		for (RemoveRoomChange removeRoomChange : removeRoomChanges.values()) {
			if (removeRoomChange.applyChange() != null) {
				removeRoomChange = new RemoveRoomChange(board, removeRoomChange.getRoom());
				for (DisplayTile roomTile : board.getRoomTiles(removeRoomChange.getRoom()))
					removeRoomChange.addChangedTile(roomTile);
				removeRoomChange.applyChange();
			}				
			changes.add(removeRoomChange);
		}
		
		boardCreationPanel.resizeBoardWithoutChangeTracking(newHeight, newWidth);		
		
		return null;
	}
	
	@Override
	public void undoChange() {
		boardCreationPanel.resizeBoardWithoutChangeTracking(oldHeight, oldWidth);
		
		List<Room> rooms = boardCreationPanel.getRooms();
		List<Suspect> suspects = boardCreationPanel.getSuspects();
		for (Change change : changes) {
			if (change instanceof RemoveRoomChange && !rooms.contains(((RemoveRoomChange)change).getRoom()))
				continue;
			else if (change instanceof RemovePlayerStartChange && !suspects.contains(((RemovePlayerStartChange)change).getSuspect()))
				continue;
			
			change.undoChange();
		}
	}
	
	@Override
	public void redoChange() {
		List<Room> rooms = boardCreationPanel.getRooms();
		List<Suspect> suspects = boardCreationPanel.getSuspects();
		for (Change change : changes) {
			if (change instanceof RemoveRoomChange && !rooms.contains(((RemoveRoomChange)change).getRoom()))
				continue;
			else if (change instanceof RemovePlayerStartChange && !suspects.contains(((RemovePlayerStartChange)change).getSuspect()))
				continue;
			
			change.redoChange();
		}
		
		boardCreationPanel.resizeBoardWithoutChangeTracking(newHeight, newWidth);
	}
}
