package gui;

import action.Action;
import action.*;
import model.*;
import socket.ParticipantSocket;

import javax.swing.*;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * GUI used by a player joining a hosted game.  Handles/directs all display for the participating player.
 */
public class ParticipantGUI extends ClueGUI {
	private static final long serialVersionUID = 1L;
	
	private ParticipantSocket socket;

	private int controlledSuspectId;
	private List<Card> playerCards;
	
	/**
	 * Creates a new Participant GUI.
	 */
	public ParticipantGUI() {
		socket = null;
		
		newGameMenuItem.setText("Join Game");
		newGameMenuItem.addActionListener(e -> joinGame());
		replacePlayerMenuItem.setVisible(false);
		optionsMenuItem.setVisible(false);
		
		joinGame();
	}
	
	/**
	 * Joins a new game.
	 */
	private void joinGame() {
		// If a game is already in process, prompt the user to see if they want to end it and begin anew
		if (socket != null && !socket.isCleanlyClosed()) {
			String message = "Would you like to drop the host and join a different game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		
		if (socket != null)
			socket.close();
		JoinGameDialog dialog = new JoinGameDialog(this, defaultIP);
		dialog.setVisible(true);
		
		socket = dialog.getParticipantSocket();
		if (socket == null)
			return;
		defaultIP = socket.getHost();
		
		Thread socketListenerThread = new Thread(() -> {
			while (true) {
				String actionMessage = socket.getActionMessage();
				if (actionMessage == null) {
					if (socket.isCleanlyClosed())
						break;

					String message = "Lost connection to host: would you like to reconnect?";
					int choice = JOptionPane.showConfirmDialog(ParticipantGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
					if (choice != JOptionPane.YES_OPTION) {
						socket.close();
						break;
					}
					while (true) {
						try {
							socket.reconnect();
							break;
						}
						catch (IOException ex) {
							message = "Unable to connect to host: would you like to try again?";
							choice = JOptionPane.showConfirmDialog(ParticipantGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
							if (choice != JOptionPane.YES_OPTION) {
								socket.close();
								isGameOver = true;
								return;
							}
						}
					}
					continue;
				}

				@SuppressWarnings("rawtypes")
				Action action = Action.parseAction(actionMessage);
				String[] replyMessages = action.performAction(ParticipantGUI.this);

				if (replyMessages != null)
					for (String replyMessage : replyMessages)
						socket.sendActionMessage(replyMessage);
			}

			isGameOver = true;
		});
		socketListenerThread.start();
	}
	
	@Override
	protected void exitProgram() {
		if (socket != null && !isGameOver) {
			int choice = JOptionPane.showConfirmDialog(this, "Disconnect from host and exit program?", "Quit", 
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		
		if (socket != null)
			socket.close();
		updateConfigFile();
				
		this.dispose();
		System.exit(0);
	}

	/**
	 * Ends the game and displays the results.
	 * @param playerName name of the player who won (or null if everyone lost)
	 * @param roomId id of the room where the crime was committed
	 * @param suspectId id of the suspect who committed the crime
	 * @param weaponId id of the weapon used to commit the crime
	 */
	public void endGame(String playerName, int roomId, int suspectId, int weaponId) {
		endGame(playerName, getRoom(roomId), getSuspect(suspectId), getWeapon(weaponId));
	}
	
	@Override
	public void informAllPlayers(String displayMessage) {
		socket.sendActionMessage(new ShareInfoAction().createMessage(displayMessage));
	}
	
	@Override
	protected void notifyOfSuspectMove(Suspect suspect, int responsiblePlayerId) {
		if (responsiblePlayerId != controlledSuspectId)
			return;
		
		Board.TilePosition newTile = board.getTilePosition(board.getSuspectTile(suspect));
		socket.sendActionMessage(new MoveSuspectToTileAction().createMessage(suspect.getId(), newTile.row, newTile.col, responsiblePlayerId));
	}
	
	@Override
	protected void displayOptions() {}
	
	@Override
	protected int getControlledSuspectId() {
		return controlledSuspectId;
	}
	
	@Override
	protected void makeSuggestion(int playerId, Room suggestedRoom, Suspect suggestedSuspect, Weapon suggestedWeapon) {
		socket.sendActionMessage(new MakeSuggestionAction().createMessage(playerId, suggestedRoom.getId(), suggestedSuspect.getId(), suggestedWeapon.getId()));
	}
	
	@Override
	protected void makeAccusation(int playerId, Room accusedRoom, Suspect accusedSuspect, Weapon accusedWeapon) {
		socket.sendActionMessage(new MakeAccusationAction().createMessage(playerId, accusedRoom.getId(), accusedSuspect.getId(), accusedWeapon.getId()));
	}
	
	/**
	 * Forces this player to disprove the current suggestion with a card if possible.  Sends the result back to the host.
	 * @param suggestingPlayerId id of the player who made the suggestion
	 * @param suggestedRoomId id of the suggested room
	 * @param suggestedSuspectId id of the suggested suspect
	 * @param suggestedWeaponId id of the suggested weapon
	 */
	public void disproveSuggestion(int suggestingPlayerId, int suggestedRoomId, int suggestedSuspectId, int suggestedWeaponId) {
		String suggestingPlayerName = getSuspect(suggestingPlayerId).getName();
		DisproveSuggestionDialog dialog = new DisproveSuggestionDialog(this, suggestingPlayerName, getRoom(suggestedRoomId), 
				                    								   getSuspect(suggestedSuspectId), getWeapon(suggestedWeaponId));
		dialog.setVisible(true);
		socket.sendActionMessage(new DisproveSuggestionAction().createMessage(suggestingPlayerId, controlledSuspectId, dialog.getDisprovingCard(),
																			  suggestedRoomId, suggestedSuspectId, suggestedWeaponId));		
	}
	
	/**
	 * Sets the suspect id of the suspect this player will control.
	 * @param suspectId the suspect id of the suspect this player will control
	 */
	public void setSuspectId(int suspectId) {
		controlledSuspectId = suspectId;
	}
	
	/**
	 * Sets the mapping of how many card each player has.
	 * @param playerCardCountMap map of player names to the number of cards they currently hold
	 */
	public void setPlayerCardCountMap(LinkedHashMap<String, Integer> playerCardCountMap) {
		this.playerCardCountMap = playerCardCountMap;
	}
	
	@Override
	public List<Card> getCards() {
		return playerCards;
	}
	
	/**
	 * Sets the players cards.
	 * @param cards cards to set
	 */
	public void setCards(List<Card> cards) {
		playerCards = cards;
		displayCardsDialog = new DisplayCardsDialog(this, playerCards);
		displayCardsDialog.setVisible(true);
	}
	
	/**
	 * If it is this player's turn, start his turn.
	 * @param playerId id of the player whose turn it is
	 */
	public void beginPlayerTurn(int playerId) {
		if (controlledSuspectId == playerId)
			startPlayerTurn();
	}
	
	/**
	 * Ends this player's turn and notifies the host.
	 */
	public void endTurn() {
		socket.sendActionMessage(new EndTurnAction().createMessage());
	}
}
