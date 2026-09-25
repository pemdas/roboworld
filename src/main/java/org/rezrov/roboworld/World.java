package org.rezrov.roboworld;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the state of the world, both the static elements
 * and the dynamic ones.
 * 
 * All access to dynamic data (e.g. everything that's not in the WorldMap)
 * must be synchronized for thread-safety.
 */
public class World {

   private WorldMap map;
   private RobotImpl robot;
   private Map<Coord2D, Item> items;

   public World(String walls, DiscreteWorldPosition robotStartingPosition) throws WorldMap.MapParseException {
      this(walls, robotStartingPosition, new Scenario.Options());
   }

   public World(String walls, DiscreteWorldPosition robotStartingPosition, Scenario.Options options)
         throws WorldMap.MapParseException {
      map = new WorldMap(walls, options);
      items = new HashMap<>(options.items);
      robot = new RobotImpl(this, robotStartingPosition);
   }

   public RobotImpl robot() {
      return robot;
   }

   // Attempt to put an item at the given location. If an item is already at that
   // location, return false and leave the existing item unchanged. Otherwise,
   // return
   // true.
   synchronized public boolean putItem(Coord2D location, Item item) {
      return items.putIfAbsent(location, item) == null;
   }

   // Take the item at the given location and remove it from the environment.
   // Returns NONE if there was no item at that location.
   synchronized public Item takeItem(Coord2D location) {
      Item ret = items.getOrDefault(location, Item.NONE);
      if (ret != Item.NONE) {
         items.remove(location);
      }
      return ret;
   }

   synchronized public Item itemAt(Coord2D location) {
      return items.getOrDefault(location, Item.NONE);
   }

   // Returns true iff all the items of this type in the environment are at
   // locations which are goals for that item type.
   synchronized public boolean allItemsAtGoals(Item itemType) {

      if (robot.carriedItem() == itemType) {
         // If the robot is carrying one, that one is definitely not in a goal.
         return false;
      }

      // For each item, the corresponding itemGoal map should have an entry with
      // the same item.
      for (var entry : items.entrySet()) {
         assert entry.getValue() != Item.NONE;
         if (entry.getValue() != itemType) {
            continue;
         }
         if (map.itemGoalPositions().getOrDefault(entry.getKey(), Item.NONE) != entry.getValue()) {
            return false;
         }
      }
      return true;
   }

   // This is awkward, but to keep things thread-safe, we can't just
   // return the items themselves, we have to return a copy.
   synchronized public Collection<Map.Entry<Coord2D, Item>> items() {
      return new ArrayList<Map.Entry<Coord2D, Item>>(items.entrySet());
   }

   synchronized public boolean robotInGoal() {
      return map().robotGoalPositions().contains(robot.position().asCoord2D());
   }

   public WorldMap map() {
      return map;
   }

}