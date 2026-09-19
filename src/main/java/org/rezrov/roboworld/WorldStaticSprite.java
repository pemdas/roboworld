package org.rezrov.roboworld;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * A static sprite, backed by a BufferedImage.
 */
public class WorldStaticSprite extends PositionedWorldDrawable {

   private BufferedImage image;
   double spriteScale;

   public WorldStaticSprite(BufferedImage image, double spriteScale) {
      this(image, spriteScale, new StaticWorldPositionSource(new ContinuousWorldPosition()));
   }

   public WorldStaticSprite(BufferedImage image, double spriteScale, WorldPositionSource positionSource) {
      super(positionSource);
      this.image = image;
      this.spriteScale = spriteScale;
   }

   @Override
   public void draw(Graphics2D g) {
      AffineTransform saved = g.getTransform();
      double cellWidth = spriteScale * image.getWidth();
      double cellHeight = spriteScale * image.getHeight();
      ContinuousWorldPosition position = getPosition();
      g.translate(position.x() - cellWidth / 2.0, position.y() - cellHeight / 2.0);
      g.rotate(position.heading(), cellWidth / 2.0, cellHeight / 2.0);
      g.scale(spriteScale, spriteScale);
      g.drawRenderedImage(image, new AffineTransform());
      g.setTransform(saved);
   }
}
