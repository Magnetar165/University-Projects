Welcome to our controlling software for the robot car.

First, open the wifi_http_dummy_code.ino file from the wifi_http_dummy_code folder in ArduinoIDE.

Then connect to the Arduino board and upload the code from wifi_http_dummy_code.ino.


Next, compile all files (ArduinoClient.java, DistanceCalc.java, GUI.java, MovementLogger.java, MovementRecord.java and Sensors.java).


Then run GUI.java.


A window will open and a panel with two buttons will be visible. Press Direction control and you will be taken to the Direction control panel. In the upper left corner, you will see data from sensors (IR sensor, ultrasound sensor) as well as the duration of the current command's execution. At the bottom right, you will see several buttons. Press Start Line Following and the robot will execute the line following algorithm and follow the line. Press the up arrow and the robot will move forwards, press the down arrow and the robot will move backwards, press the left arrow and the robot will move to the left. Press the right arrow to make the robot turn right, press the counterclockwise arrow to make the robot turn counterclockwise, and press the clockwise arrow to make the robot turn clockwise.


Press the Back button to return to the menu.

Press the Distance statistics button to open the panel where you will see distance statistics showing the total distance travelled and the distance travelled for each command.


To exit the GUI, press the X on the window.