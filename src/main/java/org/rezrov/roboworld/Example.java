package org.rezrov.roboworld;

public class Example {
   public static void main(String[] args) {
      Robot r = Scenario.setUp(Scenario.TEST1);
      r.turnLeft();
      r.moveForward();
      for (int i = 0; i < 1000; i++) {
         r.turnRight();
         r.turnRight();
         r.moveForward();
      }
      r.turnLeft();
      r.turnLeft();
   }
}
