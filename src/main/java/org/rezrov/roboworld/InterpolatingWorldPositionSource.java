package org.rezrov.roboworld;

/**
 * A position source that interpolates between two positions over some period of
 * time.
 */
public class InterpolatingWorldPositionSource implements WorldPositionSource {

   // For now we just do linear interpolation, but we may want to add other
   // strategies like easing in, etc. May also want to add support for looping
   // and doing out-and-back interpolation.
   public enum Strategy {
      LINEAR,
      SINE
   };

   private ContinuousWorldPosition start;
   private ContinuousWorldPosition end;
   private double startTime;
   private double duration;
   private Strategy strategy;
   private TimeSource timeSource;

   public InterpolatingWorldPositionSource(ContinuousWorldPosition start, ContinuousWorldPosition end, double startTime,
         double duration,
         TimeSource timeSource, Strategy strategy) {
      assert duration > 0;
      this.start = start;
      this.end = end;
      this.startTime = startTime;
      this.duration = duration;
      this.timeSource = timeSource;
      this.strategy = strategy;
   }

   @Override
   public ContinuousWorldPosition getPosition() {
      double p = (timeSource.now() - startTime) / duration;
      if (p >= 1.0) {
         return end;
      }
      switch (strategy) {
         case LINEAR:
            // p is already what we want it to be.
            break;
         case SINE:
            p = ((-Math.cos(p * Math.PI) + 1) / 2);
            break;
      }
      return new ContinuousWorldPosition(start.x() + p * (end.x() - start.x()),
            start.y() + p * (end.y() - start.y()),
            start.heading() + p * start.headingOffset(end));
   }
}
