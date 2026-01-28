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

package com.solarwinds.android

import io.opentelemetry.android.session.SessionProvider
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.context.Context
import io.opentelemetry.sdk.trace.data.LinkData
import io.opentelemetry.sdk.trace.samplers.Sampler
import io.opentelemetry.sdk.trace.samplers.SamplingResult
import java.nio.charset.StandardCharsets

/**
 * A custom OpenTelemetry sampler that makes sampling decisions based on a session ID. The session
 * ID is provided externally via [SessionProvider].
 */
class SessionIdBasedSampler(
  private val scaler: Double,
  private val sessionProvider: SessionProvider,
) : Sampler {
  private val threshold: Long = (scaler * Long.MAX_VALUE).toLong()

  override fun shouldSample(
    parentContext: Context,
    traceId: String,
    name: String,
    spanKind: SpanKind,
    attributes: Attributes,
    parentLinks: List<LinkData>,
  ): SamplingResult {
    val sessionId = sessionProvider.getSessionId()
    if (sessionId.isNotEmpty()) {
      val hashedValue = hash(sessionId)
      if (hashedValue < threshold) {
        return SamplingResult.recordAndSample()
      }
    }

    return SamplingResult.drop()
  }

  override fun getDescription(): String =
    "SessionIdBasedSampler{scaler=$scaler, threshold=$threshold}"

  private fun hash(sessionId: String): Long {
    val bytes = sessionId.toByteArray(StandardCharsets.UTF_8)
    return hashBytes(bytes)
  }

  private fun hashBytes(bytes: ByteArray): Long {
    var hash: Long = 0xcbf29ce484222325uL.toLong()
    for (bite in bytes) {
      hash = (hash * 1099511628211L) xor bite.toLong()
    }
    return hash and Long.MAX_VALUE
  }
}
