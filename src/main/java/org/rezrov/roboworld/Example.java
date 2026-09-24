package org.rezrov.roboworld;

public class Example {
   public static void main(String[] args) {
      Robot r = Scenario.setUp(Scenario.TEST1);
      r.turnLeft();
      r.moveForward();
      r.turnLeft();
      r.moveForward();
      r.grab();
      r.moveForward();
      r.turnLeft();
      r.moveForward();
      r.drop();
      r.moveForward();
      r.grab();
      r.moveForward();
      r.turnLeft();
      r.moveForward();
      r.moveForward();
      r.turnLeft();
      r.moveForward();
      r.drop();
      r.moveForward();
   }
}
