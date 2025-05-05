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

package com.solarwinds.android.view.click
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.AttributeKey.doubleKey
import io.opentelemetry.api.common.AttributeKey.longKey
import io.opentelemetry.api.common.AttributeKey.stringKey

const val APP_SCREEN_CLICK_EVENT_NAME = "app.screen.click"
const val VIEW_CLICK_EVENT_NAME = "event.app.widget.click"
val viewNameAttr: AttributeKey<String> = stringKey("app.widget.name")

val xCoordinateAttr: AttributeKey<Double> = doubleKey("app.screen.coordinate.x")
val yCoordinateAttr: AttributeKey<Double> = doubleKey("app.screen.coordinate.y")
val viewIdAttr: AttributeKey<Long> = longKey("app.widget.id")

