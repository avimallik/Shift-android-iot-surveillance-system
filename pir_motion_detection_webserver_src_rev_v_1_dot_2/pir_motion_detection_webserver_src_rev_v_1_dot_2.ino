#include <ESP8266WiFi.h>
#include <ArduinoJson.h>          // https://github.com/bblanchon/ArduinoJson
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

LiquidCrystal_I2C lcd(0x3F, 16, 2);

//PIR Sensor's config variables
int pirInput = D3; //NodeMCU ESP8166MOD Digital 1 pinout
//PIR Sensor's detection state
long pirState;

//PIR Status
int pirStatus = 0;

//Network parameter
String request;
char* ssid = "vector";
char* password = "oxygen2020";

//IPAddress ip(192, 168, 0, 107); //set static ip
//IPAddress gateway(192, 168, 0, 1); //set gateway
//IPAddress subnet(255, 255, 255, 0);//set subnet

///////////////////////////////////

//WiFiServer object
WiFiServer server(80);

void setup(){

// PIR pin declaration
//pinMode(pirInput, INPUT);   

//Ultrasonic pin config
//pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
//pinMode(echoPin, INPUT); // Sets the echoPin as an Input

//Init Serial USB
Serial.begin(115200);

//Init ESPBrowser
//WiFi.config(ip, gateway, subnet);
WiFi.begin(ssid, password);
  
// Connect to Wifi network.
while (WiFi.status() != WL_CONNECTED){
  delay(500);
  Serial.print(F("."));
}
  server.begin();
  Serial.println();
  Serial.println(WiFi.localIP());
  server.begin();

  //Wire begin
//  Wire.begin(2,0);
  lcd.init();   // initializing the LCD
  lcd.backlight(); // Enable or Turn On the backlight  
  lcd.print(WiFi.localIP());
}

void loop(){
  
WiFiClient client = server.available();

  if (!client) {
    return;
  }
  // Wait until the client sends some data
  Serial.println("new client");
  while(!client.available()){
    delay(1);
  }
  // Read the first line of the request
  String request = client.readStringUntil('\r');
  Serial.println(request);
  client.flush();

  //pir status json funstion call
  pirWebView(client);//Return webpage
}

void pirWebView(WiFiClient client){

  ///PIR data read
  pirState = digitalRead(pirInput);

  //PIR state detection
  if(pirState == HIGH){
    pirStatus = 1;
    delay(1000);
  }else{
    pirStatus = 0;
    delay(1000);
  }

  ////Send wbepage to client
  client.println("HTTP/1.1 200 OK");           // This tells the browser that the request to provide data was accepted
  client.println("Access-Control-Allow-Origin: *");  //Tells the browser it has accepted its request for data from a different domain (origin).
  client.println("Content-Type: application/json;charset=utf-8");  //Lets the browser know that the data will be in a JSON format
  client.println("Server: Arduino");           // The data is coming from an Arduino Web Server (this line can be omitted)
  client.println("Connection: close");         // Will close the connection at the end of data transmission.
  client.println();                            // You need to include this blank line - it tells the browser that it has reached the end of the Server reponse header.
  
  client.print("{\"pir_status\": \"");
  client.print(pirStatus,0);   
  client.print("\"}");                    
  
  delay(1);
}
