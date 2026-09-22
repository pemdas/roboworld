package org.rezrov.roboworld;

/**
 * Position of something in the world in discrete space (e.g. exactly in one
 * cell, facing precisely N, S, E, or W).
 */
public class DiscreteWorldPosition {
   private final int x;
   private final int y;
   private final Direction direction;

   public int x() {
      return x;
   }

   public int y() {
      return y;
   }

   public Direction direction() {
      return direction;
   }

   public DiscreteWorldPosition(DiscreteWorldPosition other) {
      x = other.x;
      y = other.y;
      direction = other.direction;
   }

   public DiscreteWorldPosition(int x, int y, Direction direction) {
      this.x = x;
      this.y = y;
      this.direction = direction;
   }

   /**
    * Return the resulting position if we move forward from the current position.
    */
   public DiscreteWorldPosition forward() {
      switch (direction) {
         case UP:
            return new DiscreteWorldPosition(x, y - 1, direction);
         case RIGHT:
            return new DiscreteWorldPosition(x + 1, y, direction);
         case DOWN:
            return new DiscreteWorldPosition(x, y + 1, direction);
         case LEFT:
            return new DiscreteWorldPosition(x - 1, y, direction);
         default:
            throw new AssertionError("Bad direction");
      }
   }

   /**
    * Return the resulting position if we turn left from the current position.
    */
   public DiscreteWorldPosition left() {
      return new DiscreteWorldPosition(x, y, direction.left());
   }

   /**
    * Return the resulting position if we turn right from the current position.
    */
   public DiscreteWorldPosition right() {
      return new DiscreteWorldPosition(x, y, direction.right());
   }

   /**
    * Return the equivalent ContinuousWorldPosition.
    */
   public ContinuousWorldPosition asContinuous() {
      return new ContinuousWorldPosition(x, y, direction.asHeading());
   }

   public Coord2D asCoord2D() {
      return new Coord2D(x, y);
   }

   @Override
   public String toString() {
      return "(" + x + " " + y + " " + direction + ")";
   }

   @Override
   public boolean equals(Object rhs) {
      if (!(rhs instanceof DiscreteWorldPosition)) {
         return false;
      }
      var o = (DiscreteWorldPosition) rhs;
      return x == o.x && y == o.y && direction == o.direction;
   }
}
