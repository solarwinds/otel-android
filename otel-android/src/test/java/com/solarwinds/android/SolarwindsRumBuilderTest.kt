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
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import io.opentelemetry.android.OpenTelemetryRum
import io.opentelemetry.android.OpenTelemetryRumBuilder
import io.opentelemetry.android.config.OtelRumConfig
import io.opentelemetry.android.session.SessionProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SolarwindsRumBuilderTest {
  private val context: Context = mockk()
  private val mockOtelRumBuilder: OpenTelemetryRumBuilder = mockk()
  private val mockSessionProvider: SessionProvider = mockk()
  private val mockRumInstance: OpenTelemetryRum = mockk()

  private lateinit var solarwindsRumBuilder: SolarwindsRumBuilder

  @BeforeEach
  fun setup() {
    solarwindsRumBuilder =
      SolarwindsRumBuilder()
        .collectorUrl("http://example.com")
        .apiToken("test_token")
        .sessionProvider(mockSessionProvider)
        .scaleRatio(0.75)
  }

  @AfterEach
  fun tearDown() {
    unmockkAll()
  }

  @Test
  fun verifyBuilderMethodsAreCalled() {
    mockkObject(OpenTelemetryRumBuilder)
    mockkObject(SolarwindsRum)

    every { OpenTelemetryRumBuilder.create(any<Context>(), any<OtelRumConfig>()) } returns
      mockOtelRumBuilder

    every { mockOtelRumBuilder.mergeResource(any()) } returns mockOtelRumBuilder
    every { mockOtelRumBuilder.setSessionProvider(any()) } returns mockOtelRumBuilder
    every { mockOtelRumBuilder.addSpanExporterCustomizer(any()) } returns mockOtelRumBuilder
    every { mockOtelRumBuilder.addLogRecordExporterCustomizer(any()) } returns mockOtelRumBuilder
    every { mockOtelRumBuilder.addMeterProviderCustomizer(any()) } returns mockOtelRumBuilder
    every { mockOtelRumBuilder.addTracerProviderCustomizer(any()) } returns mockOtelRumBuilder
    every { mockOtelRumBuilder.build() } returns mockRumInstance
    every { SolarwindsRum.initialize(any()) } returns mockk()

    solarwindsRumBuilder.build(context)

    verify { mockOtelRumBuilder.mergeResource(any()) }
    verify { mockOtelRumBuilder.setSessionProvider(mockSessionProvider) }
    verify { mockOtelRumBuilder.addSpanExporterCustomizer(any()) }
    verify { mockOtelRumBuilder.addLogRecordExporterCustomizer(any()) }
    verify { mockOtelRumBuilder.addMeterProviderCustomizer(any()) }
    verify { mockOtelRumBuilder.addTracerProviderCustomizer(any()) }
    verify { mockOtelRumBuilder.build() }
    verify { SolarwindsRum.initialize(mockRumInstance) }
  }
}
