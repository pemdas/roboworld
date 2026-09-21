package org.rezrov.roboworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ContinuousRobotTest {
   private Environment env;

   public ContinuousRobotTest() {
      env = new Environment(2, 2);
      env.addWall(new DiscreteWorldPosition(0, 0, Direction.RIGHT));
   }

   @Test
   public void turnLeft() {
      ContinuousRobot r = new ContinuousRobot(env, new DiscreteWorldPosition(0, 1, Direction.RIGHT));
      r.setWarpSpeed(true);
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
      ContinuousRobot r = new ContinuousRobot(env, new DiscreteWorldPosition(1, 0, Direction.DOWN));
      r.setWarpSpeed(true);
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

      ContinuousRobot r = new ContinuousRobot(env, new DiscreteWorldPosition(0, 0, Direction.RIGHT));
      r.setWarpSpeed(true);
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
      ContinuousRobot r = new ContinuousRobot(env, new DiscreteWorldPosition(0, 0, Direction.RIGHT));
      r.setWarpSpeed(true);
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
