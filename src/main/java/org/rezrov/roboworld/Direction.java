package org.rezrov.roboworld;

public enum Direction {
   // These values are chosen to make asHeading() trivial.
   RIGHT(0), DOWN(1), LEFT(2), UP(3);

   private final int direction;

   private Direction(int val) {
      this.direction = val;
   }

   public Direction left() {
      return values()[(direction + 3) % 4];
   }

   public Direction right() {
      return values()[(direction + 1) % 4];
   }

   public Direction opposite() {
      return values()[(direction + 2) % 4];
   }

   // Convert to a WorldPosition-style heading.
   public double asHeading() {
      return direction * Math.PI / 2;
   }

}
