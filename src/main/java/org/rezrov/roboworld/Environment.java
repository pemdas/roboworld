package org.rezrov.roboworld;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class represents an environment in which a robot operates. This includes
 * the walls and any other items the robot may interact with.
 */
public class Environment {

   // Dimensions of the map. Valid robot x positions are from 0...width-1 and
   // y positions are from 0...height - 1. The map is always rectangular (though
   // it's not a requirement that all areas of the map be reachable).
   // The map coordinates follow screen coordinates conventions -- the origin is
   // the top left, with positive x to the right and positive y down.

   // To simplify bounds checking
   private int width;
   private int height;

   // Every map implicitly has walls all round the outside edges (e.g. every
   // cell with x=0 implicitly has a wall to the left)
   //
   // This means we just need to represent interior walls. The convention here is
   // to represent bottom and right walls based on cell coordinates. If you need
   // to check a top or left wall, adjust your coordinates. True means a wall is
   // present.

   private boolean[][] rightWalls;
   private boolean[][] bottomWalls;

   // Cell item contents (or NONE)
   private Item[][] items;

   // Cell item goal (or NONE)
   private Item[][] itemGoals;

   /**
    * Create an empty environment of the given size.
    */
   public Environment(int width, int height) {
      assert width > 0;
      assert height > 0;
      this.width = width;
      this.height = height;

      // -1 because the far right column has implicit right walls, and the
      // bottom row has implicit bottom walls.
      rightWalls = new boolean[width - 1][height];
      bottomWalls = new boolean[width][height - 1];

      items = new Item[width][height];
      itemGoals = new Item[width][height];
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            items[x][y] = itemGoals[x][y] = Item.NONE;
         }
      }
   }

   // Add an item goal location to
   public void addItemGoalLocation(Item item, Coord2D location) {
      if (itemGoals[location.x][location.y] != Item.NONE) {
         // Since we should only be adding item goals at scenario setup, this is a hard
         // error.
         throw new IllegalStateException("Attempt to put two item goals in the same location");
      }
   }

   public boolean addItemLocation(Item item, Coord2D location) {
      if (items[location.x][location.y] != Item.NONE) {
         // The robot can try to drop things in squares that already have a thing, and we
         // want to crash the robot in that case, which is why this is a soft error.
         return false;
      }
      items[location.x][location.y] = item;
      return true;
   }

   public Item itemAt(Coord2D location) {
      return items[location.x][location.y];
   }

   // Returns true if all the items in the environment are
   public boolean allItemsAtGoals() {
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            if (items[x][y] != Item.NONE &&
                  items[x][y] != itemGoals[x][y]) {
               return false;
            }
         }
      }
      return true;
   }

   public boolean allItemsGoalsHaveItems() {
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            if (itemGoals[x][y] != Item.NONE &&
                  items[x][y] != itemGoals[x][y]) {
               return false;
            }
         }
      }
      return true;
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

   public Environment(String asciiArt) throws MapParseException {
      // Regular expressions used to validate the map.

      String input = asciiArt.trim();
      // Note this split also trims whitespace at the start and end of each line as
      // well.
      String[] lines = input.split("\\s*\n\\s*");
      validateAsciiArtMap(lines);
      width = lines[0].length() / 2;
      height = lines.length / 2;
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

   private HashSet<Coord2D> goalCells = new HashSet<>();

   public void setGoalCells(Collection<Coord2D> cells) {
      goalCells = new HashSet<>(cells);
   }

   public boolean isGoalCell(Coord2D c) {
      return goalCells.contains(c);
   }

   public Collection<Coord2D> goalCells() {
      return goalCells;
   }

   public void addWall(DiscreteWorldPosition pos) {
      assert isInBounds(pos.x(), pos.y());
      if (isExternalBoundary(pos)) {
         // Nothing to do.
         return;
      }
      switch (pos.direction()) {
         case UP:
            bottomWalls[pos.x()][pos.y() - 1] = true;
            break;
         case LEFT:
            rightWalls[pos.x() - 1][pos.y()] = true;
            break;
         case DOWN:
            bottomWalls[pos.x()][pos.y()] = true;
            break;
         case RIGHT:
            rightWalls[pos.x()][pos.y()] = true;
            break;
      }
   }

   // Valid robot coordinates range from 0 to width-1
   public int getWidth() {
      return width;
   }

   // Valid robot coordinates range from 0 to height-1
   public int getHeight() {
      return height;
   }

   public boolean isInBounds(Coord2D pos) {
      return isInBounds(pos.x, pos.y);
   }

   public boolean isInBounds(int x, int y) {
      return x >= 0 && y >= 0 && x < width && y < height;
   }

   private boolean isExternalBoundary(DiscreteWorldPosition pos) {
      return (pos.direction() == Direction.UP && pos.y() == 0)
            || (pos.direction() == Direction.DOWN && pos.y() == height - 1)
            || (pos.direction() == Direction.LEFT && pos.x() == 0)
            || (pos.direction() == Direction.RIGHT && pos.x() == width - 1);
   }

   public boolean isFacingWall(DiscreteWorldPosition pos) {
      assert isInBounds(pos.x(), pos.y());
      // Take care of the implicit boundary walls first.
      if (isExternalBoundary(pos)) {
         return true;
      }
      switch (pos.direction()) {
         case UP:
            return bottomWalls[pos.x()][pos.y() - 1];
         case LEFT:
            return rightWalls[pos.x() - 1][pos.y()];
         case DOWN:
            return bottomWalls[pos.x()][pos.y()];
         case RIGHT:
            return rightWalls[pos.x()][pos.y()];
         default:
            throw new AssertionError("Bad direction");
      }
   }

}