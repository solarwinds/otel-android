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

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;

public class TraceStateSamplingResult implements SamplingResult {
  private final SamplingResult delegated;
  private final Attributes additionalAttributes;

  private TraceStateSamplingResult(
      SamplingResult delegated, Attributes additionalAttributes) {
    this.delegated = delegated;
    this.additionalAttributes = additionalAttributes;
  }

  public static SamplingResult wrap(
      SamplingResult result, Attributes additionalAttributes) {
    return new TraceStateSamplingResult(result, additionalAttributes);
  }

  @Override
  public SamplingDecision getDecision() {
    return delegated.getDecision();
  }

  @Override
  public Attributes getAttributes() {
    return delegated.getAttributes().toBuilder().putAll(additionalAttributes).build();
  }
}
