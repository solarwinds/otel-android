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

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.incubator.logs.ExtendedLogRecordBuilder;
import io.opentelemetry.api.logs.Logger;

public final class WebsocketEventGenerator {

    private WebsocketEventGenerator() {}

    private static final String SCOPE = "sw.apm.events";

    private static OpenTelemetry openTelemetry = OpenTelemetry.noop();

    private static Logger logger = openTelemetry.getLogsBridge().loggerBuilder(SCOPE).build();

    public static void configure(OpenTelemetry openTelemetry) {
        WebsocketEventGenerator.openTelemetry = openTelemetry;
        logger =
                openTelemetry
                        .getLogsBridge()
                        .loggerBuilder("sw.apm.events")
                        .setInstrumentationVersion("0.0.1")
                        .build();
    }

    public static void generateEvent(String eventName, Attributes attributes) {
        ExtendedLogRecordBuilder logRecordBuilder =
                (ExtendedLogRecordBuilder) logger.logRecordBuilder();
        logRecordBuilder.setEventName(eventName).setAllAttributes(attributes).emit();
    }
}
