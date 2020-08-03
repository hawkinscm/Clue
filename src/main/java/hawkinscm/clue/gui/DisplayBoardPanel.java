package hawkinscm.clue.gui;

import hawkinscm.clue.gui.ImageHelper.ImageSize;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.model.Room;
import hawkinscm.clue.model.Suspect;

/**
 * Display for a CLUE board.
 */
public class DisplayBoardPanel extends JPanel implements Observer {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<ImageIcon> roomIcons;
	private ArrayList<Board.TilePosition> roomTileStartPositions;
	private ArrayList<Board.TilePosition> roomTileEndPositions;
	private ImageIcon backgroundImage;
		
	private Board board;
	private int tileSize;
	
	/**
	 * Constructor for a CLUE board display
	 * @param board CLUE board to display
	 * @param rooms rooms on the clue board
	 * @param displayRoomPictures whether or not room pictures should be displayed
	 * @param backgroundImageFilename image to use for the board background
	 * @param backgroundColor color to use for the board background or as a transparent color if board background is not null
	 */
	public DisplayBoardPanel(Board board, List<Room> rooms, boolean displayRoomPictures, String backgroundImageFilename, Color backgroundColor) {
		super(new GridLayout(board.getHeight(), board.getWidth(), 0, 0));
		this.board = board;
		
		roomIcons = new ArrayList<>();
		roomTileStartPositions = new ArrayList<>();
		roomTileEndPositions = new ArrayList<>();
		
		resizeBoardDisplay(575, 600);
		
		for (DisplayTile tile : board.getTiles()) {
			tile.setPreferredSize(new Dimension(tileSize, tileSize));
			add(tile);
			if (tile.isRemovedTile())
				tile.setVisible(false);
			for (MouseListener listener : tile.getMouseListeners())
				tile.removeMouseListener(listener);
		}
	
		for (Room room : rooms) {
			if (room.getPictureName() == null || !displayRoomPictures) {
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
			else {
				int smallestRow = Board.MAX_HEIGHT;
				int smallestCol = Board.MAX_WIDTH;
				int largestRow = -1;
				int largestCol = -1;
				for (DisplayTile roomTile : board.getRoomTiles(room)) {
					Board.TilePosition tilePosition = board.getTilePosition(roomTile);
					if (tilePosition.row < smallestRow) smallestRow = tilePosition.row;
					if (tilePosition.col < smallestCol) smallestCol = tilePosition.col;
					if (tilePosition.row > largestRow) largestRow = tilePosition.row;
					if (tilePosition.col > largestCol) largestCol = tilePosition.col;
				}
				if (smallestRow >= Board.MAX_HEIGHT || smallestCol >= Board.MAX_WIDTH)
					continue;
				
				roomIcons.add(ImageHelper.getTransparentIcon(room.getPictureName(), room.getTransparentPictureColor()));
				roomTileStartPositions.add(new Board.TilePosition(smallestRow, smallestCol));
				roomTileEndPositions.add(new Board.TilePosition(largestRow, largestCol));
			}
		}
		
		if (backgroundImageFilename != null) {
			setBackground(Color.BLACK);
			backgroundImage = ImageHelper.getTransparentIcon(backgroundImageFilename, backgroundColor);
		}
		else
			setBackground(backgroundColor);
	}
	
	/**
	 * Handles the sizing and painting of the board and its tiles on a resize.
	 * @param newHeight height to set the board to
	 * @param newWidth width to set the board to
	 */
	public void resizeBoardDisplay(int newHeight, int newWidth) {
		int maxTileHeight = newHeight / board.getHeight();
		int maxTileWidth = newWidth / board.getWidth();
		int borderSize = 2;
		tileSize = Math.min(maxTileHeight, maxTileWidth) - borderSize;
		if (tileSize < 10)
			tileSize = 10;
		
		for (Component tile : getComponents())
			if (tile instanceof DisplayTile)
				tile.setPreferredSize(new Dimension(tileSize, tileSize));
	
		revalidate();
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		for (int idx = 0; idx < roomIcons.size(); idx++) {
			Board.TilePosition startPosition = roomTileStartPositions.get(idx);
			Board.TilePosition endPosition = roomTileEndPositions.get(idx);
			Point imageStart = new Point(startPosition.col * tileSize, startPosition.row * tileSize);
			Dimension imageSize = new Dimension(((endPosition.col + 1) - startPosition.col) * tileSize, ((endPosition.row + 1) - startPosition.row) * tileSize);
			g.drawImage(roomIcons.get(idx).getImage(), imageStart.x, imageStart.y, imageSize.width, imageSize.height, null);
		}
		
		if (backgroundImage != null)
			g.drawImage(backgroundImage.getImage(), 0, 0, board.getWidth() * tileSize, board.getHeight() * tileSize, null);
		
		int firstBlockSize = tileSize / 2;
		int lastBlockSize = (tileSize + 1) / 2;
		for (DisplayTile tile : board.getTiles()) {
			if (tile.isRoomTile() && tile.hasSuspect()) {
				Board.TilePosition tilePosition = board.getTilePosition(tile);
				List<Suspect> suspects = tile.getSuspects();
				int suspectSize = Math.min(suspects.size(), 4);
				
				int x = tilePosition.col * tileSize;
				int y = tilePosition.row * tileSize;
				g.setColor(suspects.get(0).getColor());
				g.fillRect(x, y, firstBlockSize, firstBlockSize);
				
				x += firstBlockSize;
				g.setColor(suspects.get((suspectSize > 1) ? 1 : 0).getColor());
				g.fillRect(x, y, lastBlockSize, firstBlockSize);
				
				y += firstBlockSize;
				g.setColor(suspects.get(suspectSize - 1).getColor());
				g.fillRect(x, y, lastBlockSize, lastBlockSize);
				
				x -= firstBlockSize;
				g.setColor(suspects.get((suspectSize == 4) ? 2 : 0).getColor());
				g.fillRect(x, y, firstBlockSize, lastBlockSize);
				
				y -= firstBlockSize;
				ImageHelper.ImageSize imageSize = (tileSize > 35) ? ImageSize.LARGE : ((tileSize > 19) ? ImageSize.MEDIUM : ImageSize.SMALL);
				int imageSizeAsInt = (imageSize == ImageSize.LARGE) ? 32 : ((imageSize == ImageSize.MEDIUM) ? 16 : 8);
				x += (tileSize - imageSizeAsInt) / 2;
				y += (tileSize - imageSizeAsInt) / 2;
				g.drawImage(ImageHelper.getIcon(ImageHelper.ImageType.FOOTSTEPS, imageSize).getImage(), x, y, imageSizeAsInt, imageSizeAsInt, null);
			}
		}
	}

	@Override
	public void update(Observable observable, Object obj) {
		if (observable instanceof Board)
			if (obj instanceof Boolean && (Boolean) obj)
				repaint();
	}
}
