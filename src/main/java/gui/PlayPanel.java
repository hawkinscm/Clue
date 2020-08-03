package gui;

import gui.DisplayTile.Direction;
import model.Board;
import model.Randomizer;
import model.Room;
import model.Suspect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Panel that displays and handles a player's turn: (moving, making suggestions, making accusations, and ending the turn)
 */
public class PlayPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private ClueGUI clueGUI;
	private Board board;
	private Suspect suspect;
	
	private CustomButton rollButton;
	private CustomButton secretPassageButton;
	private CustomButton suggestionButton;
	private CustomButton accuseButton;
	private CustomButton endTurnButton;
	
	private JPanel dicePanel;
	
	private JPanel movePanel;
	private CustomButton upButton;
	private CustomButton downButton;
	private CustomButton leftButton;
	private CustomButton rightButton;
	private CustomButton undoButton;
	private CustomButton restartButton;
	private CustomButton exitRoomButton;
	private CustomButton endMoveButton;
	private JLabel remainingStepsLabel;
	
	private Room startingRoom;
	private StepHistory stepHistory;
	private int remainingSteps;
	
	/**
	 * Creates a new Play Panel.
	 * @param gui ClueGUI that owns this dialog
	 * @param clueBoard board of the current CLUE game
	 * @param controlledSuspect suspect controlled by this player
	 */
	public PlayPanel(ClueGUI gui, Board clueBoard, Suspect controlledSuspect) {
		clueGUI = gui;
		board = clueBoard;
		suspect = controlledSuspect;
		
		stepHistory = new StepHistory();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		
		c.gridx++;
		rollButton = new CustomButton("Roll") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				rollButton.setVisible(false);
				secretPassageButton.setVisible(false);
				suggestionButton.setVisible(false);
				roll();
			}
		};
		add(rollButton, c);
		
		c.gridx++;
		dicePanel = new JPanel();
		add(dicePanel, c);
		
		c.gridx++;
		initializeMovePanel();
		add(movePanel, c);
		
		initializeKeyBindings();
		
		c.gridx++;
		secretPassageButton = new CustomButton("Secret Passage") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				List<Room> linkedRooms = new ArrayList<>();
				for (DisplayTile tile : board.getRoomTiles(startingRoom)) {
					if (tile.isPassage() && tile.getPassageConnection().isRoomTile()) {
						Room linkedRoom = tile.getPassageConnection().getRoom();
						if (linkedRoom != startingRoom && !linkedRooms.contains(linkedRoom))
							linkedRooms.add(linkedRoom);
					}
				}
				
				if (linkedRooms.isEmpty())
					return;
				
				Room selectedRoom;
				if (linkedRooms.size() == 1) {
					selectedRoom = linkedRooms.get(0);
					board.moveSuspectToRoom(suspect, linkedRooms.get(0));
				}
				else {
					SelectRoomDialog dialog = new SelectRoomDialog(clueGUI, "Select a secret passage connected room to move to.", linkedRooms);						
					dialog.setVisible(true);
					selectedRoom = dialog.getSelectedRoom();
					if (selectedRoom == null)
						return;
					else
						board.moveSuspectToRoom(suspect, selectedRoom);
				}
				
				rollButton.setVisible(false);
				secretPassageButton.setVisible(false);
				suggestionButton.setVisible(true);
				clueGUI.informAllPlayers(suspect.getName() + " has taken a secret passage to the " + selectedRoom.getName() + ".");
			}
		};
		add(secretPassageButton, c);
		
		c.gridx++;
		suggestionButton = new CustomButton("Make Suggestion") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				endTurnButton.setEnabled(false);
				SuggestionDialog dialog = new SuggestionDialog(clueGUI, board.getSuspectTile(suspect).getRoom());
				dialog.setVisible(true);
				if (dialog.madeSuggestion()) {
					suggestionButton.setVisible(false);
					rollButton.setVisible(false);
					secretPassageButton.setVisible(false);
					endTurnButton.setVisible(true);
					endTurnButton.requestFocusInWindow();
				}
				else
					endTurnButton.setEnabled(true);
			}
		};
		add(suggestionButton, c);
		
		c.gridx++;
		accuseButton = new CustomButton("ACCUSE!") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				new AccuseDialog(clueGUI).setVisible(true);
				if (!PlayPanel.this.isVisible() && !stepHistory.isEmpty())
					stepHistory.clear();
			}
		};
		add(accuseButton, c);
		
		c.gridx++;
		endTurnButton = new CustomButton("End Turn") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				PlayPanel.this.setVisible(false);
				clueGUI.endTurn();
			}
		};
		add(endTurnButton, c);
	}
	
	/**
	 * Initializes the panel that allows a player to move (direction buttons, undo, redo, restart, number of moves left, exiting rooms, and ending the move).
	 */
	private void initializeMovePanel() {
		movePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(2, 2, 2, 2);
		
		undoButton = new CustomButton("Undo") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (stepHistory.isEmpty())
					return;
				
				DisplayTile currentTile = board.getSuspectTile(suspect);
				DisplayTile previousTile = stepHistory.undoStep();
				
				Direction direction = null;
				if (currentTile.getRoom() != null) {
					for (Direction doorDirection : Direction.values())
						if (previousTile.hasDoor(doorDirection))
							direction = doorDirection.getOpposite();
				}
				else
					direction = board.getDirection(previousTile, currentTile);
				
				board.moveSuspectToTile(suspect, direction, previousTile);
				
				if (previousTile.isRoomTile()) 
					exitRoomButton.setEnabled(true);
				evaluatePossibleSteps(previousTile);
				endMoveButton.setEnabled(false);
			}
		};
		undoButton.setToolTipText("Backspace Key");
		movePanel.add(undoButton, c);
		
		ImageIcon footstepArrow = ImageHelper.getIcon(ImageHelper.ImageType.FOOTSTEPS, ImageHelper.ImageSize.SMALL);
		
		c.gridx++;
		upButton = new CustomButton(footstepArrow) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (upButton.isEnabled() && upButton.isVisible())
					stepToTile(Direction.NORTH);					
			}
		};
		upButton.setToolTipText("Up Arrow Key");
		movePanel.add(upButton, c);
		
		c.gridx++;
		restartButton = new CustomButton("Restart") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (stepHistory.isEmpty())
					return;

				DisplayTile originalTile = stepHistory.clear();
				board.moveSuspectToTile(suspect, Direction.NORTH, originalTile);
				
				if (originalTile.isRoomTile())
					exitRoomButton.setEnabled(true);
				evaluatePossibleSteps(originalTile);
				endMoveButton.setEnabled(false);
			}
		};
		movePanel.add(restartButton, c);
		
		c.gridx = 0;
		c.gridy++;
		leftButton = new CustomButton(ImageHelper.rotateIcon(footstepArrow, 270)) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (leftButton.isEnabled() && leftButton.isVisible())
					stepToTile(Direction.WEST);
			}
		};
		leftButton.setToolTipText("Left Arrow Key");
		movePanel.add(leftButton, c);

		c.gridx++;
		remainingStepsLabel = new JLabel("");
		movePanel.add(remainingStepsLabel, c);

		c.gridx++;
		rightButton = new CustomButton(ImageHelper.rotateIcon(footstepArrow, 90)) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (rightButton.isEnabled() && rightButton.isVisible())
					stepToTile(Direction.EAST);
			}
		};
		rightButton.setToolTipText("Right Arrow Key");
		movePanel.add(rightButton, c);
		
		c.gridx = 0;
		c.gridy++;
		exitRoomButton = new CustomButton("Exit Room") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				ExitRoomDialog dialog = new ExitRoomDialog(clueGUI, board, startingRoom);
				dialog.setVisible(true);
				DisplayTile exitTile = dialog.getSelectedExitTile();
				if (exitTile == null)
					return;
				
				exitRoomButton.setEnabled(false);
				DisplayTile currentTile = board.getSuspectTile(suspect);
				Direction suspectDirection = Direction.NORTH;
				for (Direction direction : Direction.values()) {
					if (exitTile.hasDoor(direction) && board.getAdjacentTile(exitTile, direction).getRoom() == startingRoom) {
						suspectDirection = direction.getOpposite();
						break;
					}
				}
				if (exitTile.isRoomTile())
					exitTile = board.moveSuspectToRoom(suspect, exitTile.getRoom());
				else
					board.moveSuspectToTile(suspect, suspectDirection, exitTile);
				stepHistory.addStep(currentTile);
				
				evaluatePossibleSteps(exitTile);
				endMoveButton.requestFocusInWindow();
			}
		};
		movePanel.add(exitRoomButton, c);
				
		c.gridx++;
		downButton = new CustomButton(ImageHelper.rotateIcon(footstepArrow, 180)) {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				if (downButton.isEnabled() && downButton.isVisible())
					stepToTile(Direction.SOUTH);
			}
		};
		downButton.setToolTipText("Down Arrow Key");
		movePanel.add(downButton, c);
		
		c.gridx++;
		endMoveButton = new CustomButton("End Move") {
			private static final long serialVersionUID = 1L;
			public void buttonClicked() {
				stepHistory.clear();
				movePanel.setVisible(false);
				dicePanel.setVisible(false);
				if (board.getSuspectTile(suspect).isRoomTile())
					suggestionButton.setVisible(true);
				else {
					endTurnButton.setVisible(true);
					endTurnButton.requestFocusInWindow();
				}
			}
		};
		movePanel.add(endMoveButton, c);
	}
	
	/**
	 * Sets up key binding for the move panel buttons (arrow keys for the direction buttons and backspace for undo)
	 */
	private void initializeKeyBindings() {
		AbstractAction upAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if (upButton.isVisible() && upButton.isEnabled())
					upButton.buttonClicked();
			}
		};		
		getActionMap().put("UP_ACTION", upAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "UP_ACTION");
		
		AbstractAction downAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if (downButton.isVisible() && downButton.isEnabled())
					downButton.buttonClicked();
			}
		};		
		getActionMap().put("DOWN_ACTION", downAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DOWN_ACTION");
		
		AbstractAction leftAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if (leftButton.isVisible() && leftButton.isEnabled())
					leftButton.buttonClicked();
			}
		};		
		getActionMap().put("LEFT_ACTION", leftAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "LEFT_ACTION");
		
		AbstractAction rightAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if (rightButton.isVisible() && rightButton.isEnabled())
					rightButton.buttonClicked();
			}
		};		
		getActionMap().put("RIGHT_ACTION", rightAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "RIGHT_ACTION");
		
		AbstractAction undoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if (undoButton.isVisible() && undoButton.isEnabled())
					undoButton.buttonClicked();
			}
		};		
		getActionMap().put("UNDO_ACTION", undoAction);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "UNDO_ACTION");
	}
	
	/**
	 * Rolls the dice and allows the player to move.
	 */
	private void roll() {
		dicePanel.removeAll();
		
		int numDice = clueGUI.getNumDice();
		remainingSteps = 0;
		for (int die = 1; die <= numDice; die++) {
			int roll = Randomizer.getRandom(6) + 1;
			remainingSteps += roll;
			dicePanel.add(new JLabel(ImageHelper.getDie(roll)));
		}
		clueGUI.informAllPlayers(suspect.getName() + " rolled a " + remainingSteps + ".");
			
		dicePanel.setVisible(true);

		DisplayTile currentTile = board.getSuspectTile(suspect);
		exitRoomButton.setEnabled(currentTile.isRoomTile());
		evaluatePossibleSteps(currentTile);
		endMoveButton.setEnabled(false);
		remainingStepsLabel.setText(Integer.toString(remainingSteps));
		movePanel.setVisible(true);
	}
	
	/**
	 * Evaluates the directions that the player can currently move and enables/disables the directional buttons and end move button appropriately.
	 * @param currentTile the tile where the player is currently located
	 */
	private void evaluatePossibleSteps(DisplayTile currentTile) {
		boolean hasRemainingSteps = (!currentTile.isRoomTile() && remainingSteps > 0);
		upButton.setEnabled(hasRemainingSteps && canStepToTile(board.getAdjacentTile(currentTile, Direction.NORTH), Direction.SOUTH));
		downButton.setEnabled(hasRemainingSteps && canStepToTile(board.getAdjacentTile(currentTile, Direction.SOUTH), Direction.NORTH));
		leftButton.setEnabled(hasRemainingSteps && canStepToTile(board.getAdjacentTile(currentTile, Direction.WEST), Direction.EAST));
		rightButton.setEnabled(hasRemainingSteps && canStepToTile(board.getAdjacentTile(currentTile, Direction.EAST), Direction.WEST));
		
		if (!upButton.isEnabled() && !downButton.isEnabled() && !leftButton.isEnabled() && !rightButton.isEnabled())
			endMoveButton.setEnabled(true);
	}
	
	/**
	 * Returns whether or not a player can step to the specified tile from the given direction.
	 * @param tile tile to evaluate the move to
	 * @param fromDirection direction from which to evaluate the move
	 * @return true if the player can move to the specified tile from the given direction; false otherwise
	 */
	private boolean canStepToTile(DisplayTile tile, Direction fromDirection) {
		if (tile == null)
			return false;
		
		if (tile.isRoomTile()) {
			return tile.hasDoor(fromDirection) && tile.getRoom() != startingRoom;
		}
		
		if (tile.hasSuspect())
			return false;
		
		if (tile.isPassage() && tile.getPassageConnection().hasSuspect())
			return false;
		
		if (tile.isRemovedTile())
			return false;

		return !stepHistory.contains(tile);
	}
	
	/**
	 * Steps from the current tile to the tile in the given direction.
	 * @param direction direction to step/move
	 */
	private void stepToTile(Direction direction) {
		DisplayTile currentTile = board.getSuspectTile(suspect);
		DisplayTile newTile = board.getAdjacentTile(currentTile, direction);
		if (!canStepToTile(newTile, direction.getOpposite()))
			return;
		
		if (newTile.isPassage())
			newTile = newTile.getPassageConnection();
			
		if (newTile.isRoomTile())
			newTile = board.moveSuspectToRoom(suspect, newTile.getRoom());
		else
			board.moveSuspectToTile(suspect, direction, newTile);
		stepHistory.addStep(currentTile);
		
		evaluatePossibleSteps(newTile);
	}
	
	/**
	 * Displays this play panel with the appropriate buttons visible/enabled according to what the player is currently allowed to do.
	 * @param canUseSecretPassage whether or not the player is in a room with a secret passage he can use 
	 * @param canMakeSuggestion whether or not the player can make a suggestion without having to move to a new room
	 */
	public void display(boolean canUseSecretPassage, boolean canMakeSuggestion) {
		boolean isBlocked = true;
		DisplayTile currentTile = board.getSuspectTile(suspect);
		startingRoom = null;
		if (currentTile.isRoomTile()) {
			if (board.getFreeExitTiles(currentTile.getRoom()).size() > 0)
				isBlocked = false;
			startingRoom = currentTile.getRoom();
		}
		else {
			for (DisplayTile.Direction direction : DisplayTile.Direction.values())
				if (canStepToTile(board.getAdjacentTile(currentTile, direction), direction.getOpposite()))
					isBlocked = false;
		}
		if (isBlocked)
			clueGUI.informAllPlayers(suspect.getName() + " is blocked in and unable to roll the dice to move.");
		
		rollButton.setVisible(!isBlocked);
		secretPassageButton.setVisible(canUseSecretPassage);
		suggestionButton.setVisible(canMakeSuggestion && startingRoom != null);
		endTurnButton.setVisible(isBlocked && !canUseSecretPassage && !canMakeSuggestion);
		if (endTurnButton.isVisible())
			endTurnButton.requestFocusInWindow();
		
		dicePanel.setVisible(false);
		movePanel.setVisible(false);
		setVisible(true);
	}
	
	/**
	 * Enabled the end turn button.  This is triggered whenever this player's suggestion is proven wrong or if no one is able to prove it wrong. 
	 */
	public void enableEndTurn() {
		endTurnButton.setEnabled(true);
	}
	
	/**
	 * Used to keep a change history of steps/moves by the player in order to perform undo, redo, and restart operations.
	 */
	private class StepHistory {
		Stack<DisplayTile> stepStack;
		
		/**
		 * Create a new Step History
		 */
		public StepHistory() {
			stepStack = new Stack<>();
		}
		
		/**
		 * Adds the given tile as a previous tile to the step history
		 * @param tile previous tile to add
		 */
		public void addStep(DisplayTile tile) {
			remainingSteps--;
			remainingStepsLabel.setText(Integer.toString(remainingSteps));
			stepStack.add(tile);
			if (!tile.isRoomTile())
				tile.setBackground(Color.GRAY);
			undoButton.setEnabled(true);
			restartButton.setEnabled(true);
		}
		
		/**
		 * Undoes the last step.
		 * @return the previous tile that was in the step history
		 */
		public DisplayTile undoStep() {
			remainingSteps++;
			remainingStepsLabel.setText(Integer.toString(remainingSteps));
			DisplayTile tile = stepStack.pop();
			if (stepStack.isEmpty()) {
				undoButton.setEnabled(false);
				restartButton.setEnabled(false);
			}
			return tile;
		}
		
		/**
		 * Removes all tiles, clearing the step history
		 * @return the first tile in the step history (i.e. the tile where the player was before he started moving)
		 */
		public DisplayTile clear() {			
			remainingSteps += stepStack.size();
			remainingStepsLabel.setText(Integer.toString(remainingSteps));
			DisplayTile originalTile = stepStack.firstElement();
			
			for (DisplayTile tile : stepStack)
				if (!tile.isRoomTile())
					tile.setUnremoved();
			
			stepStack.clear();
			undoButton.setEnabled(false);
			restartButton.setEnabled(false);
			return originalTile;
		}
		
		/**
		 * Whether or not the step history is empty.
		 * @return true if the step history is empty (i.e. the player has not yet moved); false otherwise
		 */
		public boolean isEmpty() {
			return stepStack.isEmpty();
		}
		
		/**
		 * Whether or not the step history contains the given tile.
		 * @param tile tile to check
		 * @return true if the step history contains the given tile (i.e. the player stepped on that tile while moving); false otherwise
		 */
		public boolean contains(DisplayTile tile) {
			return stepStack.contains(tile);
		}
	}
}
