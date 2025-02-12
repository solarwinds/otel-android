package com.solarwinds.android;

import io.opentelemetry.android.OpenTelemetryRum;

/**
 * Provides user-friendly interface for generating OTel telemetry
 */
public class SolarwindsRum {
    private final OpenTelemetryRum openTelemetryRum;

    public SolarwindsRum(OpenTelemetryRum openTelemetryRum) {
        this.openTelemetryRum = openTelemetryRum;
    }

    public OpenTelemetryRum getOpenTelemetryRum() {
        return openTelemetryRum;
    }
}
