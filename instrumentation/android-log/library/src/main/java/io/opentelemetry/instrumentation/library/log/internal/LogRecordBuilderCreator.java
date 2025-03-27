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

package io.opentelemetry.instrumentation.library.log.internal;

import io.opentelemetry.android.instrumentation.InstallationContext;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.incubator.logs.ExtendedLogRecordBuilder;
import io.opentelemetry.api.incubator.logs.ExtendedLogger;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogRecordBuilderCreator {

    private LogRecordBuilderCreator() {}

    private static ExtendedLogger logger =
            (ExtendedLogger)
                    OpenTelemetry.noop()
                            .getLogsBridge()
                            .loggerBuilder("io.opentelemetry.android.log.noop")
                            .build();

    public static void configure(InstallationContext context) {
        logger =
                (ExtendedLogger)
                        context.getOpenTelemetry()
                                .getLogsBridge()
                                .loggerBuilder("io.opentelemetry.android.log")
                                .build();
    }

    public static ExtendedLogRecordBuilder createLogRecordBuilder() {
        return logger.logRecordBuilder();
    }

    public static String printStacktrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            throwable.printStackTrace(printWriter);
        }
        return stringWriter.toString();
    }

    public static String getEventName(Throwable throwable) {
        String eventName = throwable.getClass().getCanonicalName();
        if (eventName == null) eventName = throwable.getClass().getSimpleName();
        return eventName;
    }
}
