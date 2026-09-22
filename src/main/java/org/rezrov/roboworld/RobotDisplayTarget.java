package org.rezrov.roboworld;

/**
 * Interface for a display target for a robot. This mostly exists right now to
 * provide
 * a dependency injection point for unit testing the robot.
 */
public interface RobotDisplayTarget {
   public void moveRobot(ContinuousWorldPosition from, ContinuousWorldPosition to, double movementTime);

   public void updateRobotStats(RobotStats stats);
}
