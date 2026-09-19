package org.rezrov.roboworld;

/**
 * Something that can provide world positions.
 */
public interface WorldPositionSource {
   ContinuousWorldPosition getPosition();
}
