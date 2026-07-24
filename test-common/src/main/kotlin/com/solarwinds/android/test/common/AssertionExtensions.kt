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

package com.solarwinds.android.test.common

import io.opentelemetry.sdk.testing.assertj.LogRecordDataAssert
import io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat

fun LogRecordDataAssert.hasEventName(eventName: String): LogRecordDataAssert {
  isNotNull()
  val actual = this.actual()
  val getEventNameMethod = actual.javaClass.methods.find {
    it.name == "getEventName" && it.parameterCount == 0
  }
  assertThat(getEventNameMethod)
      .withFailMessage("Expected %s to declare getEventName()", actual.javaClass.name)
      .isNotNull()
  val actualEventName = getEventNameMethod!!.invoke(actual)
  assertThat(actualEventName)
      .withFailMessage(
          "Expected eventName to be %s but was %s for %s",
          eventName,
          actualEventName,
          actual.javaClass.name,
      )
      .isEqualTo(eventName)
  return this
}
