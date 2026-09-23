package org.rezrov.roboworld;

import java.util.HashSet;

public class RobotImpl implements Robot {
   private Environment env;
   private DiscreteWorldPosition position;
   // private WorldPosition pose;
   // private WorldPosition targetPose = null;
   private boolean isCrashed = false;

   private RobotDisplayTarget displayTarget = null;
   private RobotStats stats = new RobotStats();

   // Animation times for movements at 1x speed.
   private static double TURN_TIME = 0.6;
   private static double MOVE_TIME = 1.0;

   public RobotImpl(Environment env, DiscreteWorldPosition position) {
      this.env = env;
      this.position = new DiscreteWorldPosition(position);
      assert env.isInBounds(position.x(), position.y());
   }

   /**
    * Convenience constructor for testing.
    */
   public RobotImpl(Environment env, DiscreteWorldPosition position, RobotDisplayTarget displayTarget) {
      this(env, position);
      setDisplayTarget(displayTarget);
   }

   // Robots get a two-phase construction to resolve some circular dependencies
   // in the scenario set up. We want to be able to construct the robot impl
   // before the window exists.
   //
   // If this is not called before the robot actually starts getting used, you'll
   // quickly crash.
   public void setDisplayTarget(RobotDisplayTarget displayTarget) {
      this.displayTarget = displayTarget;
   }

   synchronized public DiscreteWorldPosition position() {
      return position;
   }

   private HashSet<StackTraceElement> turnLeftCallSites = new HashSet<>();

   /** Turn left 90 degrees. If the robot has crashed, this doesn't do anything. */
   public void turnLeft() {
      ContinuousWorldPosition startPos, endPos;
      synchronized (this) {
         if (isCrashed) {
            return;
         }
         ++stats.numTurnsLeft;
         if (turnLeftCallSites.add(Thread.currentThread().getStackTrace()[2])) {
            ++stats.numTurnLeftCallSites;
         }
         displayTarget.updateRobotStats(stats);
         startPos = position.asContinuous();
         position = position.left();
         endPos = position.asContinuous();
      }
      moveDisplayed(startPos, endPos, TURN_TIME);
   }

   private HashSet<StackTraceElement> turnRightCallSites = new HashSet<>();

   /** Turn right 90 degrees */
   public void turnRight() {
      ContinuousWorldPosition startPos, endPos;
      synchronized (this) {
         if (isCrashed) {
            return;
         }
         ++stats.numTurnsRight;
         if (turnRightCallSites.add(Thread.currentThread().getStackTrace()[2])) {
            ++stats.numTurnRightCallSites;
         }
         displayTarget.updateRobotStats(stats);
         startPos = position.asContinuous();
         position = position.right();
         endPos = position.asContinuous();
      }
      moveDisplayed(startPos, endPos, TURN_TIME);
   }

   private HashSet<StackTraceElement> moveForwardCallSites = new HashSet<>();

   // Invoke with care. The application thread may be suspended while the
   // display is updated, and the display may want to grab the position of the
   // robot while the application thread is suspended. This, we should *never*
   // call this while locked, since we could cause a deadlock.
   private void moveDisplayed(ContinuousWorldPosition curPosition, ContinuousWorldPosition nextPosition,
         double movementTime) {
      assert !Thread.holdsLock(this);
      displayTarget.moveRobot(curPosition, nextPosition, movementTime);
   }

   public void moveForward() {
      ContinuousWorldPosition startPos, endPos;
      synchronized (this) {
         if (blocked()) {
            isCrashed = true;
         }
         if (isCrashed) {
            return;
         }
         ++stats.numMovesForward;
         if (moveForwardCallSites.add(Thread.currentThread().getStackTrace()[2])) {
            ++stats.numMoveForwardCallSites;
         }
         displayTarget.updateRobotStats(stats);
         startPos = position.asContinuous();
         position = position.forward();
         endPos = position.asContinuous();
      }
      moveDisplayed(startPos, endPos, MOVE_TIME);
   }

   /**
    * Check if the way forward is currently blocked.
    * 
    * @return true if the way is blocked, false otherwise.
    */
   public synchronized boolean blocked() {
      return env.isFacingWall(position);
   }

   /**
    * Check if the robot has crashed.
    * 
    * @return true if the robot has crashed, false otherwise.
    */
   public synchronized boolean crashed() {
      return isCrashed;
   }
}