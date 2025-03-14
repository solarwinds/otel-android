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


import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.solarwinds.android.sampling.AndroidSettingsFetcher;
import com.solarwinds.android.sampling.AndroidSettingsWorker;
import com.solarwinds.android.sampling.SolarwindsSampler;
import com.solarwinds.joboe.sampling.SamplingConfiguration;
import com.solarwinds.joboe.sampling.SettingsManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import io.opentelemetry.android.OpenTelemetryRum;
import io.opentelemetry.android.OpenTelemetryRumBuilder;
import io.opentelemetry.android.config.OtelRumConfig;
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


/**
 * Builder for {@link SolarwindsRum} that simplifies configuration of the underlying OTel SDK
 */
public class SolarwindsRumBuilder {

    private String collectorUrl;

    private String apiToken;

    private OtelRumConfig otelRumConfig = new OtelRumConfig();

    private SessionProvider sessionProvider = null;

    private String sessionIdKey = "session.id";

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

    public SolarwindsRum build(Application application) {
        if (sessionProvider != null) {
            Supplier<Attributes> globalAttributesSupplier = otelRumConfig.getGlobalAttributesSupplier();
            otelRumConfig.setGlobalAttributes(new SessionIdAppender(globalAttributesSupplier,
                    AttributeKey.stringKey(sessionIdKey), sessionProvider));
        }

        OpenTelemetryRumBuilder builder = OpenTelemetryRum.builder(application, otelRumConfig);
        builder
                .mergeResource(SolarwindsResourceProvider.create())
                .addSpanExporterCustomizer(this::createSpanExporter)
                .addLogRecordExporterCustomizer(this::createLogExporter)
                .addMeterProviderCustomizer(this::customizeMetricProvider)
                .addTracerProviderCustomizer(this::customizeTracerProvider);

        Data data = new Data.Builder()
                .putString("appName", readAppName(application))
                .putString("collectorUrl", collectorUrl)
                .putString("apiToken", apiToken)
                .build();

        scheduleWorker(application, data);
        SettingsManager.initialize(new AndroidSettingsFetcher(),
                SamplingConfiguration.builder()
                        .build());
        return new SolarwindsRum(builder.build());
    }

    private SdkTracerProviderBuilder customizeTracerProvider(SdkTracerProviderBuilder sdkTracerProviderBuilder, Application application) {
        return sdkTracerProviderBuilder.setSampler(new SolarwindsSampler());
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

    private static String readAppName(Application application) {
        try {
            int stringId =
                    application.getApplicationContext().getApplicationInfo().labelRes;
            return application.getApplicationContext().getString(stringId);
        } catch (Throwable e) {
            return "unknown_service:android";
        }
    }

    private void scheduleWorker(Application application, Data data) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(AndroidSettingsWorker.class, 1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .setInputData(data)
                .build();

        WorkManager workManager = WorkManager.getInstance(application.getApplicationContext());
        workManager.enqueueUniquePeriodicWork(
                AndroidSettingsFetcher.class.getSimpleName(),
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                periodicWorkRequest
        );
    }
}
