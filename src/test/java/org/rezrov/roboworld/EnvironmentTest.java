package org.rezrov.roboworld;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EnvironmentTest {

   @Test
   public void testImplicitWalls() {
      // Create an empty world, check that the exterior walls exist.
      // @formatter:off
      // +---+---+
      // |       |
      // +   +   +
      // |       |
      // +---+---+
      // @formatter:on
      Environment w = new Environment(2, 2);
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.UP)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.LEFT)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.DOWN)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.RIGHT)));

      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.UP)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.LEFT)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.DOWN)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.RIGHT)));

      assertFalse(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.UP)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.LEFT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.DOWN)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.RIGHT)));

      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.UP)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.LEFT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.DOWN)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.RIGHT)));
   }

   @Test
   public void testVerticalWalls() {
      // @formatter:off
      // +---+---+
      // |       |
      // +   +   +
      // |   |   |
      // +   +   +
      // |   |   |
      // +---+---+
      // @formatter:on
      Environment w = new Environment(2, 3);
      w.addWall(new DiscreteWorldPosition(0, 1, Direction.RIGHT));
      w.addWall(new DiscreteWorldPosition(1, 2, Direction.LEFT));
      w.addWall(new DiscreteWorldPosition(1, 1, Direction.LEFT)); // nop, wall exists
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.RIGHT)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.LEFT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.RIGHT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.LEFT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 2, Direction.RIGHT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 2, Direction.LEFT)));
   }

   @Test
   public void testHorzontalWalls() {
      // @formatter:off
      // +---+---+---+
      // |           |
      // +---+   +---+
      // |           |
      // +---+---+---+
      // @formatter:on
      Environment w = new Environment(3, 2);
      w.addWall(new DiscreteWorldPosition(0, 1, Direction.UP));
      w.addWall(new DiscreteWorldPosition(0, 0, Direction.DOWN)); // nop, wall exists
      w.addWall(new DiscreteWorldPosition(2, 0, Direction.DOWN));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.DOWN)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.DOWN)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(2, 0, Direction.DOWN)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.UP)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.UP)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(2, 1, Direction.UP)));
   }

}
