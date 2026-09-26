package org.rezrov.roboworld;

/**
 * Something that can provide world positions.
 */
public interface WorldPositionSource {
   ContinuousWorldPosition getPosition();

   // Returns true if the position is moving. False if the position
   // is not moving (and will not move again in the future.
   boolean moving();
}
