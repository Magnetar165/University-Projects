#include <WiFi101.h>
// Feather M0 WiFi (WINC1500) pins
const int WINC_CS  = 8, WINC_IRQ = 7, WINC_RST = 4, WINC_EN = 2;

// Motor and sensor pins
const int FL_PWM = 6,  FL_DIR = 5;     // Front Left Motor
const int FR_PWM = 9,  FR_DIR = 10;     // Front Right Motor
const int RL_PWM = 18,  RL_DIR = 19;     // Rear Left Motor
const int RR_PWM = 11,  RR_DIR = 12;     // Rear Right Motor
const int ULTRASOUND_TRIGGER_PIN = 0;
const int ULTRASOUND_ECHO_PIN = 1;
const int LEFT_IR_SENSOR_DIGITAL = A0;
const int RIGHT_IR_SENSOR_DIGITAL = A2;
const int LEFT_IR_SENSOR_ANALOG = A1;
const int RIGHT_IR_SENSOR_ANALOG = A3;
bool isLineFollowingActive = false;
bool isUTurnActive = false;
bool hasEnteredCurve = false;

unsigned long curveStart = 0;
unsigned long endNoLineStart = 0;

// Emergency stop helper variables
float ultrasoundSpeed = 0.0343;
float duration;
float distance;

// Line following helper variables
const int IR_THRESHOLD = 500;
const int LF_LOST_THRESHOLD = 30;
int lostIncrement = -1; // <0 means robot is likely to the left of line; >0 means robot is likely to the right of line
// char lastTurn = 'f'; // 'f' = forward, 'l' = left, 'r' = right

// Manual controls variables
int motorSpeed = 80;
char lastMotionCmd = 'x';

// Distance statistics variables
unsigned long lastDuration = 0;

// Line Following Algorithm for Maze
bool isLineFollowingMazeSolvingActive = false;
enum RobotState{
    FOLLOW_LINE,
    CHECK_INTERSECTION,
    TURN_LEFT_STATE,
    TURN_AROUND_STATE,
    DRIVE_UNTIL_LINE_STATE,
    END_STATE
};

RobotState currentState = FOLLOW_LINE;

unsigned long stateStartTime = 0;

// WiFi access point variables 
const char ssid[] = "FeatherAP";
const char pass[] = "test1234";
WiFiServer server(80);

// convert IPAddress to String
String ipToString(const IPAddress& ip) {
  return String(ip[0]) + "." + String(ip[1]) + "." + String(ip[2]) + "." + String(ip[3]);
}

// Helper functions that can be implemented in other functions to remove clutter
bool onLine(bool left, bool right){
  return left || right;
}

bool isEnd(bool left, bool right){
  return !left && !right;
}



// motor setup function
void setupMotor(int pwm, int dir) {
  pinMode(pwm, OUTPUT);  
  pinMode(dir, OUTPUT);  
}

// stop all motors (set speed to 0)
void stopAllMotors() {
  setMotor(FL_PWM, FL_DIR, 0, true);  
  setMotor(FR_PWM, FR_DIR, 0, true);  
  setMotor(RL_PWM, RL_DIR, 0, true);  
  setMotor(RR_PWM, RR_DIR, 0, true);  
}

// drive a motor at a specific speed and direction
void setMotor(int pwm, int dir, int speed, bool forward) {
  digitalWrite(dir, forward ? HIGH : LOW);  
  analogWrite(pwm, speed);                  
}

// moves forward for 2 seconds
void moveForward() {
  unsigned long start = millis();
  setMotor(FL_PWM, FL_DIR, motorSpeed, true);
  setMotor(FR_PWM, FR_DIR, motorSpeed, true);
  setMotor(RL_PWM, RL_DIR, motorSpeed, true);
  setMotor(RR_PWM, RR_DIR, motorSpeed, true);

  delay(2000);
  stopAllMotors();
  unsigned long end = millis();
  lastDuration = end - start;
    Serial.print("moveForward() time: ");
    Serial.println(lastDuration);
}

// moves backwards for 2 seconds
void moveReverse() {
  unsigned long start = millis();
  setMotor(FL_PWM, FL_DIR, motorSpeed, false);
  setMotor(FR_PWM, FR_DIR, motorSpeed, false);
  setMotor(RL_PWM, RL_DIR, motorSpeed, false);
  setMotor(RR_PWM, RR_DIR, motorSpeed, false);

  delay(2000); 
  stopAllMotors();
  unsigned long end = millis();
  lastDuration = end - start;
    Serial.print("moveReverse() time: ");
    Serial.println(lastDuration);
}

// turns counterclockwise for 2 seconds
void turnLeft() {
  unsigned long start = millis();
  setMotor(FL_PWM, FL_DIR, motorSpeed*1.2, false);
  setMotor(FR_PWM, FR_DIR, motorSpeed*1.2, true);
  setMotor(RL_PWM, RL_DIR, motorSpeed*1.2, false);
  setMotor(RR_PWM, RR_DIR, motorSpeed*1.2, true);

  delay(2000); 
  stopAllMotors();
  unsigned long end = millis();
  lastDuration = end - start;
    Serial.print("turnLeft() time: ");
    Serial.println(lastDuration);
}

// turns clockwise for 2 seconds
void turnRight() {
  unsigned long start = millis();
  setMotor(FL_PWM, FL_DIR, motorSpeed*1.2, true);
  setMotor(FR_PWM, FR_DIR, motorSpeed*1.2, false);
  setMotor(RL_PWM, RL_DIR, motorSpeed*1.2, true);
  setMotor(RR_PWM, RR_DIR, motorSpeed*1.2, false);

  delay(2000); 
  stopAllMotors();
  unsigned long end = millis();
  lastDuration = end - start;
    Serial.print("turnRight() time: ");
    Serial.println(lastDuration);
}
void spinLeft() {
  unsigned long start = millis();

  setMotor(FL_PWM, FL_DIR, 0, false);
  setMotor(FR_PWM, FR_DIR, 0, false);
  setMotor(RL_PWM, RL_DIR, motorSpeed, true);
  setMotor(RR_PWM, RR_DIR, motorSpeed, true);

  delay(1000); 
  stopAllMotors();

  unsigned long end = millis();
   lastDuration = end - start;
    Serial.print("spinLeft() time: ");
    Serial.println(lastDuration);
}

void spinRight() {
  unsigned long start = millis();

  setMotor(FL_PWM, FL_DIR, motorSpeed, true);
  setMotor(FR_PWM, FR_DIR, motorSpeed, true);
  setMotor(RL_PWM, RL_DIR, 0, false);
  setMotor(RR_PWM, RR_DIR, 0, false);

  delay(1000);
  stopAllMotors();

  unsigned long end = millis();
   lastDuration = end - start;
    Serial.print("spinRight() time: ");
    Serial.println(lastDuration);
}
// moves laterally left for 2 seconds
void moveLeft(){
  unsigned long start = millis();
  setMotor(FL_PWM, FL_DIR, motorSpeed*1.5, false);
  setMotor(FR_PWM, FR_DIR, motorSpeed*1.5, true);
  setMotor(RL_PWM, RL_DIR, motorSpeed*1.5, true);
  setMotor(RR_PWM, RR_DIR, motorSpeed*1.5, false);

  delay(2000); 
  stopAllMotors();
  unsigned long end = millis();
  lastDuration = end - start;
    Serial.print("moveLeft() time: ");
    Serial.println(lastDuration);
}

// moves laterally right for 2 seconds
void moveRight(){
  unsigned long start = millis();
  setMotor(FL_PWM, FL_DIR, motorSpeed*1.5, true);
  setMotor(FR_PWM, FR_DIR, motorSpeed*1.5, false);
  setMotor(RL_PWM, RL_DIR, motorSpeed*1.5, false);
  setMotor(RR_PWM, RR_DIR, motorSpeed*1.5, true);

  delay(2000); 
  stopAllMotors();
  unsigned long end = millis();
  lastDuration = end - start;
    Serial.print("moveRight() time: ");
    Serial.println(lastDuration);
}

// setup robot before launch
void setup() {
  Serial.begin(115200);
  pinMode(ULTRASOUND_TRIGGER_PIN, OUTPUT);
  pinMode(ULTRASOUND_ECHO_PIN, INPUT);
  pinMode(LEFT_IR_SENSOR_DIGITAL, INPUT);
  pinMode(RIGHT_IR_SENSOR_DIGITAL, INPUT);
  pinMode(LEFT_IR_SENSOR_ANALOG, INPUT);
  pinMode(RIGHT_IR_SENSOR_ANALOG, INPUT);

  WiFi.setPins(WINC_CS, WINC_IRQ, WINC_RST, WINC_EN);

  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WINC1500 not detected"); while (1) {}
  }

  Serial.print("FW: "); Serial.println(WiFi.firmwareVersion());
  Serial.println("Starting AP…");
  int s = WiFi.beginAP(ssid, pass, 6);            
  if (s != WL_AP_LISTENING) {
    Serial.print("WPA2 AP failed ("); Serial.print(s); Serial.println("). Trying OPEN…");
    s = WiFi.beginAP(ssid, 6);                    
    if (s != WL_AP_LISTENING) { Serial.println("AP failed"); while (1) {} }
  }

  delay(8000); 
  Serial.print("AP IP: "); Serial.println(ipToString(WiFi.localIP())); 
  server.begin();

  setupMotor(FL_PWM, FL_DIR);
  setupMotor(FR_PWM, FR_DIR);
  setupMotor(RL_PWM, RL_DIR);
  setupMotor(RR_PWM, RR_DIR);
  stopAllMotors();
}

void serve(WiFiClient& c){
  c.setTimeout(1500);
  String rl=c.readStringUntil('\n');        
  int sp1=rl.indexOf(' '), sp2=rl.indexOf(' ',sp1+1);
  String uri=(sp1>0&&sp2>sp1)?rl.substring(sp1+1,sp2):"/";
  int q=uri.indexOf('?'); String pth=(q>=0)?uri.substring(0,q):uri; String qry=(q>=0)?uri.substring(q+1):"";
  while(true){ String h=c.readStringUntil('\n'); if(h.length()==0||h=="\r") break; } 
  route(c,pth,qry);
}

void route(WiFiClient& c,const String& path,const String& q){
  if(path=="/"||path=="") { handleRoot(c); return; }
  if(path=="/forward")    { handleForward(c); return; }
  if(path == "/reverse")  { handleReverse(c); return; }
  if(path == "/turn_left") { handleTurnLeft(c); return; }
  if(path == "/turn_right") { handleTurnRight(c); return; }
  if(path == "/spin_left") { handleSpinLeft(c); return; }
  if(path == "/spin_right") { handleSpinRight(c); return; }
  if(path == "/left") { handleMoveLeft(c); return; }
  if(path == "/right") { handleMoveRight(c); return; }
  if(path == "/sensors") { handleSensors(c); return; }
  if(path == "/distance") { handleDistance(c); return; }
  if(path == "/start_line_follow") { handleStartLineFollow(c); return; }
  if(path == "/stop_line_follow")  { handleStopLineFollow(c); return; }
  if(path == "/u_turn") { handleUTurn(c); return; }
}

void handleRoot(WiFiClient& client){
  const char body[] = "Initial Page";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}
void handleUTurn(WiFiClient& client) {
  isUTurnActive = true;
  hasEnteredCurve = false;
  endNoLineStart = 0;

  const char body[] = "U-Turn started";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/plain\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1);
  client.print("\r\n\r\n");
  client.print(body);
}


void handleForward(WiFiClient& client){
  moveForward();
  const String body = "Moved Forward.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleReverse(WiFiClient& client){
  moveReverse();
  const char body[] = "Moved Backwards.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleTurnLeft(WiFiClient& client){
  turnLeft();
  const char body[] = "Turned Left.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleTurnRight(WiFiClient& client){
  turnRight();
  const char body[] = "Turned Right.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleSpinLeft(WiFiClient& client){
  spinLeft();
  const char body[] = "Spinned Left.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleSpinRight(WiFiClient& client){
  spinRight();
  const char body[] = "Spinned Right.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleMoveLeft(WiFiClient& client){
  moveLeft();
  const char body[] = "Moved Left.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleMoveRight(WiFiClient& client){
  moveRight();
  const char body[] = "Moved Right.";
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); client.print(sizeof(body) - 1); client.print("\r\n\r\n");
  client.print(body);
  delay(1);
}

void handleStartLineFollow(WiFiClient& client) {
  isLineFollowingActive = true;
  const char body[] = "Line Following Started!";

  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); 
  client.print(sizeof(body) - 1);
  client.print("\r\n\r\n");
  client.print(body);
}

void handleStopLineFollow(WiFiClient& client) {
  isLineFollowingActive = false;
  stopAllMotors();

  const char body[] = "Line Following Stopped";

  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: text/html\r\n");
  client.print("Connection: close\r\n");
  client.print("Content-Length: "); 
  client.print(sizeof(body) - 1);
  client.print("\r\n\r\n");
  client.print(body);
}

void handleDistance(WiFiClient& client) {
  digitalWrite(ULTRASOUND_TRIGGER_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(ULTRASOUND_TRIGGER_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(ULTRASOUND_TRIGGER_PIN, LOW);
  duration = pulseIn(ULTRASOUND_ECHO_PIN, HIGH);
  distance = (duration * ultrasoundSpeed)/2;
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Types: application/json\r\n");
  client.print("Connection: close\r\n\r\n");
  client.print("{\"distance_cm\":"); client.print(distance, 2);
  client.print("}");
}

void handleSensors(WiFiClient& client) {
  int leftValue = analogRead(LEFT_IR_SENSOR_ANALOG);
  int rightValue = analogRead(RIGHT_IR_SENSOR_ANALOG);
  
  
  bool leftOnLine = leftValue < IR_THRESHOLD;
  bool rightOnLine = rightValue < IR_THRESHOLD;
  
  client.print("HTTP/1.1 200 OK\r\n");
  client.print("Content-Type: application/json\r\n");
  client.print("Connection: close\r\n\r\n");
  client.print("{\"left\":"); client.print(leftOnLine ? "true" : "false");
  client.print(",\"right\":"); client.print(rightOnLine ? "true" : "false");
  client.print("}");
  delay(1);
}


void lineFollowStep() {
  bool leftDigital = digitalRead(LEFT_IR_SENSOR_DIGITAL);
  bool rightDigital = digitalRead(RIGHT_IR_SENSOR_DIGITAL);

  Serial.print("IR Left: ");
  Serial.print(leftDigital);
  Serial.print(" IR Right: ");
  Serial.println(rightDigital);

  int baseSpeed = 80;
  int turnSpeed = 40;

  if (leftDigital && !rightDigital) { // touching left sensor
    lostIncrement = 1;
    // turn left
    setMotor(FL_PWM, FL_DIR, turnSpeed*2.5, false);
    setMotor(FR_PWM, FR_DIR, turnSpeed*2.5, true);
    setMotor(RL_PWM, RL_DIR, turnSpeed*2.5, false);
    setMotor(RR_PWM, RR_DIR, turnSpeed*2.5, true);
    // lastTurn = 'l';
    delay(200);
  }
  else if (!leftDigital && rightDigital) { // touching right sensor
    lostIncrement = -1;
    // turn right
    setMotor(FL_PWM, FL_DIR, turnSpeed*2.5, true);
    setMotor(FR_PWM, FR_DIR, turnSpeed*2.5, false);
    setMotor(RL_PWM, RL_DIR, turnSpeed*2.5, true);
    setMotor(RR_PWM, RR_DIR, turnSpeed*2.5, false);
    // lastTurn = 'r';
    delay(200);
  }
  else if (!leftDigital && !rightDigital) { // not touching any sensors
    if (lostIncrement >= 0) {
      lostIncrement++;
    }
    else {
      lostIncrement--;
    }

    if (lostIncrement > LF_LOST_THRESHOLD) {
      // turn left
      setMotor(FL_PWM, FL_DIR, turnSpeed*2.3, false);
      setMotor(FR_PWM, FR_DIR, turnSpeed*2.3, true);
      setMotor(RL_PWM, RL_DIR, turnSpeed*2.3, false);
      setMotor(RR_PWM, RR_DIR, turnSpeed*2.3, true);
      delay(20);
    }
    else if (lostIncrement < -LF_LOST_THRESHOLD) {
      // turn right
      setMotor(FL_PWM, FL_DIR, turnSpeed*2.3, true);
      setMotor(FR_PWM, FR_DIR, turnSpeed*2.3, false);
      setMotor(RL_PWM, RL_DIR, turnSpeed*2.3, true);
      setMotor(RR_PWM, RR_DIR, turnSpeed*2.3, false);
      delay(20);
    }
    else {
      // go forward
      setMotor(FL_PWM, FL_DIR, baseSpeed, true);
      setMotor(FR_PWM, FR_DIR, baseSpeed, true);
      setMotor(RL_PWM, RL_DIR, baseSpeed, true);
      setMotor(RR_PWM, RR_DIR, baseSpeed, true);
      delay(5);
    }
  }
  else {
    stopAllMotors();
    delay(150);

    lostIncrement = 1;
    // turn left
    setMotor(FL_PWM, FL_DIR, turnSpeed*3, false);
    setMotor(FR_PWM, FR_DIR, turnSpeed*3, true);
    setMotor(RL_PWM, RL_DIR, turnSpeed*3, false);
    setMotor(RR_PWM, RR_DIR, turnSpeed*3, true);
    // lastTurn = 'l';
    delay(200);
  }
}

//This function uses line-following to make the robot follow a corridor to do a U-turn
void uTurnStep(){
  bool left  = digitalRead(LEFT_IR_SENSOR_DIGITAL);
  bool right = digitalRead(RIGHT_IR_SENSOR_DIGITAL);

  int savedSpeed = motorSpeed;
  motorSpeed = 55;
  lineFollowStep();
  motorSpeed = savedSpeed;

//we check whether one sensor is active more that usual, therefore we're in the turning point of the u-turn
  if(!hasEnteredCurve){
    if((left && !right) || (!left && right)){
      if(curveStart == 0) curveStart = millis(); // i use millis here, since it doesn't block the functionality of the board like delay does. This should be changed for other functions too
      if(millis() - curveStart >= 180){
        hasEnteredCurve = true;
      }
    } else {
      curveStart = 0;
    }
  }

  if(hasEnteredCurve && isEnd(left, right)){
    if(endNoLineStart == 0) endNoLineStart = millis();
    if(millis() - endNoLineStart >= 200){
      stopAllMotors();
      isUTurnActive = false;
      hasEnteredCurve = false;
      curveStart = 0;
      endNoLineStart = 0;
    }
  } else {
    endNoLineStart = 0;
  }
}

void checkEmergencyStop() {
  digitalWrite(ULTRASOUND_TRIGGER_PIN, LOW);
  delayMicroseconds(2);
  digitalWrite(ULTRASOUND_TRIGGER_PIN, HIGH);
  delayMicroseconds(10);
  digitalWrite(ULTRASOUND_TRIGGER_PIN, LOW);
  duration = pulseIn(ULTRASOUND_ECHO_PIN, HIGH);
  distance = (duration * ultrasoundSpeed)/2;

  const float EMERGENCY_STOP_DISTANCE = 15.0;

  if (distance < EMERGENCY_STOP_DISTANCE) {
    stopAllMotors();
    Serial.println("EMERGENCY STOP: Obstacle detected!");
    
    delay(500);
  }
}

void loop() {
   if (isUTurnActive) {
      uTurnStep();
    }
  }

  if (isLineFollowingActive) {
    checkEmergencyStop();
    if (isLineFollowingActive) {
      lineFollowStep();
    }
  }

  WiFiClient client = server.available();
  if (!client) return;
  client.setTimeout(2000);
  serve(client);
  client.stop();
}
