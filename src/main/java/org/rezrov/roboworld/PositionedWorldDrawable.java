package org.rezrov.roboworld;

// Something drawable in the world that's associated with a position.  
//
// This class is thread-safe.
public abstract class PositionedWorldDrawable implements WorldDrawable {

   // Delegate that determines the position of this drawable.
   private WorldPositionSource positionSource;

   /**
    * Convenience constructor.
    */
   public PositionedWorldDrawable(WorldPositionSource positionSource) {
      this.positionSource = positionSource;
   }

   synchronized public ContinuousWorldPosition getPosition() {
      return positionSource.getPosition();
   }

   // Convenience method to statically set the position.
   synchronized public void setPosition(ContinuousWorldPosition position) {
      this.positionSource = new StaticWorldPositionSource(position);
   }

   synchronized public void setPositionSource(WorldPositionSource positionSource) {
      this.positionSource = positionSource;
   }

}
