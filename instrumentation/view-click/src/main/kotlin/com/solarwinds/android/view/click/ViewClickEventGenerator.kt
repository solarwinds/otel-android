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
import android.view.View
import android.view.ViewGroup
import android.view.Window
import java.lang.ref.WeakReference
import java.util.LinkedList

internal object ViewClickEventGenerator {
    private var windowRef: WeakReference<Window>? = null

    private val viewCoordinates = IntArray(2)

    fun startTracking(window: Window) {
        windowRef = WeakReference(window)
        val currentCallback = window.callback
        val newCallback = WindowCallbackWrapper(currentCallback)

        window.callback = newCallback
    }

    fun generateClick(motionEvent: MotionEvent?) {
        windowRef?.get()?.let { window ->
            if (motionEvent != null && motionEvent.actionMasked == MotionEvent.ACTION_UP) {
                EventBuilderCreator.createEvent(appScreenClickEventName)
                    .setAttribute(yCoordinateAttr, motionEvent.y.toDouble())
                    .setAttribute(xCoordinateAttr, motionEvent.x.toDouble())
                    .emit()

                findTargetForTap(window.decorView, motionEvent.x, motionEvent.y)?.let { view ->
                    EventBuilderCreator.createEvent(viewClickEventName)
                        .setAllAttributes(EventBuilderCreator.createViewAttributes(view))
                        .emit()
                }
            }
        }
    }

    fun stopTracking() {
        windowRef?.get()?.run {
            if (callback is WindowCallbackWrapper) {
                callback = (callback as WindowCallbackWrapper).unwrap()
            }
        }
        windowRef = null
    }

    private fun findTargetForTap(
        decorView: View,
        x: Float,
        y: Float,
    ): View? {
        val queue = LinkedList<View>()
        queue.addFirst(decorView)
        var target: View? = null

        while (queue.isNotEmpty()) {
            val view = queue.removeFirst()
            if (isJetpackComposeView(view)) {
                return null
            }

            if (isValidClickTarget(view)) {
                target = view
            }

            if (view is ViewGroup) {
                handleViewGroup(view, x, y, queue)
            }
        }
        return target
    }

    private fun isValidClickTarget(view: View): Boolean = view.isClickable && view.isVisible

    private fun handleViewGroup(
        view: ViewGroup,
        x: Float,
        y: Float,
        stack: LinkedList<View>,
    ) {
        if (!view.isVisible) return

        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            if (hitTest(child, x, y) && !isJetpackComposeView(child)) {
                stack.add(child)
            }
        }
    }

    private fun hitTest(
        view: View,
        x: Float,
        y: Float,
    ): Boolean {
        view.getLocationInWindow(viewCoordinates)
        val vx = viewCoordinates[0]
        val vy = viewCoordinates[1]

        val w = view.width
        val h = view.height
        return !(x < vx || x > vx + w || y < vy || y > vy + h)
    }

    private fun isJetpackComposeView(view: View): Boolean = view::class.java.name.startsWith("androidx.compose.ui.platform.ComposeView")
}
