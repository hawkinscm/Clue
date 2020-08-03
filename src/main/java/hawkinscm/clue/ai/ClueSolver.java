package hawkinscm.clue.ai;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jdd.bdd.BDD;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public class ClueSolver {
  private Random random;
  private BDD bdd;

  private Map<AICard, Integer> variables;
  private List<AICard> cards;
//  private List<AICard> seenCards;

  private List<AICard> possibleRooms;
  private List<AICard> possibleSuspects;
  private List<AICard> possibleWeapons;

  private int roomKB;
  private int suspectKB;
  private int weaponKB;
  private Point location;
  private boolean seenCards;

  public ClueSolver(Point startingLocation, List<AICard> cards) {
    this.cards = cards;
    this.location = startingLocation;

    random = new Random(344);
    bdd = new BDD(100);

    variables = createVariables();
    List<Integer> roomConditions = buildConditions(CardType.ROOM);
    List<Integer> suspectConditions = buildConditions(CardType.SUSPECT);
    List<Integer> weaponConditions = buildConditions(CardType.WEAPON);
    possibleRooms = new ArrayList<AICard>(AICard.getRooms().size());
    possibleSuspects = new ArrayList<AICard>(AICard.getSuspects().size());
    possibleWeapons = new ArrayList<AICard>(AICard.getWeapons().size());

    roomKB = buildInitialKB(roomConditions, CardType.ROOM);
    suspectKB = buildInitialKB(suspectConditions, CardType.SUSPECT);
    weaponKB = buildInitialKB(weaponConditions, CardType.WEAPON);

    updatePossibilities();
  }

  private Map<AICard, Integer> createVariables() {
    Map<AICard, Integer> vars = new HashMap<AICard, Integer>(AICard.values().length);
    for (AICard card : AICard.values()) {
      vars.put(card, bdd.createVar());
    }
    return vars;
  }

  public static String translatePerson(String personName) {
    return personName.toUpperCase().substring(personName.indexOf(' ') + 1);
  }

  public static String translateRoom(String roomName) {
    return roomName.toUpperCase().replaceAll(" ", "_");
  }

  public static String translateWeapon(String weaponName) {
    return weaponName.toUpperCase().replaceAll(" ", "_");
  }

  private List<Integer> buildConditions(CardType type) {
    List<AICard> cards = AICard.getCards(type);
    List<Integer> conditions = new ArrayList<Integer>();
    for (int i = 0; i < cards.size(); ++i) {
      int notJ = bdd.getOne();
      for (int j = 0; j < cards.size(); ++j) {
        if (i == j) {
          continue;
        }
        notJ = bdd.and(notJ, bdd.not(variables.get(cards.get(j))));
      }
      conditions.add(bdd.ref(bdd.biimp(variables.get(cards.get(i)), notJ)));
    }
    return conditions;
  }

  public void showCard(AICard card) {
    switch (card.getType()) {
      case SUSPECT:
        suspectKB = bdd.ref(bdd.and(suspectKB, bdd.not(variables.get(card))));
        break;
      case WEAPON:
        weaponKB = bdd.ref(bdd.and(weaponKB, bdd.not(variables.get(card))));
        break;
      case ROOM:
        roomKB = bdd.ref(bdd.and(roomKB, bdd.not(variables.get(card))));
        break;
    }
    seenCards = true;
    updatePossibilities();
  }

  public void showNotCard(AICard card) {
    switch (card.getType()) {
      case SUSPECT:
        suspectKB = bdd.ref(bdd.and(suspectKB, variables.get(card)));
        break;
      case WEAPON:
        weaponKB = bdd.ref(bdd.and(weaponKB, variables.get(card)));
        break;
      case ROOM:
        roomKB = bdd.ref(bdd.and(roomKB, variables.get(card)));
        break;
    }
    updatePossibilities();
  }

  public List<AICard> chooseRoomDirection(AICard... availableRooms) {
    List<AICard> findRooms = new ArrayList<AICard>();
    List<AICard> findSuspectOrWeapon = new ArrayList<AICard>();
    for (AICard room : availableRooms) {
      if (room.getType() != CardType.ROOM) {
        continue;
      }
      if (possibleRooms.size() > 1 && possibleRooms.contains(room)) {
        findRooms.add(room);
      }
      else if (cards.contains(room) || (possibleRooms.size() == 1 && possibleRooms.contains(room))) {
        findSuspectOrWeapon.add(room);
      }
    }
    boolean preferRoom = findSuspectOrWeapon.isEmpty() || possibleRooms.size() - 1 > possibleSuspects.size() + possibleWeapons.size() - 2;
    if (preferRoom && !findRooms.isEmpty()) {
      return findRooms;
    }
    else if (!findSuspectOrWeapon.isEmpty() && stillNeedToFindSuspectOrWeapon()) {
      return findSuspectOrWeapon;
    }
    return null;
  }

  public AICard chooseRoom(AICard... availableRooms) {
    List<AICard> findRooms = new ArrayList<AICard>();
    List<AICard> findSuspectOrWeapon = new ArrayList<AICard>();
    for (AICard room : availableRooms) {
      if (room.getType() != CardType.ROOM) {
        continue;
      }
      if (possibleRooms.size() > 1 && possibleRooms.contains(room)) {
        findRooms.add(room);
      }
      else if (cards.contains(room) || (possibleRooms.size() == 1 && possibleRooms.contains(room))) {
        findSuspectOrWeapon.add(room);
      }
    }
    boolean preferRoom = findSuspectOrWeapon.isEmpty() || possibleRooms.size() - 1 > possibleSuspects.size() + possibleWeapons.size() - 2;
    if (preferRoom && !findRooms.isEmpty()) {
      return findRooms.get(random.nextInt(findRooms.size()));
    }
    else if (!findSuspectOrWeapon.isEmpty() && stillNeedToFindSuspectOrWeapon()) {
      return findSuspectOrWeapon.get(random.nextInt(findSuspectOrWeapon.size()));
    }
    return null;
  }

  private boolean stillNeedToFindSuspectOrWeapon() {
    return possibleSuspects.size() + possibleWeapons.size() > 2;
  }

  public AICard[] getSuggestion(AICard room) {
    AICard[] result = new AICard[2];
    if (cards.contains(room) || possibleRooms.size() == 1) {
      result[0] = choosePossibility(possibleSuspects);
      result[1] = choosePossibility(possibleWeapons);
    }
    else {
      result[0] = getCard(CardType.SUSPECT);
      result[1] = getCard(CardType.WEAPON);
      if (result[0] == null) {
        result[0] = choosePossibility(possibleSuspects);
      }
      if (result[1] == null) {
        result[1] = choosePossibility(possibleWeapons);
      }
    }
    seenCards = false;
    return result;
  }

  private AICard getCard(CardType type) {
    for (AICard card : cards) {
      if (card.getType() == type) {
        return card;
      }
    }
    return null;
  }

  private int buildInitialKB(List<Integer> conditions, CardType type) {
    int kb = bdd.getOne();
    for (AICard card : cards) {
      if (card.getType() == type) {
        kb = bdd.and(kb, bdd.not(variables.get(card)));
      }
    }
    for (int condition : conditions) {
      kb = bdd.and(kb, condition);
    }
    return bdd.ref(kb);
  }

  public AICard choosePossibility(List<AICard> possibleCards) {
    return possibleCards.get(random.nextInt(possibleCards.size()));
  }

  public void printSuggestions() {
    System.out.println("roomKB: " + roomKB);
    System.out.println("suspectKB: " + suspectKB);
    System.out.println("weaponKB: " + weaponKB);

    System.out.println("Possible rooms:");
    for (AICard card : possibleRooms) {
      System.out.println(card.name());
    }
    System.out.println("Possible suspects:");
    for (AICard card : possibleSuspects) {
      System.out.println(card.name());
    }
    System.out.println("Possible weapons:");
    for (AICard card : possibleWeapons) {
      System.out.println(card.name());
    }
  }

  private void updatePossibilities() {
    possibleRooms.clear();
    possibleSuspects.clear();
    possibleWeapons.clear();
    addPossibleCards(roomKB, possibleRooms, AICard.getCards(CardType.ROOM));
    addPossibleCards(suspectKB, possibleSuspects, AICard.getCards(CardType.SUSPECT));
    addPossibleCards(weaponKB, possibleWeapons, AICard.getCards(CardType.WEAPON));
  }

  private void addPossibleCards(int kb, List<AICard> possibleCards, List<AICard> allCards) {
    for (AICard card : allCards) {
      int state = bdd.and(kb, variables.get(card));
//      System.out.println("kb and " + card.name() + ": " + state);
      if (state != 0) {
        possibleCards.add(card);
      }
    }
  }

  public AICard[] getAccusation() {
    if (possibleRooms.size() == 1 && possibleSuspects.size() == 1 && possibleWeapons.size() == 1) {
      return new AICard[]{possibleRooms.get(0), possibleSuspects.get(0), possibleWeapons.get(0)};
    }
    return null;
  }

  public void showNoProofSuggestion(AICard... suggestion) {
    for (AICard card : suggestion) {
      if (!cards.contains(card)) {
        showNotCard(card);
      }
    }
  }

  public void positionChanged(int col, int row) {
    this.location = new Point(col, row);
  }

  public Point getLocation() {
    return location;
  }

  public static AICard getSecretRoom(AICard room) {
    switch (room) {
      case STUDY:
        return AICard.KITCHEN;
      case LOUNGE:
        return AICard.CONSERVATORY;
      case CONSERVATORY:
        return AICard.LOUNGE;
      case KITCHEN:
        return AICard.STUDY;
    }
    return null;
  }

  public boolean hasSeenCards() {
    return seenCards;
  }
}
