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

import android.view.MotionEvent
import android.view.Window.Callback

class WindowCallbackWrapper(
    private val callback: Callback,
    private val viewClickEventGenerator: ViewClickEventGenerator
) : Callback by callback {

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        viewClickEventGenerator.generateClick(event)
        return callback.dispatchTouchEvent(event)
    }

    fun unwrap(): Callback = callback
}
