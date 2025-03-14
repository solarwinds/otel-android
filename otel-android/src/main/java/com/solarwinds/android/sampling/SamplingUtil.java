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

package com.solarwinds.android.sampling;

import com.solarwinds.joboe.sampling.Metadata;
import com.solarwinds.joboe.sampling.TraceDecision;
import com.solarwinds.joboe.sampling.TraceDecisionUtil;
import com.solarwinds.joboe.sampling.XTraceOption;
import com.solarwinds.joboe.sampling.XTraceOptions;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.ContextKey;

import java.util.regex.Pattern;

public class SamplingUtil {
  private SamplingUtil() {}

  public static final String SW_TRACESTATE_KEY = "sw";
  static final Pattern SPAN_ID_REGEX = Pattern.compile("[0-9a-fA-F]{16}");
  static final ContextKey<XTraceOptions> TRIGGER_TRACE_KEY = ContextKey.named("sw-trigger-trace-key");
  static final ContextKey<String> TRACE_STATE_KEY = ContextKey.named("tracestate-original-value");
  static final String LAYER_NAME_PLACEHOLDER = "%s:%s";

  public static boolean isValidSwTraceState(String swVal) {
    if (swVal == null || !swVal.contains("-")) {
      return false;
    }

    final String[] swTraceState = swVal.split("-");
    if (swTraceState.length != 2) {
      return false;
    }

    // 16 is the hex length of the Otel span id
    return (SPAN_ID_REGEX.matcher(swTraceState[0]).matches())
        && (swTraceState[1].equals("00") || swTraceState[1].equals("01"));
  }

  public static void addXtraceOptionsToAttribute(
      TraceDecision traceDecision,
      XTraceOptions xtraceOptions,
      AttributesBuilder attributesBuilder) {
    if (xtraceOptions != null) {
      xtraceOptions
          .getCustomKvs()
          .forEach(
              ((stringXtraceOption, s) -> attributesBuilder.put(stringXtraceOption.getKey(), s)));

      if (traceDecision.getRequestType()
              == TraceDecisionUtil.RequestType.AUTHENTICATED_TRIGGER_TRACE
          || traceDecision.getRequestType()
              == TraceDecisionUtil.RequestType.UNAUTHENTICATED_TRIGGER_TRACE) {
        attributesBuilder.put("TriggeredTrace", true);
      }

      String swKeys = xtraceOptions.getOptionValue(XTraceOption.SW_KEYS);
      if (swKeys != null) {
        attributesBuilder.put("SWKeys", swKeys);
      }
    }
  }


  /**
   * Converts an OpenTelemetry span context to a hex string.
   *
   * @param context otel span context
   * @return span context as hex string
   */
  public static String w3cContextToHexString(SpanContext context) {
    return Metadata.CURRENT_VERSION_HEXSTRING
            + Metadata.HEXSTRING_DELIMETER
            + context.getTraceId()
            + Metadata.HEXSTRING_DELIMETER
            + context.getSpanId()
            + Metadata.HEXSTRING_DELIMETER
            + context.getTraceFlags().asHex();
  }
}
