package org.rezrov.roboworld;

/**
 * Trivial position source for something that doesn't move.
 */
public class StaticWorldPositionSource implements WorldPositionSource {
   ContinuousWorldPosition position;

   public StaticWorldPositionSource(ContinuousWorldPosition position) {
      this.position = position;
   }

   @Override
   public ContinuousWorldPosition getPosition() {
      return position;
   }
}
