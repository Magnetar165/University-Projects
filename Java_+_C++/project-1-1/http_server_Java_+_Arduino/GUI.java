import java.awt.*;
import java.awt.event.*;
import javax.swing.*;



public class GUI extends JFrame {
    //Layout manager that allows switching between panels (cards).
    private CardLayout cardLayout;
    //Container that holds the different panels (cards).
    private JPanel mainPanel;
    private final ArduinoClient arduinoClient = new ArduinoClient();

    private JTable distanceTable;
    private JScrollPane distanceScrollPane;
    private JLabel leftBoolLabel;
    private JLabel rightBoolLabel;
    private JLabel obstacleBoolLabel;
    private Timer sensorTimer;
    private boolean isLineFollowing = false;
    private JLabel ultrasonicLabel;
    private JLabel durationLabel;

    //Scene Switcher
    public GUI() {
        setTitle("The CEO");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 900);                 //size of the window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // // The two screens in the CardLayout container
        mainPanel.add(MenuPanel(), "Menu");
        mainPanel.add(DirectionPanel(), "Direction");
        mainPanel.add(DistancePanel(), "Distance");

        //// Add the container to the frame and make it visible
        add(mainPanel);
        setVisible(true);       

        
    }

    private JPanel MenuPanel() {
        // Using a null layout so we can position components manually
        JPanel panel = new JPanel(null);
        JButton directionControlButton = new JButton("Direction control");
        JButton distanceButton = new JButton("Distance statistics");
        directionControlButton.setBounds(650, 200, 200, 40);
        distanceButton.setBounds(650, 260, 200, 40);
        //button alignment

        //// When clicked, show the "Direction" card
        directionControlButton.addActionListener(e -> cardLayout.show(mainPanel, "Direction"));
        distanceButton.addActionListener(e -> cardLayout.show(mainPanel, "Distance"));
        
        panel.add(directionControlButton);
        panel.add(distanceButton);
        return panel;
    }
    private JPanel DirectionPanel() {
        JPanel panel = new JPanel(null);
        
        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 830, 100, 30);

        JButton toggleMovementButton = new JButton("Activate/Deactivate");
        JButton startLineFollowing = new JButton("Line Following");

        JButton forwardButton = new JButton("↑");
        JButton backwardButton = new JButton("↓");
        JButton leftButton = new JButton("←");
        JButton rightButton = new JButton("→");
        JButton turnLeftButton = new JButton("⟲");
        JButton turnRightButton = new JButton("⟳");

        JLabel specialManeuversLabel = new JLabel("Special Maneuvers", SwingConstants.CENTER);
        JButton reverseInStraightLineButton = new JButton("Drive Backwards");
        JButton emergencyStopButton = new JButton("Emergency Stop");
        JButton threePointTurnButton = new JButton("3-Point Turn");
        JButton uTurnButton = new JButton("U-Turn");

        JSlider speedEditSlider = new JSlider(80, 160, 80);
        speedEditSlider.setPaintTrack(true);
        speedEditSlider.setPaintTicks(true);
        speedEditSlider.setPaintLabels(true);
        speedEditSlider.setMajorTickSpacing(40);
        speedEditSlider.setMinorTickSpacing(10);
        JLabel speedEditLabel = new JLabel("speed", SwingConstants.CENTER);

        JSlider durationEditSlider = new JSlider(1, 5, 2);
        durationEditSlider.setPaintTrack(true);
        durationEditSlider.setPaintTicks(true);
        durationEditSlider.setPaintLabels(true);
        durationEditSlider.setMajorTickSpacing(1);
        JLabel durationEditLabel = new JLabel("duration", SwingConstants.CENTER);
        // JButton spinLeftButton = new JButton("Spin ←");
        // JButton spinRightButton = new JButton("Spin →");


        toggleMovementButton.setBounds(80, 10, 165, 50);
        startLineFollowing.setBounds(255, 10, 165, 50);

        forwardButton.setBounds(200, 100, 100, 100);
        backwardButton.setBounds(200, 220, 100, 100);
        leftButton.setBounds(80, 220, 100, 100);
        rightButton.setBounds(320, 220, 100, 100);
        turnLeftButton.setBounds(90, 110, 80, 80);
        turnRightButton.setBounds(330, 110, 80, 80);
        speedEditSlider.setBounds(90, 340, 155, 50);
        speedEditLabel.setBounds(20, 350, 60, 30);
        durationEditSlider.setBounds(255, 340, 155, 50);
        durationEditLabel.setBounds(430, 350, 60, 30);

        specialManeuversLabel.setBounds(130, 420, 240, 30);
        reverseInStraightLineButton.setBounds(80, 450, 165, 50);
        emergencyStopButton.setBounds(255, 450, 165, 50);
        threePointTurnButton.setBounds(80, 510, 165, 50);
        uTurnButton.setBounds(255, 510, 165, 50);
        // spinLeftButton.setBounds(80, 350, 100, 100);
        // spinRightButton.setBounds(320, 350, 100, 100);

        JPanel buttonFrame = new JPanel(null);

        buttonFrame.add(toggleMovementButton);
        buttonFrame.add(startLineFollowing);

        buttonFrame.setBounds(800, 100, 500, 650);
        buttonFrame.add(forwardButton);
        buttonFrame.add(backwardButton);
        buttonFrame.add(leftButton);
        buttonFrame.add(rightButton);
        buttonFrame.add(turnLeftButton);
        buttonFrame.add(turnRightButton);
        buttonFrame.add(speedEditSlider);
        buttonFrame.add(speedEditLabel);
        buttonFrame.add(durationEditSlider);
        buttonFrame.add(durationEditLabel);

        buttonFrame.add(specialManeuversLabel);
        buttonFrame.add(reverseInStraightLineButton);
        buttonFrame.add(emergencyStopButton);
        buttonFrame.add(threePointTurnButton);
        buttonFrame.add(uTurnButton);
        // buttonFrame.add(spinRightButton);
        // buttonFrame.add(spinLeftButton);


        speedEditSlider.addChangeListener( new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider s = (JSlider)e.getSource();
                if (!s.getValueIsAdjusting()) { // optional: nur wenn losgelassen
                    int newValue = s.getValue();
                }
            }

        );
        durationEditSlider.addChangeListener(null);


        startLineFollowing.addActionListener(e -> {
            if (!isLineFollowing) {
                arduinoClient.sendCommand("start_line_follow");
                startLineFollowing.setText("Stop Line Following");
                isLineFollowing = true;
            } else {
                arduinoClient.sendCommand("stop_line_follow");
                startLineFollowing.setText("Start Line Following");
                isLineFollowing = false;
            }
        });
        forwardButton.addActionListener(e -> arduinoClient.sendCommand("forward"));
        backwardButton.addActionListener(e -> arduinoClient.sendCommand("reverse"));
        leftButton.addActionListener(e -> arduinoClient.sendCommand("left"));
        rightButton.addActionListener(e -> arduinoClient.sendCommand("right"));
        turnLeftButton.addActionListener(e -> arduinoClient.sendCommand("turn_left"));
        turnRightButton.addActionListener(e -> arduinoClient.sendCommand("turn_right"));
        uTurnButton.addActionListener(e -> arduinoClient.sendCommand("u_turn"));
        // spinLeftButton.addActionListener(e -> arduinoClient.sendCommand("spin_left"));
        // spinRightButton.addActionListener(e -> arduinoClient.sendCommand("spin_right"));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        rightBoolLabel = new JLabel("Right Black line: FALSE");
        leftBoolLabel = new JLabel("Left Black line : FALSE");

        /// Label of the ultrasonic sensor
        ultrasonicLabel = new JLabel("Ultrasonic : --- cm");

       
        JLabel tittleForStatsLabel = new JLabel(" Diagnostics for Sensors ");

        /// Label for the duration
        durationLabel = new JLabel("Millis: 0");

        tittleForStatsLabel.setBounds(160, 1, 200, 30);
        leftBoolLabel.setBounds(200, 17, 300, 30);
        rightBoolLabel.setBounds(200, 47, 300, 30);

        /// set the bounds of the label for ultrasonic sensor data
        ultrasonicLabel.setBounds(200, 77, 300, 30);

        ///  set the bounds of the label for duration
        durationLabel.setBounds(200, 97, 300, 30);
       
        /// Add components to the panel
        panel.add(tittleForStatsLabel);
        panel.add(leftBoolLabel);
        panel.add(rightBoolLabel);
        panel.add(backButton);
        panel.add(buttonFrame);
        panel.add(ultrasonicLabel);
        panel.add(durationLabel);

        //updating sensor labels every 500 ms 
        sensorTimer = new Timer(500, e -> updateSensorLabels());
        sensorTimer.start();

        //updating ultrasonic sensor every 500 ms
        Timer ultrasonicTimer = new Timer(500, e -> updateUltrasonic());
        ultrasonicTimer.start();

        //updating duration every 500 ms
        Timer durationTimer = new Timer(500, e -> updateDuration());
        durationTimer.start();

        
        return panel;
    }

    private JPanel DistancePanel()
    {
        // Using a null layout so we can position components manually
        JPanel panel = new JPanel(null);

        /// Back button to return to the main menu card
        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 830, 100, 30);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Menu"));

        /// Table that will later be filled with distance data
        distanceTable = new JTable();
        distanceTable.setFont(new Font("Arial", Font.PLAIN, 14));
        distanceTable.setRowHeight(26);
        distanceTable.setEnabled(false); // Display only, no editing

        /// Scroll pane so that the table remains scrollable when there are many rows
        distanceScrollPane = new JScrollPane(distanceTable);
        distanceScrollPane.setBounds(50, 50, 800, 140);

        /// Add components to the panel
        panel.add(distanceScrollPane);
        panel.add(backButton);

        /// Load the current statistics
        updateDistanceTable();

        return panel;
    }

    private Object[][] createDistanceTableData (MovementLogger logger)
    {
        /// Subtotal for each possible driving/turning direction
        double forward = 0, reverse = 0, left = 0, right = 0, turn_left = 0, turn_right = 0, total = 0;

        /// Run through all stored movement records
        for (MovementRecord record : logger.getAllRecords())
        {
            /// Calculate the distance travelled for each individual record
            DistanceCalc calc = new DistanceCalc(record);
            double d = calc.getDistance();

            if (record.getCommand().equals("forward"))
            {
                forward = forward + d;
            } else if (record.getCommand().equals("reverse"))
            {
                reverse = reverse + d;
            }else if (record.getCommand().equals("left"))
            {
                left = left + d;
            } else if (record.getCommand().equals("right"))
            {
                right = right + d;
            } else if (record.getCommand().equals("turn_left"))
            {
                turn_left = turn_left + d;
            } else if (record.getCommand().equals("turn_right"))
            {
                turn_right = turn_right + d;
            }
            total = total + d;
        }

        /// Column headings (used by the table model)
        String[] columns = {"Comands", "Distance in m"};

        /// Each entry consists of: command string and formatted distance
        return new Object[][] {
                {"forward", String.format("%.3f", forward)},
                {"reverse", String.format("%.3f", reverse)},
                {"left", String.format("%.3f", left)},
                {"right", String.format("%.3f", right)},
                {"turn_left", String.format("%.3f", turn_left)},
                {"turn_right", String.format("%.3f", turn_right)},
                {"total", String.format("%.3f", total)}
        };


    }

    /// Update method for the table of the driven distance
    private void updateDistanceTable() {
        distanceTable.setModel(new javax.swing.table.DefaultTableModel(createDistanceTableData(arduinoClient.getMovementLogger()), new String[] {"Commands", "Distance in m"}));
    }

    /// Update method for the IR sensor data labels
    private void updateSensorLabels() {
        new Thread(() -> {
            Sensors sensors = arduinoClient.getSensors();
            if (sensors != null) {
                SwingUtilities.invokeLater(() -> {
                    leftBoolLabel.setText("Left Black line : " + (sensors.leftOnLine ? "TRUE" : "FALSE"));
                    rightBoolLabel.setText("Right Black line: " + (sensors.rightOnLine ? "TRUE" : "FALSE"));
                }); // This updates the label into sensoring the black line. It should theoritically work 
            }
        }).start();
    }

    /// Update method for the Ultrasonic sensor data label
    private void updateUltrasonic() {
        new Thread(() -> {
            double distanceUSS = arduinoClient.getDistanceUSS();

                SwingUtilities.invokeLater(() -> {
                    String txtDistanceUSS = (distanceUSS < 0) ? "---" : String.format("%.1f", distanceUSS);
                    ultrasonicLabel.setText("Ultrasonic : " + txtDistanceUSS + " cm");
                });

        }).start();
    }

    /// Update method for the duration of actual command
    private void updateDuration() {
        new Thread(() -> {
                MovementRecord lastDuration = arduinoClient.getMovementLogger().getLastRecord();
                int duration = (lastDuration != null) ? lastDuration.getDuration() : -1;
                if (duration > 0) {
                    SwingUtilities.invokeLater(() -> durationLabel.setText("Millis: " + duration));
                } else {
                    SwingUtilities.invokeLater(() -> durationLabel.setText("Millis: ---"));
                }
        }).start();

    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}

