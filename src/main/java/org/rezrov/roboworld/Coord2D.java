package org.rezrov.roboworld;

// 2d integer coordinates.
public class Coord2D
      implements Comparable<Coord2D> {
   public int x;
   public int y;

   public Coord2D(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public Coord2D() {
      this.x = 0;
      this.y = 0;
   }

   public Coord2D(Coord2D other) {
      this.x = other.x;
      this.y = other.y;
   }

   @Override
   public int hashCode() {
      return Long.hashCode(((long) x << 32) | y);
   }

   @Override
   public boolean equals(Object o) {
      Coord2D c = (Coord2D) o;
      return x == c.x && y == c.y;
   }

   public int compareTo(Coord2D other) {
      if (x != other.x) {
         return Integer.compare(x, other.x);
      } else {
         return Integer.compare(y, other.y);
      }
   }

   public ContinuousWorldPosition asContinuous() {
      return new ContinuousWorldPosition(x, y, 0);
   }

   // Get neighbor coordinates.
   Coord2D left() {
      return new Coord2D(x - 1, y);
   }

   Coord2D right() {
      return new Coord2D(x + 1, y);
   }

   Coord2D up() {
      return new Coord2D(x, y - 1);
   }

   Coord2D down() {
      return new Coord2D(x, y + 1);
   }

   @Override
   public String toString() {
      return "Coord (" + x + ", " + y + ")";
   }

}
