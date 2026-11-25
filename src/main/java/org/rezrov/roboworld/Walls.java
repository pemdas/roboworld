package org.rezrov.roboworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class Walls {
   private boolean[][] rightWalls;
   private boolean[][] bottomWalls;

   /**
    * Create a new empty walls. Only the implicit boundary wall will exist.
    * 
    * Add walls using setWall()
    */
   public Walls(int width, int height) {
      rightWalls = new boolean[width - 1][height];
      bottomWalls = new boolean[width][height - 1];
   }

   /** 
    * Create walls from ASCII art.  The ASCII art looks like this:
    * 
    * @formatter:off
    * +-+-+-+-+
    * | |     |
    * + + + + +
    * |     | |
    * +-+ + + +
    * |   |   |
    * +-+-+-+-+
    *
    * @formatter:on
    * 
    * That would create a map which is width 3, height 2, and has 4 interior walls.  Every 'corner' space must be a plus,
    * even if no walls attached at that corner, and the exterior rectangle must always have walls. 
    * 
    * The parser trims whitespace at the start and end of lines, as well as any whitespace at the start or end of input.
    */
   public Walls(String walls)
         throws WallsParseException {
      String[] lines = walls.trim().split("\\s*\n\\s*");
      validateAsciiArtMap(lines);
      int width = lines[0].length() / 2;
      int height = lines.length / 2;
      rightWalls = new boolean[width - 1][height];
      bottomWalls = new boolean[width][height - 1];
      for (int y = 0; y < height; y++) {
         String line = lines[2 * y + 1];
         for (int x = 0; x < width - 1; x++) {
            if (line.charAt(2 * x + 2) == '|') {
               rightWalls[x][y] = true;
            }
         }
      }

      for (int y = 0; y < height - 1; y++) {
         String line = lines[2 * y + 2];
         for (int x = 0; x < width; x++) {
            if (line.charAt(2 * x + 1) == '-') {
               bottomWalls[x][y] = true;
            }
         }
      }
   }

   // Create a random, fully connected maze using randomized Prim's algorithm.
   //
   // This isn't terribly optimized, but should run plenty fast for the size of
   // mazes we're generating.
   static Walls createRandomMaze(int width, int height, Random r) {
      Walls ret = new Walls(width, height);
      for (var row : ret.rightWalls) {
         Arrays.fill(row, true);
      }
      for (var col : ret.bottomWalls) {
         Arrays.fill(col, true);
      }
      // Cells that are already "in" the maze. Start with one random cell.
      HashSet<Coord2D> connected = new HashSet<Coord2D>();

      connected.add(new Coord2D(r.nextInt(width), r.nextInt(height)));

      var candidateWalls = new ArrayList<DiscreteWorldPosition>();
      while (connected.size() < width * height) {
         candidateWalls.clear();
         // Gather current candidate walls to be removed
         for (Coord2D c : connected) {
            if (c.x > 0 && !connected.contains(c.left())) {
               candidateWalls.add(new DiscreteWorldPosition(c.x, c.y, Direction.LEFT));
            }
            if (c.x < width - 1 && !connected.contains(c.right())) {
               candidateWalls.add(new DiscreteWorldPosition(c.x, c.y, Direction.RIGHT));
            }
            if (c.y > 0 && !connected.contains(c.up())) {
               candidateWalls.add(new DiscreteWorldPosition(c.x, c.y, Direction.UP));
            }
            if (c.y < height - 1 && !connected.contains(c.down())) {
               candidateWalls.add(new DiscreteWorldPosition(c.x, c.y, Direction.DOWN));
            }
         }
         DiscreteWorldPosition toRemove = candidateWalls.get(r.nextInt(candidateWalls.size()));
         ret.setWall(toRemove, false);
         connected.add(toRemove.forward().asCoord2D());
      }
      return ret;
   }

   static Walls createRandomMaze(int width, int height) {
      return createRandomMaze(width, height, new Random());
   }

   // Validation regexps.
   private static final String TOP_BOTTOM_REGEX = "\\+-\\+(-\\+)*";
   // 'odd' and 'even' here are based on the 0-based line index.
   private static final String ODD_LINE_REGEX = "\\|( [\\| ])* \\|";
   private static final String EVEN_LINE_REGEX = "\\+([- ]\\+)+";

   // Validate an ascii art map that has been split into lines for us. If this
   // returns without throwing an exception, the map is well formed.
   private void validateAsciiArtMap(String[] lines) throws WallsParseException {
      // Do basic checking first.
      if (lines.length < 3) {
         throw new WallsParseException("Insufficient lines");
      }
      if (lines.length % 2 == 0) {
         throw new WallsParseException("Number of lines must be odd");
      }
      if (!lines[0].matches(TOP_BOTTOM_REGEX)) {
         throw new WallsParseException("Malformed line 1");
      }
      int firstLineLength = lines[0].length();
      for (int i = 1; i < lines.length - 1; i++) {
         if (lines[i].length() != firstLineLength ||
               !lines[i].matches((i % 2 == 0) ? EVEN_LINE_REGEX : ODD_LINE_REGEX)) {
            throw new WallsParseException("Malformed line " + (i + 1));
         }
      }
      if (lines[lines.length - 1].length() != firstLineLength ||
            !lines[lines.length - 1].matches(TOP_BOTTOM_REGEX)) {
         throw new WallsParseException("Malformed line " + (lines.length));
      }
   }

   // Valid robot coordinates range from 0 to width-1
   public int width() {
      return bottomWalls.length;
   }

   // Valid robot coordinates range from 0 to height-1
   public int height() {
      return rightWalls[0].length;
   }

   private boolean isInBounds(int x, int y) {
      return x >= 0 && y >= 0 && x < width() && y < height();
   }

   public boolean isFacingWall(DiscreteWorldPosition pos) {
      assert isInBounds(pos.x(), pos.y());
      // Take care of the implicit boundary walls first.
      switch (pos.direction()) {
         case UP:
            return pos.y() == 0 || bottomWalls[pos.x()][pos.y() - 1];
         case LEFT:
            return pos.x() == 0 || rightWalls[pos.x() - 1][pos.y()];
         case DOWN:
            return pos.y() == height() - 1 || bottomWalls[pos.x()][pos.y()];
         case RIGHT:
            return pos.x() == width() - 1 || rightWalls[pos.x()][pos.y()];
         default:
            throw new AssertionError("Bad direction");
      }
   }

   /**
    * Set the wall referred to by pos to exist or not exist.
    * 
    * It is an error to try to remove a boundary wall (or an out of bounds wall)
    * 
    * Either case will show up as an index out of bounds error.
    */
   public void setWall(DiscreteWorldPosition pos, boolean state) {
      switch (pos.direction()) {
         case UP:
            bottomWalls[pos.x()][pos.y() - 1] = state;
            break;
         case DOWN:
            bottomWalls[pos.x()][pos.y()] = state;
            break;
         case LEFT:
            rightWalls[pos.x() - 1][pos.y()] = state;
            break;
         case RIGHT:
            rightWalls[pos.x()][pos.y()] = state;
            break;
         default:
            throw new AssertionError("Bad direction");
      }
   }

}

class WallsParseException extends RuntimeException {
   public WallsParseException(String msg) {
      super(msg);
   }
}
