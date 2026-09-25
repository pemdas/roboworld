package org.rezrov.roboworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RobotImplTest {
   static class FakeRobotDisplayTarget
         implements RobotDisplayTarget {
      public void updateRobotStats(RobotStats stats) {
      }

      public void moveRobot(ContinuousWorldPosition start, ContinuousWorldPosition end, double movementTime) {
      }

      public void setRobotCarriedItem(Item item) {
      }

      public void setRobotDone() {
      }
   }

   // All tests in here use this maze.
   private RobotImpl createRobot(DiscreteWorldPosition startingPosition) {
      RobotImpl r = new World("" +
            "+-+-+\n" +
            "| | |\n" +
            "+ + +\n" +
            "|   |\n" +
            "+-+-+\n", startingPosition).robot();
      r.setDisplayTarget(new FakeRobotDisplayTarget());
      return r;
   }

   @Test
   public void turnLeft() {
      RobotImpl r = createRobot(new DiscreteWorldPosition(0, 1, Direction.RIGHT));
      assertEquals(r.position(), new DiscreteWorldPosition(0, 1, Direction.RIGHT));
      r.turnLeft();
      assertEquals(r.position(), new DiscreteWorldPosition(0, 1, Direction.UP));
      r.turnLeft();
      assertEquals(r.position(), new DiscreteWorldPosition(0, 1, Direction.LEFT));
      r.turnLeft();
      assertEquals(r.position(), new DiscreteWorldPosition(0, 1, Direction.DOWN));
      r.turnLeft();
      assertEquals(r.position(), new DiscreteWorldPosition(0, 1, Direction.RIGHT));
   }

   @Test
   public void turnRight() {
      RobotImpl r = createRobot(new DiscreteWorldPosition(1, 0, Direction.DOWN));
      assertEquals(r.position(), new DiscreteWorldPosition(1, 0, Direction.DOWN));
      r.turnRight();
      assertEquals(r.position(), new DiscreteWorldPosition(1, 0, Direction.LEFT));
      r.turnRight();
      assertEquals(r.position(), new DiscreteWorldPosition(1, 0, Direction.UP));
      r.turnRight();
      assertEquals(r.position(), new DiscreteWorldPosition(1, 0, Direction.RIGHT));
      r.turnRight();
      assertEquals(r.position(), new DiscreteWorldPosition(1, 0, Direction.DOWN));
   }

   @Test
   public void movement() {
      // Starting state:
      // @formatter:off
      // +---+---+
      // |R->|   |
      // +   +   +
      // |       |
      // +---+---+
      // @formatter:on
      RobotImpl r = createRobot(new DiscreteWorldPosition(0, 0, Direction.RIGHT));
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.RIGHT), r.position());
      r.turnLeft();
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.UP), r.position());
      r.turnLeft();
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.LEFT), r.position());
      r.turnLeft();
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.DOWN), r.position());
      r.moveForward();
      assertEquals(new DiscreteWorldPosition(0, 1, Direction.DOWN), r.position());
      r.turnRight();
      assertEquals(new DiscreteWorldPosition(0, 1, Direction.LEFT), r.position());
      r.turnRight();
      r.turnRight();
      r.moveForward();
      assertEquals(new DiscreteWorldPosition(1, 1, Direction.RIGHT), r.position());
      r.turnLeft();
      r.moveForward();
      assertEquals(new DiscreteWorldPosition(1, 0, Direction.UP), r.position());
      r.turnLeft();
      assertEquals(new DiscreteWorldPosition(1, 0, Direction.LEFT), r.position());
   }

   @Test
   public void crash() {
      RobotImpl r = createRobot(new DiscreteWorldPosition(0, 0, Direction.RIGHT));

      assertFalse(r.crashed());
      r.moveForward();
      assertTrue(r.crashed());
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.RIGHT), r.position());

      // Turns and moves should do nothing when we've crashed.
      r.turnLeft();
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.RIGHT), r.position());
      r.turnRight();
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.RIGHT), r.position());

      // This is kind of silly, since we're still facing a wall, but should still be a
      // NOP.
      r.moveForward();
      assertEquals(new DiscreteWorldPosition(0, 0, Direction.RIGHT), r.position());

   }

}
