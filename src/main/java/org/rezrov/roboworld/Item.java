package org.rezrov.roboworld;

public enum Item {
   NONE,
   KIKI,
   BOUBA;

   @Override
   // Make toString() return the name with just the first letter capitalized
   // instead
   // of shouting.
   public String toString() {
      String allCaps = super.toString();
      return allCaps.charAt(0) + allCaps.substring(1).toLowerCase();
   }
}
