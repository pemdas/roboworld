package org.rezrov.roboworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

// This encapsulates everything we need to run a scenario -- the starting states of the robot, environment, and also any goals that must be satisfied for 
// the scenario to be complete.
//
// It's also the entry point for users to create a scenario and play it. 
public class Scenario {

   ArrayList<Goal> goals = new ArrayList<>();

   // TODO - The scenario creation process is a mess. Clean it up.
   // Should have clean separation between scenario elements and practical objects
   // needed to run.
   private World world;
   private RobotImpl robot;
   private RobotDisplayTarget window;

   // A description of the scenario, presented to the student.
   // private String description;

   public Scenario(World world, DiscreteWorldPosition robotStartPosition) {
      this.world = world;
      this.robot = new RobotImpl(world, robotStartPosition);
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

   // The set of all ending positions for the robot which are considered "correct".
   // If empty, any ending position is considered correct.
   HashSet<Coord2D> goalPositions = new HashSet<>();

   // Add a goal that the robot must not crash.
   void addNoCrashGoal() {
      goals.add(new Goal("No crashes") {
         public boolean goalSatisfied() {
            return !robot.crashed();
         }
      });
   }

   // Add a goal that all items are in item goal spots.
   // This is separated out into a goal for each type of item that exists in the
   // world.
   void addItemsGoals() {
      // Determine which items exist.
      TreeMap<Item, Integer> itemCount = new TreeMap<Item, Integer>();
      for (Item item : world.items().values()) {
         itemCount.put(item, itemCount.getOrDefault(item, 0) + 1);
      }

      for (var entry : itemCount.entrySet()) {
         String goalDesc;
         if (entry.getValue() > 1) {
            goalDesc = "All " + entry.getKey() + "s placed";
         } else {
            goalDesc = entry.getKey() + " placed";
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

   // Add a goal position for the robot. If the robot ends in any goal position, it
   // has met the goal. If no goal poses are added, then the robot can end in any
   // position.
   public void setGoalCells(Collection<Coord2D> cells) {
      assert !cells.isEmpty();
      world.setGoalCells(cells);
      goals.add(new Goal("Robot in " + ((cells.size() > 1) ? "any " : "") + "goal cell") {
         @Override
         public boolean goalSatisfied() {
            return world.isGoalCell(robot.position().asCoord2D());
         }
      });
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

   private static Scenario test1Scene() throws World.MapParseException {
      World e = new World("" +
            "+-+-+-+\n" +
            "|     |\n" +
            "+ + + +\n" +
            "|     |\n" +
            "+ + + +\n" +
            "| | | |\n" +
            "+ +-+ +\n" +
            "|     |\n" +
            "+-+-+-+\n");
      e.addItemGoal(new Coord2D(0, 1), Item.STAR);
      e.addItemGoal(new Coord2D(2, 2), Item.MOON);

      e.putItem(new Coord2D(1, 0), Item.STAR);
      e.putItem(new Coord2D(0, 2), Item.MOON);

      Scenario ret = new Scenario(e, new DiscreteWorldPosition(2, 1, Direction.RIGHT));
      ret.addItemsGoals();
      ret.addNoCrashGoal();
      ret.setGoalCells(Arrays.asList(new Coord2D(0, 0), new Coord2D(0, 1), new Coord2D(2, 1)));
      return ret;
   }

   public World world() {
      return world;
   }
}