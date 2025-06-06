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
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.AttributeKey.longKey
import io.opentelemetry.api.common.AttributeKey.stringKey

val viewNameAttr: AttributeKey<String> = stringKey("android.view.name")
val viewClassNameAttr: AttributeKey<String> = stringKey("android.view.classname")
val viewIdAttr: AttributeKey<Long> = longKey("android.view.id")

const val clickEventName = "click"
internal fun View.getViewClassName(): String {
    return this.javaClass.canonicalName ?: this.javaClass.simpleName
}