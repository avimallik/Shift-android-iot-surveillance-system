# shift-android-iot-surveillance-system
Android surveillance system with NodeMCU and PIR compatibility

# Description
This is a research and development project, built for tracking objects activity using PIR sensor. Where a NodeMCU is the backbone to detect PIR state and present it as a JSON object. We can access the NodeMCU IoT server using a Static or Dynamic IP. "Shift" Android app is responsible to capture object's image after getting PIR feedback. In addition, it sends image to the "Shift Client" app using FCM (Firebase Cloud Messaging) with respective time and date.

# Technologies
1. Frontend - Java, kotlin
2. Backend - PhP RestFul API
3. Firebase Cloud Messaging
4. Microcontrollers - ESP8266 (NodeMCU IoT Platform)
