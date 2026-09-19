package org.rezrov.roboworld;

/**
 * Position of something in the world in discrete space (e.g. exactly in one
 * cell, facing precisely N, S, E, or W).
 */
public class DiscreteWorldPosition {
   // Could consider making this class immutable after construction?
   private int x;
   private int y;
   private Direction direction;

   public int x() {
      return x;
   }

   public int y() {
      return y;
   }

   public Direction direction() {
      return direction;
   }

   public void setX(int x) {
      this.x = x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public void setDirection(Direction direction) {
      this.direction = direction;
   }

   public DiscreteWorldPosition(DiscreteWorldPosition other) {
      copyFrom(other);
   }

   public DiscreteWorldPosition(int x, int y, Direction direction) {
      this.x = x;
      this.y = y;
      this.direction = direction;
   }

   public void copyFrom(DiscreteWorldPosition other) {
      x = other.x;
      y = other.y;
      direction = other.direction;
   }

   /**
    * Return the resulting position if we move forward from the current position.
    */
   public DiscreteWorldPosition forward() {
      DiscreteWorldPosition ret = new DiscreteWorldPosition(this);
      switch (direction) {
         case UP:
            ret.y--;
            break;
         case RIGHT:
            ret.x++;
            break;
         case DOWN:
            ret.y++;
            break;
         case LEFT:
            ret.x--;
            break;
      }
      return ret;
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

}
