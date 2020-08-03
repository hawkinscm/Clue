package ai;

import java.awt.*;

import gui.DisplayTile;
import javaff.data.Action;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public class Step {
  public final DisplayTile.Direction direction;
  public final Point destination;
  public final AICard room;
  public final boolean startsInRoom;

  public Step(Action action) {
    String[] actionParts = action.toString().split(" ");
    if ("up".equals(actionParts[2])) {
      this.direction = DisplayTile.Direction.NORTH;
    }
    else if ("down".equals(actionParts[2])) {
      this.direction = DisplayTile.Direction.SOUTH;
    }
    else if ("left".equals(actionParts[2])) {
      this.direction = DisplayTile.Direction.WEST;
    }
    else {
      this.direction = DisplayTile.Direction.EAST;
    }
    if (actionParts[3].startsWith("sq")) {
      String[] squareParts = actionParts[3].split("-");
      destination = new Point(Integer.parseInt(squareParts[1]), Integer.parseInt(squareParts[2]));
      room = null;
    }
    else {
      room = AICard.valueOf(actionParts[3].toUpperCase());
      destination = null;
    }
    startsInRoom = !actionParts[1].startsWith("sq");
  }

  @Override
  public String toString() {
    return "take main.java.action of moving " + direction.name() + " to " + (room == null ? destination : room.name());
  }
}
