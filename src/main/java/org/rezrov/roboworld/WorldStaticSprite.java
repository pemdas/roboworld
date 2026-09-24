package org.rezrov.roboworld;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * A static sprite, backed by a BufferedImage.
 */
public class WorldStaticSprite extends PositionedWorldDrawable {

   private BufferedImage image;

   public WorldStaticSprite(BufferedImage image) {
      this(image, new StaticWorldPositionSource(new ContinuousWorldPosition()));
   }

   public WorldStaticSprite(BufferedImage image, WorldPositionSource positionSource) {
      super(positionSource);
      this.image = image;
   }

   @Override
   public void draw(Graphics2D g) {
      Resources.drawImage(g, image, getPosition());
   }
}
