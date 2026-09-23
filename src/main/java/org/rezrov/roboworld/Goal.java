package org.rezrov.roboworld;

public abstract class Goal {
   String description;

   public Goal(String description) {
      this.description = description;
   }

   abstract public boolean goalSatisfied();
}
