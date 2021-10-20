# AllSpeak
The AllSpeak webapp is a chat application that auto-translates messages to the user's selected language.

# Live Demo
[https://allspeak.bernardguiang.com](https://allspeak.bernardguiang.com)

# Local Environment Setup

The React frontend uses a STOMP over WebSocket client to send and receive messages from the backend.
- To run the app locally, change the STOMP client's brokerURL to localhost:8080 in on src/main/frontend/src/component/Main.js
  - brokerURL: "ws://localhost:8080/allspeak"

The Spring Boot backend expects the following environment variables:
- "GOOGLE_API_KEY" - a Google Cloud Translation API key
