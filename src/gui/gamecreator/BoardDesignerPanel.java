package gui.gamecreator;

import gui.CustomButton;
import gui.CustomDialog;
import gui.DisplayBoardPanel;
import gui.DisplayTile;
import gui.ImageHelper;
import gui.Messenger;
import gui.gamecreator.change.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import model.Board;
import model.Room;
import model.Suspect;

/**
 * Panel for designing a CLUE board.
 */
public class BoardDesignerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private final int defaultBoardHeight = 25;
	private final int defaultBoardWidth = 25;
	private int inputHeight;
	private int inputWidth;
	
	GameDesignerDisplay gameDesignerDisplay;
	
	private JPanel boardDisplayPanel;
	private Board board;	
	private MouseListener editModeMouseListener;
	
	private JTextField boardHeightTextField;
	private JTextField boardWidthTextField; 
	
	private CustomButton undoButton;
	private CustomButton redoButton;	
	private Stack<Change> undoStack;
	private Stack<Change> redoStack;
	
	private CustomButton saveButton;
	private Change lastChangeSaved;
	
	private LinkedList<CustomButton> toolbarButtons;
	private JLabel toolHelpLabel;
	private JPanel toolbarHelperPanel;
	
	boolean changeInProgress;
	
	private List<Suspect> suspects;
	private List<Room> rooms;

	private int headerHeight;
	private int toolbarWidth;
	
	private ImageHelper.ImageSize cursorSize;
	
	private EditMode selectedEditMode;
	private Room selectedRoom;
	private Suspect selectedSuspect;
	
	/**
	 * Enumeration for Edit Modes.
	 */
	private enum EditMode {
		ROOM,
		DOOR,
		REMOVE_TILE,
		PASSAGE,
		PLAYER_START;
		
		/**
		 * Returns the Image Type used for the cursor of this Edit Mode.
		 * @return the Image Type used for the cursor of this Edit Mode
		 */
		public ImageHelper.ImageType getImageType() {
			switch (this) {
				case ROOM         : return ImageHelper.ImageType.ROOM;
				case DOOR         : return ImageHelper.ImageType.DOOR;
				case REMOVE_TILE  : return ImageHelper.ImageType.REMOVE_TILE;
				case PASSAGE      : return ImageHelper.ImageType.PASSAGE;
				case PLAYER_START : return ImageHelper.ImageType.FOOTSTEPS;
				default           : return null;
			}
		}
		
		/**
		 * Returns the instruction summary for this Edit Mode.
		 * @return the instruction summary for this Edit Mode
		 */
		public String getInstructions() {
			switch (this) {
				case ROOM         : return "EDIT ROOM: Left-click and drag the mouse to create or add to the tiles of the SELECTED room. Right-click to remove tiles.";
				case DOOR         : return "EDIT DOOR: Left-click the tile on one side of a wall and drag through it to create a door. Right-click and drag to remove.";
				case REMOVE_TILE  : return "REMOVE TILE: Left-click and/or drag on non-room tiles to remove them as tiles on the board. Right-click to restore them.";
				case PASSAGE      : return "EDIT PASSAGE: Press the left mouse-button to start a passage, then drag and release on a new tile to end it. Right-click to remove.";
				case PLAYER_START : return "EDIT PLAYER START: Left-click on a tile to create or move a starting tile for the selected player. Right-click to remove.";
				default           : return " ";
			}
		}
	}
	
	/**
	 * Creates a new Board Designer Panel
	 * @param owner the GameDesignerDisplay that owns this panel
	 */
	public BoardDesignerPanel(GameDesignerDisplay owner) {
		gameDesignerDisplay = owner;
		headerHeight = 0;
		toolbarWidth = 0;
		board = new Board(defaultBoardHeight, defaultBoardWidth);
		cursorSize = ImageHelper.ImageSize.MEDIUM;
		undoStack = new Stack<Change>();
		redoStack = new Stack<Change>();
		lastChangeSaved = null;
		toolbarButtons = new LinkedList<CustomButton>();
		changeInProgress = false;
		suspects = new ArrayList<Suspect>(0);
		rooms = new ArrayList<Room>(0);
		createEditModeMouseListener();
		
		addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentResized(ComponentEvent e) {
				resizeBoardWithoutChangeTracking(inputHeight, inputWidth);
			}
			public void componentShown(ComponentEvent e) {}
			
		});

		AbstractAction undoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				undoLastChange();
			}
		};		
		getActionMap().put("UNDO_ACTION", undoAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "UNDO_ACTION");
		
		AbstractAction redoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				redoLastUndoneChange();
			}
		};		
		getActionMap().put("REDO_ACTION", redoAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "REDO_ACTION");
		
		AbstractAction saveAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				saveChanges();
			}
		};		
		getActionMap().put("SAVE_ACTION", saveAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "SAVE_ACTION");
		
		boardDisplayPanel = new JPanel();
		toolHelpLabel = new JLabel(" ");
		toolbarHelperPanel = new JPanel();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 5, 0, 5);		
		c.anchor = GridBagConstraints.NORTHWEST;
		add(createToolbarPanel(), c);
		c.anchor = GridBagConstraints.NORTH;
		toolbarWidth += 35;
		
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx++;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(new JLabel(), c);
		
		c.gridx += 2;
		add(new JLabel(), c);
		c.weighty = 0;
		c.weightx = 0;
		c.fill = GridBagConstraints.NONE;
		c.gridheight = 1;
		
		c.insets = new Insets(5, 5, 0, 5);
		c.gridy = 0;
		c.gridx = 1;
		c.gridwidth = 3;
		add(createBoardSizePanel(), c);
		int boardSizePanelHeight = 25;
		headerHeight += (boardSizePanelHeight + c.insets.top + c.insets.bottom);
				
		c.gridy++;
		c.insets.top = 0;
		c.insets.bottom = 5;
		headerHeight += (15 + c.insets.top + c.insets.bottom);
		add(toolHelpLabel, c);
		c.gridwidth = 1;
		
		c.gridx++;
		c.gridy++;
		c.insets.top = 0;
		c.insets.bottom = 5;
		c.gridheight = 2;
		toolbarWidth += (c.insets.left + c.insets.right);
		headerHeight += (c.insets.top + c.insets.bottom);
		add(boardDisplayPanel, c);
		c.gridheight = 1;
		
		c.gridx = 0;
		c.gridy++;
		c.insets.top = 25;
		toolbarHelperPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		toolbarHelperPanel.setVisible(false);
		add(toolbarHelperPanel, c);
	}
	
	/**
	 * Creates and returns the editing toolbar.
	 * @return the editing toolbar
	 */
	private JPanel createToolbarPanel() {
		JPanel toolbarPanel = new JPanel(new GridLayout(12, 1, 0, 0));
		toolbarPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
				
		CustomButton roomButton = createToolbarButton(EditMode.ROOM, "     Room     ");
		toolbarPanel.add(roomButton);
		
		toolbarPanel.add(createToolbarButton(EditMode.DOOR, "     Room Door     "));
		
		toolbarPanel.add(createToolbarButton(EditMode.REMOVE_TILE, "     Remove Tile     "));
		
		toolbarPanel.add(createToolbarButton(EditMode.PASSAGE, "     Secret Passage     "));
		
		toolbarPanel.add(createToolbarButton(EditMode.PLAYER_START, "     Player Start     "));
		
		JLabel spacingLabel = new JLabel();
		spacingLabel.setBackground(Color.GRAY);
		spacingLabel.setOpaque(true);
		toolbarPanel.add(spacingLabel);					

		undoButton = new CustomButton(ImageHelper.getUndo()) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				undoLastChange();
			}
		};
		undoButton.setToolTipText("     Undo (Ctrl+Z)     ");
		undoButton.setPreferredSize(new Dimension(25, 25));
		undoButton.setFocusable(false);
		toolbarButtons.add(undoButton);
		toolbarPanel.add(undoButton);
		undoButton.setEnabled(false);
		
		redoButton = new CustomButton(ImageHelper.getRedo()) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				redoLastUndoneChange();
			}
		};
		redoButton.setToolTipText("     Redo (Ctrl+Y)     ");
		redoButton.setPreferredSize(new Dimension(25, 25));
		redoButton.setFocusable(false);
		toolbarButtons.add(redoButton);
		toolbarPanel.add(redoButton);
		redoButton.setEnabled(false);
		
		saveButton = new CustomButton(ImageHelper.getDisk()) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				saveChanges();
			}
		};
		saveButton.setToolTipText("     Save Changes (Ctrl+S)     ");
		saveButton.setPreferredSize(new Dimension(25, 25));
		saveButton.setFocusable(false);
		toolbarButtons.add(saveButton);
		toolbarPanel.add(saveButton);
		saveButton.setEnabled(false);
		
		CustomButton validateButton = new CustomButton(ImageHelper.getCheck()) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (board.validate(rooms, suspects))
					Messenger.display("This is a completed, playable Clue Board.", "Clue Board Validation");
			}
		};
		validateButton.setToolTipText("     Validate Playable Board     ");
		validateButton.setPreferredSize(new Dimension(25, 25));
		validateButton.setFocusable(false);
		toolbarButtons.add(validateButton);
		toolbarPanel.add(validateButton);
		
		CustomButton previewButton = new CustomButton(ImageHelper.getEye()) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				previewBoard();
			}
		};
		previewButton.setToolTipText("     Preview Board     ");
		previewButton.setPreferredSize(new Dimension(25, 25));
		previewButton.setFocusable(false);
		toolbarButtons.add(previewButton);
		toolbarPanel.add(previewButton);
		
		CustomButton helpButton = new CustomButton("?") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				(new HelpDialog(gameDesignerDisplay)).setVisible(true);
			}
		};
		helpButton.setToolTipText("     Display Help     ");
		helpButton.setPreferredSize(new Dimension(25, 25));
		helpButton.setFocusable(false);
		toolbarButtons.add(helpButton);
		toolbarPanel.add(helpButton);
		
		roomButton.buttonClicked();
		
		return toolbarPanel;
	}
	
	/**
	 * Reloads/refreshes the EditMode toolbar help information.
	 */
	private void reloadToolbarHelps() {
		if (selectedRoom != null) {
			selectedRoom.setColor(null);
			for (DisplayTile roomTile : board.getRoomTiles(selectedRoom))
				roomTile.setRoom(selectedRoom);
		}
		
		if (selectedEditMode != null)
			setToolHelpText(selectedEditMode.getInstructions(), false);
		
		if (selectedEditMode == EditMode.ROOM) {
			int numRooms = rooms.size();
			if (numRooms == 0)
				return;
			
			toolbarHelperPanel.removeAll();
			toolbarHelperPanel.setLayout(new GridLayout(numRooms, 1, 0, 0));
				
			final CustomButton[] toolbarHelperButtons = new CustomButton[numRooms];
			for (int roomIdx = 0; roomIdx < numRooms; roomIdx++) {
				final Room room = rooms.get(roomIdx);
				CustomButton toolbarHelperButton = new CustomButton(Integer.toString(roomIdx + 1)) {
					private static final long serialVersionUID = 1L;
					public void buttonClicked() {
						for (int buttonIdx = 0; buttonIdx < toolbarHelperButtons.length; buttonIdx++) {
							CustomButton button = toolbarHelperButtons[buttonIdx];
							if (button == this) {
								button.setBorder(null);
								button.setBackground(Color.YELLOW);
								
								if (selectedRoom != null) {
									selectedRoom.setColor(null);
									for (DisplayTile roomTile : board.getRoomTiles(selectedRoom))
										roomTile.setRoom(selectedRoom);
								}
								
								selectedRoom = rooms.get(buttonIdx);
								selectedRoom.setColor(Color.GREEN);
								for (DisplayTile roomTile : board.getRoomTiles(selectedRoom))
									roomTile.setRoom(selectedRoom);
							}
							else {
								button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
								button.setBackground(null);
							}
						}
					}
				};
				toolbarHelperButton.setToolTipText("     " + room.getName() + "     ");
				toolbarHelperButton.setPreferredSize(new Dimension(25, 25));
				toolbarHelperButton.setFocusable(false);
				toolbarHelperButtons[roomIdx] = toolbarHelperButton;
				toolbarHelperPanel.add(toolbarHelperButton);
			}
			
			int roomIndex = rooms.indexOf(selectedRoom);
			if (roomIndex == -1) roomIndex = 0;
			toolbarHelperButtons[roomIndex].buttonClicked();
			
			toolbarHelperPanel.setVisible(true);
		}
		else if (selectedEditMode == EditMode.DOOR)
			toolbarHelperPanel.setVisible(false);
		else if (selectedEditMode == EditMode.REMOVE_TILE)
			toolbarHelperPanel.setVisible(false);
		else if (selectedEditMode == EditMode.PASSAGE)
			toolbarHelperPanel.setVisible(false);
		else if (selectedEditMode == EditMode.PLAYER_START) {
			int numSuspects = suspects.size();
			if (numSuspects == 0)
				return;
			
			toolbarHelperPanel.removeAll();
			toolbarHelperPanel.setLayout(new GridLayout(numSuspects, 1, 0, 0));
				
			final CustomButton[] toolbarHelperButtons = new CustomButton[numSuspects];
			for (int suspectIdx = 0; suspectIdx < numSuspects; suspectIdx++) {
				CustomButton toolbarHelperButton = new CustomButton() {
					private static final long serialVersionUID = 1L;
					public void buttonClicked() {
						for (int buttonIdx = 0; buttonIdx < toolbarHelperButtons.length; buttonIdx++) {
							CustomButton button = toolbarHelperButtons[buttonIdx];
							if (button == this) {
								button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
								selectedSuspect = suspects.get(buttonIdx);
							}
							else
								button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
						}
					}
				};
				toolbarHelperButton.setBackground(suspects.get(suspectIdx).getColor());
				toolbarHelperButton.setToolTipText("     " + suspects.get(suspectIdx).getName() + "     ");
				toolbarHelperButton.setPreferredSize(new Dimension(25, 25));
				toolbarHelperButton.setFocusable(false);
				toolbarHelperButtons[suspectIdx] = toolbarHelperButton;
				toolbarHelperPanel.add(toolbarHelperButton);
			}
			
			int suspectIndex = suspects.indexOf(selectedSuspect);
			if (suspectIndex == -1)	suspectIndex = 0;
			toolbarHelperButtons[suspectIndex].buttonClicked();
			
			toolbarHelperPanel.setVisible(true);
		}
		else
			toolbarHelperPanel.setVisible(false);
	}
	
	/**
	 * Creates and returns a toolbar button.
	 * @param editMode Edit Mode for this button
	 * @param toolTipText tool tip for this button
	 * @return a created toolbar button
	 */
	private CustomButton createToolbarButton(final EditMode editMode, String toolTipText) {
		CustomButton toolbarButton = new CustomButton(ImageHelper.getIcon(editMode.getImageType(), ImageHelper.ImageSize.MEDIUM)) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				selectButton(this);
				selectedEditMode = editMode;
				reloadToolbarHelps();
				
				boardDisplayPanel.setCursor(ImageHelper.getCursor(editMode.getImageType(), cursorSize));
			}
		};
		toolbarButton.setName(editMode.toString() + "_BUTTON");
		toolbarButton.setToolTipText(toolTipText);
		toolbarButton.setPreferredSize(new Dimension(25, 25));
		toolbarButton.setFocusable(false);
		toolbarButtons.add(toolbarButton);
		return toolbarButton;
	}
	
	/**
	 * Creates and returns a panel for changing the board tile size.
	 * @return a created board size panel
	 */
	private JPanel createBoardSizePanel() {
		JPanel boardSizePanel = new JPanel();
		boardSizePanel.add(new JLabel("Board Size In Tiles (height X width):"));
		
		boardHeightTextField = new JTextField("" + board.getHeight());
		inputHeight = board.getHeight();
		boardHeightTextField.setMinimumSize(new Dimension(15, 5));
		boardHeightTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				try {
					inputHeight = Integer.parseInt(boardHeightTextField.getText());
					if (inputHeight < Board.MIN_HEIGHT) {
						inputHeight = Board.MIN_HEIGHT;
						boardHeightTextField.setText("" + inputHeight);
					}
					else if (inputHeight > Board.MAX_HEIGHT) {
						inputHeight = Board.MAX_HEIGHT;
						boardHeightTextField.setText("" + inputHeight);
					}
				}
				catch (Exception ex) {
					boardHeightTextField.setText("" + inputHeight);
				}
				if (inputHeight < 10)
					boardHeightTextField.setText("0" + inputHeight);
			}			
		});
		boardSizePanel.add(boardHeightTextField);
		
		boardSizePanel.add(new JLabel("X"));	
		
		boardWidthTextField = new JTextField("" + board.getWidth());
		inputWidth = board.getWidth();
		boardWidthTextField.setMinimumSize(new Dimension(15, 5));
		boardWidthTextField.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent arg0) {}
			public void focusLost(FocusEvent arg0) {
				try {
					inputWidth = Integer.parseInt(boardWidthTextField.getText());
					if (inputWidth < Board.MIN_WIDTH) {
						inputWidth = Board.MIN_WIDTH;
						boardWidthTextField.setText("" + inputWidth);
					}
					else if (inputWidth > Board.MAX_WIDTH) {
						inputWidth = Board.MAX_WIDTH;
						boardWidthTextField.setText("" + inputWidth);
					}
				}
				catch (Exception ex) {
					boardWidthTextField.setText("" + inputWidth);
				}
				if (inputWidth < 10)
					boardWidthTextField.setText("0" + inputWidth);
			}			
		});
		boardSizePanel.add(boardWidthTextField);
		
		CustomButton resizeButton = new CustomButton("Resize") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				resizeBoard();
			}			
		};
		boardSizePanel.add(resizeButton);
		
		return boardSizePanel;
	}
	
	/**
	 * Creates a mouse listener to handle each Edit Mode.
	 */
	private void createEditModeMouseListener() {
		editModeMouseListener = new MouseListener() {
			int pressedButton = -1;
			Change change = null;
			
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {
				if (change != null && e.getSource() instanceof DisplayTile) {
					change.addChangedTile((DisplayTile)e.getSource());
				}
			}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				if (!(e.getSource() instanceof DisplayTile) || pressedButton != -1)
					return;
				
				if (e.getButton() == MouseEvent.BUTTON1) {
					switch (selectedEditMode) {
						case ROOM :
							change = new AddRoomChange(board, selectedRoom);
							break;
						case DOOR :
							change = new AddDoorChange(board);
							break;
						case REMOVE_TILE :
							change = new RemoveTileChange();
							break;
						case PASSAGE :
							change = new AddPassageChange(board);
							break;
						case PLAYER_START :
							change = new AddPlayerStartChange(board, selectedSuspect);
							break;
						default :
							return;
					}
					change.addChangedTile((DisplayTile)e.getSource());
				}
				else if (e.getButton() == MouseEvent.BUTTON3) {
					switch (selectedEditMode) {
						case ROOM :
							change = new RemoveRoomChange(board, selectedRoom);
							break;
						case DOOR :
							change = new RemoveDoorChange(board);
							break;
						case REMOVE_TILE :
							change = new UnremoveTileChange();
							break;
						case PASSAGE :
							change = new RemovePassageChange(board);
							break;
						case PLAYER_START :
							change = new RemovePlayerStartChange();
							break;
						default :
							return;
					}
					change.addChangedTile((DisplayTile)e.getSource());
				}
				pressedButton = e.getButton();
				changeInProgress = true;
			}
			public void mouseReleased(MouseEvent e) {
				if (change == null || e.getButton() != pressedButton)
					return;
					
				String errorMessage = change.applyChange();
				if (errorMessage == null) {
					undoStack.push(change);
					undoButton.setEnabled(true);
					checkSaveState();
					redoStack.clear();
					redoButton.setEnabled(false);
					setToolHelpText(selectedEditMode.getInstructions(), false);
				}
				else if (!errorMessage.trim().equals(""))
					setToolHelpText(errorMessage, true);
					
				change = null;
				pressedButton = -1;
				changeInProgress = false;
			}		
		};
	}

	/**
	 * Handles the resizing of the board and the change history (undo/redo) that goes with it.
	 */
	private void resizeBoard() {
		ResizeBoardChange change = new ResizeBoardChange(this, board, inputHeight, inputWidth);
		change.applyChange();
		undoStack.push(change);
		undoButton.setEnabled(true);
		checkSaveState();
		redoStack.clear();
		redoButton.setEnabled(false);
	}
	
	/**
	 * Handles the resizing of the board without worrying about change history (undo/redo).
	 * @param newHeight new board tile height
	 * @param newWidth new board tile width
	 */
	public void resizeBoardWithoutChangeTracking(int newHeight, int newWidth) {
		boardHeightTextField.setText(Integer.toString(newHeight));
		boardWidthTextField.setText(Integer.toString(newWidth));
		inputHeight = newHeight;
		inputWidth = newWidth;
		
		boardDisplayPanel.removeAll();
		boardDisplayPanel.setLayout(new GridLayout(newHeight, newWidth));
		
		int totalHeight = getSize().height - headerHeight;
		int totalWidth = getSize().width - toolbarWidth;
		int maxTileHeight = totalHeight / newHeight;
		int maxTileWidth = totalWidth / newWidth;
		int borderSize = 2;
		int tileSize = Math.min(maxTileHeight, maxTileWidth) - borderSize;
		if (tileSize < 10)
			tileSize = 10;
		
		if (tileSize < 20)
			cursorSize = ImageHelper.ImageSize.SMALL;
		else if (tileSize < 36)
			cursorSize = ImageHelper.ImageSize.MEDIUM;
		else
			cursorSize = ImageHelper.ImageSize.LARGE;		
		board.setSize(newHeight, newWidth);
		for (DisplayTile tile : board.getTiles()) {
			tile.removeMouseListener(editModeMouseListener);
			tile.addMouseListener(editModeMouseListener);
			tile.setPreferredSize(new Dimension(tileSize, tileSize));
			boardDisplayPanel.add(tile);
		}
	
		for (CustomButton toolbarButton : toolbarButtons) {
			String toolbarName = toolbarButton.getName();
			if (toolbarName != null && toolbarButton.getName().startsWith(selectedEditMode.toString()))
				toolbarButton.buttonClicked();
		}
		
		revalidate();
		repaint();
	}
	
	/**
	 * Handles the selection of a toolbar button.
	 * @param selectedButton button to select
	 */
	private void selectButton(CustomButton selectedButton) {
		for (CustomButton button : toolbarButtons) {
			if (button == selectedButton) {
				button.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
			}
			else {
				button.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			}
		}
	}
	
	/**
	 * Sets the tool help display text.
	 * @param text text to display
	 * @param isError whether or not the text is an error message
	 */
	private void setToolHelpText(String text, boolean isError) {
		if (isError)
			toolHelpLabel.setForeground(Color.RED);
		else
			toolHelpLabel.setForeground(Color.BLUE);
				
		toolHelpLabel.setText(text);
	}
	
	/**
	 * Undoes the last change that was made.
	 */
	private void undoLastChange() {
		if (undoStack.isEmpty() || changeInProgress)
			return;
		
		Change change = undoStack.pop();
		change.undoChange();
		redoStack.push(change);
		redoButton.setEnabled(true);
		if (undoStack.isEmpty())
			undoButton.setEnabled(false);
		checkSaveState();
	}
	
	/**
	 * Redoes the last change that was undone.
	 */
	private void redoLastUndoneChange() {
		if (redoStack.isEmpty() || changeInProgress)
			return;
		
		Change change = redoStack.pop();
		change.redoChange();
		undoStack.push(change);
		undoButton.setEnabled(true);
		checkSaveState();
		if (redoStack.isEmpty())
			redoButton.setEnabled(false);
	}
	
	/**
	 * Returns the current list of suspects for this created game.
	 * @return the current list of suspects for this created game
	 */
	public List<Suspect> getSuspects() {
		return suspects;
	}
	
	/**
	 * Sets the list of suspects for this created game.
	 * @param newSuspects list of suspects to set
	 */
	public void setSuspects(List<Suspect> newSuspects) {
		for (int suspectIdx = 0; suspectIdx < suspects.size(); suspectIdx++) {
			Suspect suspect = suspects.get(suspectIdx);
			DisplayTile suspectTile = board.getSuspectTile(suspect);
			
			if (newSuspects.size() <= suspectIdx) {
				Iterator<Change> undoIter = undoStack.iterator();
				while(undoIter.hasNext()) {
					Change change = undoIter.next();
					if (change instanceof AddPlayerStartChange && ((AddPlayerStartChange)change).getSuspect() == suspect) {
						if (change == lastChangeSaved) {
							int changeIndex = undoStack.indexOf(change);
							lastChangeSaved = (changeIndex <= 0) ? null : undoStack.get(changeIndex - 1);
						}
						undoIter.remove();
					}
					else if (change instanceof RemovePlayerStartChange && ((RemovePlayerStartChange)change).getSuspect() == suspect) {
						if (change == lastChangeSaved) {
							int changeIndex = undoStack.indexOf(change);
							lastChangeSaved = (changeIndex <= 0) ? null : undoStack.get(changeIndex - 1);
						}
						undoIter.remove();
					}
				}
				if (undoStack.isEmpty())
					undoButton.setEnabled(false);
				checkSaveState();
				
				Iterator<Change> redoIter = redoStack.iterator();
				while(redoIter.hasNext()) {
					Change change = redoIter.next();
					if (change instanceof AddPlayerStartChange && ((AddPlayerStartChange)change).getSuspect() == suspect)
						redoIter.remove();
					else if (change instanceof RemovePlayerStartChange && ((RemovePlayerStartChange)change).getSuspect() == suspect)
						redoIter.remove();
				}
				if (redoStack.isEmpty())
					redoButton.setEnabled(false);

				if (suspectTile != null)
					suspectTile.removeSuspect(suspect);
			}
			else if (suspectTile != null)
				suspectTile.addSuspect(suspect, suspectTile.getSuspectDirection());
		}
		
		this.suspects = newSuspects;
		reloadToolbarHelps();
	}
	
	/**
	 * Returns the current list of rooms for this created game.
	 * @return the current list of rooms for this created game
	 */
	public List<Room> getRooms() {
		return rooms;
	}
	
	/**
	 * Sets the list of rooms for this created game.
	 * @param newRooms list of rooms to set
	 */
	public void setRooms(List<Room> newRooms) {		
		for (int roomIdx = newRooms.size() - 1; roomIdx < rooms.size() - 1; roomIdx++) {
			Room room = rooms.get(roomIdx);
			Iterator<Change> undoIter = undoStack.iterator();
			while(undoIter.hasNext()) {
				Change change = undoIter.next();
				if (change instanceof AddRoomChange && ((AddRoomChange)change).getRoom() == room) {
					if (change == lastChangeSaved) {
						int changeIndex = undoStack.indexOf(change);
						lastChangeSaved = (changeIndex <= 0) ? null : undoStack.get(changeIndex - 1);
					}
					undoIter.remove();
				}
				else if (change instanceof RemoveRoomChange && ((RemoveRoomChange)change).getRoom() == room) {
					if (change == lastChangeSaved) {
						int changeIndex = undoStack.indexOf(change);
						lastChangeSaved = (changeIndex <= 0) ? null : undoStack.get(changeIndex - 1);
					}
					undoIter.remove();
				}
			}
			if (undoStack.isEmpty())
				undoButton.setEnabled(false);
			checkSaveState();
			
			Iterator<Change> redoIter = redoStack.iterator();
			while(redoIter.hasNext()) {
				Change change = redoIter.next();
				if (change instanceof AddRoomChange && ((AddRoomChange)change).getRoom() == room)
					redoIter.remove();
				else if (change instanceof RemoveRoomChange && ((RemoveRoomChange)change).getRoom() == room)
					redoIter.remove();
			}
			if (redoStack.isEmpty())
				redoButton.setEnabled(false);	
			
			if (board.hasRoom(room))
				board.removeRoom(room);			
		}
		
		this.rooms = newRooms;
		reloadToolbarHelps();
	}
	
	/**
	 * Returns the current board for this created game.
	 * @return the current board for this created game
	 */
	public Board getBoard() {
		return board;
	}
	
	/**
	 * Returns whether or not there are any unsaved changes on this board.
	 * @return true if the board is different from when it was last saved; false otherwise
	 */
	public boolean unsavedChanges() {
		return saveButton.isEnabled();
	}
	
	/**
	 * Updates the save button to be enabled/disabled based on whether or not the board is different since the last save.
	 */
	private void checkSaveState() {
		if (undoStack.isEmpty())
			saveButton.setEnabled(lastChangeSaved != null);	
		else
			saveButton.setEnabled(undoStack.lastElement() != lastChangeSaved);
	}
	
	/**
	 * Saves the created game in its current state.
	 */
	private void saveChanges() {
		if (!saveButton.isEnabled() || changeInProgress)
			return;
		
		gameDesignerDisplay.saveChanges();		
		
		saveButton.setEnabled(false);
		lastChangeSaved = undoStack.isEmpty() ? null : undoStack.lastElement();
	}
	
	/**
	 * Displays a preview of how the board will look in a real game.
	 */
	public void previewBoard() {
		int previewHeight = 600;
		int previewWidth = 600;
		CustomDialog previewBoardDialog = new CustomDialog(gameDesignerDisplay, "CLUE Board Preview");
		previewBoardDialog.setSize(previewHeight, previewWidth);
		
		final Board previewBoard = new Board(board);
		DisplayBoardPanel displayBoardPanel = new DisplayBoardPanel(previewBoard, rooms, gameDesignerDisplay.displayRoomPicturesOnBoard(), 
				                                                    gameDesignerDisplay.getBackgroundImageFilename(), gameDesignerDisplay.getBackgroundColor());
		displayBoardPanel.resizeBoardDisplay(previewHeight - 25, previewWidth);
		previewBoardDialog.add(displayBoardPanel);
		previewBoardDialog.refresh();
		previewBoardDialog.setVisible(true);
	}
	
	/**
	 * Loads a board with generated changes from given data.
	 * @param boardToLoad CLUE board to load
	 * @param newRooms rooms to load
	 * @param newSuspects suspects to load
	 */
	public void loadBoard(Board boardToLoad, List<Room> newRooms, List<Suspect> newSuspects) {
		while (!undoStack.isEmpty())
			undoStack.pop().undoChange();
		redoStack.clear();
		
		setRooms(newRooms);
		setSuspects(newSuspects);
		
		LinkedList<String> errorMessages = new LinkedList<String>();
		applyLoadBoardChange(new ResizeBoardChange(this, board, boardToLoad.getHeight(), boardToLoad.getWidth()), errorMessages);
		
		for (Room room : newRooms) {
			if (!boardToLoad.hasRoom(room))
				continue;
			
			AddRoomChange addRoomChange = new AddRoomChange(board, room);
			for (DisplayTile tile : boardToLoad.getRoomTiles(room))
				addRoomChange.addChangedTile(board.getTile(boardToLoad.getTilePosition(tile)));
			applyLoadBoardChange(addRoomChange, errorMessages);
		}
		
		for (DisplayTile tile : boardToLoad.getTiles()) {
			for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
				if (tile.hasDoor(direction)) {
					DisplayTile adjacentTile = boardToLoad.getAdjacentTile(tile, direction);
					if (adjacentTile == null)
						continue;
					
					AddDoorChange addDoorChange = new AddDoorChange(board);
					addDoorChange.addChangedTile(board.getTile(boardToLoad.getTilePosition(tile)));
					addDoorChange.addChangedTile(board.getTile(boardToLoad.getTilePosition(adjacentTile)));
					applyLoadBoardChange(addDoorChange, errorMessages);
				}					
			}
			
			if (tile.isRemovedTile()) {
				RemoveTileChange removeTileChange = new RemoveTileChange();
				removeTileChange.addChangedTile(board.getTile(boardToLoad.getTilePosition(tile)));
				applyLoadBoardChange(removeTileChange, errorMessages);
			}
			else if (tile.isPassage()) {
				DisplayTile correspondingTile = board.getTile(boardToLoad.getTilePosition(tile));
				if (correspondingTile.isPassage())
					continue;
				
				AddPassageChange addPassageChange = new AddPassageChange(board);
				addPassageChange.addChangedTile(correspondingTile);
				addPassageChange.addChangedTile(board.getTile(boardToLoad.getTilePosition(tile.getPassageConnection())));
				applyLoadBoardChange(addPassageChange, errorMessages);
			}
			else if (tile.hasSuspect()) {
				DisplayTile changingTile = board.getTile(boardToLoad.getTilePosition(tile));
				do {
					AddPlayerStartChange addPlayerStartChange = new AddPlayerStartChange(board, tile.getSuspect());
					addPlayerStartChange.addChangedTile(changingTile);
					applyLoadBoardChange(addPlayerStartChange, errorMessages);
				} while (tile.getSuspectDirection() != null && changingTile.getSuspectDirection() != tile.getSuspectDirection());
			}
		}
		
		if (!errorMessages.isEmpty()) {
			String message = "<html>";
			message += "Some changes from the loaded Clue Board are invalid and were not applied:";
			for (String errorMessage : errorMessages)
				message += "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + errorMessage;
			message += "</html>";
			Messenger.warn(message, "Invalid Clue Board Values");
		}
		
		undoButton.setEnabled(true);
		redoButton.setEnabled(false);
		lastChangeSaved = undoStack.lastElement();
		saveButton.setEnabled(false);
	}
	
	/**
	 * Used when loading data to create a change history.
	 * @param change change to apply and add to the change history.
	 * @param errorMessages continued list of error messages built while loading data
	 */
	private void applyLoadBoardChange(Change change, LinkedList<String> errorMessages) {
		String errorMessage = change.applyChange();
		if (errorMessage == null)
			undoStack.push(change);
		else if (!errorMessage.trim().equals(""))
			errorMessages.add(errorMessage);
	}
}
