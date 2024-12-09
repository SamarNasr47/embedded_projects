#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <Keypad.h>
#include <LiquidCrystal.h>
#include <ESP32Servo.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include <Ultrasonic.h>

// Replace these with your network credentials
const char* ssid = "####";
const char* password = "####";

// Replace with your Firebase project details
#define DATABASE_URL "https://mysmarthome-d0d8d-default-rtdb.firebaseio.com/"
#define API_KEY "AIzaSyBt53slz1OSWLxZtbYR8JRrke-G6hoizoc"

Initialize the LCD (RS, E, D4, D5, D6, D7)
LiquidCrystal lcd(2, 4, 14, 25, 35, 34);

// Keypad configuration
const byte ROWS = 4; // four rows
const byte COLS = 4; // four columns
char keys[ROWS][COLS] = {
  {'1','2','3','A'},
  {'4','5','6','B'},
  {'7','8','9','C'},
  {'*','0','#','D'}
};
byte rowPins[ROWS] = {22, 23, 21, 19}; // connect to the row pinouts of the keypad
byte colPins[COLS] = {27, 26, 33, 32}; // connect to the column pinouts of the keypad
Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);

// Ultrasonic sensor pins
const int trigPin = 13;
const int echoPin = 12;

// Servo motor pin
const int servoPin = 15;
Servo myServo;

// LED pins
const int lightLedPin = 18;
const int fanLedPin = 5;

// Threshold distance in centimeters
const int distanceThreshold = 50;

// Firebase data object
FirebaseData fbdo;
FirebaseConfig config;
FirebaseAuth auth;

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;

bool doorLocked = true; // Fixed typo from "doorLooked" to "doorLocked"
bool lightStatus = false;
bool fanStatus = false;

void setup() {
  Serial.begin(115200);

  Initialize LCD
  lcd.begin(16, 2);

  // Initialize WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("Connected with IP: "); 
  Serial.println(WiFi.localIP()); 
  Serial.println();
  
  // Initialize Firebase
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("signUp OK");
    signupOK = true;
  } else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
    Serial.printf("%s\n------------------------------------------------");
  }
  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // Initialize ultrasonic sensor pins
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  // Initialize servo motor
  myServo.attach(servoPin);
  myServo.write(0); // Initial position

  // Initialize LED pins
  pinMode(lightLedPin, OUTPUT);
  pinMode(fanLedPin, OUTPUT);
  digitalWrite(lightLedPin, LOW); // Ensure LEDs are off initially
  digitalWrite(fanLedPin, LOW);   // Ensure LEDs are off initially
}

void loop() {
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();

    //password and entry 
    char enteredPassword[5];
    int idx = 0;
    lcd.clear();
    lcd.print("Enter Password:");

    if (doorLocked) {
      while (idx < 4) {
        char key = keypad.getKey();
        if (key) {
          enteredPassword[idx] = key;
          idx++;
          Serial.print("Entered key: ");
          Serial.println(key);
        }
      }
      enteredPassword[4] = '\0'; // Null-terminate the string

      // Verify password
      if (Firebase.RTDB.getString(&fbdo, "/password")) {
        String storedPassword = fbdo.stringData();
        String enteredPasswordStr = enteredPassword;
        Serial.print("Stored Password: ");
        Serial.println(storedPassword);
        Serial.print("Entered Password: ");
        Serial.println(enteredPasswordStr);

        if (storedPassword == enteredPasswordStr) {
          Serial.println("Correct Password");
          doorLocked = false;
          bool x = false;
          if (Firebase.RTDB.setBool(&fbdo, "/attack", false )) { 
            Serial.println("Attack status successfully saved to Firebase");
            lcd.clear();
            lcd.print("Pass");
            delay(10000);
            lcd.clear();
            lcd.print("Pass");
            myServo.write(90);
            delay(10000); // Keep it in this position for 10 seconds
            myServo.write(0); // Move back to 0 degrees
            delay(2000);
          } else {
            Serial.println("Failed to save attack status: " + fbdo.errorReason());
          }
          
        } else {
          Serial.println("Wrong Password");
          lcd.clear();
          lcd.print("Failed");
          doorLocked = false;
          bool x = true;
          if (Firebase.RTDB.setBool(&fbdo, "/attack", true )) { 
            Serial.println("Attack status successfully saved to Firebase");
          } else {
            Serial.println("Failed to save attack status: " + fbdo.errorReason());
          }
          delay(2000);
        }
      } else {
        Serial.println("Failed to get password from Firebase: " + fbdo.errorReason());
      }
    }

    //ultrasonic area
    Ultrasonic ultra(trigPin ,echoPin );
    int dist = ultra.read();
    if (dist < distanceThreshold) {
      Firebase.RTDB.setBool(&fbdo, "/alert", true);
      Serial.println("dist < 50 alert = true");

    } else {
      Firebase.RTDB.setBool(&fbdo, "/alert", false);
      Serial.println("dist > 50 alert = false");
    }
    
    // Check LED status for "light" from Firebase
    if (Firebase.RTDB.getBool(&fbdo, "/light")) {
      if (fbdo.dataType() == "boolean") {
        lightStatus = fbdo.boolData();
        Serial.println("Successful READ from " + fbdo.dataPath() + ": " + String(lightStatus) + " (" + fbdo.dataType() + ")");
        if (lightStatus) {
          digitalWrite(lightLedPin, HIGH); // Turn on LED
        } else {
          digitalWrite(lightLedPin, LOW); // Turn off LED
        }
        Serial.println("light status");
        Serial.println(lightStatus);
      } 
    } else {
      Serial.println("FAILED: " + fbdo.errorReason());
    }
    
    // Check LED status for "fan" from Firebase
    if (Firebase.RTDB.getBool(&fbdo, "/fan")) {
      if (fbdo.dataType() == "boolean") {
        fanStatus = fbdo.boolData();
        Serial.println("Successful READ from " + fbdo.dataPath() + ": " + String(fanStatus) + " (" + fbdo.dataType() + ")");
        if (fanStatus) {
          digitalWrite(fanLedPin, HIGH); // Turn on LED
        } else {
          digitalWrite(fanLedPin, LOW); // Turn off LED
        }
        Serial.println("fan status");
        Serial.println(fanStatus);
      } 
    } else {
      Serial.println("FAILED: " + fbdo.errorReason());
    }
    
    delay(10000); // Delay between checks
  }
}