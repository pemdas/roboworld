package org.rezrov.roboworld;

// Something drawable in the world that's associated with a position.  
public abstract class PositionedWorldDrawable implements WorldDrawable {

   // Delegate that determines the position of this drawable.
   private WorldPositionSource positionSource;

   /**
    * Convenience constructor.
    */
   public PositionedWorldDrawable(WorldPositionSource positionSource) {
      this.positionSource = positionSource;
   }

   public WorldPosition getPosition() {
      return positionSource.getPosition();
   }

   // Convenience method to statically set the position.
   public void setPosition(WorldPosition position) {
      this.positionSource = new StaticWorldPositionSource(position);
   }

   public void setPositionSource(WorldPositionSource positionSource) {
      this.positionSource = positionSource;
   }

}
