package org.rezrov.roboworld;

/**
 * Interface for a display target for a robot. This mostly exists right now to
 * provide a dependency injection point for unit testing the robot.
 *
 * Note that all of these hooks are expected to be called from the application
 * thread the display is responsible for doing appropriate synchronization.
 */
public interface RobotDisplayTarget {
   public void moveRobot(ContinuousWorldPosition from, ContinuousWorldPosition to, double movementTime);

   public void setRobotCarriedItem(Item item);

   public void updateRobotStats(RobotStats stats);

   public void setRobotDone();
}
