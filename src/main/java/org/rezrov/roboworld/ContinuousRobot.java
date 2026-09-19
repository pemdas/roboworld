package org.rezrov.roboworld;

import java.util.HashSet;

public class ContinuousRobot implements Robot {
   private Environment env;
   private DiscreteWorldPosition position;
   // private WorldPosition pose;
   // private WorldPosition targetPose = null;
   private boolean isCrashed = false;
   private PositionedWorldDrawable sprite = new WorldStaticSprite(Resources.ROBOT_SPRITE,
         Resources.ROBOT_SPRITE_CELL_SCALE);

   // Animation times for movements at 1x speed.
   private static double TURN_TIME = 0.6;
   private static double MOVE_TIME = 1.0;

   // The world time at which the most recent robot action completed.
   private double lastActionFinishedWorldTime;

   public ContinuousRobot(Environment env, DiscreteWorldPosition position) {
      this.env = env;
      this.position = new DiscreteWorldPosition(position);
      sprite.setPosition(position.asContinuous());
      assert env.isInBounds(position.x(), position.y());
      lastActionFinishedWorldTime = 0;
   }

   synchronized public PositionedWorldDrawable sprite() {
      return sprite;
   }

   // Wait until elapsed time has passed on the world time source.
   synchronized private void waitUntilWorldTime(double t) {
      // Only notify once. If we get an interrupted wake, but the time
      // hasn't yet passed our end time, there's still a notify that
      // should be coming our way.
      TimeSource.worldTimeSource().runAt(t, () -> lockedNotify());
      while (TimeSource.worldTimeSource().now() < t) {
         try {
            wait();
         } catch (InterruptedException e) {
            // Ignored
         }
      }
   }

   synchronized private void lockedNotify() {
      notify();
   }

   synchronized public DiscreteWorldPosition position() {
      return position;
   }

   private int numTurnLeftCalls = 0;

   // Returns the number of times turnLeft() has been called.
   synchronized public int numTurnLeftCalls() {
      return numTurnLeftCalls;
   }

   private HashSet<StackTraceElement> turnLeftCallSites = new HashSet<>();

   /**
    * Returns the total number of callsites from which turnLeft() was called. Note
    * this is different from the number of calls
    */
   synchronized public int numTurnLeftCallSites() {
      return turnLeftCallSites.size();
   }

   /** Turn left 90 degrees. If the robot has crashed, this doesn't do anything. */
   synchronized public void turnLeft() {
      ++numTurnLeftCalls;
      turnLeftCallSites.add(Thread.currentThread().getStackTrace()[2]);
      if (!isCrashed) {
         go(position.left(), TURN_TIME);
      }
   }

   private int numTurnRightCalls = 0;

   // Returns the number of times turnRight() has been called.
   synchronized public int numTurnRightCalls() {
      return numTurnRightCalls;
   }

   private HashSet<StackTraceElement> turnRightCallSites = new HashSet<>();

   /**
    * Returns the total number of callsites from which turnRight() was called. Note
    * this is different from the number of calls
    */
   synchronized public int numTurnRightCallSites() {
      return turnRightCallSites.size();
   }

   /** Turn right 90 degrees */
   synchronized public void turnRight() {
      ++numTurnRightCalls;
      turnRightCallSites.add(Thread.currentThread().getStackTrace()[2]);
      if (!isCrashed) {
         go(position.right(), TURN_TIME);
      }
   }

   private int numMoveForwardCalls = 0;

   // Returns the number of times moveForward() has been called.
   synchronized public int numMoveForwardCalls() {
      return numMoveForwardCalls;
   }

   private HashSet<StackTraceElement> moveForwardCallSites = new HashSet<>();

   /**
    * Returns the total number of callsites from which moveForward() was called.
    * Note this is different from the number of calls
    */
   synchronized public int numMoveForwardCallSites() {
      return moveForwardCallSites.size();
   }

   private void go(DiscreteWorldPosition nextPosition, double movementTime) {
      double worldTime = TimeSource.worldTimeSource().now();
      double actionEndTime = lastActionFinishedWorldTime + movementTime;
      if (worldTime >= actionEndTime) {
         // System.out.print("-");
         // World time is already past where this action should finish.
         sprite.setPosition(nextPosition.asContinuous());
      } else {
         // System.out.println();
         var start = position.asContinuous();
         var end = nextPosition.asContinuous();
         sprite.setPositionSource(new InterpolatingWorldPositionSource(start,
               end,
               lastActionFinishedWorldTime,
               movementTime,
               TimeSource.worldTimeSource(), InterpolatingWorldPositionSource.Strategy.LINEAR));
         waitUntilWorldTime(actionEndTime);
      }
      position = nextPosition;
      lastActionFinishedWorldTime = actionEndTime;
   }

   /**
    * Attempt to move forward one space. Returns true on success, false if the way
    * was blocked
    */
   synchronized public void moveForward() {
      ++numMoveForwardCalls;
      moveForwardCallSites.add(Thread.currentThread().getStackTrace()[2]);
      if (isCrashed) {
         return;
      }
      if (blocked()) {
         isCrashed = true;
         return;
      }
      go(position.forward(), MOVE_TIME);
   }

   /**
    * Check if the way forward is currently blocked.
    * 
    * @return true if the way is blocked, false otherwise.
    */
   public boolean blocked() {
      return env.isFacingWall(position);
   }

   /**
    * Check if the robot has crashed.
    * 
    * @return true if the robot has crashed, false otherwise.
    */
   public boolean crashed() {
      return isCrashed;
   }

}