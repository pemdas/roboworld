package org.rezrov.roboworld;

/**
 * A position source that interpolates between two positions over some period of
 * time.
 */
public class InterpolatingWorldPositionSource implements WorldPositionSource {
   private ContinuousWorldPosition startPosition;
   private ContinuousWorldPosition endPosition;

   // Local time for the interpolation.
   private InterpolationStrategy strategy;
   private TimeSource timeSource;
   private double startTime;
   private double endTime;

   public InterpolatingWorldPositionSource(ContinuousWorldPosition startPosition, ContinuousWorldPosition endPosition,
         double startTime, double endTime,
         TimeSource timeSource,
         InterpolationStrategy strategy) {
      assert endTime > startTime;
      this.startPosition = new ContinuousWorldPosition(startPosition);
      this.endPosition = new ContinuousWorldPosition(endPosition);
      this.timeSource = timeSource;
      this.startTime = startTime;
      this.endTime = endTime;
      this.strategy = strategy;
   }

   @Override
   public ContinuousWorldPosition getPosition() {
      double p = (timeSource.now() - startTime) / (endTime - startTime);
      if (p <= 0.0) {
         return startPosition;
      } else if (p >= 1.0) {
         return endPosition;
      }
      switch (strategy) {
         case LINEAR:
            // p is already what we want it to be.
            break;
         case SINE:
            p = ((-Math.cos(p * Math.PI) + 1) / 2);
            break;
      }
      return new ContinuousWorldPosition(startPosition.x() + p * (endPosition.x() - startPosition.x()),
            startPosition.y() + p * (endPosition.y() - startPosition.y()),
            startPosition.heading() + p * startPosition.headingOffset(endPosition));
   }

   @Override
   public boolean moving() {
      return timeSource.now() < endTime;
   }
}
