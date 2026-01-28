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

import io.opentelemetry.sdk.logs.data.internal.ExtendedLogRecordData
import io.opentelemetry.sdk.testing.assertj.LogRecordDataAssert
import io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat

fun LogRecordDataAssert.hasEventName(eventName: String): LogRecordDataAssert {
  isNotNull()
  assertThat(this.actual()).isInstanceOf(ExtendedLogRecordData::class.java)
  assertThat((this.actual() as ExtendedLogRecordData).eventName).isEqualTo(eventName)
  return this
}
