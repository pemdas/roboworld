package org.rezrov.roboworld;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WorldMapTest {

   @Test
   public void testImplicitWalls() {
      // Create an empty world, check that the exterior walls exist.
      WorldMap w = new WorldMap("" +
            "+-+-+\n" +
            "|   |\n" +
            "+ + +\n" +
            "|   |\n" +
            "+-+-+\n");
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
      // Create a world with some vertical interior walls, check that they exist.
      // @formatter:off
      // +---+---+
      // |       |
      // +   +   +
      // |   |   |
      // +   +   +
      // |   |   |
      // +---+---+
      // @formatter:on
      WorldMap w = new WorldMap("" +
            "+-+-+\n" +
            "|   |\n" +
            "+ + +\n" +
            "| | |\n" +
            "+ + +\n" +
            "| | |\n" +
            "+-+-+\n");
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.RIGHT)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.LEFT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.RIGHT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.LEFT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 2, Direction.RIGHT)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(1, 2, Direction.LEFT)));
   }

   @Test
   public void testHorzontalWalls() {
      // Create a world with some horizontal interior walls, check that they exist.
      WorldMap w = new WorldMap("" +
            "+-+-+-+\n" +
            "|     |\n" +
            "+-+ +-+\n" +
            "|     |\n" +
            "+-+-+-+\n");
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 0, Direction.DOWN)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 0, Direction.DOWN)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(2, 0, Direction.DOWN)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(0, 1, Direction.UP)));
      assertFalse(w.isFacingWall(new DiscreteWorldPosition(1, 1, Direction.UP)));
      assertTrue(w.isFacingWall(new DiscreteWorldPosition(2, 1, Direction.UP)));
   }

}
