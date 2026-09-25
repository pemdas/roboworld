package org.rezrov.roboworld;

import java.awt.image.BufferedImage;

public enum Item {
   NONE("none", "none", null, null, null),
   STAR("star", "stars", Resources.loadImage("star_item.png"),
         Resources.loadImage("star_item_outline.png"), '*'),
   MOON("moon", "moons", Resources.loadImage("moon_item.png"),
         Resources.loadImage("moon_item_outline.png"), '@');

   final private String singularName;
   final private String pluralName;
   final private BufferedImage image;
   final private BufferedImage goalImage;
   final private Character letter;

   private Item(String singularName, String pluralName, BufferedImage image, BufferedImage goalImage,
         Character letter) {
      this.singularName = singularName;
      this.pluralName = pluralName;
      this.image = image;
      this.goalImage = goalImage;
      this.letter = letter;
   }

   public static class NotFoundException extends Exception {
      private NotFoundException(String msg) {
         super(msg);
      }
   }

   public String singularName() {
      return singularName;
   }

   public String pluralName() {
      return pluralName;
   }

   public BufferedImage image() {
      return image;
   }

   public BufferedImage goalImage() {
      return goalImage;
   }

   public Character letter() {
      return letter;
   }

   static Item fromLetter(Character letter) throws NotFoundException {
      // Unless we eventually add a ridiculous number of items, linear search here is
      // fine

      for (Item item : values()) {
         if (item != NONE && item.letter.equals(letter)) {
            return item;
         }
      }
      throw new NotFoundException("No Item associated with letter '" + letter + "'");

   }

}
