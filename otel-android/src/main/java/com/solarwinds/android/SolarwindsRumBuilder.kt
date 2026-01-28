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

package com.solarwinds.android

import android.content.Context
import io.opentelemetry.android.OpenTelemetryRumBuilder
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.session.SessionProvider
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.logs.export.LogRecordExporter
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder
import io.opentelemetry.sdk.metrics.export.AggregationTemporalitySelector
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder
import io.opentelemetry.sdk.trace.export.SpanExporter
import java.util.UUID

/** Builder for [SolarwindsRum] that simplifies configuration of the underlying OTel SDK */
class SolarwindsRumBuilder {
  private var collectorUrl: String = "https://otel.collector.na-01.cloud.solarwinds.com"
  private var apiToken: String? = null
  private var otelRumConfig: OtelRumConfig = OtelRumConfig()
  private var sessionProvider: SessionProvider = DEFAULT_PROVIDER
  private var scaleRatio: Double = 0.5

  fun collectorUrl(collectorUrl: String): SolarwindsRumBuilder = apply {
    this.collectorUrl = collectorUrl
  }

  fun apiToken(apiToken: String): SolarwindsRumBuilder = apply { this.apiToken = apiToken }

  fun otelRumConfig(otelRumConfig: OtelRumConfig): SolarwindsRumBuilder = apply {
    this.otelRumConfig = otelRumConfig
  }

  fun sessionProvider(sessionProvider: SessionProvider): SolarwindsRumBuilder = apply {
    this.sessionProvider = sessionProvider
  }

  fun scaleRatio(scaleRatio: Double): SolarwindsRumBuilder = apply { this.scaleRatio = scaleRatio }

  fun build(context: Context): SolarwindsRum {
    val builder =
      OpenTelemetryRumBuilder.create(context, otelRumConfig)
        .mergeResource(SolarwindsResourceProvider.create())
        .setSessionProvider(sessionProvider)
        .addSpanExporterCustomizer(::createSpanExporter)
        .addLogRecordExporterCustomizer(::createLogExporter)
        .addMeterProviderCustomizer(::customizeMetricProvider)
        .addTracerProviderCustomizer(::customizeTracerProvider)

    return SolarwindsRum.initialize(builder.build())
  }

  private fun customizeTracerProvider(
    sdkTracerProviderBuilder: SdkTracerProviderBuilder,
    context: Context,
  ): SdkTracerProviderBuilder =
    sdkTracerProviderBuilder.setSampler(SessionIdBasedSampler(scaleRatio, sessionProvider))

  private fun createSpanExporter(spanExporter: SpanExporter): OtlpGrpcSpanExporter =
    OtlpGrpcSpanExporter.builder()
      .setEndpoint(collectorUrl)
      .addHeader("authorization", "Bearer $apiToken")
      .build()

  private fun customizeMetricProvider(
    sdkMeterProviderBuilder: SdkMeterProviderBuilder,
    context: Context,
  ): SdkMeterProviderBuilder {
    val metricExporter =
      OtlpGrpcMetricExporter.builder()
        .setEndpoint(collectorUrl)
        .addHeader("authorization", "Bearer $apiToken")
        .setAggregationTemporalitySelector(AggregationTemporalitySelector.deltaPreferred())
        .build()

    return sdkMeterProviderBuilder.registerMetricReader(PeriodicMetricReader.create(metricExporter))
  }

  private fun createLogExporter(logRecordExporter: LogRecordExporter): OtlpGrpcLogRecordExporter =
    OtlpGrpcLogRecordExporter.builder()
      .setEndpoint(collectorUrl)
      .addHeader("authorization", "Bearer $apiToken")
      .build()

  companion object {
    private val DEFAULT_PROVIDER =
      object : SessionProvider {
        override fun getSessionId(): String = UUID.randomUUID().toString()
      }
  }
}
