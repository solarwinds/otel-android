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

package com.solarwinds.instrumentation.view.library

import android.view.View
import io.opentelemetry.android.instrumentation.InstallationContext
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.incubator.logs.ExtendedLogRecordBuilder
import io.opentelemetry.api.incubator.logs.ExtendedLogger

object EventCreator {
    private var eventLogger = OpenTelemetry.noop()
        .logsBridge
        .loggerBuilder("io.opentelemetry.android.event.view.noop")
        .build() as ExtendedLogger

    @JvmStatic
    fun configure(context: InstallationContext) {
        eventLogger =
            context.openTelemetry
                .logsBridge
                .loggerBuilder("io.opentelemetry.android.event.view")
                .build() as ExtendedLogger
    }

    @JvmStatic
    fun createEvent(name: String): ExtendedLogRecordBuilder {
        return eventLogger.logRecordBuilder()
            .setEventName(name)
    }

    @JvmStatic
    fun createViewAttributes(view: View): Attributes {
        val builder = Attributes.builder()
        builder.put(viewNameAttr, viewToName(view))
        builder.put(viewIdAttr, view.id.toLong())

        builder.put(viewClassNameAttr, view.getViewClassName())
        return builder.build()
    }

    @JvmStatic
    fun viewToName(view: View): String {
        return try {
            view.resources?.getResourceEntryName(view.id) ?: view.id.toString()
        } catch (throwable: Throwable) {
            view.id.toString()
        }
    }

}
