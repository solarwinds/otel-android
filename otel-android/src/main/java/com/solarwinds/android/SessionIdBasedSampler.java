package com.solarwinds.android;

import androidx.annotation.NonNull;
import io.opentelemetry.android.session.SessionProvider;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

/**
 * A custom OpenTelemetry sampler that makes sampling decisions based on a session ID. The session
 * ID is provided externally via {@link SessionProvider}.
 */
public class SessionIdBasedSampler implements Sampler {

    private final double rate;

    private final double threshold;

    private final SessionProvider sessionProvider;

    public SessionIdBasedSampler(double rate, SessionProvider sessionProvider) {
        this.rate = rate;
        this.sessionProvider = sessionProvider;
        this.threshold = rate * Long.MAX_VALUE;
    }

    @Override
    public SamplingResult shouldSample(
            @NonNull Context parentContext,
            @NonNull String traceId,
            @NonNull String name,
            @NonNull SpanKind spanKind,
            @NonNull Attributes attributes,
            @NonNull List<LinkData> parentLinks) {

        String sessionId = sessionProvider.getSessionId();
        if (!sessionId.isEmpty()) {
            long hashedValue = hash(sessionId);
            if (hashedValue < threshold) {
                return SamplingResult.recordAndSample();
            }
        }

        return SamplingResult.drop();
    }

    @Override
    public String getDescription() {
        return String.format(
                Locale.getDefault(),
                "SessionIdBasedSampler{rate=%f, threshold=%f}",
                rate,
                threshold);
    }

    private long hash(String sessionId) {
        byte[] bytes = sessionId.getBytes(StandardCharsets.UTF_8);
        return hashBytes(bytes);
    }

    private long hashBytes(byte[] bytes) {
        long hash = 98764321261L;
        for (byte bite : bytes) {
            hash = (hash * 343443379) ^ (bite & 0xff);
        }
        return hash & Long.MAX_VALUE;
    }
}
