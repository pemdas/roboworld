package org.rezrov.roboworld;

import java.awt.Graphics2D;

/**
 * Something that draws itself onto the world map, scaling is size
 * with the map.
 */
public interface WorldDrawable {
   /**
    * Render this drawable to the given graphics context. The graphics transform
    * will be set such that rendering coordinates a map coordinates, with the
    * the origin at the middle of the top left cell, and each cell being length
    * 1.
    *
    * @param g Graphics context for the MapPanel
    */
   public void draw(Graphics2D g);
}
