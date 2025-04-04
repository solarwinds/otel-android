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

package io.opentelemetry.instrumentation.library.log;

import static io.opentelemetry.instrumentation.library.log.internal.LogRecordBuilderCreator.createLogRecordBuilder;
import static io.opentelemetry.instrumentation.library.log.internal.LogRecordBuilderCreator.getTypeName;
import static io.opentelemetry.instrumentation.library.log.internal.LogRecordBuilderCreator.printStacktrace;
import static io.opentelemetry.semconv.ExceptionAttributes.EXCEPTION_STACKTRACE;
import static io.opentelemetry.semconv.ExceptionAttributes.EXCEPTION_TYPE;

import android.util.Log;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.Severity;

public class AndroidLogSubstitutions {

    public static AttributeKey<String> TAG_KEY = AttributeKey.stringKey("android.log.tag");

    public static int substitutionForVerbose(String tag, String message) {
        createLogRecordBuilder()
                .setSeverity(Severity.TRACE)
                .setAllAttributes(Attributes.builder().put(TAG_KEY, tag).build())
                .setBody(message)
                .emit();

        return Log.v(tag, message);
    }

    public static int substitutionForVerbose2(String tag, String message, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.TRACE)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .setBody(message)
                .emit();

        return Log.v(tag, message, throwable);
    }

    public static int substitutionForDebug(String tag, String message) {
        createLogRecordBuilder()
                .setSeverity(Severity.DEBUG)
                .setAllAttributes(Attributes.builder().put(TAG_KEY, tag).build())
                .setBody(message)
                .emit();

        return Log.d(tag, message);
    }

    public static int substitutionForDebug2(String tag, String message, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.DEBUG)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .setBody(message)
                .emit();

        return Log.d(tag, message, throwable);
    }

    public static int substitutionForInfo(String tag, String message) {
        createLogRecordBuilder()
                .setSeverity(Severity.INFO)
                .setAllAttributes(Attributes.builder().put(TAG_KEY, tag).build())
                .setBody(message)
                .emit();

        return Log.i(tag, message);
    }

    public static int substitutionForInfo2(String tag, String message, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.INFO)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .setBody(message)
                .emit();

        return Log.i(tag, message, throwable);
    }

    public static int substitutionForWarn(String tag, String message) {
        createLogRecordBuilder()
                .setSeverity(Severity.WARN)
                .setAllAttributes(Attributes.builder().put(TAG_KEY, tag).build())
                .setBody(message)
                .emit();

        return Log.w(tag, message);
    }

    public static int substitutionForWarn2(String tag, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.WARN)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .emit();

        return Log.w(tag, throwable);
    }

    public static int substitutionForWarn3(String tag, String message, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.WARN)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .setBody(message)
                .emit();

        return Log.w(tag, message, throwable);
    }

    public static int substitutionForError(String tag, String message) {
        createLogRecordBuilder()
                .setSeverity(Severity.ERROR)
                .setAllAttributes(Attributes.builder().put(TAG_KEY, tag).build())
                .setBody(message)
                .emit();

        return Log.e(tag, message);
    }

    public static int substitutionForError2(String tag, String message, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.ERROR)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .setBody(message)
                .emit();

        return Log.e(tag, message, throwable);
    }

    public static int substitutionForWtf(String tag, String message) {
        createLogRecordBuilder()
                .setSeverity(Severity.UNDEFINED_SEVERITY_NUMBER)
                .setAllAttributes(Attributes.builder().put(TAG_KEY, tag).build())
                .setBody(message)
                .emit();

        return Log.wtf(tag, message);
    }

    public static int substitutionForWtf2(String tag, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.UNDEFINED_SEVERITY_NUMBER)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .emit();

        return Log.wtf(tag, throwable);
    }

    public static int substitutionForWtf3(String tag, String message, Throwable throwable) {
        createLogRecordBuilder()
                .setSeverity(Severity.UNDEFINED_SEVERITY_NUMBER)
                .setAllAttributes(
                        Attributes.builder()
                                .put(TAG_KEY, tag)
                                .put(EXCEPTION_TYPE, getTypeName(throwable))
                                .put(EXCEPTION_STACKTRACE, printStacktrace(throwable))
                                .build())
                .setBody(message)
                .emit();

        return Log.wtf(tag, message, throwable);
    }
}
