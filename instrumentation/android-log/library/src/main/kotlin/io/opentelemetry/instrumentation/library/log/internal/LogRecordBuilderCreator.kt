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

package io.opentelemetry.instrumentation.library.log.internal

import io.opentelemetry.android.instrumentation.InstallationContext
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.incubator.logs.ExtendedLogRecordBuilder
import io.opentelemetry.api.incubator.logs.ExtendedLogger

object LogRecordBuilderCreator {
    private var logger = OpenTelemetry.noop()
        .logsBridge
        .loggerBuilder("io.opentelemetry.android.log.noop")
        .build() as ExtendedLogger

    @JvmStatic
    fun configure(context: InstallationContext) {
        logger =
            context.openTelemetry
                .logsBridge
                .loggerBuilder("io.opentelemetry.android.log")
                .build() as ExtendedLogger
    }

    @JvmStatic
    fun createLogRecordBuilder(): ExtendedLogRecordBuilder {
        return logger.logRecordBuilder()
    }

    @JvmStatic
    fun printStacktrace(throwable: Throwable): String {
        return throwable.stackTraceToString()
    }

    @JvmStatic
    fun getTypeName(throwable: Throwable): String {
        var eventName = throwable.javaClass.canonicalName
        if (eventName == null) eventName = throwable.javaClass.simpleName
        return eventName!!
    }
}
