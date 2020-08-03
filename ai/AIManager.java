package ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gui.DisplayTile;
import gui.HostGUI;
import model.Board;
import model.Card;
import model.ClueGameData;
import model.Player;
import model.Randomizer;
import model.Room;
import model.Suspect;
import model.Weapon;

/**
 * Handler for AI.
 */
public class AIManager {
	private List<Player> players;
	private List<Room> rooms;
	private List<Suspect> suspects;
	private List<Weapon> weapons;
	
	private LinkedList<SuggestionResult> suggestionRecord;
	private ArrayList<AINotebook> playerNotebooks;
  private Map<Player, ClueSolver> solvers;


  /**
	 * Creates a new AI Manager.
   * @param clueGameData
   * @param players all card-holding players in the current game
   * @param rooms all rooms in the game
   * @param suspects all suspects in the game
   * @param weapons all weapons in the game
   */
	public AIManager(ClueGameData clueGameData, List<Player> players, List<Room> rooms, List<Suspect> suspects, List<Weapon> weapons) {
    this.solvers = new HashMap<Player, ClueSolver>();
		this.players = players;
		this.rooms = rooms;
		this.suspects = suspects;
		this.weapons = weapons;
		
		playerNotebooks = new ArrayList<AINotebook>();
		suggestionRecord = new LinkedList<SuggestionResult>();
	}
	
	// todo
	public void initializeAIPlayer(Player player) {
    List<AICard> cards = new ArrayList<AICard>(player.getCards().size());
    for (Card card : player.getCards()) {
      System.out.println(card.getName());
      cards.add(AICard.valueOf(card.getName()));
    }

    Point startingLocation = getStartingLocation(AICard.valueOf(ClueSolver.translatePerson(player.getName())));
    ClueSolver solver = new ClueSolver(startingLocation, cards);
    solvers.put(player, solver);
	}

  private Point getStartingLocation(AICard aiCard) {
    switch (aiCard) {
      case SCARLET:
        return new Point(16, 0);
      case MUSTARD:
        return new Point(23, 7);
      case WHITE:
        return new Point(14, 24);
      case GREEN:
        return new Point(9, 24);
      case PEACOCK:
        return new Point(0, 18);
      case PLUM:
        return new Point(0, 5);
    }
    return null;
  }

  /**
	 * Adds a single suggestion record.
	 * @param suggester the person who made a suggestion
	 * @param responder the person who answered the suggestion (will be null if no one could help)
	 * @param card card that was shown by the responder (will be null if responder is null)
	 */
	public void addSuggesionResult(Player suggester, Player responder, Card card) {
		suggestionRecord.add(new SuggestionResult(suggester, responder, card));
	}

  /**
   * Handles a turn for a computer player.
   */
  public void takeTurn(Player player, HostGUI hostGUI) {
    try {
      ClueSolver solver = solvers.get(player);
      DisplayTile currentTile = hostGUI.board.getSuspectTile(hostGUI.getControlledSuspect(player));
      boolean canUseSecretPassage = false;
      AICard currentPlayerRoom = null;
      if (currentTile.isRoomTile()) {
        currentPlayerRoom = AICard.valueOf(ClueSolver.translateRoom(currentTile.getRoom().getName()));
        for (DisplayTile tile : hostGUI.board.getRoomTiles(currentTile.getRoom())) {
          if (tile.isPassage() && tile.getPassageConnection().isRoomTile()) {
            canUseSecretPassage = true;
            break;
          }
        }
      }
      Board.TilePosition currentPosition = hostGUI.board.getTilePosition(currentTile);
      Point location = solver.getLocation();

      boolean controlledSuspectTransferred = false;
      if (currentPosition.col != location.x || currentPosition.row != location.y) {
        controlledSuspectTransferred = true;
        solver.positionChanged(currentPosition.col, currentPosition.row);
      }

      AICard suggestedRoom = null;
      if (currentPlayerRoom != null && controlledSuspectTransferred) {
        suggestedRoom = solver.chooseRoom(currentPlayerRoom);
      }

      if (suggestedRoom == null && canUseSecretPassage) {
        suggestedRoom = solver.chooseRoom(ClueSolver.getSecretRoom(currentPlayerRoom));
        if (suggestedRoom != null) {
          useSecretPassage(hostGUI, player);
        }
      }

      if (suggestedRoom == null) {
        //roll the dice and select the room to go to based on the roll
        int stepsAllowed = roll(hostGUI, player);

        List<AICard> roomsToMoveTo = new ArrayList<AICard>(AICard.getRooms());
        List<Plan> plans = null;
        if (currentPlayerRoom != null) {
          roomsToMoveTo.remove(currentPlayerRoom);
          plans = Planner.plan(null, currentPlayerRoom, roomsToMoveTo);
        }
        else {
          plans = Planner.plan(location, null, roomsToMoveTo);
        }

        List<Plan> completablePlans = new ArrayList<Plan>(roomsToMoveTo.size());
        for (Plan plan : plans) {
          System.out.println("Plan(" + plan.size() + ") to get to: " + plan.getDestination().name());
          if (stepsAllowed >= plan.size()) {
            completablePlans.add(plan);
          }
        }

        //choose which room of the ones that you can get to are located
        if (!completablePlans.isEmpty()) {
          suggestedRoom = solver.chooseRoom(getDestinations(completablePlans));
        }

        //if you can't get to a room or have no need to go to that room, travel toward a different room
        Plan planToFollow = null;
        if (suggestedRoom != null) {
          planToFollow = getPlan(suggestedRoom, plans);
        }
        else {
          Collections.sort(plans); //by closest rooms
          List<AICard> rooms = solver.chooseRoomDirection(getDestinations(plans));
          for (AICard room : rooms) {
            planToFollow = getPlan(room, plans);
          }
        }

        followPlan(hostGUI, player, stepsAllowed, planToFollow);

      }

      if (suggestedRoom != null) {
        //make suggestion
        AICard[] suggestion = solver.getSuggestion(suggestedRoom);
        hostGUI.makeSuggestion(player, suggestedRoom, suggestion[0], suggestion[1]);
        if (!solver.hasSeenCards()) {
          solver.showNoProofSuggestion(suggestedRoom, suggestion[0], suggestion[1]);
        }
      }

      AICard[] accusation = solver.getAccusation();
      if (accusation != null) {
        hostGUI.makeAccusation(player, accusation[0], accusation[1], accusation[2]);
      }
    }
    catch (NullPointerException e) {
      e.printStackTrace();
      System.out.println("No plan to follow because the gui is moving the person outside the box");
    }
    //end turn
  }

  /**
   * Steps from the current tile to the tile in the given direction.
   * @param direction direction to step/move
   */
  private void stepToTile(HostGUI hostGUI, Player player, DisplayTile.Direction direction) {
    Suspect suspect = hostGUI.getSuspect(player.getId());
    DisplayTile currentTile = hostGUI.board.getSuspectTile(suspect);
    DisplayTile newTile = hostGUI.board.getAdjacentTile(currentTile, direction);
    if (newTile.isPassage()) {
      newTile = newTile.getPassageConnection();
    }

    if (newTile.isRoomTile()) {
      hostGUI.board.moveSuspectToRoom(suspect, newTile.getRoom());
    }
    else {
      hostGUI.board.moveSuspectToTile(suspect, direction, newTile);
    }
    currentTile = hostGUI.board.getSuspectTile(suspect);
    Board.TilePosition position = hostGUI.board.getTilePosition(currentTile);
    solvers.get(player).positionChanged(position.col, position.row);
  }

  private void useSecretPassage(HostGUI hostGUI, Player player) {
    Suspect suspect = hostGUI.getSuspect(player.getId());
    DisplayTile currentTile = hostGUI.board.getSuspectTile(suspect);
    Room startingRoom = currentTile.getRoom();
    List<Room> linkedRooms = new ArrayList<Room>();
    for (DisplayTile tile : hostGUI.board.getRoomTiles(startingRoom)) {
      if (tile.isPassage() && tile.getPassageConnection().isRoomTile()) {
        Room linkedRoom = tile.getPassageConnection().getRoom();
        if (linkedRoom != startingRoom && !linkedRooms.contains(linkedRoom))
          linkedRooms.add(linkedRoom);
      }
    }
    Room selectedRoom = linkedRooms.get(Randomizer.getRandom(linkedRooms.size()));
    hostGUI.board.moveSuspectToRoom(suspect, selectedRoom);
    hostGUI.informAllPlayers(suspect.getName() + " has taken a secret passage to the " + selectedRoom.getName() + ".");
    currentTile = hostGUI.board.getSuspectTile(suspect);
    Board.TilePosition position = hostGUI.board.getTilePosition(currentTile);
    solvers.get(player).positionChanged(position.col, position.row);
  }

  private void followPlan(HostGUI hostGUI, Player player, int stepsAllowed, Plan plan) {
    System.out.println("following plan to " + plan.getDestination().name() + " for " + stepsAllowed + " steps...");
    for (Step step : plan.getSteps()) {
      if (stepsAllowed == 0) {
        break;
      }
      if (step.startsInRoom) {
        Suspect suspect = hostGUI.getSuspect(player.getId());
        DisplayTile exitTile = hostGUI.board.getTile(step.destination.y, step.destination.x);
        DisplayTile currentTile = hostGUI.board.getSuspectTile(suspect);
        DisplayTile.Direction suspectDirection = DisplayTile.Direction.NORTH;
        for (DisplayTile.Direction direction : DisplayTile.Direction.values()) {
          if (exitTile.hasDoor(direction) && hostGUI.board.getAdjacentTile(exitTile, direction).getRoom() == currentTile.getRoom()) {
            suspectDirection = direction.getOpposite();
            break;
          }
        }
        if (exitTile.isRoomTile()) {
          hostGUI.board.moveSuspectToRoom(suspect, exitTile.getRoom());
        }
        else {
          hostGUI.board.moveSuspectToTile(suspect, suspectDirection, exitTile);
        }
      }
      stepToTile(hostGUI, player, step.direction);
      stepsAllowed--;
    }
  }

  private Plan getPlan(AICard destination, List<Plan> plans) {
    for (Plan plan : plans) {
      if (plan.getDestination() == destination) {
        return plan;
      }
    }
    return null;
  }

  private AICard[] getDestinations(List<Plan> plans) {
    AICard[] rooms = new AICard[plans.size()];
    int i = 0;
    for (Plan plan : plans) {
      rooms[i++] = plan.getDestination();
    }
    return rooms;
  }

  private int roll(HostGUI hostGUI, Player player) {
    int remainingSteps = 0;
    for (int i = 0; i < hostGUI.getNumDice(); ++i) {
      remainingSteps += Randomizer.getRandom(6) + 1;
    }
    hostGUI.informAllPlayers(player.getName() + " rolled a " + remainingSteps + ".");
    return remainingSteps;
  }
	
	private void buildDetectiveNotebookFromLatestSuggestions(Player player) {
		
	}

  public void movePlayer(Suspect suspect, Board.TilePosition newTile) {
    for (Player player : players) {
      if (player.isComputer() && player.getName().equals(suspect.getName())) {
        solvers.get(player).positionChanged(newTile.col, newTile.row);
      }
    }
  }

  public void showCard(Player suggestingPlayer, Player answeringPlayer, Card disprovingCard) {
    if (suggestingPlayer.isComputer() && disprovingCard != null) {
      solvers.get(suggestingPlayer).showCard(AICard.valueOf(disprovingCard.getName()));
    }
  }
}
