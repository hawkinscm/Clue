package hawkinscm.clue.ai;

import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javaff.JavaFF;
import javaff.data.Action;
import javaff.data.GroundProblem;
import javaff.data.TotalOrderPlan;
import javaff.data.UngroundProblem;
import javaff.parser.PDDL21parser;
import javaff.parser.ParseException;
import javaff.planning.State;
import javaff.planning.TemporalMetricState;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public class Planner {
  private static long id = 0;
  private static Map<AICard, String> connections;

  static {
    connections = new HashMap<>();
    connections.put(AICard.STUDY, "(adj STUDY down sq-6-4)\n" +
        "(adj sq-6-4 up STUDY)\n");
    connections.put(AICard.HALL, "(adj HALL left sq-8-4)\n" +
        "(adj sq-8-4 right HALL)\n" +
        "(adj HALL down sq-11-7)\n" +
        "(adj HALL down sq-12-7)\n" +
        "(adj sq-11-7 up HALL)\n" +
        "(adj sq-12-7 up HALL)\n");
    connections.put(AICard.LOUNGE, "(adj LOUNGE down sq-17-6)\n" +
        "(adj sq-17-6 up LOUNGE)\n");
    connections.put(AICard.LIBRARY, "(adj LIBRARY right sq-7-8)\n" +
        "(adj sq-7-8 left LIBRARY)\n" +
        "(adj LIBRARY down sq-3-11)\n" +
        "(adj sq-3-11 up LIBRARY)\n");
    connections.put(AICard.DINING_ROOM, "(adj DINING_ROOM up sq-17-8)\n" +
        "(adj sq-17-8 down DINING_ROOM)\n" +
        "(adj DINING_ROOM left sq-15-12)\n" +
        "(adj sq-15-12 right DINING_ROOM)\n");
    connections.put(AICard.BILLIARD_ROOM, "(adj BILLIARD_ROOM up sq-1-11)\n" +
        "(adj sq-1-11 down BILLIARD_ROOM)\n" +
        "(adj BILLIARD_ROOM left sq-6-13)\n" +
        "(adj sq-6-13 right BILLIARD_ROOM)\n");
    connections.put(AICard.CONSERVATORY, "(adj CONSERVATORY right sq-5-19)\n" +
        "(adj sq-5-17 left CONSERVATORY)\n");
    connections.put(AICard.BALLROOM, "(adj BALLROOM left sq-7-19)\n" +
        "(adj sq-7-19 right BALLROOM)\n" +
        "(adj BALLROOM right sq-16-19)\n" +
        "(adj sq-16-19 left BALLROOM)\n" +
        "(adj BALLROOM up sq-9-16)\n" +
        "(adj BALLROOM up sq-14-16)\n" +
        "(adj sq-9-16 down BALLROOM)\n" +
        "(adj sq-14-16 down BALLROOM)\n");
    connections.put(AICard.KITCHEN, "(adj KITCHEN up sq-19-17)\n" +
        "(adj sq-19-17 down KITCHEN)");
  }



  public static final String DOMAIN_PDDL = "(define (domain clue-a)\n" +
      "  (:requirements :strips) ;; maybe not necessary\n" +
      "  (:types square direction)\n" +
      "  (:predicates\n" +
      "   (adj ?square-1 - square ?dir - direction ?square-2 - square)\n" +
      "   (standingAt ?square - square))\n" +
      "  (:action move\n" +
      "    :parameters (?from - square ?dir - direction ?to - square)\n" +
      "    :precondition (and (adj ?from ?dir ?to)\n" +
      "                       (standingAt ?from))\n" +
      "    :effect (and (not (standingAt ?from))\n" +
      "                 (standingAt ?to))\n" +
      "    )\n" +
      ")";
  private static final boolean DEBUG = false;

  public static List<Plan> plan(Point startingPoint, AICard startingRoom, List<AICard> destinations) {
    List<Plan> plans = new ArrayList<Plan>(destinations.size());
    for (AICard destination : destinations) {
      List<Step> steps = planRoute(startingPoint, startingRoom, destination);
      plans.add(new Plan(destination, steps));
    }
    return plans;
  }

  private static List<Step> planRoute(Point startingPoint, AICard startingRoom, AICard destination) {
    PDDL21parser.reset();
    long startTime = System.currentTimeMillis();
    ByteArrayOutputStream domainWriteStream = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(domainWriteStream);
    ps.println(Planner.DOMAIN_PDDL);
    ps.flush();
    try {
      new PDDL21parser(new ByteArrayInputStream(domainWriteStream.toByteArray())).parseDomain();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }

    ByteArrayOutputStream problemOutputStream = new ByteArrayOutputStream();
    ps = new PrintStream(problemOutputStream);
    ps.print(Planner.getProblem(startingPoint, startingRoom, destination));
    ps.flush();
    PDDL21parser problemParser = new PDDL21parser(new ByteArrayInputStream(problemOutputStream.toByteArray()));
    boolean problemParsed = false;
    try {
      problemParsed = problemParser.parseProblem();
    }
    catch (ParseException e) {
      e.printStackTrace();
    }
    System.out.println("Problem parsed: " + problemParsed);
    if (!problemParsed) {
      System.exit(1);
    }

    TotalOrderPlan top = Planner.getPlan(startTime);
    List<Step> steps = new ArrayList<Step>(top.getPlanLength());
    for (Action action : top) {
      steps.add(new Step(action));
    }
    return steps;
  }

  public static String getProblem(Point start, AICard startingRoom, AICard destination) {
    String startingString = start != null ? "sq-"+start.x+"-"+start.y : startingRoom.name();

    return "(define (problem Prob" + (id++) + ")\n" +
        "  (:domain clue-a)\n" +
        "(:objects\n" +
        "sq-0-5\n" +
        "sq-0-18\n" +
        "sq-1-4\n" +
        "sq-1-5\n" +
        "sq-1-11\n" +
        "sq-1-17\n" +
        "sq-1-18\n" +
        "sq-2-4\n" +
        "sq-2-5\n" +
        "sq-2-11\n" +
        "sq-2-17\n" +
        "sq-2-18\n" +
        "sq-3-4\n" +
        "sq-3-5\n" +
        "sq-3-11\n" +
        "sq-3-17\n" +
        "sq-3-18\n" +
        "sq-4-4\n" +
        "sq-4-5\n" +
        "sq-4-11\n" +
        "sq-4-17\n" +
        "sq-4-18\n" +
        "sq-5-4\n" +
        "sq-5-5\n" +
        "sq-5-11\n" +
        "sq-5-17\n" +
        "sq-5-18\n" +
        "sq-5-19\n" +
        "sq-6-4\n" +
        "sq-6-5\n" +
        "sq-6-6\n" +
        "sq-6-10\n" +
        "sq-6-11\n" +
        "sq-6-12\n" +
        "sq-6-13\n" +
        "sq-6-14\n" +
        "sq-6-15\n" +
        "sq-6-16\n" +
        "sq-6-17\n" +
        "sq-6-18\n" +
        "sq-6-19\n" +
        "sq-6-20\n" +
        "sq-6-21\n" +
        "sq-6-22\n" +
        "sq-7-0\n" +
        "sq-7-1\n" +
        "sq-7-2\n" +
        "sq-7-3\n" +
        "sq-7-4\n" +
        "sq-7-5\n" +
        "sq-7-6\n" +
        "sq-7-7\n" +
        "sq-7-8\n" +
        "sq-7-9\n" +
        "sq-7-10\n" +
        "sq-7-11\n" +
        "sq-7-12\n" +
        "sq-7-13\n" +
        "sq-7-14\n" +
        "sq-7-15\n" +
        "sq-7-16\n" +
        "sq-7-17\n" +
        "sq-7-18\n" +
        "sq-7-19\n" +
        "sq-7-20\n" +
        "sq-7-21\n" +
        "sq-7-22\n" +
        "sq-7-23\n" +
        "sq-8-1\n" +
        "sq-8-2\n" +
        "sq-8-3\n" +
        "sq-8-4\n" +
        "sq-8-5\n" +
        "sq-8-6\n" +
        "sq-8-7\n" +
        "sq-8-8\n" +
        "sq-8-9\n" +
        "sq-8-10\n" +
        "sq-8-11\n" +
        "sq-8-12\n" +
        "sq-8-13\n" +
        "sq-8-14\n" +
        "sq-8-15\n" +
        "sq-8-16\n" +
        "sq-8-23\n" +
        "sq-9-7\n" +
        "sq-9-15\n" +
        "sq-9-16\n" +
        "sq-9-23\n" +
        "sq-9-24\n" +
        "sq-10-7\n" +
        "sq-10-15\n" +
        "sq-10-16\n" +
        "sq-11-7\n" +
        "sq-11-15\n" +
        "sq-11-16\n" +
        "sq-12-7\n" +
        "sq-12-15\n" +
        "sq-12-16\n" +
        "sq-13-7\n" +
        "sq-13-15\n" +
        "sq-13-16\n" +
        "sq-14-7\n" +
        "sq-14-8\n" +
        "sq-14-9\n" +
        "sq-14-10\n" +
        "sq-14-11\n" +
        "sq-14-12\n" +
        "sq-14-13\n" +
        "sq-14-14\n" +
        "sq-14-15\n" +
        "sq-14-16\n" +
        "sq-14-23\n" +
        "sq-14-24\n" +
        "sq-15-1\n" +
        "sq-15-2\n" +
        "sq-15-3\n" +
        "sq-15-4\n" +
        "sq-15-5\n" +
        "sq-15-6\n" +
        "sq-15-7\n" +
        "sq-15-8\n" +
        "sq-15-9\n" +
        "sq-15-10\n" +
        "sq-15-11\n" +
        "sq-15-12\n" +
        "sq-15-13\n" +
        "sq-15-14\n" +
        "sq-15-15\n" +
        "sq-15-16\n" +
        "sq-15-23\n" +
        "sq-16-0\n" +
        "sq-16-1\n" +
        "sq-16-2\n" +
        "sq-16-3\n" +
        "sq-16-4\n" +
        "sq-16-5\n" +
        "sq-16-6\n" +
        "sq-16-7\n" +
        "sq-16-8\n" +
        "sq-16-15\n" +
        "sq-16-16\n" +
        "sq-16-17\n" +
        "sq-16-18\n" +
        "sq-16-19\n" +
        "sq-16-20\n" +
        "sq-16-21\n" +
        "sq-16-22\n" +
        "sq-16-23\n" +
        "sq-17-6\n" +
        "sq-17-7\n" +
        "sq-17-8\n" +
        "sq-17-15\n" +
        "sq-17-16\n" +
        "sq-17-17\n" +
        "sq-17-18\n" +
        "sq-17-19\n" +
        "sq-17-20\n" +
        "sq-17-21\n" +
        "sq-17-22\n" +
        "sq-18-6\n" +
        "sq-18-7\n" +
        "sq-18-8\n" +
        "sq-18-15\n" +
        "sq-18-16\n" +
        "sq-18-17\n" +
        "sq-19-6\n" +
        "sq-19-7\n" +
        "sq-19-8\n" +
        "sq-19-16\n" +
        "sq-19-17\n" +
        "sq-20-6\n" +
        "sq-20-7\n" +
        "sq-20-8\n" +
        "sq-20-16\n" +
        "sq-20-17\n" +
        "sq-21-6\n" +
        "sq-21-7\n" +
        "sq-21-8\n" +
        "sq-21-16\n" +
        "sq-21-17\n" +
        "sq-22-6\n" +
        "sq-22-7\n" +
        "sq-22-8\n" +
        "sq-22-16\n" +
        "sq-22-17\n" +
        "sq-23-7\n" +
        "sq-23-17\n" +
        (startingRoom == null ? getRooms(destination) : getRooms(startingRoom, destination)) +
        " - square\n" +
        "left right up down - direction)" +
        "\n" +
        " (:init\n" +
        "(standingAt " + startingString + ")\n" +
        "(adj sq-0-5 right sq-1-5)\n" +
        "(adj sq-0-18 right sq-1-18)\n" +
        "(adj sq-1-4 right sq-2-4)\n" +
        "(adj sq-1-4 down sq-1-5)\n" +
        "(adj sq-1-5 left sq-0-5)\n" +
        "(adj sq-1-5 right sq-2-5)\n" +
        "(adj sq-1-5 up sq-1-4)\n" +
        "(adj sq-1-11 right sq-2-11)\n" +
        "(adj sq-1-17 right sq-2-17)\n" +
        "(adj sq-1-17 down sq-1-18)\n" +
        "(adj sq-1-18 left sq-0-18)\n" +
        "(adj sq-1-18 right sq-2-18)\n" +
        "(adj sq-1-18 up sq-1-17)\n" +
        "(adj sq-2-4 left sq-1-4)\n" +
        "(adj sq-2-4 right sq-3-4)\n" +
        "(adj sq-2-4 down sq-2-5)\n" +
        "(adj sq-2-5 left sq-1-5)\n" +
        "(adj sq-2-5 right sq-3-5)\n" +
        "(adj sq-2-5 up sq-2-4)\n" +
        "(adj sq-2-11 left sq-1-11)\n" +
        "(adj sq-2-11 right sq-3-11)\n" +
        "(adj sq-2-17 left sq-1-17)\n" +
        "(adj sq-2-17 right sq-3-17)\n" +
        "(adj sq-2-17 down sq-2-18)\n" +
        "(adj sq-2-18 left sq-1-18)\n" +
        "(adj sq-2-18 right sq-3-18)\n" +
        "(adj sq-2-18 up sq-2-17)\n" +
        "(adj sq-3-4 left sq-2-4)\n" +
        "(adj sq-3-4 right sq-4-4)\n" +
        "(adj sq-3-4 down sq-3-5)\n" +
        "(adj sq-3-5 left sq-2-5)\n" +
        "(adj sq-3-5 right sq-4-5)\n" +
        "(adj sq-3-5 up sq-3-4)\n" +
        "(adj sq-3-11 left sq-2-11)\n" +
        "(adj sq-3-11 right sq-4-11)\n" +
        "(adj sq-3-17 left sq-2-17)\n" +
        "(adj sq-3-17 right sq-4-17)\n" +
        "(adj sq-3-17 down sq-3-18)\n" +
        "(adj sq-3-18 left sq-2-18)\n" +
        "(adj sq-3-18 right sq-4-18)\n" +
        "(adj sq-3-18 up sq-3-17)\n" +
        "(adj sq-4-4 left sq-3-4)\n" +
        "(adj sq-4-4 right sq-5-4)\n" +
        "(adj sq-4-4 down sq-4-5)\n" +
        "(adj sq-4-5 left sq-3-5)\n" +
        "(adj sq-4-5 right sq-5-5)\n" +
        "(adj sq-4-5 up sq-4-4)\n" +
        "(adj sq-4-11 left sq-3-11)\n" +
        "(adj sq-4-11 right sq-5-11)\n" +
        "(adj sq-4-17 left sq-3-17)\n" +
        "(adj sq-4-17 right sq-5-17)\n" +
        "(adj sq-4-17 down sq-4-18)\n" +
        "(adj sq-4-18 left sq-3-18)\n" +
        "(adj sq-4-18 right sq-5-18)\n" +
        "(adj sq-4-18 up sq-4-17)\n" +
        "(adj sq-5-4 left sq-4-4)\n" +
        "(adj sq-5-4 right sq-6-4)\n" +
        "(adj sq-5-4 down sq-5-5)\n" +
        "(adj sq-5-5 left sq-4-5)\n" +
        "(adj sq-5-5 right sq-6-5)\n" +
        "(adj sq-5-5 up sq-5-4)\n" +
        "(adj sq-5-11 left sq-4-11)\n" +
        "(adj sq-5-11 right sq-6-11)\n" +
        "(adj sq-5-17 left sq-4-17)\n" +
        "(adj sq-5-17 right sq-6-17)\n" +
        "(adj sq-5-17 down sq-5-18)\n" +
        "(adj sq-5-18 left sq-4-18)\n" +
        "(adj sq-5-18 right sq-6-18)\n" +
        "(adj sq-5-18 up sq-5-17)\n" +
        "(adj sq-5-18 down sq-5-19)\n" +
        "(adj sq-5-19 right sq-6-19)\n" +
        "(adj sq-5-19 up sq-5-18)\n" +
        "(adj sq-6-4 left sq-5-4)\n" +
        "(adj sq-6-4 right sq-7-4)\n" +
        "(adj sq-6-4 down sq-6-5)\n" +
        "(adj sq-6-5 left sq-5-5)\n" +
        "(adj sq-6-5 right sq-7-5)\n" +
        "(adj sq-6-5 up sq-6-4)\n" +
        "(adj sq-6-5 down sq-6-6)\n" +
        "(adj sq-6-6 right sq-7-6)\n" +
        "(adj sq-6-6 up sq-6-5)\n" +
        "(adj sq-6-10 right sq-7-10)\n" +
        "(adj sq-6-10 down sq-6-11)\n" +
        "(adj sq-6-11 left sq-5-11)\n" +
        "(adj sq-6-11 right sq-7-11)\n" +
        "(adj sq-6-11 up sq-6-10)\n" +
        "(adj sq-6-11 down sq-6-12)\n" +
        "(adj sq-6-12 right sq-7-12)\n" +
        "(adj sq-6-12 up sq-6-11)\n" +
        "(adj sq-6-12 down sq-6-13)\n" +
        "(adj sq-6-13 right sq-7-13)\n" +
        "(adj sq-6-13 up sq-6-12)\n" +
        "(adj sq-6-13 down sq-6-14)\n" +
        "(adj sq-6-14 right sq-7-14)\n" +
        "(adj sq-6-14 up sq-6-13)\n" +
        "(adj sq-6-14 down sq-6-15)\n" +
        "(adj sq-6-15 right sq-7-15)\n" +
        "(adj sq-6-15 up sq-6-14)\n" +
        "(adj sq-6-15 down sq-6-16)\n" +
        "(adj sq-6-16 right sq-7-16)\n" +
        "(adj sq-6-16 up sq-6-15)\n" +
        "(adj sq-6-16 down sq-6-17)\n" +
        "(adj sq-6-17 left sq-5-17)\n" +
        "(adj sq-6-17 right sq-7-17)\n" +
        "(adj sq-6-17 up sq-6-16)\n" +
        "(adj sq-6-17 down sq-6-18)\n" +
        "(adj sq-6-18 left sq-5-18)\n" +
        "(adj sq-6-18 right sq-7-18)\n" +
        "(adj sq-6-18 up sq-6-17)\n" +
        "(adj sq-6-18 down sq-6-19)\n" +
        "(adj sq-6-19 left sq-5-19)\n" +
        "(adj sq-6-19 right sq-7-19)\n" +
        "(adj sq-6-19 up sq-6-18)\n" +
        "(adj sq-6-19 down sq-6-20)\n" +
        "(adj sq-6-20 right sq-7-20)\n" +
        "(adj sq-6-20 up sq-6-19)\n" +
        "(adj sq-6-20 down sq-6-21)\n" +
        "(adj sq-6-21 right sq-7-21)\n" +
        "(adj sq-6-21 up sq-6-20)\n" +
        "(adj sq-6-21 down sq-6-22)\n" +
        "(adj sq-6-22 right sq-7-22)\n" +
        "(adj sq-6-22 up sq-6-21)\n" +
        "(adj sq-7-0 down sq-7-1)\n" +
        "(adj sq-7-1 right sq-8-1)\n" +
        "(adj sq-7-1 up sq-7-0)\n" +
        "(adj sq-7-1 down sq-7-2)\n" +
        "(adj sq-7-2 right sq-8-2)\n" +
        "(adj sq-7-2 up sq-7-1)\n" +
        "(adj sq-7-2 down sq-7-3)\n" +
        "(adj sq-7-3 right sq-8-3)\n" +
        "(adj sq-7-3 up sq-7-2)\n" +
        "(adj sq-7-3 down sq-7-4)\n" +
        "(adj sq-7-4 left sq-6-4)\n" +
        "(adj sq-7-4 right sq-8-4)\n" +
        "(adj sq-7-4 up sq-7-3)\n" +
        "(adj sq-7-4 down sq-7-5)\n" +
        "(adj sq-7-5 left sq-6-5)\n" +
        "(adj sq-7-5 right sq-8-5)\n" +
        "(adj sq-7-5 up sq-7-4)\n" +
        "(adj sq-7-5 down sq-7-6)\n" +
        "(adj sq-7-6 left sq-6-6)\n" +
        "(adj sq-7-6 right sq-8-6)\n" +
        "(adj sq-7-6 up sq-7-5)\n" +
        "(adj sq-7-6 down sq-7-7)\n" +
        "(adj sq-7-7 right sq-8-7)\n" +
        "(adj sq-7-7 up sq-7-6)\n" +
        "(adj sq-7-7 down sq-7-8)\n" +
        "(adj sq-7-8 right sq-8-8)\n" +
        "(adj sq-7-8 up sq-7-7)\n" +
        "(adj sq-7-8 down sq-7-9)\n" +
        "(adj sq-7-9 right sq-8-9)\n" +
        "(adj sq-7-9 up sq-7-8)\n" +
        "(adj sq-7-9 down sq-7-10)\n" +
        "(adj sq-7-10 left sq-6-10)\n" +
        "(adj sq-7-10 right sq-8-10)\n" +
        "(adj sq-7-10 up sq-7-9)\n" +
        "(adj sq-7-10 down sq-7-11)\n" +
        "(adj sq-7-11 left sq-6-11)\n" +
        "(adj sq-7-11 right sq-8-11)\n" +
        "(adj sq-7-11 up sq-7-10)\n" +
        "(adj sq-7-11 down sq-7-12)\n" +
        "(adj sq-7-12 left sq-6-12)\n" +
        "(adj sq-7-12 right sq-8-12)\n" +
        "(adj sq-7-12 up sq-7-11)\n" +
        "(adj sq-7-12 down sq-7-13)\n" +
        "(adj sq-7-13 left sq-6-13)\n" +
        "(adj sq-7-13 right sq-8-13)\n" +
        "(adj sq-7-13 up sq-7-12)\n" +
        "(adj sq-7-13 down sq-7-14)\n" +
        "(adj sq-7-14 left sq-6-14)\n" +
        "(adj sq-7-14 right sq-8-14)\n" +
        "(adj sq-7-14 up sq-7-13)\n" +
        "(adj sq-7-14 down sq-7-15)\n" +
        "(adj sq-7-15 left sq-6-15)\n" +
        "(adj sq-7-15 right sq-8-15)\n" +
        "(adj sq-7-15 up sq-7-14)\n" +
        "(adj sq-7-15 down sq-7-16)\n" +
        "(adj sq-7-16 left sq-6-16)\n" +
        "(adj sq-7-16 right sq-8-16)\n" +
        "(adj sq-7-16 up sq-7-15)\n" +
        "(adj sq-7-16 down sq-7-17)\n" +
        "(adj sq-7-17 left sq-6-17)\n" +
        "(adj sq-7-17 up sq-7-16)\n" +
        "(adj sq-7-17 down sq-7-18)\n" +
        "(adj sq-7-18 left sq-6-18)\n" +
        "(adj sq-7-18 up sq-7-17)\n" +
        "(adj sq-7-18 down sq-7-19)\n" +
        "(adj sq-7-19 left sq-6-19)\n" +
        "(adj sq-7-19 up sq-7-18)\n" +
        "(adj sq-7-19 down sq-7-20)\n" +
        "(adj sq-7-20 left sq-6-20)\n" +
        "(adj sq-7-20 up sq-7-19)\n" +
        "(adj sq-7-20 down sq-7-21)\n" +
        "(adj sq-7-21 left sq-6-21)\n" +
        "(adj sq-7-21 up sq-7-20)\n" +
        "(adj sq-7-21 down sq-7-22)\n" +
        "(adj sq-7-22 left sq-6-22)\n" +
        "(adj sq-7-22 up sq-7-21)\n" +
        "(adj sq-7-22 down sq-7-23)\n" +
        "(adj sq-7-23 right sq-8-23)\n" +
        "(adj sq-7-23 up sq-7-22)\n" +
        "(adj sq-8-1 left sq-7-1)\n" +
        "(adj sq-8-1 down sq-8-2)\n" +
        "(adj sq-8-2 left sq-7-2)\n" +
        "(adj sq-8-2 up sq-8-1)\n" +
        "(adj sq-8-2 down sq-8-3)\n" +
        "(adj sq-8-3 left sq-7-3)\n" +
        "(adj sq-8-3 up sq-8-2)\n" +
        "(adj sq-8-3 down sq-8-4)\n" +
        "(adj sq-8-4 left sq-7-4)\n" +
        "(adj sq-8-4 up sq-8-3)\n" +
        "(adj sq-8-4 down sq-8-5)\n" +
        "(adj sq-8-5 left sq-7-5)\n" +
        "(adj sq-8-5 up sq-8-4)\n" +
        "(adj sq-8-5 down sq-8-6)\n" +
        "(adj sq-8-6 left sq-7-6)\n" +
        "(adj sq-8-6 up sq-8-5)\n" +
        "(adj sq-8-6 down sq-8-7)\n" +
        "(adj sq-8-7 left sq-7-7)\n" +
        "(adj sq-8-7 right sq-9-7)\n" +
        "(adj sq-8-7 up sq-8-6)\n" +
        "(adj sq-8-7 down sq-8-8)\n" +
        "(adj sq-8-8 left sq-7-8)\n" +
        "(adj sq-8-8 up sq-8-7)\n" +
        "(adj sq-8-8 down sq-8-9)\n" +
        "(adj sq-8-9 left sq-7-9)\n" +
        "(adj sq-8-9 up sq-8-8)\n" +
        "(adj sq-8-9 down sq-8-10)\n" +
        "(adj sq-8-10 left sq-7-10)\n" +
        "(adj sq-8-10 up sq-8-9)\n" +
        "(adj sq-8-10 down sq-8-11)\n" +
        "(adj sq-8-11 left sq-7-11)\n" +
        "(adj sq-8-11 up sq-8-10)\n" +
        "(adj sq-8-11 down sq-8-12)\n" +
        "(adj sq-8-12 left sq-7-12)\n" +
        "(adj sq-8-12 up sq-8-11)\n" +
        "(adj sq-8-12 down sq-8-13)\n" +
        "(adj sq-8-13 left sq-7-13)\n" +
        "(adj sq-8-13 up sq-8-12)\n" +
        "(adj sq-8-13 down sq-8-14)\n" +
        "(adj sq-8-14 left sq-7-14)\n" +
        "(adj sq-8-14 up sq-8-13)\n" +
        "(adj sq-8-14 down sq-8-15)\n" +
        "(adj sq-8-15 left sq-7-15)\n" +
        "(adj sq-8-15 right sq-9-15)\n" +
        "(adj sq-8-15 up sq-8-14)\n" +
        "(adj sq-8-15 down sq-8-16)\n" +
        "(adj sq-8-16 left sq-7-16)\n" +
        "(adj sq-8-16 right sq-9-16)\n" +
        "(adj sq-8-16 up sq-8-15)\n" +
        "(adj sq-8-23 left sq-7-23)\n" +
        "(adj sq-8-23 right sq-9-23)\n" +
        "(adj sq-9-7 left sq-8-7)\n" +
        "(adj sq-9-7 right sq-10-7)\n" +
        "(adj sq-9-15 left sq-8-15)\n" +
        "(adj sq-9-15 right sq-10-15)\n" +
        "(adj sq-9-15 down sq-9-16)\n" +
        "(adj sq-9-16 left sq-8-16)\n" +
        "(adj sq-9-16 right sq-10-16)\n" +
        "(adj sq-9-16 up sq-9-15)\n" +
        "(adj sq-9-23 left sq-8-23)\n" +
        "(adj sq-9-23 down sq-9-24)\n" +
        "(adj sq-9-24 up sq-9-23)\n" +
        "(adj sq-10-7 left sq-9-7)\n" +
        "(adj sq-10-7 right sq-11-7)\n" +
        "(adj sq-10-15 left sq-9-15)\n" +
        "(adj sq-10-15 right sq-11-15)\n" +
        "(adj sq-10-15 down sq-10-16)\n" +
        "(adj sq-10-16 left sq-9-16)\n" +
        "(adj sq-10-16 right sq-11-16)\n" +
        "(adj sq-10-16 up sq-10-15)\n" +
        "(adj sq-11-7 left sq-10-7)\n" +
        "(adj sq-11-7 right sq-12-7)\n" +
        "(adj sq-11-15 left sq-10-15)\n" +
        "(adj sq-11-15 right sq-12-15)\n" +
        "(adj sq-11-15 down sq-11-16)\n" +
        "(adj sq-11-16 left sq-10-16)\n" +
        "(adj sq-11-16 right sq-12-16)\n" +
        "(adj sq-11-16 up sq-11-15)\n" +
        "(adj sq-12-7 left sq-11-7)\n" +
        "(adj sq-12-7 right sq-13-7)\n" +
        "(adj sq-12-15 left sq-11-15)\n" +
        "(adj sq-12-15 right sq-13-15)\n" +
        "(adj sq-12-15 down sq-12-16)\n" +
        "(adj sq-12-16 left sq-11-16)\n" +
        "(adj sq-12-16 right sq-13-16)\n" +
        "(adj sq-12-16 up sq-12-15)\n" +
        "(adj sq-13-7 left sq-12-7)\n" +
        "(adj sq-13-7 right sq-14-7)\n" +
        "(adj sq-13-15 left sq-12-15)\n" +
        "(adj sq-13-15 right sq-14-15)\n" +
        "(adj sq-13-15 down sq-13-16)\n" +
        "(adj sq-13-16 left sq-12-16)\n" +
        "(adj sq-13-16 right sq-14-16)\n" +
        "(adj sq-13-16 up sq-13-15)\n" +
        "(adj sq-14-7 left sq-13-7)\n" +
        "(adj sq-14-7 right sq-15-7)\n" +
        "(adj sq-14-7 down sq-14-8)\n" +
        "(adj sq-14-8 right sq-15-8)\n" +
        "(adj sq-14-8 up sq-14-7)\n" +
        "(adj sq-14-8 down sq-14-9)\n" +
        "(adj sq-14-9 right sq-15-9)\n" +
        "(adj sq-14-9 up sq-14-8)\n" +
        "(adj sq-14-9 down sq-14-10)\n" +
        "(adj sq-14-10 right sq-15-10)\n" +
        "(adj sq-14-10 up sq-14-9)\n" +
        "(adj sq-14-10 down sq-14-11)\n" +
        "(adj sq-14-11 right sq-15-11)\n" +
        "(adj sq-14-11 up sq-14-10)\n" +
        "(adj sq-14-11 down sq-14-12)\n" +
        "(adj sq-14-12 right sq-15-12)\n" +
        "(adj sq-14-12 up sq-14-11)\n" +
        "(adj sq-14-12 down sq-14-13)\n" +
        "(adj sq-14-13 right sq-15-13)\n" +
        "(adj sq-14-13 up sq-14-12)\n" +
        "(adj sq-14-13 down sq-14-14)\n" +
        "(adj sq-14-14 right sq-15-14)\n" +
        "(adj sq-14-14 up sq-14-13)\n" +
        "(adj sq-14-14 down sq-14-15)\n" +
        "(adj sq-14-15 left sq-13-15)\n" +
        "(adj sq-14-15 right sq-15-15)\n" +
        "(adj sq-14-15 up sq-14-14)\n" +
        "(adj sq-14-15 down sq-14-16)\n" +
        "(adj sq-14-16 left sq-13-16)\n" +
        "(adj sq-14-16 right sq-15-16)\n" +
        "(adj sq-14-16 up sq-14-15)\n" +
        "(adj sq-14-23 right sq-15-23)\n" +
        "(adj sq-14-23 down sq-14-24)\n" +
        "(adj sq-14-24 up sq-14-23)\n" +
        "(adj sq-15-1 right sq-16-1)\n" +
        "(adj sq-15-1 down sq-15-2)\n" +
        "(adj sq-15-2 right sq-16-2)\n" +
        "(adj sq-15-2 up sq-15-1)\n" +
        "(adj sq-15-2 down sq-15-3)\n" +
        "(adj sq-15-3 right sq-16-3)\n" +
        "(adj sq-15-3 up sq-15-2)\n" +
        "(adj sq-15-3 down sq-15-4)\n" +
        "(adj sq-15-4 right sq-16-4)\n" +
        "(adj sq-15-4 up sq-15-3)\n" +
        "(adj sq-15-4 down sq-15-5)\n" +
        "(adj sq-15-5 right sq-16-5)\n" +
        "(adj sq-15-5 up sq-15-4)\n" +
        "(adj sq-15-5 down sq-15-6)\n" +
        "(adj sq-15-6 right sq-16-6)\n" +
        "(adj sq-15-6 up sq-15-5)\n" +
        "(adj sq-15-6 down sq-15-7)\n" +
        "(adj sq-15-7 left sq-14-7)\n" +
        "(adj sq-15-7 right sq-16-7)\n" +
        "(adj sq-15-7 up sq-15-6)\n" +
        "(adj sq-15-7 down sq-15-8)\n" +
        "(adj sq-15-8 left sq-14-8)\n" +
        "(adj sq-15-8 right sq-16-8)\n" +
        "(adj sq-15-8 up sq-15-7)\n" +
        "(adj sq-15-8 down sq-15-9)\n" +
        "(adj sq-15-9 left sq-14-9)\n" +
        "(adj sq-15-9 up sq-15-8)\n" +
        "(adj sq-15-9 down sq-15-10)\n" +
        "(adj sq-15-10 left sq-14-10)\n" +
        "(adj sq-15-10 up sq-15-9)\n" +
        "(adj sq-15-10 down sq-15-11)\n" +
        "(adj sq-15-11 left sq-14-11)\n" +
        "(adj sq-15-11 up sq-15-10)\n" +
        "(adj sq-15-11 down sq-15-12)\n" +
        "(adj sq-15-12 left sq-14-12)\n" +
        "(adj sq-15-12 up sq-15-11)\n" +
        "(adj sq-15-12 down sq-15-13)\n" +
        "(adj sq-15-13 left sq-14-13)\n" +
        "(adj sq-15-13 up sq-15-12)\n" +
        "(adj sq-15-13 down sq-15-14)\n" +
        "(adj sq-15-14 left sq-14-14)\n" +
        "(adj sq-15-14 up sq-15-13)\n" +
        "(adj sq-15-14 down sq-15-15)\n" +
        "(adj sq-15-15 left sq-14-15)\n" +
        "(adj sq-15-15 right sq-16-15)\n" +
        "(adj sq-15-15 up sq-15-14)\n" +
        "(adj sq-15-15 down sq-15-16)\n" +
        "(adj sq-15-16 left sq-14-16)\n" +
        "(adj sq-15-16 right sq-16-16)\n" +
        "(adj sq-15-16 up sq-15-15)\n" +
        "(adj sq-15-23 left sq-14-23)\n" +
        "(adj sq-15-23 right sq-16-23)\n" +
        "(adj sq-16-0 down sq-16-1)\n" +
        "(adj sq-16-1 left sq-15-1)\n" +
        "(adj sq-16-1 up sq-16-0)\n" +
        "(adj sq-16-1 down sq-16-2)\n" +
        "(adj sq-16-2 left sq-15-2)\n" +
        "(adj sq-16-2 up sq-16-1)\n" +
        "(adj sq-16-2 down sq-16-3)\n" +
        "(adj sq-16-3 left sq-15-3)\n" +
        "(adj sq-16-3 up sq-16-2)\n" +
        "(adj sq-16-3 down sq-16-4)\n" +
        "(adj sq-16-4 left sq-15-4)\n" +
        "(adj sq-16-4 up sq-16-3)\n" +
        "(adj sq-16-4 down sq-16-5)\n" +
        "(adj sq-16-5 left sq-15-5)\n" +
        "(adj sq-16-5 up sq-16-4)\n" +
        "(adj sq-16-5 down sq-16-6)\n" +
        "(adj sq-16-6 left sq-15-6)\n" +
        "(adj sq-16-6 right sq-17-6)\n" +
        "(adj sq-16-6 up sq-16-5)\n" +
        "(adj sq-16-6 down sq-16-7)\n" +
        "(adj sq-16-7 left sq-15-7)\n" +
        "(adj sq-16-7 right sq-17-7)\n" +
        "(adj sq-16-7 up sq-16-6)\n" +
        "(adj sq-16-7 down sq-16-8)\n" +
        "(adj sq-16-8 left sq-15-8)\n" +
        "(adj sq-16-8 right sq-17-8)\n" +
        "(adj sq-16-8 up sq-16-7)\n" +
        "(adj sq-16-15 left sq-15-15)\n" +
        "(adj sq-16-15 right sq-17-15)\n" +
        "(adj sq-16-15 down sq-16-16)\n" +
        "(adj sq-16-16 left sq-15-16)\n" +
        "(adj sq-16-16 right sq-17-16)\n" +
        "(adj sq-16-16 up sq-16-15)\n" +
        "(adj sq-16-16 down sq-16-17)\n" +
        "(adj sq-16-17 right sq-17-17)\n" +
        "(adj sq-16-17 up sq-16-16)\n" +
        "(adj sq-16-17 down sq-16-18)\n" +
        "(adj sq-16-18 right sq-17-18)\n" +
        "(adj sq-16-18 up sq-16-17)\n" +
        "(adj sq-16-18 down sq-16-19)\n" +
        "(adj sq-16-19 right sq-17-19)\n" +
        "(adj sq-16-19 up sq-16-18)\n" +
        "(adj sq-16-19 down sq-16-20)\n" +
        "(adj sq-16-20 right sq-17-20)\n" +
        "(adj sq-16-20 up sq-16-19)\n" +
        "(adj sq-16-20 down sq-16-21)\n" +
        "(adj sq-16-21 right sq-17-21)\n" +
        "(adj sq-16-21 up sq-16-20)\n" +
        "(adj sq-16-21 down sq-16-22)\n" +
        "(adj sq-16-22 right sq-17-22)\n" +
        "(adj sq-16-22 up sq-16-21)\n" +
        "(adj sq-16-22 down sq-16-23)\n" +
        "(adj sq-16-23 left sq-15-23)\n" +
        "(adj sq-16-23 up sq-16-22)\n" +
        "(adj sq-17-6 left sq-16-6)\n" +
        "(adj sq-17-6 right sq-18-6)\n" +
        "(adj sq-17-6 down sq-17-7)\n" +
        "(adj sq-17-7 left sq-16-7)\n" +
        "(adj sq-17-7 right sq-18-7)\n" +
        "(adj sq-17-7 up sq-17-6)\n" +
        "(adj sq-17-7 down sq-17-8)\n" +
        "(adj sq-17-8 left sq-16-8)\n" +
        "(adj sq-17-8 right sq-18-8)\n" +
        "(adj sq-17-8 up sq-17-7)\n" +
        "(adj sq-17-15 left sq-16-15)\n" +
        "(adj sq-17-15 right sq-18-15)\n" +
        "(adj sq-17-15 down sq-17-16)\n" +
        "(adj sq-17-16 left sq-16-16)\n" +
        "(adj sq-17-16 right sq-18-16)\n" +
        "(adj sq-17-16 up sq-17-15)\n" +
        "(adj sq-17-16 down sq-17-17)\n" +
        "(adj sq-17-17 left sq-16-17)\n" +
        "(adj sq-17-17 right sq-18-17)\n" +
        "(adj sq-17-17 up sq-17-16)\n" +
        "(adj sq-17-17 down sq-17-18)\n" +
        "(adj sq-17-18 left sq-16-18)\n" +
        "(adj sq-17-18 up sq-17-17)\n" +
        "(adj sq-17-18 down sq-17-19)\n" +
        "(adj sq-17-19 left sq-16-19)\n" +
        "(adj sq-17-19 up sq-17-18)\n" +
        "(adj sq-17-19 down sq-17-20)\n" +
        "(adj sq-17-20 left sq-16-20)\n" +
        "(adj sq-17-20 up sq-17-19)\n" +
        "(adj sq-17-20 down sq-17-21)\n" +
        "(adj sq-17-21 left sq-16-21)\n" +
        "(adj sq-17-21 up sq-17-20)\n" +
        "(adj sq-17-21 down sq-17-22)\n" +
        "(adj sq-17-22 left sq-16-22)\n" +
        "(adj sq-17-22 up sq-17-21)\n" +
        "(adj sq-18-6 left sq-17-6)\n" +
        "(adj sq-18-6 right sq-19-6)\n" +
        "(adj sq-18-6 down sq-18-7)\n" +
        "(adj sq-18-7 left sq-17-7)\n" +
        "(adj sq-18-7 right sq-19-7)\n" +
        "(adj sq-18-7 up sq-18-6)\n" +
        "(adj sq-18-7 down sq-18-8)\n" +
        "(adj sq-18-8 left sq-17-8)\n" +
        "(adj sq-18-8 right sq-19-8)\n" +
        "(adj sq-18-8 up sq-18-7)\n" +
        "(adj sq-18-15 left sq-17-15)\n" +
        "(adj sq-18-15 down sq-18-16)\n" +
        "(adj sq-18-16 left sq-17-16)\n" +
        "(adj sq-18-16 right sq-19-16)\n" +
        "(adj sq-18-16 up sq-18-15)\n" +
        "(adj sq-18-16 down sq-18-17)\n" +
        "(adj sq-18-17 left sq-17-17)\n" +
        "(adj sq-18-17 right sq-19-17)\n" +
        "(adj sq-18-17 up sq-18-16)\n" +
        "(adj sq-19-6 left sq-18-6)\n" +
        "(adj sq-19-6 right sq-20-6)\n" +
        "(adj sq-19-6 down sq-19-7)\n" +
        "(adj sq-19-7 left sq-18-7)\n" +
        "(adj sq-19-7 right sq-20-7)\n" +
        "(adj sq-19-7 up sq-19-6)\n" +
        "(adj sq-19-7 down sq-19-8)\n" +
        "(adj sq-19-8 left sq-18-8)\n" +
        "(adj sq-19-8 right sq-20-8)\n" +
        "(adj sq-19-8 up sq-19-7)\n" +
        "(adj sq-19-16 left sq-18-16)\n" +
        "(adj sq-19-16 right sq-20-16)\n" +
        "(adj sq-19-16 down sq-19-17)\n" +
        "(adj sq-19-17 left sq-18-17)\n" +
        "(adj sq-19-17 right sq-20-17)\n" +
        "(adj sq-19-17 up sq-19-16)\n" +
        "(adj sq-20-6 left sq-19-6)\n" +
        "(adj sq-20-6 right sq-21-6)\n" +
        "(adj sq-20-6 down sq-20-7)\n" +
        "(adj sq-20-7 left sq-19-7)\n" +
        "(adj sq-20-7 right sq-21-7)\n" +
        "(adj sq-20-7 up sq-20-6)\n" +
        "(adj sq-20-7 down sq-20-8)\n" +
        "(adj sq-20-8 left sq-19-8)\n" +
        "(adj sq-20-8 right sq-21-8)\n" +
        "(adj sq-20-8 up sq-20-7)\n" +
        "(adj sq-20-16 left sq-19-16)\n" +
        "(adj sq-20-16 right sq-21-16)\n" +
        "(adj sq-20-16 down sq-20-17)\n" +
        "(adj sq-20-17 left sq-19-17)\n" +
        "(adj sq-20-17 right sq-21-17)\n" +
        "(adj sq-20-17 up sq-20-16)\n" +
        "(adj sq-21-6 left sq-20-6)\n" +
        "(adj sq-21-6 right sq-22-6)\n" +
        "(adj sq-21-6 down sq-21-7)\n" +
        "(adj sq-21-7 left sq-20-7)\n" +
        "(adj sq-21-7 right sq-22-7)\n" +
        "(adj sq-21-7 up sq-21-6)\n" +
        "(adj sq-21-7 down sq-21-8)\n" +
        "(adj sq-21-8 left sq-20-8)\n" +
        "(adj sq-21-8 right sq-22-8)\n" +
        "(adj sq-21-8 up sq-21-7)\n" +
        "(adj sq-21-16 left sq-20-16)\n" +
        "(adj sq-21-16 right sq-22-16)\n" +
        "(adj sq-21-16 down sq-21-17)\n" +
        "(adj sq-21-17 left sq-20-17)\n" +
        "(adj sq-21-17 right sq-22-17)\n" +
        "(adj sq-21-17 up sq-21-16)\n" +
        "(adj sq-22-6 left sq-21-6)\n" +
        "(adj sq-22-6 down sq-22-7)\n" +
        "(adj sq-22-7 left sq-21-7)\n" +
        "(adj sq-22-7 right sq-23-7)\n" +
        "(adj sq-22-7 up sq-22-6)\n" +
        "(adj sq-22-7 down sq-22-8)\n" +
        "(adj sq-22-8 left sq-21-8)\n" +
        "(adj sq-22-8 up sq-22-7)\n" +
        "(adj sq-22-16 left sq-21-16)\n" +
        "(adj sq-22-16 down sq-22-17)\n" +
        "(adj sq-22-17 left sq-21-17)\n" +
        "(adj sq-22-17 right sq-23-17)\n" +
        "(adj sq-22-17 up sq-22-16)\n" +
        "(adj sq-23-7 left sq-22-7)\n" +
        "(adj sq-23-17 left sq-22-17)\n" +
        (startingRoom == null ? getRoomConnections(destination) : getRoomConnections(startingRoom, destination)) +
        ")\n" +
        "(:goal (standingAt " + destination.name() + ")" +
        ")\n" +
        ")\n";
  }

  private static String getRooms(AICard... destinations) {
    StringBuilder roomString = new StringBuilder();
    for (AICard room : destinations) {
      roomString.append(room.name()).append("\n");
    }
    return roomString.toString();
  }

  private static String getRoomConnections(AICard... destinations) {
    StringBuilder rooms = new StringBuilder();
    for (AICard room : destinations) {
      rooms.append(connections.get(room));
    }
    return rooms.toString();
  }

  public static TotalOrderPlan getPlan(long startTime) {
    UngroundProblem unground = PDDL21parser.getUngroundProblem();

    if (unground == null) {
      System.out.println("Parsing error - see console for details");
      System.exit(1);
    }

    GroundProblem ground = unground.ground();

    long afterGrounding = System.currentTimeMillis();

    // ********************************
    // Search for a plan
    // ********************************

    // Get the initial state
    TemporalMetricState initialState = ground.getTemporalMetricInitialState();

    State goalState = JavaFF.performFFSearch(initialState);

    long afterPlanning = System.currentTimeMillis();

    TotalOrderPlan top = null;
    if (goalState != null) {
      top = (TotalOrderPlan) goalState.getSolution();
    }
    //noinspection PointlessBooleanExpression,ConstantConditions
    if (top != null && DEBUG) {
      top.print(System.out);
    }

    double groundingTime = (afterGrounding - startTime) / 1000.00;
    double planningTime = (afterPlanning - afterGrounding) / 1000.00;

    System.out.println("Instantiation Time =\t\t" + groundingTime + "sec");
    System.out.println("Planning Time =\t" + planningTime + "sec");
    return top;
  }


}
