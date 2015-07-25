package gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import model.Board;
import model.Room;

/**
 * Allows a player to choose an exit for the room they are in.
 */
public class ExitRoomDialog extends CustomDialog {
	private static final long serialVersionUID = 1L;

	private boolean onlyOneFreeExit;
	private DisplayTile selectedExitTile;
	
	/**
	 * Creates an Exit Room Dialog (if there is only one exit, it will be auto-selected without any display)
	 * @param owner parent/owner of this dialog
	 * @param board CLUE game board
	 * @param room room the player is in
	 */
	public ExitRoomDialog(JFrame owner, final Board board, Room room) {
		super(owner, "Exit Room");
		
		selectedExitTile = null;
		onlyOneFreeExit = false;
		
		final Board exitRoomBoard = new Board(board);
		List<DisplayTile> exitTiles = exitRoomBoard.getFreeExitTiles(room);
		if (exitTiles.size() == 1) {
			onlyOneFreeExit = true;
			selectedExitTile = board.getTile(exitRoomBoard.getTilePosition(exitTiles.get(0)));
			dispose();
			return;
		}
		
		for (DisplayTile tile : exitRoomBoard.getTiles())
			if (!exitTiles.contains(tile) && tile.getRoom() != room) {
				tile.removeSuspects();
				tile.setRemoved();
			}
		
		add(new JLabel("The available Exit Tiles are highlighted in red. Choose one by clicking on it."), c);
		
		c.gridy++;
		add(new DisplayBoardPanel(exitRoomBoard, Arrays.asList(room), false, null, null), c);
		
		for (DisplayTile exitTile : exitTiles) {
			exitTile.setBackground(Color.RED);
			exitTile.addMouseListener(new MouseListener() {
				private boolean isInside = false;
				
				public void mouseClicked(MouseEvent e) {}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() != MouseEvent.BUTTON1 || !isInside || !isEnabled() || !(e.getComponent() instanceof DisplayTile))
						return;
				
					selectedExitTile = board.getTile(exitRoomBoard.getTilePosition((DisplayTile) e.getComponent()));
					dispose();
				}
				public void mouseEntered(MouseEvent e) {isInside = true;}
				public void mouseExited(MouseEvent e) {isInside = false;}
			});
		}
		
		refresh();
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (!visible || !onlyOneFreeExit)
			super.setVisible(visible);
	}
	
	/**
	 * Returns the tile the player selected as an exit.
	 * @return the tile the player selected as an exit
	 */
	public DisplayTile getSelectedExitTile() {
		return selectedExitTile;
	}
}
