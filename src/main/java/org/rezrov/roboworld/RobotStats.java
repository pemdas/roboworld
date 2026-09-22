package org.rezrov.roboworld;

/**
 * Statistics tracked by the robot. This class is thread-safe.
 */
public class RobotStats {
   public int numMovesForward = 0;
   public int numTurnsLeft = 0;
   public int numTurnsRight = 0;
   public int numMoveForwardCallSites = 0;
   public int numTurnLeftCallSites = 0;
   public int numTurnRightCallSites = 0;

   // Default constructor
   public RobotStats() {
   }

   // Copy constructor.
   public RobotStats(RobotStats other) {
      numMovesForward = other.numMovesForward;
      numTurnsLeft = other.numTurnsLeft;
      numTurnsRight = other.numTurnsRight;
      numMoveForwardCallSites = other.numMoveForwardCallSites;
      numTurnLeftCallSites = other.numTurnLeftCallSites;
      numTurnRightCallSites = other.numTurnRightCallSites;
   }
}
