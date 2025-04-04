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

package io.opentelemetry.instrumentation.library.log.testing

import com.solarwinds.android.test.common.SolarwindsRumRule
import io.opentelemetry.api.logs.Severity
import io.opentelemetry.instrumentation.library.log.AndroidLogSubstitutions
import io.opentelemetry.instrumentation.library.log.LoggingTestUtil
import io.opentelemetry.semconv.ExceptionAttributes
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

class InstrumentationTest {
    @Rule
    @JvmField
    var solarwindsRumRule: SolarwindsRumRule = SolarwindsRumRule()

    private val tag = "log.test"

    private val tagKey = AndroidLogSubstitutions.TAG_KEY

    @Test
    fun test_verbose_logging() {
        val message = "testing verbose"
        LoggingTestUtil.v(tag, message)
        LoggingTestUtil.v(tag, message, RuntimeException("verbose error"))

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems

        assertThat(finishedLogRecordItems.size).isEqualTo(2)
        assertThat(finishedLogRecordItems[0].attributes.get(tagKey)).isEqualTo(tag)
        assertThat(finishedLogRecordItems[0].severity).isEqualTo(Severity.TRACE)

        assertThat(finishedLogRecordItems[0].bodyValue!!.asString()).isEqualTo(message)
        assertThat(finishedLogRecordItems[1].attributes.get(ExceptionAttributes.EXCEPTION_TYPE)).isEqualTo(
            RuntimeException::class.qualifiedName,
        )
    }

    @Test
    fun test_debug_logging() {
        val message = "testing debug"
        LoggingTestUtil.d(tag, message)
        LoggingTestUtil.d(tag, message, RuntimeException("debug error"))

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems

        assertThat(finishedLogRecordItems.size).isEqualTo(2)
        assertThat(finishedLogRecordItems[0].attributes.get(tagKey)).isEqualTo(tag)
        assertThat(finishedLogRecordItems[0].severity).isEqualTo(Severity.DEBUG)

        assertThat(finishedLogRecordItems[0].bodyValue!!.asString()).isEqualTo(message)
        assertThat(finishedLogRecordItems[1].attributes.get(ExceptionAttributes.EXCEPTION_TYPE)).isEqualTo(
            RuntimeException::class.qualifiedName,
        )
    }

    @Test
    fun test_info_logging() {
        val message = "testing info"
        LoggingTestUtil.i(tag, message)
        LoggingTestUtil.i(tag, message, RuntimeException("info error"))

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems

        assertThat(finishedLogRecordItems.size).isEqualTo(2)
        assertThat(finishedLogRecordItems[0].attributes.get(tagKey)).isEqualTo(tag)
        assertThat(finishedLogRecordItems[0].severity).isEqualTo(Severity.INFO)

        assertThat(finishedLogRecordItems[0].bodyValue!!.asString()).isEqualTo(message)
        assertThat(finishedLogRecordItems[1].attributes.get(ExceptionAttributes.EXCEPTION_TYPE)).isEqualTo(
            RuntimeException::class.qualifiedName,
        )
    }

    @Test
    fun test_warn_logging() {
        val message = "testing warn"
        LoggingTestUtil.w(tag, message)
        LoggingTestUtil.w(tag, throwable = RuntimeException("warn error"))
        LoggingTestUtil.w(tag, message, RuntimeException("warn error"))

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems

        assertThat(finishedLogRecordItems.size).isEqualTo(3)
        assertThat(finishedLogRecordItems[0].attributes.get(tagKey)).isEqualTo(tag)
        assertThat(finishedLogRecordItems[0].severity).isEqualTo(Severity.WARN)

        assertThat(finishedLogRecordItems[0].bodyValue!!.asString()).isEqualTo(message)
        assertThat(finishedLogRecordItems[1].attributes.get(ExceptionAttributes.EXCEPTION_TYPE)).isEqualTo(
            RuntimeException::class.qualifiedName,
        )
        assertThat(finishedLogRecordItems[2].bodyValue!!.asString()).isEqualTo(message)
    }

    @Test
    fun test_error_logging() {
        val message = "testing warn"
        LoggingTestUtil.e(tag, message)
        LoggingTestUtil.e(tag, message, RuntimeException("error"))

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems

        assertThat(finishedLogRecordItems.size).isEqualTo(2)
        assertThat(finishedLogRecordItems[0].attributes.get(tagKey)).isEqualTo(tag)
        assertThat(finishedLogRecordItems[0].severity).isEqualTo(Severity.ERROR)

        assertThat(finishedLogRecordItems[1].bodyValue!!.asString()).isEqualTo(message)
        assertThat(finishedLogRecordItems[1].attributes.get(ExceptionAttributes.EXCEPTION_TYPE)).isEqualTo(
            RuntimeException::class.qualifiedName,
        )
    }

    @Test
    fun test_wtf_logging() {
        val message = "testing wtf"
        LoggingTestUtil.wtf(tag, message)
        LoggingTestUtil.wtf(tag, throwable = RuntimeException("wtf error"))
        LoggingTestUtil.wtf(tag, message, RuntimeException("wtf error"))

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems

        assertThat(finishedLogRecordItems.size).isEqualTo(3)
        assertThat(finishedLogRecordItems[0].attributes.get(tagKey)).isEqualTo(tag)
        assertThat(finishedLogRecordItems[0].severity).isEqualTo(Severity.UNDEFINED_SEVERITY_NUMBER)

        assertThat(finishedLogRecordItems[0].bodyValue!!.asString()).isEqualTo(message)
        assertThat(finishedLogRecordItems[1].attributes.get(ExceptionAttributes.EXCEPTION_TYPE)).isEqualTo(
            RuntimeException::class.qualifiedName,
        )
        assertThat(finishedLogRecordItems[2].bodyValue!!.asString()).isEqualTo(message)
    }
}
