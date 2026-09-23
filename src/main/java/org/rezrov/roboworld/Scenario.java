package org.rezrov.roboworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;

// This encapsulates everything we need to run a scenario -- the starting states of the robot, environment, and also any goals that must be satisfied for 
// the scenario to be complete.
//
// It's also the entry point for users to create a scenario and play it. 
public class Scenario {

   abstract class Goal {
      String description;

      public Goal(String description) {
         this.description = description;
      }

      abstract public boolean goalSatisfied();
   }

   ArrayList<Goal> goals = new ArrayList<>();

   // TODO - The scenario creation process is a mess. Clean it up.
   // Should have clean separation between scenario elements and practical objects
   // needed to run.
   private Environment env;
   private DiscreteWorldPosition robotStartPosition;
   private RobotImpl robot;
   private RobotWindow window;

   // A description of the scenario, presented to the student.
   private String description;

   // (Make goal validation a callback? Makes scenarios impossible to create
   // declaratively, but adds the most flexibility...)

   public Scenario(Environment env, DiscreteWorldPosition robotStartPosition) {
      this.env = env;
      this.robotStartPosition = robotStartPosition;
      robot = new RobotImpl(env, robotStartPosition);
   }

   static class WatcherThread extends Thread {
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

   public void createWindow() {
      try {
         Thread appThread = Thread.currentThread();
         SwingUtilities.invokeAndWait(() -> {
            window = new RobotWindow("RoboWorld", appThread, this);
            window.setVisible(true);
         });
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
      new WatcherThread(Thread.currentThread(), new Runnable() {
         public void run() {
            window.setRobotDone();
         }
      }).start();
      robot.setDisplayTarget(window);
   }

   // The set of all ending positions for the robot which are considered "correct".
   // If empty, any
   // ending position is considered correct.
   HashSet<Coord2D> goalPositions = new HashSet<>();

   // Add a goal that the robot must not crash.
   void addNoCrashGoal() {

   }

   // Add a goal position for the robot. If the robot ends in any goal position, it
   // has met the goal. If no goal poses are added, then the robot can end in any
   // position.
   public void setGoalCells(Collection<Coord2D> cells) {
      assert !cells.isEmpty();
      env.setGoalCells(cells);
      goals.add(new Goal("Robot in " + ((cells.size() > 1) ? "any " : "") + "goal cell") {
         @Override
         public boolean goalSatisfied() {
            return env.isGoalCell(robot.position().asCoord2D());
         }
      });
   }

   public DiscreteWorldPosition robotStartPosition() {
      return robotStartPosition;
   }

   private static String goalMarker(boolean success, boolean appExited) {
      if (success) {
         return "🗹";
      } else if (appExited) {
         return "🗷";
      } else {
         return "☐";
      }
   }

   public String goalStatus(boolean appExited) {
      System.out.println("have " + goals.size() + " goals");
      String[] lines = new String[goals.size()];
      for (int i = 0; i < goals.size(); i++) {
         lines[i] = goalMarker(goals.get(i).goalSatisfied(), appExited) + " - " + goals.get(i).description;
      }
      return String.join("\n", lines);
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

   private static Scenario test1Scene() throws Environment.MapParseException {
      Environment e = new Environment("" +
            "+-+-+-+\n" +
            "|     |\n" +
            "+ + + +\n" +
            "|     |\n" +
            "+ + + +\n" +
            "| | | |\n" +
            "+ +-+ +\n" +
            "|     |\n" +
            "+-+-+-+\n");
      Scenario ret = new Scenario(e, new DiscreteWorldPosition(2, 1, Direction.RIGHT));
      ret.setGoalCells(Arrays.asList(new Coord2D(0, 0), new Coord2D(0, 1), new Coord2D(2, 1)));
      return ret;
   }

   public Environment environment() {
      return env;
   }

}