package org.rezrov.roboworld;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class ItemTest {

   // Test that each item is associated with a unique letter (other than NONE)
   @Test
   public void testLettersAreUnique() {
      HashSet<Character> used = new HashSet<>();
      for (Item it : Item.values()) {
         if (it != Item.NONE) {
            assertFalse(used.contains(it.letter()), "Letter '" + it.letter() + "' reused in multiple Items");
            used.add(it.letter());
         }
      }
   }
}
