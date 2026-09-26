package org.rezrov.roboworld;

/**
 * Interface for a display target for a robot. This allows us to isolate
 * the robot from the display, both for testing, and so we can substitute a
 * console display target when we don't want to have a GUI be shown (e.g.
 * automated grading)
 * 
 * Note that all of these hooks are expected to be called from the application
 * thread; the display is responsible for doing appropriate synchronization.
 */
public interface RobotDisplayTarget {
   public void moveRobot(ContinuousWorldPosition from, ContinuousWorldPosition to, double movementTime);

   public void setRobotCarriedItem(Item item);

   // Notify that the stats associated with the robot have changed.
   public void robotStatsChanged();

   public void setRobotDone();
}
