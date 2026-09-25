package org.rezrov.roboworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

// This encapsulates everything we need to run a scenario -- the starting states of the robot, environment, and also any goals that must be satisfied for 
// the scenario to be complete.
//
// It's also the entry point for users to create a scenario and play it. 
public class Scenario {
   // Helper class for specifying optional scenario additions.
   public static class Options {
      public Map<Coord2D, Item> itemGoalPositions = new HashMap<>();
      public Set<Coord2D> robotGoalPositions = new HashSet<>();
      public Map<Coord2D, Item> items = new HashMap<>();

      Options addItemGoalPosition(Item item, Coord2D position) {
         Item existing = itemGoalPositions.putIfAbsent(position, item);
         if (!(existing == null || existing == item)) {
            throw new AssertionError("Multiple item goals at square " + position);
         }
         return this;
      }

      Options addItemGoalPositions(Item item, Collection<Coord2D> positions) {
         for (Coord2D c : positions) {
            addItemGoalPosition(item, c);
         }
         return this;
      }

      Options addItemGoalPositions(Item item, Coord2D[] positions) {
         return addItemGoalPositions(item, Arrays.asList(positions));
      }

      Options addItem(Item item, Coord2D position) {
         Item existing = items.putIfAbsent(position, item);
         if (!(existing == null || existing == item)) {
            throw new AssertionError("Multiple items at square " + position);
         }
         return this;
      }

      Options addItems(Item item, Collection<Coord2D> positions) {
         for (Coord2D c : positions) {
            addItem(item, c);
         }
         return this;
      }

      Options addItems(Item item, Coord2D[] positions) {
         return addItems(item, Arrays.asList(positions));
      }

      Options addRobotGoalPosition(Coord2D position) {
         robotGoalPositions.add(position);
         return this;
      }

      Options addRobotGoalPositions(Collection<Coord2D> positions) {
         robotGoalPositions.addAll(positions);
         return this;
      }

      Options addRobotGoalPositions(Coord2D[] positions) {
         Collections.addAll(robotGoalPositions, positions);
         return this;
      }
   }

   ArrayList<Goal> goals = new ArrayList<>();

   // Should have clean separation between scenario elements and practical objects
   // needed to run.
   private World world;
   private RobotImpl robot;
   private RobotDisplayTarget window;

   // A description of the scenario, presented to the student.
   // private String description;

   public Scenario(String walls, DiscreteWorldPosition robotStartPosition, Options options) {
      this.world = new World(walls, options);
      this.robot = new RobotImpl(world, robotStartPosition);
      goals = new ArrayList<>();

      // Always have a "don't crash" goal.
      goals.add(new Goal("No crashes") {
         public boolean goalSatisfied() {
            return !robot.crashed();
         }
      });

      maybeAddRobotPositionGoal();
      maybeAddItemPositionGoals(options);
   }

   // If goal positions for the robot exist, add a Goal for that.
   private void maybeAddRobotPositionGoal() {
      // If there are goal positions for the robot, that's a goal.
      var robotGoalPositions = world.map().robotGoalPositions();
      if (!robotGoalPositions.isEmpty()) {
         goals.add(new Goal("Robot in " + ((robotGoalPositions.size() > 1) ? "any " : "") + "goal cell") {
            @Override
            public boolean goalSatisfied() {
               return world.map().robotGoalPositions().contains(robot.position().asCoord2D());
            }
         });
      }
   }

   private static String capitalized(String s) {
      assert !s.isEmpty();
      return s.substring(0, 1).toUpperCase() + s.substring(1);
   }

   // For each item type in the world, add a goal that the items must be in the
   // item goal positions.
   void maybeAddItemPositionGoals(Options options) {
      // Determine which items exist.
      TreeMap<Item, Integer> itemCount = new TreeMap<Item, Integer>();
      for (Item item : options.items.values()) {
         itemCount.put(item, itemCount.getOrDefault(item, 0) + 1);
      }

      for (var entry : itemCount.entrySet()) {
         String goalDesc;
         if (entry.getValue() > 1) {
            goalDesc = "All " + entry.getKey().pluralName() + " placed";
         } else {
            goalDesc = capitalized(entry.getKey().singularName()) + " placed";
         }
         goals.add(new Goal(goalDesc) {
            public boolean goalSatisfied() {
               // Be careful not to mark success if the robot is carrying
               // an item of this type.
               return robot.carriedItem() != entry.getKey() && world.allItemsAtGoals(entry.getKey());
            }
         });
      }
   }

   // This is a little specialized thread that just exists to wait until
   // the main application thread finishes and notify the other threads
   // that the robot will not be doing anything else.
   private static class WatcherThread extends Thread {
      Thread watched;
      Runnable postMortem;

      public WatcherThread(Thread watched, Runnable postMortem) {
         this.watched = watched;
         this.postMortem = postMortem;
      }

      @Override
      public void run() {
         while (watched.isAlive()) {
            try {
               watched.join();
            } catch (InterruptedException e) {
            }
         }
         SwingUtilities.invokeLater(postMortem);
      }
   }

   public RobotImpl robot() {
      return robot;
   }

   public void createWindow() {
      String display = System.getProperty("roboworld.display", "gui");
      if (display.equals("gui")) {
         try {
            SwingUtilities.invokeAndWait(() -> {
               window = new RobotWindow("RoboWorld", this);
            });
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      } else if (display.equals("console")) {
         window = new ConsoleRobotDisplayTarget(this);
      } else {
         throw new RuntimeException("Unknown roboworld.display value: '" + display + "'");
      }
      new WatcherThread(Thread.currentThread(), new Runnable() {
         public void run() {
            window.setRobotDone();
         }
      }).start();
      robot.setDisplayTarget(window);
   }

   public List<Goal> goals() {
      return goals;
   }

   static Robot setUp(int scenarioId) {
      Scenario s;
      switch (scenarioId) {
         case TEST1:
            s = test1Scene();
            break;
         default:
            throw new NoSuchElementException("Unknown scenario id: " + scenarioId);
      }
      s.createWindow();
      return s.robot;
   }

   static final public int TEST1 = 0;

   private static Scenario test1Scene() throws WorldMap.MapParseException {

      String map = "" +
            "+-+-+-+\n" +
            "|     |\n" +
            "+ + + +\n" +
            "|     |\n" +
            "+ + + +\n" +
            "| | | |\n" +
            "+ +-+ +\n" +
            "|     |\n" +
            "+-+-+-+\n";

      return new Scenario(map, new DiscreteWorldPosition(2, 1, Direction.RIGHT),
            new Options()
                  .addItemGoalPosition(Item.STAR, new Coord2D(0, 1))
                  .addItemGoalPosition(Item.MOON, new Coord2D(2, 2))
                  .addItem(Item.STAR, new Coord2D(1, 0))
                  .addItem(Item.MOON, new Coord2D(0, 2))
                  .addRobotGoalPositions(List.of(new Coord2D(0, 0), new Coord2D(0, 1), new Coord2D(2, 1))));
   }

   public World world() {
      return world;
   }
}