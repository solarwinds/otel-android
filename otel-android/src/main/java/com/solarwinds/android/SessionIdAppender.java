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
import androidx.annotation.Nullable;
import io.opentelemetry.android.session.SessionProvider;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import java.util.function.Supplier;

/**
 * Appends user provide session id to telemetry using {@link SessionProvider} as the bridge between
 * this code and user's session generation mechanism
 */
class SessionIdAppender implements Supplier<Attributes> {
    private final Supplier<Attributes> delegate;

    private final AttributeKey<String> sessionIdKey;

    private final SessionProvider sessionProvider;

    SessionIdAppender(
            @Nullable Supplier<Attributes> delegate,
            @NonNull AttributeKey<String> sessionIdKey,
            @NonNull SessionProvider sessionProvider) {
        this.delegate = delegate;
        this.sessionIdKey = sessionIdKey;
        this.sessionProvider = sessionProvider;
    }

    @Override
    public Attributes get() {
        if (delegate == null) {
            return Attributes.of(sessionIdKey, sessionProvider.getSessionId());
        }

        Attributes attributes = delegate.get();
        AttributesBuilder attributesBuilder =
                attributes.toBuilder().put(sessionIdKey, sessionProvider.getSessionId());
        return attributesBuilder.build();
    }
}
