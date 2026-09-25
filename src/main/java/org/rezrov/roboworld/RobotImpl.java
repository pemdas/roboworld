package org.rezrov.roboworld;

import java.util.HashSet;

public class RobotImpl implements Robot {
   private World env;
   private RobotDisplayTarget displayTarget = null;

   // Access to these fields may happen from multipke threads, and so
   // must be done under the (object) lock. Also, no thread should sleep
   // holding the lock.
   private DiscreteWorldPosition position;
   private boolean isCrashed = false;
   private RobotStats stats = new RobotStats();

   private Item itemInHand = Item.NONE;

   // Animation times for movements at 1x speed.
   private static double TURN_TIME = 0.6;
   private static double MOVE_TIME = 1.0;

   public RobotImpl(World env, DiscreteWorldPosition position) {
      this.env = env;
      this.position = new DiscreteWorldPosition(position);
   }

   /**
    * Convenience constructor for testing.
    */
   public RobotImpl(World env, DiscreteWorldPosition position, RobotDisplayTarget displayTarget) {
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

   // Functionality common to all the movement methods. Determine the
   // starting and ending positions in continuous space (under lock),
   // drop the lock to actually do the displayed move, then update the position.
   private void moveCommon(DiscreteWorldPosition nextPosition, double movementTime) {
      // This method may put thread to sleep, so can't hold the lock when entering.
      assert !Thread.holdsLock(this);
      ContinuousWorldPosition startPos, endPos;
      synchronized (this) {
         startPos = position.asContinuous();
         endPos = nextPosition.asContinuous();
      }
      displayTarget.moveRobot(startPos, endPos, movementTime);
      synchronized (this) {
         position = nextPosition;
      }
   }

   private HashSet<StackTraceElement> turnLeftCallSites = new HashSet<>();

   /** Turn left 90 degrees. If the robot has crashed, this doesn't do anything. */
   public void turnLeft() {
      if (crashed())
         return;
      // Use position accessor to access under lock
      moveCommon(position().left(), TURN_TIME);
      synchronized (this) {
         if (turnLeftCallSites.add(Thread.currentThread().getStackTrace()[2])) {
            ++stats.numTurnLeftCallSites;
         }
         displayTarget.updateRobotStats(stats);
      }
   }

   private HashSet<StackTraceElement> turnRightCallSites = new HashSet<>();

   /** Turn right 90 degrees */
   public void turnRight() {
      // Use position accessor to access under lock
      if (crashed())
         return;
      moveCommon(position().right(), TURN_TIME);
      synchronized (this) {
         if (turnRightCallSites.add(Thread.currentThread().getStackTrace()[2])) {
            ++stats.numTurnRightCallSites;
         }
         displayTarget.updateRobotStats(stats);
      }
   }

   private HashSet<StackTraceElement> moveForwardCallSites = new HashSet<>();

   public void moveForward() {
      synchronized (this) {
         if (blocked()) {
            isCrashed = true;
         }
      }
      if (crashed()) {
         return;
      }
      moveCommon(position.forward(), MOVE_TIME);
      synchronized (this) {
         ++stats.numMovesForward;
         if (moveForwardCallSites.add(Thread.currentThread().getStackTrace()[2])) {
            ++stats.numMoveForwardCallSites;
         }
         displayTarget.updateRobotStats(stats);
      }
   }

   /**
    * Check if the way forward is currently blocked.
    * 
    * @return true if the way is blocked, false otherwise.
    */
   public boolean blocked() {
      return env.map().isFacingWall(position());
   }

   /**
    * Check if the robot has crashed.
    * 
    * @return true if the robot has crashed, false otherwise.
    */
   public synchronized boolean crashed() {
      return isCrashed;
   }

   public synchronized Item carriedItem() {
      return itemInHand;
   }

   synchronized public Item itemOnGround() {
      return env.itemAt(position.asCoord2D());
   }

   synchronized public void grab() {
      if (isCrashed) {
         return;
      }
      if (itemInHand != Item.NONE) {
         isCrashed = true;
      } else {
         itemInHand = env.takeItem(position.asCoord2D());
         if (itemInHand == Item.NONE) {
            isCrashed = true;
         } else {
            displayTarget.setRobotCarriedItem(itemInHand);
         }
      }
   }

   synchronized public void drop() {
      if (isCrashed) {
         return;
      }
      if (itemInHand == Item.NONE || !env.putItem(position.asCoord2D(), itemInHand)) {
         isCrashed = true;
      } else {
         // Successfully dropped.
         itemInHand = Item.NONE;
         displayTarget.setRobotCarriedItem(Item.NONE);
      }
   }

}