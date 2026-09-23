package org.rezrov.roboworld;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GoalStatus extends JPanel {
   private GoalCheckBox checkBox;

   public GoalStatus(Goal goal) {
      setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      checkBox = new GoalCheckBox(goal);
      add(checkBox);
      add(Box.createRigidArea(new Dimension(5, 0)));
      JLabel desc = new JLabel(goal.description);
      desc.setFont(Resources.MEDIUM_FONT);
      add(desc);
      add(Box.createHorizontalGlue());
   }

   public void update() {
      checkBox.update();
   }
}
