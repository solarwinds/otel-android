package com.solarwinds.android;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.opentelemetry.android.session.SessionIdGenerator;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import java.util.Collections;
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
                new SessionIdBasedSampler(
                        rate, SessionIdGenerator.DEFAULT.INSTANCE::generateSessionId);
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
