package com.solarwinds.android;

import android.app.Application;


import io.opentelemetry.android.OpenTelemetryRum;
import io.opentelemetry.android.OpenTelemetryRumBuilder;
import io.opentelemetry.android.config.OtelRumConfig;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.export.SpanExporter;


/**
 * Builder for {@link SolarwindsRum} that simplifies configuration of the underlying OTel SDK
 */
public class SolarwindsRumBuilder {

    private String collectorUrl;

    private String apiToken;

    private OtelRumConfig otelRumConfig = new OtelRumConfig();

    public SolarwindsRumBuilder collectorUrl(String collectorUrl) {
        this.collectorUrl = collectorUrl;
        return this;
    }

    public SolarwindsRumBuilder apiToken(String apiToken) {
        this.apiToken = apiToken;
        return this;
    }

    public SolarwindsRumBuilder otelRumConfig(OtelRumConfig otelRumConfig) {
        this.otelRumConfig = otelRumConfig;
        return this;
    }

    public SolarwindsRum build(Application application) {
        OpenTelemetryRumBuilder builder = OpenTelemetryRum.builder(application, otelRumConfig);
        builder
                .addSpanExporterCustomizer(this::createSpanExporter)
                .addMeterProviderCustomizer(this::customizeMetricProvider)
                .addLogRecordExporterCustomizer(this::createLogExporter)
                .mergeResource(SolarwindsResourceProvider.create());

        return new SolarwindsRum(builder.build());
    }

    private OtlpGrpcSpanExporter createSpanExporter(SpanExporter spanExporter) {
        return OtlpGrpcSpanExporter.builder()
                .setEndpoint(collectorUrl)
                .addHeader("authorization", String.format("Bearer %s", apiToken))
                .build();
    }

    private SdkMeterProviderBuilder customizeMetricProvider(SdkMeterProviderBuilder sdkMeterProviderBuilder, Application application) {
        OtlpGrpcMetricExporter metricExporter = OtlpGrpcMetricExporter.builder()
                .setEndpoint(collectorUrl)
                .addHeader("authorization", String.format("Bearer %s", apiToken))
                .build();

        return sdkMeterProviderBuilder
                .registerMetricReader(
                        PeriodicMetricReader.create(
                                metricExporter
                        )
                );
    }

    private OtlpGrpcLogRecordExporter createLogExporter(LogRecordExporter logRecordExporter) {
        return OtlpGrpcLogRecordExporter.builder()
                .setEndpoint(collectorUrl)
                .addHeader("authorization", String.format("Bearer %s", apiToken))
                .build();
    }
}
