package action;

import model.Board;
import gui.ClueGUI;

/**
 * Action for moving a suspect to a new tile. 
 */
public class MoveSuspectToTileAction extends Action<ClueGUI> {

	/**
	 * Empty constructor required when inheriting from Action.
	 */
	public MoveSuspectToTileAction() {}
	
	/**
	 * Generates and returns a Move Suspect To Tile Action message.
	 * @param suspectId id of the suspect to move
	 * @param tileRow row number of the tile to move to
	 * @param tileCol column number of the tile to move to
	 * @param responsiblePlayerId id of the player who caused the suspect move
	 * @return a Move Suspect To Tile Action message
	 */
	public String createMessage(int suspectId, int tileRow, int tileCol, int responsiblePlayerId) {
		setMessage(this.getClass().getName() + MAIN_DELIM +
				   suspectId + MAIN_DELIM +
				   tileRow + MAIN_DELIM + 
				   tileCol + MAIN_DELIM +
				   responsiblePlayerId);
		return getMessage();
	}
	
	@Override
	public Class<ClueGUI> getActionTypeClass() {
		return ClueGUI.class;
	}
	
	@Override
	public String[] performAction(ClueGUI gui) {
		String[] data = this.getMessageWithoutClassHeader().split(MAIN_DELIM);
		int suspectId = Integer.parseInt(data[0]);
		int tileRow = Integer.parseInt(data[1]);
		int tileCol = Integer.parseInt(data[2]);
		int responsiblePlayerId = Integer.parseInt(data[3]);
		gui.moveSuspectToTile(suspectId, new Board.TilePosition(tileRow, tileCol), responsiblePlayerId);
		return null;
	}
}
