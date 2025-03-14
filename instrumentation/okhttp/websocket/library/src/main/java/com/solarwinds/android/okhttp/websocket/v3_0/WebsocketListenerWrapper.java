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

package com.solarwinds.android.okhttp.websocket.v3_0;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.opentelemetry.api.common.Attributes;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebsocketListenerWrapper extends WebSocketListener {
    private final WebSocketListener delegate;

    public WebsocketListenerWrapper(WebSocketListener delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Attributes attributes = WebsocketAttributeExtractor.extractAttributes(webSocket);
        WebsocketEventGenerator.generateEvent("websocket.close", attributes);
        delegate.onClosed(webSocket, code, reason);
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        Attributes attributes = WebsocketAttributeExtractor.extractAttributes(webSocket);
        WebsocketEventGenerator.generateEvent("websocket.open", attributes);
        delegate.onOpen(webSocket, response);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        Attributes attributes = WebsocketAttributeExtractor.extractAttributes(webSocket);
        WebsocketEventGenerator.generateEvent(
                "websocket.message",
                attributes.toBuilder()
                        .put("message.type", "text")
                        .put("message.size", text.length())
                        .build());
        delegate.onMessage(webSocket, text);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        Attributes attributes = WebsocketAttributeExtractor.extractAttributes(webSocket);
        WebsocketEventGenerator.generateEvent(
                "websocket.message",
                attributes.toBuilder()
                        .put("message.type", "bytes")
                        .put("message.size", bytes.size())
                        .build());
        delegate.onMessage(webSocket, bytes);
    }

    @Override
    public void onFailure(
            @NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        Attributes attributes = WebsocketAttributeExtractor.extractAttributes(webSocket);
        WebsocketEventGenerator.generateEvent("websocket.error", attributes);
        delegate.onFailure(webSocket, t, response);
    }
}
