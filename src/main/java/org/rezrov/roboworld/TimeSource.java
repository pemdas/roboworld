package org.rezrov.roboworld;

import java.util.Collections;
import java.util.LinkedList;

/**
 * This class takes care of distributing a consistent wall and world time
 * to anyone that wants it.
 * 
 * Wall time runs continually. World time only runs when the robot is running.
 * To give a consistent view of the world, both time sources are updated
 * once per frame.
 * 
 * This class is thread safe.
 */
public class TimeSource {
   static private TimeSource wallTimeSource = new TimeSource();
   static private TimeSource worldTimeSource = new TimeSource();

   static public TimeSource wallTimeSource() {
      return wallTimeSource;
   }

   static public TimeSource worldTimeSource() {
      return worldTimeSource;
   }

   /**
    * Schedule something to be run when the time according to this TimeSource is at
    * least t. If t is in the past, the runnable will be run the next time the
    * time source advances.
    * 
    * Tasks are run on the thread that calls advace() (and so should be short
    * latency)
    */
   synchronized public void runAt(double t, Runnable runnable) {
      scheduledTasks.add(new ScheduledTask(t, runnable));
      // Since we don't expect there to be many scheduled tasks, just sort every
      // time. If we end up in a situation where we do have a lot of tasks
      // flying around, revisit this and set up a TreeMap.
      Collections.sort(scheduledTasks, (a, b) -> Double.compare(a.t, b.t));
   }

   static private class ScheduledTask {
      public double t;
      public Runnable runnable;

      public ScheduledTask(double t, Runnable runnable) {
         this.t = t;
         this.runnable = runnable;
      }
   };

   private LinkedList<ScheduledTask> scheduledTasks = new LinkedList<ScheduledTask>(); // Kept in sorted order by t;

   private double now = 0.0;

   synchronized void advance(double dt) {
      now += dt;
      var i = scheduledTasks.iterator();
      while (i.hasNext()) {
         ScheduledTask task = i.next();
         if (task.t <= now) {
            task.runnable.run();
            i.remove();
         } else {
            break;
         }
      }
   }

   synchronized public double now() {
      return now;
   }
}
