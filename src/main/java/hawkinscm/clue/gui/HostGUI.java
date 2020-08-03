package hawkinscm.clue.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import hawkinscm.clue.action.Action;
import hawkinscm.clue.action.AssignSuspectIdAction;
import hawkinscm.clue.action.ChangeOptionsAction;
import hawkinscm.clue.action.ChangePlayerTurnAction;
import hawkinscm.clue.action.DealCardsAction;
import hawkinscm.clue.action.DisplayCardAction;
import hawkinscm.clue.action.DisplayMessageAction;
import hawkinscm.clue.action.EnableEndTurnAction;
import hawkinscm.clue.action.GameOverAction;
import hawkinscm.clue.action.MoveSuspectToTileAction;
import hawkinscm.clue.action.NewGameAction;
import hawkinscm.clue.action.RequestDisproveSuggestionAction;
import hawkinscm.clue.ai.AIManager;

import hawkinscm.clue.socket.PlayerSocket;

import hawkinscm.clue.model.Board;
import hawkinscm.clue.model.Card;
import hawkinscm.clue.model.ClueGameData;
import hawkinscm.clue.model.GameFileIOHandler;
import hawkinscm.clue.model.Player;
import hawkinscm.clue.model.PlayerType;
import hawkinscm.clue.model.Randomizer;
import hawkinscm.clue.model.Room;
import hawkinscm.clue.model.Suspect;
import hawkinscm.clue.model.Weapon;

/**
 * GUI used by the player hosting the game. Handles/directs all display for the
 * host.
 */
public class HostGUI extends ClueGUI {
	private static final long serialVersionUID = 1L;

	private LinkedList<PlayerSocket> playerSockets;
	private LinkedList<Player> players;
	private Player currentPlayer;

	private Room caseFileRoom;
	private Suspect caseFileSuspect;
	private Weapon caseFileWeapon;

	private ClueGameData clueGameData;
	private Player controlledPlayer;
	
	private AIManager aiManager;
	
	/**
	 * Constructor for the Host GUI
	 */
	public HostGUI() {
		playerSockets = new LinkedList<>();
		currentPlayer = null;
		
		caseFileRoom = null;
		caseFileSuspect = null;
		caseFileWeapon = null;

		newGameMenuItem.addActionListener(e -> newGame());
		replacePlayerMenuItem.addActionListener(e -> replacePlayer(null));
		
		int todo; // replace player doesn't work and has been altered to not allow NETWORK players to join.  Think about fixing.
	}

	/**
	 * Starts a new game.
	 */
	private void newGame() {
		// If a game is already in process, prompt the user to see if they want
		// to end it and begin anew
		if (!isGameOver) {
			String message = "End current game?";
			int choice = JOptionPane.showConfirmDialog(this, message, "New Game", JOptionPane.YES_NO_CANCEL_OPTION);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}

		// Prompt the user for CLUE game
		JFileChooser chooser = new JFileChooser("ccgs");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Custom Clue Games", "ccg");
		chooser.setFileFilter(filter);
		ClueGameData gameData = null;
		while (gameData == null) {
			int returnVal = chooser.showOpenDialog(HostGUI.this);
		    if (returnVal != JFileChooser.APPROVE_OPTION)
		    	return;
		    gameData = GameFileIOHandler.readFromFile(chooser.getSelectedFile());
		    if (gameData != null && !gameData.getBoard().validate(gameData.getRooms(), gameData.getSuspects()))
		    	gameData = null;
		}

		for (PlayerSocket playerSocket : playerSockets)
			if (!playerSocket.isCleanlyClosed())
				playerSocket.close();
		
		
		NewGameDialog playerInput = new NewGameDialog(this, gameData.getSuspects());
		playerInput.setVisible(true);
		if (playerInput.getPlayers() == null)
			return;
		players = playerInput.getPlayers();
		playerSockets = playerInput.getPlayerSockets();
		
		clueGameData = gameData;
		aiManager = new AIManager(gameData, players, gameData.getRooms(), gameData.getSuspects(), gameData.getWeapons());
		for (Player player : players)
			if (player.isComputer())
				aiManager.initializeAIPlayer(player);
		
		boolean hasSuspects = false;
		List<DisplayTile> boardTiles = gameData.getBoard().getTiles();
		for (DisplayTile tile : boardTiles) {
			if (tile.hasSuspect()) {
				if (useDefinedSuspectLocations) {
					tile.addSuspect(tile.getSuspect(), tile.getSuspectDirection());
					hasSuspects = true;
				}
				else
					tile.removeSuspects();
			}
		}
		if (!hasSuspects) {
			DisplayTile.Direction[] directions = DisplayTile.Direction.values();
			for (Suspect suspect : gameData.getSuspects()) {
				DisplayTile suspectTile = boardTiles.get(Randomizer.getRandom(boardTiles.size()));
				while (suspectTile.isRemovedTile() || suspectTile.isPassage() || suspectTile.hasSuspect())
					suspectTile = boardTiles.get(Randomizer.getRandom(boardTiles.size()));
				suspectTile.addSuspect(suspect, directions[Randomizer.getRandom(directions.length)]);
			}
		}
		
		if (!playerSockets.isEmpty()) {
			String ccgXML = GameFileIOHandler.writeToString(gameData);
			for (PlayerSocket playerSocket : playerSockets) {
				startPlayerSocketListener(playerSocket);
				playerSocket.sendActionMessage(new AssignSuspectIdAction().createMessage(playerSocket.getPlayer().getId()));
				playerSocket.sendActionMessage(new NewGameAction().createMessage(ccgXML));
				playerSocket.sendActionMessage(new ChangeOptionsAction().createMessage(numDice));
			}
		}

		loadGame(gameData);
		createAndDealCards();
		notifyPlayerSockets(new DisplayMessageAction().createMessage(gameData.getStory()));
		addMessage(gameData.getStory());
		beginNextPlayerTurn();
	}
	
	/**
	 * Sets the player controlled by this host.
	 * @param player player to set
	 */
	public void setControlledPlayer(Player player) {
		controlledPlayer = player;
	}
	
	/**
	 * Ends the current player's turn
	 */
	public void endTurn() {
		beginNextPlayerTurn();
	}

	/**
	 * Replaces the given player with a new player type. 
	 * @param player player to change; if null, asks to user to select a player to change
	 * @return returns true if the player was replaced, false otherwise.
	 */
	private boolean replacePlayer(Player player) {
		ChangePlayerTypeDialog dialog;
		if (player != null)
			dialog = new ChangePlayerTypeDialog(HostGUI.this, player);
		else if (players.isEmpty()) {
			Messenger.display("There are no players yet to replace.", "Replace Player");
			return false;
		}
		else
			dialog = new ChangePlayerTypeDialog(HostGUI.this, players);

		dialog.setVisible(true);
		player = dialog.getChangedPlayer();
		if (player == null)
			return false;

		if (dialog.getOldPlayerType() == PlayerType.NETWORK) {
			Iterator<PlayerSocket> playerSocketIter = playerSockets.iterator();
			while (playerSocketIter.hasNext()) {
				PlayerSocket playerSocket = playerSocketIter.next();
				if (player == playerSocket.getPlayer()) {
					playerSocket.close();
					playerSocketIter.remove();
					break;
				}
			}
		} 
		else if (player.getPlayerType() == PlayerType.NETWORK){
			if (!connect(player)) {
				player.setPlayerType(dialog.getOldPlayerType());
				return false;
			}
		}
		
		if (player == currentPlayer) {
			if (player.getPlayerType() == PlayerType.ALIBI_ONLY) {
				beginNextPlayerTurn();
			}
			else if (player.isComputer()) {
				int todo; // handle AI
				beginNextPlayerTurn();
			}
		}
		
		String message = player.getName() + " is now a " + player.getPlayerType().toString() + " player.";
		addMessage(message);
		notifyPlayerSockets(new DisplayMessageAction().createMessage(message));
		return true;
	}
	
	/**
	 * Connects to the given player and sends him all of the current game data.
	 * @param player player to connect to.
	 * @return true if a user was successfully connected to; false otherwise
	 */
	private boolean connect(Player player) {
		PlayerSocket playerSocket = null;
		Iterator<PlayerSocket> playerSocketIter = playerSockets.iterator();
		while (playerSocketIter.hasNext()) {
			PlayerSocket currentPlayerSocket = playerSocketIter.next();
			if (currentPlayerSocket.getPlayer() == player) {
				playerSocket = currentPlayerSocket;
				playerSocketIter.remove();
				break;
			}
		}
		if (playerSocket == null)
			playerSocket = new PlayerSocket(players.indexOf(player));
		
		try {
			playerSocket.close();
			playerSocket.reconnect();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			Messenger.error("Timed Out - Unable to connect to participant",	"Replace Player");
			playerSocket.close();
			return false;
		}
		
		playerSocket.setPlayer(player);
		playerSockets.add(playerSocket);

		startPlayerSocketListener(playerSocket);
		String ccgXML = GameFileIOHandler.writeToString(clueGameData);
		playerSocket.sendActionMessage(new AssignSuspectIdAction().createMessage(playerSocket.getPlayer().getId()));
		playerSocket.sendActionMessage(new NewGameAction().createMessage(ccgXML));
		playerSocket.sendActionMessage(new DealCardsAction().createMessage(playerSocket.getPlayer().getCards(), playerCardCountMap));
		playerSocket.sendActionMessage(new ChangeOptionsAction().createMessage(numDice));
		return true;
	}

	/**
	 * Starts a listener on the given player main.java.hawkinscm.clue.socket.
	 * @param playerSocket the main.java.hawkinscm.clue.socket to start listening to
	 */
	private void startPlayerSocketListener(final PlayerSocket playerSocket) {
		Thread socketListenerThread = new Thread(() -> {
			while (true) {
				String actionMessage = playerSocket.getActionMessage();
				if (actionMessage == null) {
					if (isGameOver)
						playerSocket.close();

					if (playerSocket.isCleanlyClosed())
						break;

					while (true) {
						String message = "Lost connection to participant \"" + playerSocket.getPlayer().getName() + "\": would you like to reconnect?";
						int choice = JOptionPane.showConfirmDialog(HostGUI.this, message, "Network Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
					    if (choice == JOptionPane.YES_OPTION) {
						    if (connect(playerSocket.getPlayer()))
							    return;
						}
					    else if (replacePlayer(playerSocket.getPlayer()))
						    return;
					}
				}

				@SuppressWarnings("rawtypes")
				Action action = Action.parseAction(actionMessage);
				String[] replyMessages = action.performAction(HostGUI.this);

				if (replyMessages != null)
					for (String replyMessage : replyMessages)
						playerSocket.sendActionMessage(replyMessage);
			}
			if (!playerSocket.isCleanlyClosed()) {
				Messenger.error("Lost connection to network player '" + playerSocket.getPlayer().getName() + "'.", "Network Error");
				playerSocket.close();
			}
		});
		socketListenerThread.start();
	}

	@Override
	public void informAllPlayers(String displayMessage) {
		addMessage(displayMessage);
		notifyPlayerSockets(new DisplayMessageAction().createMessage(displayMessage));
	}
	
	@Override
	protected void notifyOfSuspectMove(Suspect suspect, int responsiblePlayerId) {
		Board.TilePosition newTile = board.getTilePosition(board.getSuspectTile(suspect));
		for (PlayerSocket socket : playerSockets)
			if (socket.getPlayer().getId() != responsiblePlayerId)
				socket.sendActionMessage(new MoveSuspectToTileAction().createMessage(suspect.getId(), newTile.row, newTile.col, responsiblePlayerId));
	}
	
	@Override
	protected void displayOptions() {
		OptionsDialog dialog = new OptionsDialog(this, numDice, useFirstInListPlayerStart, useDefinedSuspectLocations);
		dialog.setVisible(true);
		numDice = dialog.getNumberOfDice();
		notifyPlayerSockets(new ChangeOptionsAction().createMessage(numDice));
		useFirstInListPlayerStart = dialog.useFirstInListPlayerStart();
		useDefinedSuspectLocations = dialog.useDefinedSuspectLocation();
	}
	
	@Override
	protected int getControlledSuspectId() {
		return controlledPlayer.getId();
	}
	
	@Override
	public List<Card> getCards() {
		return controlledPlayer.getCards();
	}
	
	/**
	 * Called when a player wishes to make a suggestion.
	 * @param playerId id of the player making the suggestion
	 * @param suggestedRoomId id of the suggested room
	 * @param suggestedSuspectId id of the suggested suspect
	 * @param suggestedWeaponId id of the suggested weapon
	 */
	public void makeSuggestion(int playerId, int suggestedRoomId, int suggestedSuspectId, int suggestedWeaponId) {
		makeSuggestion(playerId, getRoom(suggestedRoomId), getSuspect(suggestedSuspectId), getWeapon(suggestedWeaponId));
	}
	
	@Override
	protected void makeSuggestion(int playerId, Room suggestedRoom, Suspect suggestedSuspect, Weapon suggestedWeapon) {
		Player suggestingPlayer = getPlayerById(playerId);
		
		String message = suggestingPlayer.getName() + " suggests it was " + suggestedSuspect.getName().toUpperCase() + " in the " +
		                 suggestedRoom.getName().toUpperCase() + " with the " + suggestedWeapon.getName().toUpperCase() + ".";
		addMessage(message);
		notifyPlayerSockets(new DisplayMessageAction().createMessage(message));
		
		requestDisproveSuggestion(playerId, suggestingPlayer, suggestedRoom, suggestedSuspect, suggestedWeapon);		
	}
	
	/**
	 * Requests the next player attempt to disprove the current suggestion.
	 * @param suggestingPlayerId the id of the player who made the suggestion
	 * @param lastPlayerId the id of the last player who attempted to disprove the suggestion
	 * @param suggestedRoomId id of the suggested room
	 * @param suggestedSuspectId id of the suggested suspect
	 * @param suggestedWeaponId id of the suggested weapon
	 */
	public void requestDisproveSuggestion(int suggestingPlayerId, int lastPlayerId, int suggestedRoomId, int suggestedSuspectId, int suggestedWeaponId) {
		requestDisproveSuggestion(suggestingPlayerId, getPlayerById(lastPlayerId), getRoom(suggestedRoomId), getSuspect(suggestedSuspectId), getWeapon(suggestedWeaponId));
	}
	
	/**
	 * Requests the the next player attempt to disprove the current suggestion.
	 * @param suggestingPlayerId the id of the player who made the suggestion
	 * @param lastPlayer the last player who attempted to disprove the suggestion
	 * @param suggestedRoom the suggested room
	 * @param suggestedSuspect the suggested suspect
	 * @param suggestedWeapon the suggested weapon
	 */
	private void requestDisproveSuggestion(int suggestingPlayerId, Player lastPlayer, Room suggestedRoom, Suspect suggestedSuspect, Weapon suggestedWeapon) {
		Player nextPlayer = getNextPlayer(lastPlayer);
		while (!nextPlayer.isCardHoldingPlayer())
			nextPlayer = getNextPlayer(nextPlayer);
		
		if (nextPlayer.getId() == suggestingPlayerId) {
			String message = "No one was able to disprove " + nextPlayer.getName() + "'s suggestion.";
			addMessage(message);
			notifyPlayerSockets(new DisplayMessageAction().createMessage(message));
			if (nextPlayer == controlledPlayer)
				enableEndTurn();
			else if (nextPlayer.getPlayerType() == PlayerType.NETWORK)
				getPlayerSocketById(suggestingPlayerId).sendActionMessage(new EnableEndTurnAction().createMessage());
		}
		else if (nextPlayer.isComputer() || nextPlayer.getPlayerType() == PlayerType.ALIBI_ONLY) {
			int todo; // handle AI
			List<Card> disprovingCards = new ArrayList<>();
			for (Card card : nextPlayer.getCards())
				if (card.matches(suggestedRoom) || card.matches(suggestedSuspect) || card.matches(suggestedWeapon))
					disprovingCards.add(card);
			Card disprovingCard = (disprovingCards.isEmpty()) ? null : disprovingCards.get(Randomizer.getRandom(disprovingCards.size()));
			if (!disproveSuggestion(suggestingPlayerId, nextPlayer, disprovingCard))
				requestDisproveSuggestion(suggestingPlayerId, nextPlayer, suggestedRoom, suggestedSuspect, suggestedWeapon);
		}
		else if (nextPlayer == controlledPlayer) {
			DisproveSuggestionDialog dialog = new DisproveSuggestionDialog(this, getPlayerById(suggestingPlayerId).getName(), suggestedRoom, suggestedSuspect, suggestedWeapon);
			dialog.setVisible(true);
			if (!disproveSuggestion(suggestingPlayerId, nextPlayer, dialog.getDisprovingCard()))
				requestDisproveSuggestion(suggestingPlayerId, nextPlayer, suggestedRoom, suggestedSuspect, suggestedWeapon);
		}
		else if (nextPlayer.getPlayerType() == PlayerType.NETWORK) {
			String actionMessage = new RequestDisproveSuggestionAction().createMessage(suggestingPlayerId, suggestedRoom.getId(), suggestedSuspect.getId(), suggestedWeapon.getId());
			getPlayerSocketById(nextPlayer.getId()).sendActionMessage(actionMessage);
		}
	}
	
	/**
	 * Handles the results of an attempt to disprove the current suggestion.
	 * @param suggestingPlayerId id of the player who made the suggestion
	 * @param answeringPlayerId id of the player who attempted to answer the suggestion
	 * @param disprovingCard card that disproves the suggestion or null if unable to disprove
	 * @return true if the answering player was able to disprove the suggestion with a card; false otherwise
	 */
	public boolean disproveSuggestion(int suggestingPlayerId, int answeringPlayerId, Card disprovingCard) {
		return disproveSuggestion(suggestingPlayerId, getPlayerById(answeringPlayerId), disprovingCard);
	}
	
	/**
	 * Handles the results of an attempt to disprove the current suggestion.
	 * @param suggestingPlayerId id of the player who made the suggestion
	 * @param answeringPlayer the player who attempted to answer the suggestion
	 * @param disprovingCard card that disproves the suggestion or null if unable to disprove
	 * @return true if the answering player was able to disprove the suggestion with a card; false otherwise
	 */
	private boolean disproveSuggestion(int suggestingPlayerId, Player answeringPlayer, Card disprovingCard) {
		Player suggestingPlayer = getPlayerById(suggestingPlayerId);
		aiManager.addSuggesionResult(suggestingPlayer, answeringPlayer, disprovingCard);
		
		if (disprovingCard == null) {
			String message = answeringPlayer + " was unable to disprove the suggestion.";
			addMessage(message);
			notifyPlayerSockets(new DisplayMessageAction().createMessage(message));
			return false;
		}
		else {
			String message = answeringPlayer.getName() + " can disprove your suggestion with this card.";
			if (suggestingPlayer == controlledPlayer) {
				new DisplayCardDialog(this, disprovingCard, message).setVisible(true);
				addMessage(answeringPlayer.getName() + " disproved your suggestion with the " + disprovingCard.toString() + ".");
				enableEndTurn();
			}
			else {
				if (answeringPlayer == controlledPlayer)
					addMessage("You disproved the suggestion with the " + disprovingCard.toString() + ".");
				else
					addMessage(answeringPlayer.getName() + " showed a card to disprove the suggestion.");
				
				if (suggestingPlayer.getPlayerType() == PlayerType.NETWORK) {
					PlayerSocket playerSocket = getPlayerSocketById(suggestingPlayerId);
					playerSocket.sendActionMessage(new DisplayCardAction().createMessage(disprovingCard, message));
					playerSocket.sendActionMessage(new EnableEndTurnAction().createMessage());
				}
			}
			
			for (PlayerSocket socket : playerSockets) {
				message = null;
				if (socket.getPlayer() == suggestingPlayer)
					message = answeringPlayer.getName() + " disproved your suggestion with the " + disprovingCard.toString() + ".";
				else if (socket.getPlayer() == answeringPlayer)
					message = "You disproved " + suggestingPlayer.getName() + "'s suggestion with the " + disprovingCard.toString() + ".";
				else
					message = answeringPlayer.getName() + " showed a card to disprove the suggestion.";
				socket.sendActionMessage(new DisplayMessageAction().createMessage(message));
			}
				
			return true;
		}
	}
	
	/**
	 * Called when a player wishes to make an accusation.
	 * @param playerId id of the player making the accusation
	 * @param accusedRoomId id of the accused room
	 * @param accusedSuspectId id of the accused suspect
	 * @param accusedWeaponId id of the accused weapon
	 */
	public void makeAccusation(int playerId, int accusedRoomId, int accusedSuspectId, int accusedWeaponId) {
		makeAccusation(playerId, getRoom(accusedRoomId), getSuspect(accusedSuspectId), getWeapon(accusedWeaponId));
	}
	
	@Override
	protected void makeAccusation(int playerId, Room accusedRoom, Suspect accusedSuspect, Weapon accusedWeapon) {
		if (accusedRoom == caseFileRoom && accusedSuspect == caseFileSuspect && accusedWeapon == caseFileWeapon) {
			String winnerName = getPlayerById(playerId).getName();
			notifyPlayerSockets(new GameOverAction().createMessage(winnerName, caseFileRoom.getId(), caseFileSuspect.getId(), caseFileWeapon.getId()));
			endGame(winnerName, caseFileRoom, caseFileSuspect, caseFileWeapon);
		}
		else {
			Player accusingPlayer = getPlayerById(playerId);
			String message = accusingPlayer.getName() + " wrongly accused " + accusedSuspect.getName() + " in the " + accusedRoom.getName() + 
			                 " with the " + accusedWeapon.getName() + ".\n" + 
			                 accusingPlayer.getName() + " will no longer make guesses or take turns except to disprove suggestions by showing a card.";
			addMessage(message);
			notifyPlayerSockets(new DisplayMessageAction().createMessage(message));
			accusingPlayer.failedAccuse();
			
			Suspect accusingSuspect = getSuspect(accusingPlayer.getId());
			if (board.getSuspectTile(accusingSuspect).getRoom() == null) {
				message = accusingPlayer.getName() + " will be moved to the " + accusedRoom.getName() + " so as to not block any player's path.";
				addMessage(message);
				notifyPlayerSockets(new DisplayMessageAction().createMessage(message));
				board.moveSuspectToRoom(accusingSuspect, accusedRoom);
			}
			
			boolean allPlayersFailed = true;
			for (Player player : players) {
				if (!player.isNoTurnPlayer())
					allPlayersFailed = false;
			}
			if (allPlayersFailed) {
				notifyPlayerSockets(new GameOverAction().createMessage(null, caseFileRoom.getId(), caseFileSuspect.getId(), caseFileWeapon.getId()));
				endGame(null, caseFileRoom, caseFileSuspect, caseFileWeapon);
			}
			else
				endTurn();
		}
	}
	
	/**
	 * Returns the player specified by the given id.
	 * @param playerId id of the player to return
	 * @return the player specified by the given id
	 */
	private Player getPlayerById(int playerId) {
		for (Player player : players)
			if (player.getId() == playerId)
				return player;
		
		return null;
	}
	
	/**
	 * Returns the player main.java.hawkinscm.clue.socket specified by the given player id.
	 * @param playerId id of the player main.java.hawkinscm.clue.socket's player to return
	 * @return the player main.java.hawkinscm.clue.socket specified by the given player id
	 */
	private PlayerSocket getPlayerSocketById(int playerId) {
		for (PlayerSocket socket : playerSockets)
			if (socket.getPlayer().getId() == playerId)
				return socket;
		
		return null;
	}
	
	/**
	 * Begins the next card-holding player's turn.
	 */
	private void beginNextPlayerTurn() {
		if (currentPlayer == null) {
			if (useFirstInListPlayerStart)
				currentPlayer = players.getFirst();
			else {
				do {
					currentPlayer = players.get(Randomizer.getRandom(players.size()));
				} while (currentPlayer.isNoTurnPlayer());
			}
		}
		else
			currentPlayer = getNextPlayer(currentPlayer);
		
		if (currentPlayer.isNoTurnPlayer()) {
			beginNextPlayerTurn();
			return;
		}
		
		if (currentPlayer.isComputer()) {
			int todo; // add AI
			beginNextPlayerTurn();
			return;
		}
		
		addMessage("");
		notifyPlayerSockets(new DisplayMessageAction().createMessage(""));
		String message = currentPlayer.getName() + "'s Turn.";
		addMessage(message);
		notifyPlayerSockets(new ChangePlayerTurnAction().createMessage(currentPlayer.getId(), message));
		
		if (currentPlayer == controlledPlayer) {
			startPlayerTurn();
		}
	}
	
	/**
	 * Returns the next player following the given player.
	 * @param player player before the player to get
	 * @return the next player following the given player
	 */
	private Player getNextPlayer(Player player) {
		int nextPlayerIndex = players.indexOf(player) + 1;
		if (nextPlayerIndex >= players.size())
			nextPlayerIndex = 0;
		return players.get(nextPlayerIndex);
	}
	
	/**
	 * Creates cards from the game's rooms, suspects, and weapons; sets the case file cards aside; 
	 * and deals the remaining cards evenly to the players.
	 */
	private void createAndDealCards() {
		List<Card> roomCards = new ArrayList<>(getRooms().size());
		for (Room room : getRooms())
			roomCards.add(new Card(room));
		caseFileRoom = roomCards.remove(Randomizer.getRandom(getRooms().size())).getRoom();
		
		List<Card> suspectCards = new ArrayList<>(getSuspects().size());
		for (Suspect suspect : getSuspects())
			suspectCards.add(new Card(suspect));
		caseFileSuspect = suspectCards.remove(Randomizer.getRandom(getSuspects().size())).getSuspect();
		
		List<Card> weaponCards = new ArrayList<>(getWeapons().size());
		for (Weapon weapon : getWeapons())
			weaponCards.add(new Card(weapon));
		caseFileWeapon = weaponCards.remove(Randomizer.getRandom(getWeapons().size())).getWeapon();
		
		List<Card> allCards = new ArrayList<>();
		allCards.addAll(roomCards);
		allCards.addAll(suspectCards);
		allCards.addAll(weaponCards);
		
		List<Player> cardHoldingPlayers = new LinkedList<>();
		for (Player player : players)
			if (player.isCardHoldingPlayer())
				cardHoldingPlayers.add(player);
		int cardsPerPlayer = allCards.size() / cardHoldingPlayers.size();
		for (Player player : cardHoldingPlayers) {
			List<Card> playerCards = new LinkedList<>();
			for (int count = 1; count <= cardsPerPlayer; count++)
				playerCards.add(allCards.remove(Randomizer.getRandom(allCards.size())));
			player.setCards(playerCards);
		}
		
		if (!allCards.isEmpty())
			new ExtraCardsDialog(this, players, allCards).setVisible(true);
		
		for (Player player : cardHoldingPlayers)
			playerCardCountMap.put(player.getName(), player.getCards().size());
		
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.sendActionMessage(new DealCardsAction().createMessage(playerSocket.getPlayer().getCards(), playerCardCountMap));
		displayCardsDialog = new DisplayCardsDialog(this, controlledPlayer.getCards());
		displayCardsDialog.setVisible(true);
	}

	@Override
	protected void exitProgram() {
		if (!isGameOver) {
			int choice = JOptionPane.showConfirmDialog(null, "End current game?", "Quit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}

		updateConfigFile();
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.close();

		this.dispose();
		System.exit(0);
	}

	/**
	 * Notifies all player sockets of a specified main.java.hawkinscm.clue.action.
	 * @param actionMessage main.java.hawkinscm.clue.action message to send to all player sockets
	 */
	private void notifyPlayerSockets(String actionMessage) {
		for (PlayerSocket playerSocket : playerSockets)
			playerSocket.sendActionMessage(actionMessage);
	}
}
