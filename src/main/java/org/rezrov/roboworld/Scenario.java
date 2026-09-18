package org.rezrov.roboworld;

import java.util.HashSet;
import java.util.NoSuchElementException;

import javax.swing.SwingUtilities;

// This encapsulates everything we need to run a scenario -- the starting states of the robot, environment, and also any goals that must be satisfied for 
// the scenario to be complete.
//
// It's also the entry point for users to create a scenario and play it. 
public class Scenario {

   private Environment env;
   private ContinuousRobot robot;

   // A description of the scenario, presented to the student.
   private String description;

   // (Make goal validation a callback? Makes scenarios impossible to create
   // declaratively, but adds the most flexibility...)

   public Scenario(Environment env, ContinuousRobot r) {
      this.env = env;
      this.robot = r;
   }

   // The set of all ending positions for the robot which are considered "correct".
   // If empty, any
   // ending position is considered correct.
   HashSet<WorldPosition> goalPoses = new HashSet<>();

   // Add a goal position for the robot. If the robot ends in any goal position, it
   // has met the goal. If no goal poses are added, then the robot can end in any
   // position.
   void addGoalPose(WorldPosition pose) {
      goalPoses.add(pose);
   }

   public boolean goalsMet() {
      return goalPoses.isEmpty() || goalPoses.contains(robot.getPose());
   }

   static Robot setUp(int scenarioId) {
      Scenario scene = createScenarioFromId(scenarioId);
      try {
         Thread appThread = Thread.currentThread();
         SwingUtilities.invokeAndWait(() -> scene.createAndShowGUI(appThread));
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }

      return scene.robot;
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
      new RobotWindow("RoboWorld", appThread, env, robot).setVisible(true);

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
      ContinuousRobot r = new ContinuousRobot(e, new WorldPosition(2, 1, Direction.RIGHT));
      return new Scenario(e, r);
   }

   public Environment getEnvironment() {
      return env;
   }

   public ContinuousRobot getRobot() {
      return robot;
   }

}