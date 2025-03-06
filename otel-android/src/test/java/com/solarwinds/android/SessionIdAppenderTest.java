package com.solarwinds.android;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import io.opentelemetry.android.session.SessionProvider;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;

class SessionIdAppenderTest {
    private SessionIdAppender tested;

    private final AttributeKey<String> sessionKey = stringKey("session.id");

    private final SessionProvider sessionProvider = () -> "session-id";

    @Test
    void returnAttributeWithSessionIdWhenDelegateIsNull() {
        tested = new SessionIdAppender(null, sessionKey, sessionProvider);
        Attributes attributes = tested.get();

        assertEquals(Attributes.of(sessionKey, sessionProvider.getSessionId()), attributes);
    }

    @Test
    void returnDelegateAttributeWithSessionIdAdded() {
        tested = new SessionIdAppender(() -> Attributes.of(stringKey("attr"), "value"),
                sessionKey,
                sessionProvider);
        Attributes attributes = tested.get();

        assertEquals(
                Attributes.of(sessionKey, sessionProvider.getSessionId(), stringKey("attr"), "value"),
                attributes);
    }

    @Test
    void returnDelegateAttributeWithSessionIdOverwritten() {
        Supplier<Attributes> delegate = () -> Attributes.of(stringKey("attr"), "value",
                sessionKey, "old-id");
        tested = new SessionIdAppender(delegate, sessionKey, sessionProvider);
        Attributes attributes = tested.get();

        assertEquals(Attributes.of(sessionKey, sessionProvider.getSessionId(),
                        stringKey("attr"), "value"),
                attributes);
    }
}