package org.rezrov.roboworld;

/**
 * This is the no-GUI display target. It prints actions to the console as
 * the happen, and does not block the application thread.
 */
public class ConsoleRobotDisplayTarget implements RobotDisplayTarget {
   private Scenario scenario;
   // private RobotStats stats;

   public ConsoleRobotDisplayTarget(Scenario scenario) {
      this.scenario = scenario;
   }

   public void moveRobot(ContinuousWorldPosition from, ContinuousWorldPosition to, double movementTime) {
      System.out.println("Move to " + to);
   }

   public void setRobotCarriedItem(Item item) {
      if (item == Item.NONE) {
         System.out.println("Drop item");
      } else {
         System.out.println("Grab " + item);
      }
   }

   public void updateRobotStats(RobotStats stats) {
      // this.stats = stats;
   }

   public void setRobotDone() {
      System.out.println("Final goal states: ");
      for (Goal g : scenario.goals()) {
         System.out.println("  " + g.description() + ": " + (g.goalSatisfied() ? "SUCCESS" : "FAIL"));
      }
   }
}
