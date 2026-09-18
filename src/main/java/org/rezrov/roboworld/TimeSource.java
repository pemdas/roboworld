package org.rezrov.roboworld;

import java.util.Collections;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

/**
 * This class takes care of distributing a consistent wall and world time
 * to anyone that wants it.
 * 
 * Wall time runs continually. World time only runs when the robot is running.
 * To give a consistent view of the world, both time sources are updated
 * once per frame.
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

   // Schedule something to be run when the time according to this TimeSource is at
   // least t.
   public void runAt(double t, Runnable runnable) {
      if (t <= now) {
         // Already past, do it now.
         SwingUtilities.invokeLater(runnable);
      } else {
         scheduledTasks.add(new ScheduledTask(t, runnable));
         // Since we don't expect there to be many scheduled tasks, just sort every
         // time. If we end up in a situation where we do have a lot of tasks
         // flying around, revisit this and set up a TreeMap.
         Collections.sort(scheduledTasks, (a, b) -> Double.compare(a.t, b.t));
      }
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

   void advance(double dt) {
      now += dt;
      var i = scheduledTasks.iterator();
      while (i.hasNext()) {
         ScheduledTask task = i.next();
         if (task.t <= now) {
            SwingUtilities.invokeLater(task.runnable);
            i.remove();
         } else {
            break;
         }
      }
   }

   public double now() {
      return now;
   }
}
