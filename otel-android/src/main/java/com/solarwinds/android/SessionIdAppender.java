package com.solarwinds.android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Supplier;

import io.opentelemetry.android.session.SessionProvider;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;

/**
 * Appends user provide session id to telemetry using {@link SessionProvider} as the bridge
 * between this code and user's session generation mechanism
 */
 class SessionIdAppender implements Supplier<Attributes> {
    private final Supplier<Attributes> delegate;

    private final AttributeKey<String> sessionIdKey;

    private final SessionProvider sessionProvider;

     SessionIdAppender(@Nullable Supplier<Attributes> delegate,
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
        AttributesBuilder attributesBuilder = attributes.toBuilder()
                .put(sessionIdKey, sessionProvider.getSessionId());
        return attributesBuilder.build();
    }
}
