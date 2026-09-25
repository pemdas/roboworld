package org.rezrov.roboworld;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the state of the world, both the static elements
 * and the dynamic ones.
 * 
 * All access to dynamic data (e.g. everything that's not in the WorldMap)
 * must be synchronized for thread-safety.
 */
public class World {
   // Helper class for optional construction configuration
   public static class Config {
      private Map<Coord2D, Item> itemGoalPositions = new HashMap<>();
      private Set<Coord2D> robotGoalPositions = new HashSet<>();
      private Map<Coord2D, Item> items = new HashMap<>();

      Config addItemGoalPosition(Item item, Coord2D position) {
         Item existing = itemGoalPositions.putIfAbsent(position, item);
         if (!(existing == null || existing == item)) {
            throw new AssertionError("Multiple item goals at square " + position);
         }
         return this;
      }

      Config addItemGoalPositions(Item item, Collection<Coord2D> positions) {
         for (Coord2D c : positions) {
            addItemGoalPosition(item, c);
         }
         return this;
      }

      Config addItemGoalPositions(Item item, Coord2D[] positions) {
         return addItemGoalPositions(item, Arrays.asList(positions));
      }

      Config addItem(Item item, Coord2D position) {
         Item existing = items.putIfAbsent(position, item);
         if (!(existing == null || existing == item)) {
            throw new AssertionError("Multiple items at square " + position);
         }
         return this;
      }

      Config addItems(Item item, Collection<Coord2D> positions) {
         for (Coord2D c : positions) {
            addItem(item, c);
         }
         return this;
      }

      Config addItems(Item item, Coord2D[] positions) {
         return addItems(item, Arrays.asList(positions));
      }

      Config addRobotGoalPosition(Coord2D position) {
         robotGoalPositions.add(position);
         return this;
      }

      Config addRobotGoalPositions(Collection<Coord2D> positions) {
         robotGoalPositions.addAll(positions);
         return this;
      }

      Config addRobotGoalPositions(Coord2D[] positions) {
         Collections.addAll(robotGoalPositions, positions);
         return this;
      }
   }

   private WorldMap map;
   private Map<Coord2D, Item> items;

   public World(String walls) throws WorldMap.MapParseException {
      this(walls, new Config());
   }


   public World(String walls, Config config) throws WorldMap.MapParseException {
      map = new WorldMap(walls, config.itemGoalPositions, config.robotGoalPositions);
      items = new HashMap<>(config.items);
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

   public Map<Coord2D, Item> items() {
      return items;
   }

   public WorldMap map() {
      return map;
   }

   // public Map<Coord2D, Item> itemGoals() {
   // return itemGoals;
   // }

}