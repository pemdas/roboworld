package org.rezrov.roboworld;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import javax.imageio.ImageIO;

public class Resources {
   static {
      loadFont("NotoSans-Medium.ttf");
   }

   // World sprites are drawn at 500px/cell, so when rendering in cell coordinates
   // this is the scale factor that needs to be applied.
   final static double WORLD_SPRITE_CELL_SCALE = 1 / 500.0;

   final public static BufferedImage ROBOT_SPRITE = loadImage("robot.png");

   final public static BufferedImage PLAY_ICON = loadImage("play_icon.png");
   final public static BufferedImage PAUSE_ICON = loadImage("pause_icon.png");

   final private static String FONT_NAME = "Noto Sans Medium";
   // final private static String FONT_NAME = "Noto Sans Symbols 2";
   final public static Font LARGE_FONT = new Font(FONT_NAME, Font.PLAIN, 16);
   final public static Font MEDIUM_FONT = new Font(FONT_NAME, Font.PLAIN, 16);
   final public static Font SMALL_FONT = new Font(FONT_NAME, Font.PLAIN, 10);

   public static BufferedImage loadImage(String name) {
      try (InputStream in = Resources.class.getResourceAsStream(name)) {
         if (in == null)
            throw new IOException("Built-in resource not found: " + name);
         return ImageIO.read(in);
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      }
   }

   public static void loadFont(String name) {
      try (InputStream in = Resources.class.getResourceAsStream(name)) {
         if (in == null)
            throw new IOException("Built-in resource not found: " + name);
         GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, in));
      } catch (IOException e) {
         throw new UncheckedIOException(e);
      } catch (FontFormatException e) {
         throw new Error(e.getMessage());
      }
   }

   public static void drawImage(Graphics2D g, BufferedImage image, ContinuousWorldPosition position,
         double finalScale) {
      AffineTransform saved = g.getTransform();
      double cellWidth = finalScale * WORLD_SPRITE_CELL_SCALE * image.getWidth();
      double cellHeight = finalScale * WORLD_SPRITE_CELL_SCALE * image.getHeight();
      g.translate(position.x() - cellWidth / 2.0, position.y() - cellHeight / 2.0);
      g.rotate(position.heading(), cellWidth / 2.0, cellHeight / 2.0);
      g.scale(finalScale * WORLD_SPRITE_CELL_SCALE, finalScale * WORLD_SPRITE_CELL_SCALE);
      g.drawRenderedImage(image, new AffineTransform());
      g.setTransform(saved);
   }

   public static void drawImage(Graphics2D g, BufferedImage image, ContinuousWorldPosition position) {
      drawImage(g, image, position, 1.0);
   }

}
