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

package com.solarwinds.android.test.common

import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.sdk.logs.export.SimpleLogRecordProcessor
import io.opentelemetry.sdk.testing.exporter.InMemoryLogRecordExporter
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class SolarwindsRumRule : TestRule {
    lateinit var inMemoryLogExporter: InMemoryLogRecordExporter

    override fun apply(
        base: Statement,
        description: Description,
    ): Statement =
        object : Statement() {
            override fun evaluate() {
                setUpOpenTelemetry()
                base.evaluate()
            }
        }

    private fun setUpOpenTelemetry() {
        inMemoryLogExporter = InMemoryLogRecordExporter.create()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            OpenTelemetryRum
                .builder(ApplicationProvider.getApplicationContext())
                .addLoggerProviderCustomizer { logger, _ ->
                    logger.addLogRecordProcessor(
                        SimpleLogRecordProcessor.create(inMemoryLogExporter),
                    )
                }.build()
        }
    }
}
