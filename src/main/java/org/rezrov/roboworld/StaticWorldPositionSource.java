package org.rezrov.roboworld;

/**
 * Trivial position source for something that doesn't move.
 */
public class StaticWorldPositionSource implements WorldPositionSource {
   WorldPosition position;

   public StaticWorldPositionSource(WorldPosition position) {
      this.position = position;
   }

   @Override
   public WorldPosition getPosition() {
      return position;
   }
}
