package org.rezrov.roboworld;

import java.awt.image.BufferedImage;

public enum Item {
   NONE,
   STAR {
      @Override
      public BufferedImage image() {
         return STAR_SPRITE;
      }

      @Override
      public BufferedImage outlineImage() {
         return STAR_OUTLINE_SPRITE;
      }
   },
   MOON {
      @Override
      public BufferedImage image() {
         return MOON_SPRITE;
      }

      @Override
      public BufferedImage outlineImage() {
         return MOON_OUTLINE_SPRITE;
      }
   };

   final private static BufferedImage STAR_SPRITE = Resources.loadImage("star_item.png");
   final private static BufferedImage STAR_OUTLINE_SPRITE = Resources.loadImage("star_item_outline.png");
   final private static BufferedImage MOON_SPRITE = Resources.loadImage("moon_item.png");
   final private static BufferedImage MOON_OUTLINE_SPRITE = Resources.loadImage("moon_item_outline.png");

   // Make toString() return the name with just the first letter capitalized
   // instead of shouting.
   @Override
   public String toString() {
      String allCaps = super.toString();
      return allCaps.charAt(0) + allCaps.substring(1).toLowerCase();
   }

   public BufferedImage image() {
      throw new AssertionError("Undefined");
   }

   public BufferedImage outlineImage() {
      throw new AssertionError("Undefined");
   }

}
