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

import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.incubator.logs.ExtendedLogger
import io.opentelemetry.api.metrics.Meter

/**
 * SolarwindsRum provides an interface for generating OTel telemetry.
 * This class is a singleton and should be initialized using {@link SolarwindsRumBuilder}.
 */
class SolarwindsRum private constructor(
    private val openTelemetryRum: OpenTelemetryRum,
) {
    private val logger: ExtendedLogger =
        openTelemetryRum.getOpenTelemetry().logsBridge
            .loggerBuilder("com.solarwinds.android.rum.logs")
            .build() as ExtendedLogger

    /**
     * Emits a log event with the specified name, body, and attributes.
     *
     * @param name The name of the event.
     * @param body The body of the log event. Defaults to an empty string if not provided.
     * @param attributes Additional attributes for the log event. Defaults to an empty set if not provided.
     */
    @JvmOverloads
    fun emitEvent(
        name: String,
        body: String = "",
        attributes: Attributes = Attributes.empty(),
    ) {
        logger
            .logRecordBuilder()
            .setEventName(name)
            .setBody(body)
            .setAllAttributes(attributes)
            .emit()
    }

    /**
     * Retrieves a {@link Meter} instance for the specified scope.
     *
     * @param scope The name of the meter scope.
     * @return A {@link Meter} instance for collecting metrics.
     */
    fun meter(scope: String): Meter = openTelemetryRum.getOpenTelemetry().getMeter(scope)

    /**
     * Companion object that holds the singleton instance of {@link SolarwindsRum}.
     */
    companion object {
        lateinit var instance: SolarwindsRum
        private var initialized = false

        /**
         * Initializes the SolarwindsRum singleton with the given OpenTelemetryRum instance.
         * If already initialized, the existing instance is returned.
         *
         * @param openTelemetryRum The OpenTelemetryRum instance to use.
         * @return The initialized SolarwindsRum instance.
         */
        @JvmStatic
        fun initialize(openTelemetryRum: OpenTelemetryRum): SolarwindsRum {
            if (!initialized) {
                instance = SolarwindsRum(openTelemetryRum)
            }

            return instance
        }
    }
}
