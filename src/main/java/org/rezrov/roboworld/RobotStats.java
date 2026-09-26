package org.rezrov.roboworld;

/**
 * Statistics tracked by the robot. This class is thread-safe.
 */
public class RobotStats {
   public enum Id {
      FORWARD_MOVES(0, "Forward Moves"),
      TURNS_LEFT(1, "Left Turns"),
      TURNS_RIGHT(2, "Right Turns"),
      MOVE_FORWARD_CALLSITES(3, "Move Forward Callsites"),
      TURN_LEFT_CALLSITES(4, "Left Turn Callsites"),
      TURN_RIGHT_CALLSITES(5, "Right Turn Callsites");

      private int idx;
      private String name;

      private Id(int idx, String name) {
         this.idx = idx;
         this.name = name;
      }

      @Override
      public String toString() {
         return name;
      }
   }

   public static int numStats() {
      return Id.values().length;
   }

   private int[] stats = new int[numStats()];

   synchronized public void add(Id id, int count) {
      stats[id.idx] += count;
   }

   synchronized public int get(Id id) {
      return stats[id.idx];
   }

   // For iterating over with an int.
   synchronized public int get(int id) {
      return stats[id];
   }

}
