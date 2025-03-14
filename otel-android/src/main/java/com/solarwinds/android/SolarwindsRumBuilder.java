/*
 * © SolarWinds Worldwide, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.solarwinds.android;

import android.app.Application;
import io.opentelemetry.android.OpenTelemetryRum;
import io.opentelemetry.android.OpenTelemetryRumBuilder;
import io.opentelemetry.android.config.OtelRumConfig;
import io.opentelemetry.android.session.SessionIdGenerator;
import io.opentelemetry.android.session.SessionProvider;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.function.Supplier;

/** Builder for {@link SolarwindsRum} that simplifies configuration of the underlying OTel SDK */
public class SolarwindsRumBuilder {

    private String collectorUrl;

    private String apiToken;

    private OtelRumConfig otelRumConfig = new OtelRumConfig();

    private static final SessionProvider DEFAULT_PROVIDER =
            SessionIdGenerator.DEFAULT.INSTANCE::generateSessionId;

    private SessionProvider sessionProvider = DEFAULT_PROVIDER;

    private String sessionIdKey = "session.id";

    private double sampleRatio = 0.5;

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

    public SolarwindsRumBuilder sessionProvider(SessionProvider sessionProvider) {
        this.sessionProvider = sessionProvider;
        return this;
    }

    public SolarwindsRumBuilder sessionIdKey(String sessionIdKey) {
        this.sessionIdKey = sessionIdKey;
        return this;
    }

    public SolarwindsRumBuilder sampleRatio(double sampleRatio) {
        this.sampleRatio = sampleRatio;
        return this;
    }

    public SolarwindsRum build(Application application) {
        Supplier<Attributes> globalAttributesSupplier = otelRumConfig.getGlobalAttributesSupplier();
        otelRumConfig.setGlobalAttributes(
                new SessionIdAppender(
                        globalAttributesSupplier,
                        AttributeKey.stringKey(sessionIdKey),
                        sessionProvider));

        OpenTelemetryRumBuilder builder = OpenTelemetryRum.builder(application, otelRumConfig);
        builder.mergeResource(SolarwindsResourceProvider.create())
                .addSpanExporterCustomizer(this::createSpanExporter)
                .addLogRecordExporterCustomizer(this::createLogExporter)
                .addMeterProviderCustomizer(this::customizeMetricProvider)
                .addTracerProviderCustomizer(this::customizeTracerProvider);

        return new SolarwindsRum(builder.build());
    }

    private SdkTracerProviderBuilder customizeTracerProvider(
            SdkTracerProviderBuilder sdkTracerProviderBuilder, Application application) {
        return sdkTracerProviderBuilder.setSampler(
                new SessionIdBasedSampler(sampleRatio, sessionProvider));
    }

    private OtlpGrpcSpanExporter createSpanExporter(SpanExporter spanExporter) {
        return OtlpGrpcSpanExporter.builder()
                .setEndpoint(collectorUrl)
                .addHeader("authorization", String.format("Bearer %s", apiToken))
                .build();
    }

    private SdkMeterProviderBuilder customizeMetricProvider(
            SdkMeterProviderBuilder sdkMeterProviderBuilder, Application application) {
        OtlpGrpcMetricExporter metricExporter =
                OtlpGrpcMetricExporter.builder()
                        .setEndpoint(collectorUrl)
                        .addHeader("authorization", String.format("Bearer %s", apiToken))
                        .build();

        return sdkMeterProviderBuilder.registerMetricReader(
                PeriodicMetricReader.create(metricExporter));
    }

    private OtlpGrpcLogRecordExporter createLogExporter(LogRecordExporter logRecordExporter) {
        return OtlpGrpcLogRecordExporter.builder()
                .setEndpoint(collectorUrl)
                .addHeader("authorization", String.format("Bearer %s", apiToken))
                .build();
    }
}
