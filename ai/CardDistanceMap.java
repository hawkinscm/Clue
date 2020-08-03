package ai;

import java.util.HashMap;
import java.util.Map;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public class CardDistanceMap {
  private static Map<AICard, Map<AICard, Integer>> distances;
  static {
    Map<AICard, Integer> distance = new HashMap<AICard, Integer>(AICard.getRooms().size());
//    distance.put(AICard.STUDY, 1);
    distance.put(AICard.HALL, 4);
    distance.put(AICard.LOUNGE, 17);
    distance.put(AICard.LIBRARY, 7);
    distance.put(AICard.DINING_ROOM, 17);
    distance.put(AICard.BILLIARD_ROOM, 15);
    distance.put(AICard.CONSERVATORY, 10);
    distance.put(AICard.BALLROOM, 17);
    distance.put(AICard.KITCHEN, 0);
    distances.put(AICard.STUDY, distance);

    distance = new HashMap<AICard, Integer>(AICard.getRooms().size());
    distance.put(AICard.STUDY, 4);
//    distance.put(AICard.HALL, 4);
    distance.put(AICard.LOUNGE, 17);
    distance.put(AICard.LIBRARY, 7);
    distance.put(AICard.DINING_ROOM, 17);
    distance.put(AICard.BILLIARD_ROOM, 15);
    distance.put(AICard.CONSERVATORY, 10);
    distance.put(AICard.BALLROOM, 17);
    distance.put(AICard.KITCHEN, 0);
    distances.put(AICard.HALL, distance);

  }
}
