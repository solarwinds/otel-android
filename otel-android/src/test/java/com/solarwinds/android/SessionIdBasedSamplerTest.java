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

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SessionIdBasedSamplerTest {

    @ParameterizedTest
    @MethodSource("rates")
    void samplingRateAndRealizedRateShouldNotDifferByMoreThanTenPercent(double rate) {
        SessionIdBasedSampler tested =
                new SessionIdBasedSampler(rate, () -> UUID.randomUUID().toString());
        int sampleCount = 0;

        int count = 1000;
        for (int i = 0; i < count; i++) {
            SamplingResult samplingResult =
                    tested.shouldSample(
                            Context.current(),
                            "",
                            "",
                            SpanKind.CLIENT,
                            Attributes.empty(),
                            Collections.emptyList());

            if (samplingResult == SamplingResult.recordAndSample()) {
                sampleCount++;
            }
        }

        double diff = (double) sampleCount / count - rate;
        assertTrue(Math.abs((diff) * 100) <= 5);
    }

    private Stream<Arguments> rates() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(0.1),
                Arguments.of(0.25),
                Arguments.of(0.50),
                Arguments.of(0.75),
                Arguments.of(0.98),
                Arguments.of(1));
    }
}
