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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import io.opentelemetry.android.OpenTelemetryRum;
import io.opentelemetry.android.OpenTelemetryRumBuilder;
import io.opentelemetry.android.config.OtelRumConfig;
import io.opentelemetry.android.session.SessionProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SolarwindsRumBuilderTest {
    @Mock private Context context;

    @Mock private OpenTelemetryRumBuilder mockOtelRumBuilder;

    @Mock private SessionProvider mockSessionProvider;

    private SolarwindsRumBuilder solarwindsRumBuilder;

    @BeforeEach
    void setup() {
        solarwindsRumBuilder =
                new SolarwindsRumBuilder()
                        .collectorUrl("http://example.com")
                        .apiToken("test_token")
                        .sessionProvider(mockSessionProvider)
                        .scaleRatio(0.75);
    }

    @Test
    void verifyBuilderMethodsAreCalled() {
        try (MockedStatic<OpenTelemetryRum> mockedOtelRum =
                        Mockito.mockStatic(OpenTelemetryRum.class);
                MockedStatic<SolarwindsRum> mockedSwRum = Mockito.mockStatic(SolarwindsRum.class)) {

            mockedOtelRum
                    .when(
                            () ->
                                    OpenTelemetryRum.builder(
                                            any(Context.class), any(OtelRumConfig.class)))
                    .thenReturn(mockOtelRumBuilder);

            when(mockOtelRumBuilder.mergeResource(any())).thenReturn(mockOtelRumBuilder);
            when(mockOtelRumBuilder.setSessionProvider(any())).thenReturn(mockOtelRumBuilder);
            when(mockOtelRumBuilder.addSpanExporterCustomizer(any()))
                    .thenReturn(mockOtelRumBuilder);
            when(mockOtelRumBuilder.addLogRecordExporterCustomizer(any()))
                    .thenReturn(mockOtelRumBuilder);
            when(mockOtelRumBuilder.addMeterProviderCustomizer(any()))
                    .thenReturn(mockOtelRumBuilder);
            when(mockOtelRumBuilder.addTracerProviderCustomizer(any()))
                    .thenReturn(mockOtelRumBuilder);

            OpenTelemetryRum mockRumInstance = mock(OpenTelemetryRum.class);
            when(mockOtelRumBuilder.build()).thenReturn(mockRumInstance);

            solarwindsRumBuilder.build(context);

            verify(mockOtelRumBuilder).mergeResource(any());
            verify(mockOtelRumBuilder).setSessionProvider(mockSessionProvider);
            verify(mockOtelRumBuilder).addSpanExporterCustomizer(any());
            verify(mockOtelRumBuilder).addLogRecordExporterCustomizer(any());
            verify(mockOtelRumBuilder).addMeterProviderCustomizer(any());
            verify(mockOtelRumBuilder).addTracerProviderCustomizer(any());

            verify(mockOtelRumBuilder).build();
            mockedSwRum.verify(() -> SolarwindsRum.initialize(mockRumInstance));
        }
    }
}
