package org.rezrov.roboworld;

import java.awt.image.BufferedImage;

// An item that can exist in the world.  Items can be picked up, carried, and dropped
// in new locations.  Items may have goal locations where they are to be placed.  
public class ItemResources {
   static BufferedImage drawableFor(Item item) {
      switch (item) {
         case NONE:
            throw new Error("Can't create resources for none");
         case KIKI:
            return Resources.KIKI_SPRITE;
         case BOUBA:
            return Resources.BOUBA_SPRITE;
      }
      throw new AssertionError("Unreachable code?");
   }

   static BufferedImage outlineDrawableFor(Item item) {
      switch (item) {
         case NONE:
            throw new Error("Can't get resources for none");
         case KIKI:
            return Resources.KIKI_OUTLINE_SPRITE;
         case BOUBA:
            return Resources.BOUBA_OUTLINE_SPRITE;
      }
      throw new AssertionError("Unreachable code?");
   }
}
