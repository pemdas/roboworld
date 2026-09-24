package org.rezrov.roboworld;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

// A widget that combines a graphics check box with a label.
class GoalCheckBox extends JPanel {
   private Goal goal;
   private boolean renderedGoalState;
   static private final BufferedImage SUCCESS_CHECKBOX = Resources.loadImage("goal_success_checkbox_icon.png");
   static private final BufferedImage FAILED_CHECKBOX = Resources.loadImage("goal_failed_checkbox_icon.png");
   // static private final BufferedImage EMPTY_CHECKBOX =
   // Resources.loadImage("empty_checkbox.png");

   GoalCheckBox(Goal goal) {
      this.goal = goal;
      renderedGoalState = goal.goalSatisfied();
      var size = new Dimension(SUCCESS_CHECKBOX.getWidth(), SUCCESS_CHECKBOX.getHeight());
      setMaximumSize(size);
      setMinimumSize(size);
      setPreferredSize(size);
   }

   /**
    * Query the associated goal and update the widget if the state of the goal has
    * changed.
    */
   public void update() {
      if (goal.goalSatisfied() != renderedGoalState) {
         repaint();
      }
   }

   @Override
   public void paintComponent(Graphics g) {
      renderedGoalState = goal.goalSatisfied();
      g.clearRect(0, 0, getWidth(), getHeight());
      BufferedImage img;
      if (renderedGoalState) {
         img = SUCCESS_CHECKBOX;
      } else {
         img = FAILED_CHECKBOX;
      }
      ((Graphics2D) g).drawRenderedImage(img, new AffineTransform());
   }
}
