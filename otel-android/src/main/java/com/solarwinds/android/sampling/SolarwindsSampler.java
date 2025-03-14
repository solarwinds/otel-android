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

import static com.solarwinds.android.sampling.Constants.SW_LOG_TAG;
import static com.solarwinds.android.sampling.SamplingUtil.LAYER_NAME_PLACEHOLDER;
import static com.solarwinds.android.sampling.SamplingUtil.SW_TRACESTATE_KEY;
import static com.solarwinds.android.sampling.SamplingUtil.TRACE_STATE_KEY;
import static com.solarwinds.android.sampling.SamplingUtil.TRIGGER_TRACE_KEY;
import static com.solarwinds.android.sampling.SamplingUtil.addXtraceOptionsToAttribute;
import static com.solarwinds.android.sampling.SamplingUtil.w3cContextToHexString;
import static com.solarwinds.joboe.sampling.TraceDecisionUtil.shouldTraceRequest;

import android.util.Log;

import com.solarwinds.joboe.sampling.TraceDecision;
import com.solarwinds.joboe.sampling.XTraceOptions;

import java.util.Arrays;
import java.util.List;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import io.opentelemetry.semconv.ServerAttributes;
import io.opentelemetry.semconv.UrlAttributes;

/**
 * Sampler that uses trace decision logic from our joboe core (consult local and remote settings)
 *
 * <p>Also inject various Solarwinds specific sampling KVs into the `SampleResult`
 */
public class SolarwindsSampler implements Sampler {
    public static final SamplingResult METRICS_ONLY =
            SamplingResult.create(
                    SamplingDecision.RECORD_ONLY,
                    Attributes.of(
                            AttributeKey.booleanKey(Constants.SW_DETAILED_TRACING), false,
                            AttributeKey.booleanKey(Constants.SW_METRICS), true,
                            AttributeKey.booleanKey(Constants.SW_SAMPLER), true));

    public static final SamplingResult NOT_TRACED =
            SamplingResult.create(
                    SamplingDecision.DROP,
                    Attributes.of(
                            AttributeKey.booleanKey(Constants.SW_DETAILED_TRACING), false,
                            AttributeKey.booleanKey(Constants.SW_METRICS), false,
                            AttributeKey.booleanKey(Constants.SW_SAMPLER), true));

    public SolarwindsSampler() {
        Log.i(SW_LOG_TAG, "Attached Solarwinds' Sampler");
    }

    @Override
    public SamplingResult shouldSample(
            Context parentContext,
            String traceId,
            String name,
            SpanKind spanKind,
            Attributes attributes,
            List<LinkData> parentLinks) {
        final SpanContext parentSpanContext = Span.fromContext(parentContext).getSpanContext();
        final TraceState traceState =
                parentSpanContext.getTraceState() != null
                        ? parentSpanContext.getTraceState()
                        : TraceState.getDefault();

        final SamplingResult samplingResult;
        final AttributesBuilder additionalAttributesBuilder = Attributes.builder();
        final XTraceOptions xTraceOptions = parentContext.get(TRIGGER_TRACE_KEY);

        List<String> signals =
                Arrays.asList(
                        constructUrl(attributes), String.format(LAYER_NAME_PLACEHOLDER, spanKind, name.trim()));

        if (!parentSpanContext.isValid()) { // no valid traceparent, it is a new trace
            TraceDecision traceDecision = shouldTraceRequest(name, null, xTraceOptions, signals);
            samplingResult = toOtSamplingResult(traceDecision, xTraceOptions, true);

        } else if (parentSpanContext.isRemote()) {
            final String swTraceState = traceState.get(SW_TRACESTATE_KEY);

            if (SamplingUtil.isValidSwTraceState(swTraceState)) { // pass through for request counting
                additionalAttributesBuilder.put(Constants.SW_PARENT_ID, swTraceState.split("-")[0]);
                final String xTraceId = w3cContextToHexString(parentSpanContext);
                final TraceDecision traceDecision =
                        shouldTraceRequest(name, xTraceId, xTraceOptions, signals);

                samplingResult = toOtSamplingResult(traceDecision, xTraceOptions, false);

            } else { // no swTraceState, treat it as a new trace
                final TraceDecision traceDecision = shouldTraceRequest(name, null, xTraceOptions, signals);
                samplingResult = toOtSamplingResult(traceDecision, xTraceOptions, true);
            }

            final String traceStateValue = parentContext.get(TRACE_STATE_KEY);
            if (traceStateValue != null) {
                additionalAttributesBuilder.put(Constants.SW_UPSTREAM_TRACESTATE, traceStateValue);
            }

        } else { // local span, continue with parent based sampling
            samplingResult =
                    Sampler.parentBased(Sampler.alwaysOff())
                            .shouldSample(parentContext, traceId, name, spanKind, attributes, parentLinks);
        }

        SamplingResult result =
                TraceStateSamplingResult.wrap(
                        samplingResult, additionalAttributesBuilder.build());

        Log.d(SW_LOG_TAG, String.format("Sampling decision: %s", result.getDecision()));
        return result;
    }

    private String constructUrl(Attributes attributes) {
        String scheme = attributes.get(UrlAttributes.URL_SCHEME);
        String host = attributes.get(ServerAttributes.SERVER_ADDRESS);
        String target = attributes.get(UrlAttributes.URL_PATH);

        String url = String.format("%s://%s%s", scheme, host, target);
        Log.d(SW_LOG_TAG, String.format("Constructed url: %s", url));
        return url;
    }

    @Override
    public String getDescription() {
        return "Solarwinds Observability Sampler";
    }

    SamplingResult toOtSamplingResult(
            TraceDecision traceDecision, XTraceOptions xtraceOptions, boolean genesis) {
        SamplingResult result = NOT_TRACED;

        if (traceDecision.isSampled()) {
            final SamplingDecision samplingDecision = SamplingDecision.RECORD_AND_SAMPLE;
            final AttributesBuilder attributesBuilder = Attributes.builder();
            attributesBuilder.put(
                    Constants.SW_KEY_PREFIX + "SampleRate", traceDecision.getTraceConfig().getSampleRate());
            attributesBuilder.put(
                    Constants.SW_KEY_PREFIX + "SampleSource",
                    traceDecision.getTraceConfig().getSampleRateSourceValue());
            attributesBuilder.put(
                    Constants.SW_KEY_PREFIX + "BucketRate",
                    traceDecision
                            .getTraceConfig()
                            .getBucketRate(traceDecision.getRequestType().getBucketType()));
            attributesBuilder.put(
                    Constants.SW_KEY_PREFIX + "BucketCapacity",
                    traceDecision
                            .getTraceConfig()
                            .getBucketCapacity(traceDecision.getRequestType().getBucketType()));
            attributesBuilder.put(
                    Constants.SW_KEY_PREFIX + "RequestType", traceDecision.getRequestType().name());
            attributesBuilder.put(Constants.SW_DETAILED_TRACING, traceDecision.isSampled());
            attributesBuilder.put(Constants.SW_METRICS, traceDecision.isReportMetrics());
            attributesBuilder.put(Constants.SW_SAMPLER, true); // mark that it has been sampled by us

            if (genesis) {
                addXtraceOptionsToAttribute(traceDecision, xtraceOptions, attributesBuilder);
            }
            result = SamplingResult.create(samplingDecision, attributesBuilder.build());
        } else {
            if (traceDecision.isReportMetrics()) {
                result = METRICS_ONLY;
            }
        }
        return result;
    }
}
