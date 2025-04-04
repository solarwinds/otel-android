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

import static io.opentelemetry.api.common.AttributeKey.stringKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.opentelemetry.android.session.SessionProvider;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

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
        tested =
                new SessionIdAppender(
                        () -> Attributes.of(stringKey("attr"), "value"),
                        sessionKey,
                        sessionProvider);
        Attributes attributes = tested.get();

        assertEquals(
                Attributes.of(
                        sessionKey, sessionProvider.getSessionId(), stringKey("attr"), "value"),
                attributes);
    }

    @Test
    void returnDelegateAttributeWithSessionIdOverwritten() {
        Supplier<Attributes> delegate =
                () -> Attributes.of(stringKey("attr"), "value", sessionKey, "old-id");
        tested = new SessionIdAppender(delegate, sessionKey, sessionProvider);
        Attributes attributes = tested.get();

        assertEquals(
                Attributes.of(
                        sessionKey, sessionProvider.getSessionId(), stringKey("attr"), "value"),
                attributes);
    }
}
