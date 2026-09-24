package org.rezrov.roboworld;

public interface Robot {

   /** Turn left 90 degrees */
   public void turnLeft();

   /** Attempt to move forward. If the way is blocked, the robot will crash. */
   public void moveForward();

   /** Turn right 90 degrees */
   public void turnRight();

   /**
    * Check if the way forward is currently blocked.
    * 
    * @return true if the way is blocked, false otherwise.
    */
   public boolean blocked();

   /**
    * Check if the robot has crashed.
    * 
    * @return true if the robot has crashed, false otherwise.
    */
   public boolean crashed();

   /*
    * Returns the kind of item the robot is currently carrying, or Item.NONE if the
    * robot
    * is not currently carrying anything
    */
   public Item itemInHand();

   /**
    * Returns the kind of item on the ground at the current robot location, or
    * Item.NONE if there is no item here.
    */
   public Item itemOnGround();

   /**
    * Grab the item on the ground here. If there is no item on the ground here,
    * then the robot gets confused and crashes.
    */
   public void grab();

   /**
    * Drop the item the robot is currently carrying on the ground at the current
    * location.
    * 
    * If the robot isn't carrying anything, or there's already an item on the
    * ground here, the robot gets confused and crashes.
    */
   public void drop();

}
