package org.rezrov.roboworld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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

   static private final BufferedImage SUCCESS_CHECKBOX = Resources.loadImage("success_checkbox.png");
   static private final BufferedImage FAILED_CHECKBOX = Resources.loadImage("failed_checkbox.png");
   static private final BufferedImage EMPTY_CHECKBOX = Resources.loadImage("empty_checkbox.png");

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

   // The application (not swing) thread. We have a reference here so
   // we can detect when the student's code has finished (because the
   // application thread has exited). This is really kludgy, but seems
   // to work ok, and obviates any need for the student code to call some
   // method to indicate it has finished.
   //
   // If java had support for installing a Runnable or similar to be
   // invoked on thread exit, that would be a better way to do this, but
   // I've not found anything (portable) that allows for this on the
   // application thread.
   private Thread appThread;

   // Status elements, used to report how many times the robot has done
   // each thing it does.
   private JTextField movesStatus = new JTextField();
   private JTextField leftTurnsStatus = new JTextField();
   private JTextField rightTurnsStatus = new JTextField();
   private JTextField moveCallSites = new JTextField();
   private JTextField leftTurnCallSites = new JTextField();
   private JTextField rightTurnCallSites = new JTextField();

   // Has the main thread exited?
   private boolean robotDone = false;

   private Scenario scenario;

   GoalCheckBox[] goalCheckBoxes;

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

   class GoalCheckBox extends JPanel {
      private Scenario.Goal goal;

      GoalCheckBox(Scenario.Goal goal) {
         this.goal = goal;
         var size = new Dimension(SUCCESS_CHECKBOX.getWidth(), SUCCESS_CHECKBOX.getHeight());
         setMaximumSize(size);
         setMinimumSize(size);
         setPreferredSize(size);
      }

      @Override
      public void paintComponent(Graphics g) {
         g.clearRect(0, 0, getWidth(), getHeight());
         BufferedImage img;
         if (goal.goalSatisfied()) {
            img = SUCCESS_CHECKBOX;
         } else if (!robotDone) {
            img = EMPTY_CHECKBOX;
         } else {
            img = FAILED_CHECKBOX;
         }
         ((Graphics2D) g).drawRenderedImage(img, new AffineTransform());
      }

   }

   private JComponent createGoalPanel() {
      JPanel panel = new JPanel();
      var goalBorder = BorderFactory.createTitledBorder("Status");
      goalBorder.setTitleFont(Resources.MEDIUM_FONT);
      panel.setBorder(goalBorder);

      panel.setLayout(new GridBagLayout());
      int row = 0;
      goalCheckBoxes = new GoalCheckBox[scenario.goals().size()];
      for (var goal : scenario.goals()) {
         var gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = row;
         goalCheckBoxes[row] = new GoalCheckBox(goal);
         panel.add(goalCheckBoxes[row], gbc);
         gbc.gridx = 1;
         panel.add(makeLabel(goal.description, Resources.MEDIUM_FONT));
         row++;
      }
      return panel;
   }

   private JPanel createStatusPanel() {
      JPanel statusPanel = new JPanel();
      var statusBorder = BorderFactory.createTitledBorder("Status");
      statusBorder.setTitleFont(Resources.MEDIUM_FONT);
      statusPanel.setBorder(statusBorder);
      statusPanel.setLayout(new GridBagLayout());

      String[] statusLabels = { "Moves:", "Left Turns:", "Right Turns:" };

      JTextField[] statusFields = { movesStatus, leftTurnsStatus, rightTurnsStatus };
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
         statusFields[i].setFont(Resources.MEDIUM_FONT);
         statusFields[i].setHorizontalAlignment(SwingConstants.CENTER);
         statusFields[i].setEditable(false);
         statusFields[i].setBackground(Color.WHITE);
         statusFields[i].setPreferredSize(new Dimension(50, 25));
         statusPanel.add(statusFields[i], c);
         c.insets.top = 3; // For all rows after the first.
      }

      statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, statusPanel.getPreferredSize().height));
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

   public RobotWindow(String title, Thread appThread, Scenario scenario) {
      super(title);
      this.appThread = appThread;
      this.scenario = scenario;
      setMinimumSize(new Dimension(600, 400));
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      worldPanel = new WorldPanel(scenario.environment(), Color.CYAN, TimeSource.worldTimeSource(),
            scenario.robotStartPosition());
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
         for (GoalCheckBox g : goalCheckBoxes) {
            g.repaint();
         }
      } else if (running) {
         TimeSource.worldTimeSource().advance(worldSpeedMultiplier * dt);
         for (GoalCheckBox g : goalCheckBoxes) {
            g.repaint();
         }
      }
      worldPanel.paintImmediately(0, 0, worldPanel.getWidth(), worldPanel.getHeight());
      // X11 likes to kind of nagle algorithm events sometimes, which causes latency.
      // Flush rendering out immediately.
      Toolkit.getDefaultToolkit().sync();
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

   // Note this is called from the application thread, not the Swing thread.
   @Override
   public void updateRobotStats(RobotStats newStats) {
      RobotStats copy = new RobotStats(newStats);
      SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            leftTurnsStatus.setText("" + copy.numTurnsLeft);
            rightTurnsStatus.setText("" + copy.numTurnsRight);
            movesStatus.setText("" + copy.numMovesForward);
         }
      });
   }
}
