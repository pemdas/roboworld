package org.rezrov.roboworld;

public abstract class Goal {
   private String description;

   public Goal(String description) {
      this.description = description;
   }

   public String description() {
      return description;
   }

   abstract public boolean goalSatisfied();
}
