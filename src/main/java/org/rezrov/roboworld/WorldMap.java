package org.rezrov.roboworld;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents everything in a World which never changes. This
 * includes the walls, the robot goal cells, and the item goal cells.
 *
 * This class is immutable after construction. Since the world map is
 * constructed before the robot does anything, access to the contained data does
 * not need to be synchronized.
 */
public class WorldMap {

   // Every map implicitly has walls all round the outside edges (e.g. every
   // cell with x=0 implicitly has a wall to the left)
   //
   // This means we just need to represent interior walls. The convention here is
   // to represent bottom and right walls based on cell coordinates. If you need
   // to check a top or left wall, adjust your coordinates. True means a wall is
   // present.
   private boolean[][] rightWalls;
   private boolean[][] bottomWalls;

   // Cells which are goals for a kind of item.
   private Map<Coord2D, Item> itemGoalPositions;
   private Set<Coord2D> robotGoalPositions;

   public Map<Coord2D, Item> itemGoalPositions() {
      return itemGoalPositions;
   }

   public Set<Coord2D> robotGoalPositions() {
      return robotGoalPositions;
   }

   /** 
    * Create an environment from ASCII art of the walls.  The ASCII art looks like this:
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
    * That would create a map which is width 3, height 2, and has 4 interior walls.  Every 'corner' space must be a plus,
    * even if no walls attached at that corner, and the exterior rectangle must always have walls. 
    * 
    * The parser trims whitespace at the start and end of lines, as well as any whitespace at the start or end of input.
    */

   static class MapParseException extends RuntimeException {
      public MapParseException(String msg) {
         super(msg);
      }
   }

   public WorldMap(String walls) throws MapParseException {
      this(walls, new HashMap<Coord2D, Item>(), new HashSet<Coord2D>());
   }

   public WorldMap(String walls, Map<Coord2D, Item> itemGoalPositions, Set<Coord2D> robotGoalPositions)
         throws MapParseException {
      this.itemGoalPositions = Collections.unmodifiableMap(new HashMap<Coord2D, Item>(itemGoalPositions));
      this.robotGoalPositions = Collections.unmodifiableSet(new HashSet<Coord2D>(robotGoalPositions));

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

   // Validation regexps.
   private static final String TOP_BOTTOM_REGEX = "\\+-\\+(-\\+)*";
   // 'odd' and 'even' here are based on the 0-based line index.
   private static final String ODD_LINE_REGEX = "\\|( [\\| ])* \\|";
   private static final String EVEN_LINE_REGEX = "\\+([- ]\\+)+";

   // Validate an ascii art map that has been split into lines for us. If this
   // returns without throwing an exception, the map is well formed.
   private void validateAsciiArtMap(String[] lines) throws MapParseException {
      // Do basic checking first.
      if (lines.length < 3) {
         throw new MapParseException("Insufficient lines");
      }
      if (lines.length % 2 == 0) {
         throw new MapParseException("Number of lines must be odd");
      }
      if (!lines[0].matches(TOP_BOTTOM_REGEX)) {
         throw new MapParseException("Malformed line 1");
      }
      int firstLineLength = lines[0].length();
      for (int i = 1; i < lines.length - 1; i++) {
         if (lines[i].length() != firstLineLength ||
               !lines[i].matches((i % 2 == 0) ? EVEN_LINE_REGEX : ODD_LINE_REGEX)) {
            throw new MapParseException("Malformed line " + (i + 1));
         }
      }
      if (lines[lines.length - 1].length() != firstLineLength ||
            !lines[lines.length - 1].matches(TOP_BOTTOM_REGEX)) {
         throw new MapParseException("Malformed line " + (lines.length));
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

}