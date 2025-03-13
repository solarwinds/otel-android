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

package com.solarwinds.android.sampling;

import static com.solarwinds.android.sampling.SamplingUtil.w3cContextToHexString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.solarwinds.joboe.sampling.TraceConfig;
import com.solarwinds.joboe.sampling.TraceDecision;
import com.solarwinds.joboe.sampling.TraceDecisionUtil;
import com.solarwinds.joboe.sampling.XTraceOptions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.IdGenerator;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

@RunWith(AndroidJUnit4.class)
public class SolarwindsSamplerTest {

    @InjectMocks
    private SolarwindsSampler tested;

    @Mock
    private TraceDecision traceDecisionMock;

    @Mock
    private XTraceOptions xTraceOptionsMock;

    @Mock
    private TraceConfig traceConfigMock;

    @Mock
    private SpanContext spanContextMock;

    @Mock
    private Span spanMock;

    @Mock
    private TraceState traceStateMock;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private final IdGenerator idGenerator = IdGenerator.random();

    private AutoCloseable mocks;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @After
    public void teardown() throws Exception {
        mocks.close();
    }

    @Test
    public void returnSamplingResultGivenTraceDecisionIsSampled() {
        when(traceDecisionMock.isSampled()).thenReturn(true);
        when(traceDecisionMock.getTraceConfig()).thenReturn(traceConfigMock);
        when(traceConfigMock.getSampleRate()).thenReturn(100);

        when(traceConfigMock.getSampleRateSourceValue()).thenReturn(2);
        when(traceConfigMock.getBucketRate(any())).thenReturn(0.5);
        when(traceConfigMock.getBucketCapacity(any())).thenReturn(0.5);

        when(traceDecisionMock.getRequestType()).thenReturn(TraceDecisionUtil.RequestType.REGULAR);
        when(traceDecisionMock.isReportMetrics()).thenReturn(true);

        tested.toOtSamplingResult(traceDecisionMock, xTraceOptionsMock, false);

        verify(traceDecisionMock, atLeastOnce()).getTraceConfig();
        verify(traceConfigMock, atLeastOnce()).getSampleRate();
    }

    @Test
    public void returnSamplingResultGivenTraceDecisionIsMetricsOnly() {
        when(traceDecisionMock.isSampled()).thenReturn(false);
        when(traceDecisionMock.isReportMetrics()).thenReturn(true);

        SamplingResult actual = tested.toOtSamplingResult(traceDecisionMock, xTraceOptionsMock, false);
        assertEquals(SolarwindsSampler.METRICS_ONLY, actual);
    }

    @Test
    public void returnSamplingResultGivenTraceDecisionIsNotSample() {
        when(traceDecisionMock.isSampled()).thenReturn(false);
        when(traceDecisionMock.isReportMetrics()).thenReturn(false);

        SamplingResult actual = tested.toOtSamplingResult(traceDecisionMock, xTraceOptionsMock, false);
        assertEquals(SolarwindsSampler.NOT_TRACED, actual);
    }

    @Test
    public void verifyThatLocalTraceDecisionMachineryIsUsedWhenSpanIsRoot() {
        try (MockedStatic<Span> spanMockedStatic = mockStatic(Span.class);
             MockedStatic<TraceDecisionUtil> traceDecisionUtilMockedStatic =
                     mockStatic(TraceDecisionUtil.class)) {
            spanMockedStatic.when(() -> Span.fromContext(any())).thenReturn(spanMock);
            traceDecisionUtilMockedStatic
                    .when(
                            () ->
                                    TraceDecisionUtil.shouldTraceRequest(
                                            any(), stringArgumentCaptor.capture(), any(), any()))
                    .thenReturn(traceDecisionMock);

            when(spanContextMock.isValid()).thenReturn(false);
            when(traceDecisionMock.isSampled()).thenReturn(false);
            when(traceDecisionMock.isReportMetrics()).thenReturn(false);

            when(spanMock.getSpanContext()).thenReturn(spanContextMock);
            tested.shouldSample(
                    Context.current(),
                    idGenerator.generateTraceId(),
                    "name",
                    SpanKind.INTERNAL,
                    Attributes.empty(),
                    Collections.emptyList());

            traceDecisionUtilMockedStatic.verify(
                    () -> TraceDecisionUtil.shouldTraceRequest(any(), any(), any(), any()));
            assertNull(stringArgumentCaptor.getValue());
        }
    }

    @Test
    public void verifyThatTraceDecisionMachineryIsUsedWhenSpanParentIsRemoteAndSwTraceStateIsInvalid() {
        try (MockedStatic<Span> spanMockedStatic = mockStatic(Span.class);
             MockedStatic<TraceDecisionUtil> traceDecisionUtilMockedStatic =
                     mockStatic(TraceDecisionUtil.class)) {
            spanMockedStatic.when(() -> Span.fromContext(any())).thenReturn(spanMock);
            traceDecisionUtilMockedStatic
                    .when(() -> TraceDecisionUtil.shouldTraceRequest(any(), any(), any(), any()))
                    .thenReturn(traceDecisionMock);

            when(spanContextMock.isValid()).thenReturn(true);
            when(traceDecisionMock.isSampled()).thenReturn(false);
            when(traceDecisionMock.isReportMetrics()).thenReturn(false);

            when(spanMock.getSpanContext()).thenReturn(spanContextMock);
            when(spanContextMock.getTraceState()).thenReturn(traceStateMock);
            when(traceStateMock.get(any())).thenReturn("this is illegal");

            when(spanContextMock.isRemote()).thenReturn(true);
            tested.shouldSample(
                    Context.current(),
                    idGenerator.generateTraceId(),
                    "name",
                    SpanKind.INTERNAL,
                    Attributes.empty(),
                    Collections.emptyList());

            traceDecisionUtilMockedStatic.verify(
                    () -> TraceDecisionUtil.shouldTraceRequest(any(), any(), any(), any()));
        }
    }

    @Test
    public void verifyThatTraceDecisionMachineryIsUsedWhenSpanParentIsRemoteAndSwTraceStateIsValid() {
        try (MockedStatic<Span> spanMockedStatic = mockStatic(Span.class);
             MockedStatic<TraceDecisionUtil> traceDecisionUtilMockedStatic =
                     mockStatic(TraceDecisionUtil.class);
             MockedStatic<SamplingUtil> utilMockedStatic = mockStatic(SamplingUtil.class)) {
            spanMockedStatic.when(() -> Span.fromContext(any())).thenReturn(spanMock);
            traceDecisionUtilMockedStatic
                    .when(() -> TraceDecisionUtil.shouldTraceRequest(any(), any(), any(), any()))
                    .thenReturn(traceDecisionMock);

            String traceId = idGenerator.generateTraceId();
            utilMockedStatic.when(() -> SamplingUtil.w3cContextToHexString(spanContextMock)).thenReturn(traceId);
            utilMockedStatic.when(() -> SamplingUtil.isValidSwTraceState(anyString())).thenReturn(true);

            String spanId = idGenerator.generateSpanId();
            String swVal = String.format("%s-%s", spanId, "01");
            when(spanContextMock.isRemote()).thenReturn(true);

            when(spanContextMock.isValid()).thenReturn(true);
            when(traceDecisionMock.isSampled()).thenReturn(false);
            when(traceDecisionMock.isReportMetrics()).thenReturn(false);

            when(spanMock.getSpanContext()).thenReturn(spanContextMock);
            when(spanContextMock.getTraceState()).thenReturn(traceStateMock);
            when(traceStateMock.get(any())).thenReturn(swVal);

            tested.shouldSample(
                    Context.current(),
                    idGenerator.generateTraceId(),
                    "name",
                    SpanKind.INTERNAL,
                    Attributes.empty(),
                    Collections.emptyList());

            traceDecisionUtilMockedStatic.verify(
                    () ->
                            TraceDecisionUtil.shouldTraceRequest(
                                    any(), stringArgumentCaptor.capture(), any(), any()));
            utilMockedStatic.verify(() -> SamplingUtil.w3cContextToHexString(spanContextMock));

            assertEquals(traceId, stringArgumentCaptor.getValue());
        }
    }

    @Test
    public void returnRecordAndSampleDecisionWhenSpanIsLocalAndParentIsSample() {
        try (MockedStatic<Span> spanMockedStatic = mockStatic(Span.class)) {
            spanMockedStatic.when(() -> Span.fromContext(any())).thenReturn(spanMock);
            when(spanMock.getSpanContext()).thenReturn(spanContextMock);
            when(spanContextMock.isRemote()).thenReturn(false);

            when(spanContextMock.isValid()).thenReturn(true);
            when(spanContextMock.isSampled()).thenReturn(true);
            when(spanContextMock.getTraceState()).thenReturn(traceStateMock);

            SamplingResult actual =
                    tested.shouldSample(
                            Context.current(),
                            idGenerator.generateTraceId(),
                            "name",
                            SpanKind.INTERNAL,
                            Attributes.empty(),
                            Collections.emptyList());

            assertEquals(SamplingDecision.RECORD_AND_SAMPLE, actual.getDecision());
        }
    }

    @Test
    public void returnDropDecisionWhenLocalSpanAndParentIsNotSampled() {
        try (MockedStatic<Span> spanMockedStatic = mockStatic(Span.class)) {
            spanMockedStatic.when(() -> Span.fromContext(any())).thenReturn(spanMock);
            when(spanMock.getSpanContext()).thenReturn(spanContextMock);

            when(spanContextMock.isRemote()).thenReturn(false);
            when(spanContextMock.isValid()).thenReturn(true);
            when(spanContextMock.getTraceState()).thenReturn(traceStateMock);

            SamplingResult actual =
                    tested.shouldSample(
                            Context.current(),
                            idGenerator.generateTraceId(),
                            "name",
                            SpanKind.INTERNAL,
                            Attributes.empty(),
                            Collections.emptyList());

            assertEquals(SamplingDecision.DROP, actual.getDecision());
        }
    }
}
