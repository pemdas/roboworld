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

   final private Walls walls;

   // Cells which are goals for a kind of item.
   final private Map<Coord2D, Item> itemGoalPositions;
   final private Set<Coord2D> robotGoalPositions;

   public Map<Coord2D, Item> itemGoalPositions() {
      return itemGoalPositions;
   }

   public Set<Coord2D> robotGoalPositions() {
      return robotGoalPositions;
   }

   public WorldMap(Walls walls) {
      this(walls, new Scenario.Options());
   }

   public WorldMap(Walls walls, Scenario.Options options) {
      this.itemGoalPositions = Collections.unmodifiableMap(new HashMap<Coord2D, Item>(options.itemGoalPositions));
      this.robotGoalPositions = Collections.unmodifiableSet(new HashSet<Coord2D>(options.robotGoalPositions));
      this.walls = walls;

   }

   // Valid robot coordinates range from 0 to width-1
   public int width() {
      return walls.width();
   }

   // Valid robot coordinates range from 0 to height-1
   public int height() {
      return walls.height();
   }

   public boolean isFacingWall(DiscreteWorldPosition pos) {
      return walls.isFacingWall(pos);
   }

}