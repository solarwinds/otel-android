## Sample App

The sample app is a simple application that tries to showcase current available features. Telemetries are generated
as you interact with the application. Some telemetry require specific actions to be taken. Next we're
going to explain which actions trigger telemetry generation.

## Telemetry Generation
- Events
  - There is a websocket client that connects to `https://echo.websocket.org/.ws`. This generates
  event for websocket connection. There's no specific action required from the user to generate these
  websocket events. Also, events are generated when you cause a crash.
  - Trigger a crash: double tap any of your thoughts in the Thought screen.
- Metric
  - Metrics will generated when the user adds thought or visits the GitHub screens. The metric names
  are `thought.count` and `github.events`
- Spans
  - Spans are generated for the Android activity and fragment lifecycle events. These doesn't require
  any specific user action. Spans are also generated during the websocket connection and when a Github
  API request is made when user enters the Github screen. Spans are also generated when you cause an ANR
  - Trigger an ANR: long press on any of you thoughts in the Thought screen.
- Logs
  - All `android.util.Log.*` is exported as otlp logs.
