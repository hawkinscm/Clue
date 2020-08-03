package hawkinscm.clue.ai;

import java.util.List;

/**
 * This class...
 *
 * @author Aaron Kay
 */
public class Plan implements Comparable<Plan> {
  private AICard destination;
  private List<Step> steps;

  public Plan(AICard destination, List<Step> steps) {
    this.destination = destination;
    this.steps = steps;
  }

  public AICard getDestination() {
    return destination;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public int size() {
    return steps.size();
  }

  @Override
  public int compareTo(Plan plan) {
    return this.size() - plan.size();
  }
}
