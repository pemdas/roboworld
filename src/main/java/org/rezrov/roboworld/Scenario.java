package org.rezrov.roboworld;

import java.util.ArrayList;
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

   ArrayList<Goal> goals;

   // TODO - The scenario creation process is a mess. Clean it up.
   // Should have clean separation between scenario elements and practical objects
   // needed to run.
   private Environment env;
   private DiscreteWorldPosition robotStartPosition;
   private RobotWindow window;

   // A description of the scenario, presented to the student.
   private String description;

   private int numGoals;

   // (Make goal validation a callback? Makes scenarios impossible to create
   // declaratively, but adds the most flexibility...)

   public Scenario(Environment env, DiscreteWorldPosition robotStartPosition) {
      this.env = env;
      this.robotStartPosition = robotStartPosition;
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
   void addGoalPositions(List<Coord2D> positions) {

      if (goalPositions.isEmpty()) {
         numGoals++;
      }
      // goalPositions.add(cell);
   }

   // Get an array ofstrings, one per goal.
   public String[] goalDescriptions() {
      String[] ret = new String[goals.size()];
      for (int i = 0; i < goals.size(); i++) {
         ret[i] = goals.get(i).description;
      }
      return ret;
   }

   /**
    * Returns an array of booleans with true for each met goal and false for each
    * unmet goal
    */
   public boolean[] goalStates() {
      boolean[] ret = new boolean[goals.size()];
      for (int i = 0; i < goals.size(); i++) {
         ret[i] = goals.get(i).goalSatisfied();
      }
      return ret;
   }

   static Robot setUp(int scenarioId) {
      Scenario scene = createScenarioFromId(scenarioId);
      try {
         Thread appThread = Thread.currentThread();
         SwingUtilities.invokeAndWait(() -> scene.createAndShowGUI(appThread));

      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
      return new RobotImpl(scene.env, scene.robotStartPosition, scene.window);
   }

   static Scenario createScenarioFromId(int scenarioId) {
      switch (scenarioId) {
         case TEST1:
            return test1Scene();
         default:
            throw new NoSuchElementException("Unknown scenario id: " + scenarioId);
      }
   }

   void createAndShowGUI(Thread appThread) {
      window = new RobotWindow("RoboWorld", appThread, env, robotStartPosition);
      window.setVisible(true);

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
      return new Scenario(e, new DiscreteWorldPosition(2, 1, Direction.RIGHT));
   }

   public Environment getEnvironment() {
      return env;
   }

   // public RobotImpl getRobot() {
   // return robot;
   // }

}