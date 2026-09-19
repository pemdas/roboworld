package org.rezrov.roboworld;

import java.util.Arrays;

/**
 * Position of something in 2D space, including heading, in continous space
 */
public class ContinuousWorldPosition {
   private double x;
   private double y;
   private double heading; // Radians, normalized to [0, 2*PI)

   public double x() {
      return x;
   }

   public double y() {
      return y;
   }

   public double heading() {
      return heading;
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setHeading(double heading) {
      this.heading = normalizedHeading(heading);
   }

   // Default constructor.
   public ContinuousWorldPosition() {
   }

   public ContinuousWorldPosition(double x, double y, double heading) {
      this.x = x;
      this.y = y;
      this.heading = normalizedHeading(heading);
   }

   public ContinuousWorldPosition(ContinuousWorldPosition other) {
      copyFrom(other);
   }

   public void copyFrom(ContinuousWorldPosition other) {
      x = other.x;
      y = other.y;
      heading = other.heading;
   }

   /*
    * Return (other.heading - heading) normalized to (-Math.PI, Math.PI]
    */
   public double headingOffset(ContinuousWorldPosition other) {
      double ret = other.heading - heading;
      if (ret > Math.PI) {
         ret -= 2 * Math.PI;
      } else if (ret <= -Math.PI) {
         ret += 2 * Math.PI;
      }
      return ret;
   }

   private static double normalizedHeading(double heading) {
      heading = heading % (2 * Math.PI);
      if (heading < 0) {
         heading += 2 * Math.PI;
      }
      return heading;
   }

   @Override
   public boolean equals(Object other) {
      if (!(other instanceof ContinuousWorldPosition)) {
         throw new Error("What are you comparing?");
      }
      ContinuousWorldPosition op = (ContinuousWorldPosition) other;
      return x == op.x && y == op.y && heading == op.heading;
   }

   @Override
   public String toString() {
      return "(" + x + ", " + y + ", " + heading + ")";
   }

   @Override
   public int hashCode() {
      double[] tmp = new double[3];
      tmp[0] = x;
      tmp[1] = y;
      tmp[2] = heading;
      return Arrays.hashCode(tmp);
   }
}
