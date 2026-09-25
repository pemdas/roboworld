package org.rezrov.roboworld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RobotWindow extends JFrame
      implements RobotDisplayTarget {

   // (Maximum) frames per second at which animations run. Note that things will
   // still work correctly if the computer can't maintain this framerate (which,
   // given the modest demands, is a pretty rare situation). We just don't want
   // to burn a bunch of laptop battery spamming frames at a ridiculous rate on
   // faster hardware.
   private final static int TARGET_FPS = 60;

   private WorldPanel worldPanel;

   // Slider controlling the "playing" speed.
   private JSlider speedSlider;
   // Discrete values on the speed slider. 1x is the default.
   // The last value is labeled MAX
   static private int[] SPEED_SLIDER_VALUES = { 1, 2, 5, 10, 50, Integer.MAX_VALUE };
   double worldSpeedMultiplier = 1.0;

   // Play-pause button
   private JButton playButton;

   // When false, the robot stops wherever it is.
   private boolean running = false;

   // Icons used on the play button for playing and pausing state.
   private ImageIcon playIcon = new ImageIcon(Resources.PLAY_ICON);
   private ImageIcon pauseIcon = new ImageIcon(Resources.PAUSE_ICON);

   // Status elements, used to report how many times the robot has done
   // each thing it does.
   private JTextField movesStatus = new JTextField();
   private JTextField leftTurnsStatus = new JTextField();
   private JTextField rightTurnsStatus = new JTextField();
   private JTextField moveCallSitesStatus = new JTextField();
   private JTextField leftTurnCallSitesStatus = new JTextField();
   private JTextField rightTurnCallSitesStatus = new JTextField();

   // Has the main thread exited (and thus we won't see any more commands from a
   // Robot)?
   private boolean robotDone = false;

   private Scenario scenario;

   GoalStatus[] goalStatuses;

   RobotStats robotStats = new RobotStats();

   // Timer used to run the update loop.
   private javax.swing.Timer timer;

   // Time of the most recent frame update.
   long prevFrameTimeNanos;

   static private JLabel makeLabel(String text, Font font) {
      JLabel ret = new JLabel(text);
      ret.setFont(font);
      return ret;
   }

   public void setRobotDone() {
      robotDone = true;
   }

   private JComponent createGoalPanel() {
      JPanel panel = new JPanel();
      var goalBorder = BorderFactory.createTitledBorder("Goals");
      goalBorder.setTitleFont(Resources.MEDIUM_FONT);
      panel.setBorder(goalBorder);

      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      int row = 0;
      goalStatuses = new GoalStatus[scenario.goals().size()];

      for (var goal : scenario.goals()) {
         goalStatuses[row] = new GoalStatus(goal);
         goalStatuses[row].setAlignmentX(Component.LEFT_ALIGNMENT);
         panel.add(goalStatuses[row]);
         row++;
      }
      // ALlow maximum size horizontally to be large so we fill the pane,
      // vertically, take minimum space
      Dimension maxSize = panel.getPreferredSize();
      maxSize.width = Integer.MAX_VALUE;
      panel.setMaximumSize(maxSize);
      panel.setAlignmentX(Component.LEFT_ALIGNMENT);
      return panel;
   }

   private JPanel createStatusPanel() {
      JPanel statusPanel = new JPanel();
      var statusBorder = BorderFactory.createTitledBorder("Status");
      statusBorder.setTitleFont(Resources.MEDIUM_FONT);
      statusPanel.setBorder(statusBorder);
      statusPanel.setLayout(new GridBagLayout());

      String[] statusLabels = { "Moves:", "Left Turns:", "Right Turns:", "Move Callsites:", "Left Turn Callsites:",
            "Right Turn Callsites:" };

      JTextField[] statusFields = { movesStatus, leftTurnsStatus, rightTurnsStatus, moveCallSitesStatus,
            leftTurnCallSitesStatus, rightTurnCallSitesStatus
      };
      assert statusLabels.length == statusFields.length;
      var c = new GridBagConstraints();
      c.insets.top = 5;
      c.insets.bottom = 0;
      for (int i = 0; i < statusLabels.length; i++) {
         c.gridx = 0;
         c.gridy = i;
         c.insets.left = 10;
         c.insets.right = 5;
         if (i == statusLabels.length - 1) {
            // Last row gets padding on bottom to match top of table padding.
            c.insets.bottom = 10;
         }
         c.fill = GridBagConstraints.HORIZONTAL;
         var l = makeLabel(statusLabels[i], Resources.MEDIUM_FONT);
         l.setHorizontalAlignment(SwingConstants.RIGHT);
         statusPanel.add(l, c);
         c.gridx = 1;
         c.insets.left = 0;
         c.insets.right = 10;
         c.fill = GridBagConstraints.NONE;
         statusFields[i].setText("0");
         // statusFields[i].setFont(Resources.MEDIUM_FONT);
         statusFields[i].setHorizontalAlignment(SwingConstants.CENTER);
         statusFields[i].setEditable(false);
         statusFields[i].setBackground(Color.WHITE);
         statusFields[i].setPreferredSize(new Dimension(50, 25));
         statusPanel.add(statusFields[i], c);
         c.insets.top = 3; // For all rows after the first.
      }

      statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, statusPanel.getPreferredSize().height));
      statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

      return statusPanel;
   }

   private JPanel createRightPanel() {
      JPanel rightPanel = new JPanel();
      rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
      rightPanel.add(createStatusPanel());
      rightPanel.add(createGoalPanel());
      rightPanel.add(Box.createVerticalGlue());
      return rightPanel;
   }

   private JPanel createBottomPanel() {
      JPanel bottomPanel = new JPanel();

      playButton = new JButton();
      playButton.setIcon(playIcon);
      playButton.setFocusPainted(false);
      playButton.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
            playButton.setIcon(running ? playIcon : pauseIcon);
            synchronized (RobotWindow.this) {
               running = !running;
               if (running) {
                  RobotWindow.this.notifyAll();
               }
            }
         }
      });

      bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
      bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
      bottomPanel.add(playButton);
      bottomPanel.add(Box.createRigidArea(new Dimension(40, 0)));

      bottomPanel.add(makeLabel("Speed", Resources.LARGE_FONT));
      speedSlider = new JSlider(JSlider.HORIZONTAL, 0, SPEED_SLIDER_VALUES.length - 1, 0);
      speedSlider.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent e) {
            synchronized (RobotWindow.this) {
               worldSpeedMultiplier = SPEED_SLIDER_VALUES[speedSlider.getValue()];
            }
         }
      });
      Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
      for (int i = 0; i < SPEED_SLIDER_VALUES.length - 1; i++) {
         sliderLabels.put(i, makeLabel("" + SPEED_SLIDER_VALUES[i] + "x", Resources.SMALL_FONT));
      }
      sliderLabels.put(SPEED_SLIDER_VALUES.length - 1, makeLabel("MAX", Resources.SMALL_FONT));
      speedSlider.setLabelTable(sliderLabels);
      speedSlider.setPaintLabels(true);
      speedSlider.setSnapToTicks(true);
      speedSlider.setUI(new MetalSnapSliderUI());
      speedSlider.setMaximumSize(new Dimension(0, 1000)); // Don't stretch slider
      bottomPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Add a little space between label and slider
      bottomPanel.add(speedSlider);
      bottomPanel.add(Box.createHorizontalGlue());
      return bottomPanel;
   }

   public RobotWindow(String title, Scenario scenario) {
      super(title);
      this.scenario = scenario;
      setMinimumSize(new Dimension(600, 400));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      worldPanel = new WorldPanel(scenario.world(), Color.CYAN, TimeSource.worldTimeSource());
      getContentPane().add(worldPanel, BorderLayout.CENTER);
      getContentPane().add(createBottomPanel(), BorderLayout.PAGE_END);
      getContentPane().add(createRightPanel(), BorderLayout.LINE_END);

      // display it
      pack();
      setVisible(true);
      timer = new javax.swing.Timer(Math.round(1000.0f / TARGET_FPS), new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            timerFired();
         }
      });
      prevFrameTimeNanos = System.nanoTime();
      timer.start();
      setVisible(true);
   }

   public void timerFired() {
      long now = System.nanoTime();
      double dt = (now - prevFrameTimeNanos) / 1_000_000_000.0;
      prevFrameTimeNanos = System.nanoTime();
      TimeSource.wallTimeSource().advance(dt);
      if (robotDone) {
         // App thread exited, we don't need to do any more animations, so we can stop
         // this update timer.
         timer.stop();
         running = false;
         playButton.setIcon(playIcon);
         playButton.setEnabled(false);
         updateGoalsStatus();
      } else if (running) {
         TimeSource.worldTimeSource().advance(worldSpeedMultiplier * dt);
         updateGoalsStatus();
      }
      worldPanel.paintImmediately(0, 0, worldPanel.getWidth(), worldPanel.getHeight());
      // X11 likes to kind of nagle algorithm events sometimes, which causes latency.
      // Flush rendering out immediately.
      Toolkit.getDefaultToolkit().sync();
   }

   private void updateGoalsStatus() {
      for (var g : goalStatuses) {
         g.update();
      }
   }

   // Note this is called from the application thread, not the Swing thread.
   @Override
   public void moveRobot(ContinuousWorldPosition from, ContinuousWorldPosition to, double movementTime) {
      synchronized (this) {
         // Pause here if we're not running.
         while (!running) {
            try {
               wait();
            } catch (InterruptedException e) {
            }
         }
         if (worldSpeedMultiplier == Integer.MAX_VALUE) {
            movementTime = 0;
         }
      }
      // Don't hold the lock for this object while the robot moves.
      assert !Thread.holdsLock(this);
      worldPanel.moveRobot(from, to, movementTime);
   }

   @Override
   public void setRobotCarriedItem(Item item) {
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            worldPanel.setRobotCarriedItem(item);
         }
      });
   }

   // Note this is called from the application thread, not the Swing thread.
   @Override
   public void updateRobotStats(RobotStats newStats) {
      RobotStats copy = new RobotStats(newStats);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            leftTurnsStatus.setText("" + copy.numTurnsLeft);
            rightTurnsStatus.setText("" + copy.numTurnsRight);
            movesStatus.setText("" + copy.numMovesForward);
            leftTurnCallSitesStatus.setText("" + copy.numTurnLeftCallSites);
            rightTurnCallSitesStatus.setText("" + copy.numTurnRightCallSites);
            moveCallSitesStatus.setText("" + copy.numMoveForwardCallSites);
         }
      });
   }
}
