
# Solarwinds OpenTelemetry Instrumentation for Android

For official documentation on the Solarwinds OTel Instrumentation for Android, see [Instrument Android applications for Solarwinds](#solarwinds-opentelemetry-instrumentation-for-android).

# Features

This android library builds on top of
the [OpenTelemetry Java SDK](https://github.com/open-telemetry/opentelemetry-java).
Some of the additional features provided include:

* Crash reporting
* ANR detection
* Network change detection
* Full Android Activity and Fragment lifecycle monitoring
* Access to the OpenTelemetry APIs for manual instrumentation
* Helpers to redact any span from export, or change span attributes before export
* Slow / frozen render detection
* Offline buffering of telemetry via storage

## Sample Application

This repository includes a sample application that demonstrates some features of the Android observability agent.

To build and run the sample application, configure a `local.properties` file in the root of the project. The project requires the following properties:

```properties
collector.url=<swo otel url with scheme i.e [scheme]://[url]>
api.token=<a valid Solarwinds observability token for the endpoint>
```

See its [README](sample-app/README.md) for details. 

## Upgrades

For information on how to upgrade, see the [Upgrading documentation](#upgrades).

## Troubleshooting

For troubleshooting the Solarwinds distribution, see [Troubleshooting Java instrumentation](#troubleshooting)
in the Solarwinds Observability documentation.

# License

The Solarwinds Distribution of OpenTelemetry Android is a distribution of [OpenTelemetry Instrumentation for Android](https://github.com/open-telemetry/opentelemetry-android). 
It is licensed under the terms of the Apache Software License version 2.0. For more details, see [the license file](LICENSE).

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

Approvers:

- [solarwinds-apm](https://github.com/orgs/solarwinds/teams/eng-pub-apm-instrumentation)

Maintainers:

- [solarwinds-apm](https://github.com/orgs/solarwinds/teams/eng-pub-apm-instrumentation)

Thanks to all the people who already contributed!

<a href="https://github.com/solarwinds/apm-java/graphs/contributors">
  <img src="https://contributors-img.web.app/image?repo=solarwinds/otel-android" />
</a>
