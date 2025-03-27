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

package io.opentelemetry.instrumentation.library.log

import android.util.Log

object LoggingTestUtil {
    fun v(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        if (throwable == null) {
            Log.v(tag, message)
        } else {
            Log.v(tag, message, throwable)
        }
    }

    fun d(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        if (throwable == null) {
            Log.d(tag, message)
        } else {
            Log.d(tag, message, throwable)
        }
    }

    fun i(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        if (throwable == null) {
            Log.i(tag, message)
        } else {
            Log.i(tag, message, throwable)
        }
    }

    fun w(
        tag: String,
        message: String? = null,
        throwable: Throwable? = null,
    ) {
        if (throwable == null && message != null) {
            Log.w(tag, message)
        } else if (message == null) {
            Log.w(tag, throwable)
        } else {
            Log.w(tag, message, throwable)
        }
    }

    fun e(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        if (throwable == null) {
            Log.e(tag, message)
        } else {
            Log.e(tag, message, throwable)
        }
    }

    fun wtf(
        tag: String,
        message: String? = null,
        throwable: Throwable? = null,
    ) {
        if (throwable == null && message != null) {
            Log.wtf(tag, message)
        } else if (message == null) {
            Log.wtf(tag, throwable!!)
        } else {
            Log.wtf(tag, message, throwable)
        }
    }
}
