package org.rezrov.roboworld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * Swing widget for displaying the state of the World -- the includes the map,
 * the robot, and any other objects in the scene.
 */
public class WorldPanel extends JPanel {

   // Maybe make these configurable, eventually?
   final static float WALL_WIDTH = .07f;
   final static float HALF_WALL_WIDTH = WALL_WIDTH / 2;
   final static Color WALL_COLOR = Color.BLACK;

   // Cache the rendering of the walls to a buffered image. Updated when the size
   // changes.
   private BufferedImage cachedBackgroundImage = null;
   // Transform that sets up the rendering coordinate system such that each cell is
   // 1x1, with the origin in the
   // middle of the wall in the top left of the map.
   private AffineTransform cachedTransform = null;

   // Robot sprite.
   private PositionedWorldDrawable robotSprite;
   private Item robotCarriedItem = Item.NONE;
   private double robotCarriedHeadingOffset = 0;

   // Color used to fill in bars at the edges when the aspect ratio isn't perfect.
   private Color letterboxColor;

   // Environment and robot we're rendering
   private World env;

   private TimeSource worldTimeSource;

   public WorldPanel(World env, Color letterboxColor,
         TimeSource worldTimeSource,
         DiscreteWorldPosition startingRobotPosition) {
      this.env = env;
      this.letterboxColor = letterboxColor;
      this.worldTimeSource = worldTimeSource;
      robotSprite = new WorldStaticSprite(Resources.ROBOT_SPRITE);
      robotSprite.setPosition(startingRobotPosition.asContinuous());
   }

   @Override
   public void paintComponent(Graphics gr) {
      // long start = System.nanoTime();
      // System.out.println("Repaint");
      // Shouldn't happen, but be paranoid.
      if (getWidth() == 0 || getHeight() == 0) {
         return;
      }
      Graphics2D g = (Graphics2D) gr;
      g.setRenderingHints(new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON));
      AffineTransform savedTransform = g.getTransform();
      Dimension worldSizePx = worldSizePx();
      if (cachedBackgroundImage == null || worldSizePx.height != cachedBackgroundImage.getHeight()
            || worldSizePx.width != cachedBackgroundImage.getWidth()) {
         updateBackgroundImage(worldSizePx);
      }

      if (worldSizePx.getWidth() < getWidth()) {
         // Letterbox left and right edges.
         int leftLetterboxWidth = (getWidth() - worldSizePx.width) / 2;
         if (letterboxColor != null) {
            g.setColor(letterboxColor);
            g.fillRect(0, 0, leftLetterboxWidth, getHeight());
            // This may be 1 pixel different from the left side.
            int rightLetterboxWidth = (getWidth() - leftLetterboxWidth);
            g.fillRect(getWidth() - rightLetterboxWidth, 0, rightLetterboxWidth, getHeight());
         }
         g.translate(leftLetterboxWidth, 0);
      } else if (worldSizePx.height < getHeight()) {
         int topLetterboxHeight = (getHeight() - worldSizePx.height) / 2;
         if (letterboxColor != null) {
            // Letterbox top and bottom.
            g.setColor(letterboxColor);
            g.fillRect(0, 0, getWidth(), topLetterboxHeight);
            // This may be 1 pixel different from the top side.
            int bottomLetterboxHeight = (getHeight() - topLetterboxHeight);
            g.fillRect(0, getHeight() - topLetterboxHeight, getWidth(), bottomLetterboxHeight);
         }
         g.translate(0, topLetterboxHeight);
      }
      g.drawRenderedImage(cachedBackgroundImage, new AffineTransform());
      g.transform(cachedTransform);

      // Draw items.
      for (var itemEntry : env.items().entrySet()) {
         Resources.drawImage(g, itemEntry.getValue().image(), itemEntry.getKey().asContinuous());
      }

      robotSprite.draw(g);
      if (robotCarriedItem != Item.NONE) {
         var pos = robotSprite.getPosition();
         pos.setHeading(pos.heading() + robotCarriedHeadingOffset);
         Resources.drawImage(g, robotCarriedItem.image(), pos,
               0.5);
      }

      g.setTransform(savedTransform);
   }

   // Return the pixel dimensions we'll use to render the env.
   private Dimension worldSizePx() {
      float worldRenderWidth = env.getWidth() + WALL_WIDTH;
      float worldRenderHeight = env.getHeight() + WALL_WIDTH;
      float worldRenderAspectRatio = worldRenderWidth / worldRenderHeight;
      float panelAspectRatio = getWidth() / (float) getHeight();
      Dimension ret = new Dimension();
      if (worldRenderAspectRatio > panelAspectRatio) {
         ret.width = getWidth();
         ret.height = Math.round(getWidth() / worldRenderAspectRatio);
      } else {
         ret.height = getHeight();
         ret.width = Math.round(getHeight() * worldRenderAspectRatio);
      }
      return ret;
   }

   private void updateBackgroundImage(Dimension worldSizePx) {
      int hPx = worldSizePx.height;
      int wPx = worldSizePx.width;
      cachedBackgroundImage = new BufferedImage(wPx, hPx,
            BufferedImage.TYPE_INT_RGB);

      Graphics2D g = cachedBackgroundImage.createGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, wPx, hPx);

      g.setRenderingHints(new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON));

      // Due to the canvas area being rounded to the nearest pixel, the cell size may
      // not be precisely the same in the
      // horizontal and vertical directions, but it should be close enough that any
      // distortion is unnoticeable.
      float hCellSize = (float) (wPx / (env.getWidth() + WALL_WIDTH));
      float vCellSize = (float) (hPx / (env.getHeight() + WALL_WIDTH));

      g.scale(hCellSize, vCellSize);

      // Draw checkered backgrounds on goal cells.
      int checkers = 6;
      float ic = 1.0f / checkers;
      g.setColor(Color.LIGHT_GRAY);
      for (Coord2D c : env.goalCells()) {
         for (int x = 0; x < checkers; x++) {
            for (int y = (x % 2 == 0) ? 0 : 1; y < checkers; y += 2) {
               g.fill(new Rectangle2D.Float(c.x + x * ic, c.y + y * ic, ic, ic));
            }
         }
      }

      g.translate(HALF_WALL_WIDTH, HALF_WALL_WIDTH);

      g.setColor(WALL_COLOR);
      g.setStroke(new BasicStroke(WALL_WIDTH));
      // Draw the outer walls.
      g.draw(new Line2D.Float(0, 0, env.getWidth(), 0)); // Top
      g.draw(new Line2D.Float(0, env.getHeight(), env.getWidth(), env.getHeight())); // Bottom
      g.draw(new Line2D.Float(0, 0, 0, env.getHeight())); // Left
      g.draw(new Line2D.Float(env.getWidth(), 0, env.getWidth(), env.getHeight())); // Right

      // Draw top walls
      for (int x = 0; x < env.getWidth(); x++) {
         for (int y = 1; y < env.getHeight(); y++) {
            if (env.isFacingWall(new DiscreteWorldPosition(x, y, Direction.UP))) {
               g.draw(new Line2D.Float(x, y, x + 1, y));
            }
         }
      }
      // Draw left walls
      for (int x = 1; x < env.getWidth(); x++) {
         for (int y = 0; y < env.getHeight(); y++) {
            if (env.isFacingWall(new DiscreteWorldPosition(x, y, Direction.LEFT))) {
               g.draw(new Line2D.Float(x, y, x, y + 1));
            }
         }
      }
      // Draw "pillars"
      for (int x = 1; x < env.getWidth(); x++) {
         for (int y = 1; y < env.getHeight(); y++) {
            g.fill(new Ellipse2D.Float(x - WALL_WIDTH, y - WALL_WIDTH, 2 * WALL_WIDTH, 2 * WALL_WIDTH));
         }
      }
      // For other rendering, the coordinate system has integer coordinates *centered*
      // in those cells.
      g.translate(.5f, .5f);

      // Draw item goals.
      for (var itemGoalEntry : env.itemGoals().entrySet()) {
         Resources.drawImage(g, itemGoalEntry.getValue().goalImage(),
               itemGoalEntry.getKey().asContinuous());
      }

      cachedTransform = g.getTransform();
   }

   public void setRobotCarriedItem(Item item) {
      robotCarriedItem = item;
      robotCarriedHeadingOffset = -robotSprite.getPosition().heading();
   }

   synchronized public void moveRobot(ContinuousWorldPosition from, ContinuousWorldPosition to, double movementTime) {
      if (movementTime <= 0) {
         // Instantaneous move.
         robotSprite.setPositionSource(new StaticWorldPositionSource(to));
      } else {
         // Move where the thread should block while the animation completes.
         double now = worldTimeSource.now();
         double moveEndTime = now + movementTime;
         robotSprite
               .setPositionSource(
                     new InterpolatingWorldPositionSource(from, to, now, moveEndTime, worldTimeSource,
                           InterpolatingWorldPositionSource.Strategy.SINE));
         // Put the application thread to sleep until the move is finished.
         worldTimeSource.runAt(moveEndTime, new Runnable() {
            @Override
            public void run() {
               synchronized (WorldPanel.this) {
                  WorldPanel.this.notify();
               }
            }
         });
         while (worldTimeSource.now() < moveEndTime) {
            try {
               wait();
            } catch (InterruptedException e) {
            }
         }
      }
   }
}
