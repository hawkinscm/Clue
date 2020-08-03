package hawkinscm.clue.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public enum AICard {
  //rooms
  STUDY(CardType.ROOM),
  HALL(CardType.ROOM),
  LOUNGE(CardType.ROOM),
  LIBRARY(CardType.ROOM),
  DINING_ROOM(CardType.ROOM),
  BILLIARD_ROOM(CardType.ROOM),
  CONSERVATORY(CardType.ROOM),
  BALLROOM(CardType.ROOM),
  KITCHEN(CardType.ROOM),

  //suspects
  SCARLET(CardType.SUSPECT),
  MUSTARD(CardType.SUSPECT),
  WHITE(CardType.SUSPECT),
  GREEN(CardType.SUSPECT),
  PEACOCK(CardType.SUSPECT),
  PLUM(CardType.SUSPECT),

  //weapon
  ROPE(CardType.WEAPON),
  LEAD_PIPE(CardType.WEAPON),
  KNIFE(CardType.WEAPON),
  WRENCH(CardType.WEAPON),
  CANDLESTICK(CardType.WEAPON),
  REVOLVER(CardType.WEAPON);

  private CardType type;

  AICard(CardType type) {
    this.type = type;
  }

  public CardType getType() {
    return type;
  }
  private static List<AICard> rooms = new ArrayList<>();
  private static List<AICard> suspects = new ArrayList<>();
  private static List<AICard> weapons = new ArrayList<>();
  static {
    for (AICard card : AICard.values()) {
      switch (card.getType()) {
        case SUSPECT:
          suspects.add(card);
          break;
        case WEAPON:
          weapons.add(card);
          break;
        case ROOM:
          rooms.add(card);
          break;
      }
    }
  }
  public static List<AICard> getRooms() {
    return rooms;
  }

  public static List<AICard> getSuspects() {
    return suspects;
  }

  public static List<AICard> getWeapons() {
    return weapons;
  }

  public static List<AICard> getCards(CardType type) {
    switch (type) {
      case SUSPECT:
        return getSuspects();
      case WEAPON:
        return getWeapons();
      case ROOM:
        return getRooms();
    }
    return null;
  }
}
