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

    private final double scaler;

    private final double threshold;

    private final SessionProvider sessionProvider;

    public SessionIdBasedSampler(double scaler, SessionProvider sessionProvider) {
        this.scaler = scaler;
        this.sessionProvider = sessionProvider;
        this.threshold = scaler * Long.MAX_VALUE;
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
                "SessionIdBasedSampler{scaler=%f, threshold=%f}",
                scaler,
                threshold);
    }

    private long hash(String sessionId) {
        byte[] bytes = sessionId.getBytes(StandardCharsets.UTF_8);
        return hashBytes(bytes);
    }

    private long hashBytes(byte[] bytes) {
        long hash = 0xcbf29ce484222325L;
        for (byte bite : bytes) {
            hash = (hash * 1099511628211L) ^ bite;
        }
        return hash & Long.MAX_VALUE;
    }
}
