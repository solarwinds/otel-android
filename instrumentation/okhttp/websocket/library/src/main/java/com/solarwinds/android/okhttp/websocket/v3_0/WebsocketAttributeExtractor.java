
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

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.semconv.HttpAttributes;
import io.opentelemetry.semconv.NetworkAttributes;
import io.opentelemetry.semconv.UrlAttributes;
import okhttp3.Request;
import okhttp3.WebSocket;

class WebsocketAttributeExtractor {
    private WebsocketAttributeExtractor() {}

    static Attributes extractAttributes(WebSocket socket) {
        AttributesBuilder builder = Attributes.builder();
        Request request = socket.request();
        builder.put(NetworkAttributes.NETWORK_PROTOCOL_NAME, "websocket");

        builder.put(HttpAttributes.HTTP_REQUEST_METHOD, request.method());
        builder.put(UrlAttributes.URL_FULL, request.url().toString());
        builder.put(NetworkAttributes.NETWORK_PEER_PORT, request.url().port());

        return builder.build();
    }
}
