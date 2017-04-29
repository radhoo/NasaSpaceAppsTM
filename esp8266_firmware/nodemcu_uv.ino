#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

#define MAX_BUFFER 250
char buffer[MAX_BUFFER];

ESP8266WebServer server ( 80 );
const char *ssid = "magnasci";
const char *password = "cebit2017";
const int pinAlarm = 16; // GPIO16 - D0
const int pinAdc = A0;   // ADC0 , nodemcu uses VREF 3.3V


void handleRoot() {
  int a0 = analogRead(pinAdc);
  //float vol = a0 * 3.3 / 1023.0;
  //float uv = (vol - 0.99) * (15 - 0) / (2.8 - 0.99) + 0;
  
  snprintf ( buffer, MAX_BUFFER, "%d", a0);
  server.send ( 200, "text/html", buffer );
}

void handleNotFound() {
  snprintf ( buffer, MAX_BUFFER, "File Not Found\n\n");
	server.send ( 404, "text/plain", buffer );
}


void setup ( void ) {
  pinMode ( pinAlarm, OUTPUT );
  digitalWrite ( pinAlarm, 0 );
  
	Serial.begin ( 115200 );
	
	//WiFi.begin ( ssid, password );
  WiFi.begin(ssid, password);
  //WiFi.softAP("UV-Sense");

	while ( WiFi.status() != WL_CONNECTED ) {
		delay ( 500 );
		Serial.print ( "." );
	}

	server.on ( "/", handleRoot );
  server.onNotFound ( handleNotFound );
  
	server.begin();
}

void loop ( void ) {
	server.handleClient();
}


