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

import android.app.Activity
import android.app.Application
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.Window.Callback
import com.solarwinds.android.test.common.hasEventName
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.slot
import io.mockk.verify
import io.opentelemetry.android.instrumentation.InstallationContext
import io.opentelemetry.android.session.SessionManager
import io.opentelemetry.sdk.logs.data.internal.ExtendedLogRecordData
import io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.assertThat
import io.opentelemetry.sdk.testing.assertj.OpenTelemetryAssertions.equalTo
import io.opentelemetry.sdk.testing.junit4.OpenTelemetryRule
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@ExtendWith(MockKExtension::class)
class ViewClickInstrumentationTest {
    private lateinit var openTelemetryRule: OpenTelemetryRule

    @MockK
    lateinit var window: Window

    @MockK
    lateinit var callback: Callback

    @MockK
    lateinit var activity: Activity

    @MockK
    lateinit var application: Application

    @Before
    fun setUp() {
        openTelemetryRule = OpenTelemetryRule.create()
        MockKAnnotations.init(this, relaxUnitFun = true)
    }

    @Test
    fun capture_view_click() {
        val installationContext =
            InstallationContext(
                application,
                openTelemetryRule.openTelemetry,
                mockk<SessionManager>()
            )

        val callbackCapturingSlot = slot<ViewClickActivityCallback>()
        every { window.callback } returns callback
        every { callback.dispatchTouchEvent(any()) } returns false

        every { activity.window } returns window
        every { application.registerActivityLifecycleCallbacks(any()) } returns Unit

        ViewClickInstrumentation().install(installationContext)

        verify {
            application.registerActivityLifecycleCallbacks(capture(callbackCapturingSlot))
        }

        val viewClickActivityCallback = callbackCapturingSlot.captured
        val wrapperCapturingSlot = slot<WindowCallbackWrapper>()
        every { window.callback = any() } returns Unit

        val motionEvent =
            MotionEvent.obtain(0L, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 250f, 50f, 0)
        val mockView = mockView<View>(10012, motionEvent)
        every { window.decorView } returns mockView

        viewClickActivityCallback.onActivityResumed(activity)
        verify {
            window.callback = capture(wrapperCapturingSlot)
        }

        wrapperCapturingSlot.captured.dispatchTouchEvent(
            motionEvent
        )

        val events = openTelemetryRule.logRecords
        assertThat(events).hasSize(2)

        var event = events[0] as ExtendedLogRecordData
        assertThat(event)
            .hasEventName(appScreenClickEventName)
            .hasAttributesSatisfyingExactly(
                equalTo(xCoordinateAttr, motionEvent.x.toDouble()),
                equalTo(yCoordinateAttr, motionEvent.y.toDouble()),
            )

        event = events[1] as ExtendedLogRecordData
        assertThat(event)
            .hasEventName(viewClickEventName)
            .hasAttributesSatisfyingExactly(
                equalTo(xCoordinateAttr, mockView.x.toDouble()),
                equalTo(yCoordinateAttr, mockView.y.toDouble()),
                equalTo(viewIdAttr, mockView.id),
                equalTo(viewNameAttr, EventBuilderCreator.viewToName(mockView)),
            )
    }

    @Test
    fun capture_view_click_in_viewGroup() {
        val installationContext =
            InstallationContext(
                application,
                openTelemetryRule.openTelemetry,
                mockk<SessionManager>()
            )

        val callbackCapturingSlot = slot<ViewClickActivityCallback>()
        every { window.callback } returns callback
        every { callback.dispatchTouchEvent(any()) } returns false

        every { activity.window } returns window
        every { application.registerActivityLifecycleCallbacks(any()) } returns Unit

        ViewClickInstrumentation().install(installationContext)
        verify {
            application.registerActivityLifecycleCallbacks(capture(callbackCapturingSlot))
        }

        val viewClickActivityCallback = callbackCapturingSlot.captured
        val wrapperCapturingSlot = slot<WindowCallbackWrapper>()
        every { window.callback = any() } returns Unit

        val motionEvent =
            MotionEvent.obtain(0L, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 250f, 50f, 0)
        val mockView = mockView<View>(10012, motionEvent)
        val mockViewGroup = mockView<ViewGroup>(10013, motionEvent, clickable = false) {
            every { it.childCount } returns 1
            every { it.getChildAt(any()) } returns mockView
        }

        every { window.decorView } returns mockViewGroup
        viewClickActivityCallback.onActivityResumed(activity)

        verify {
            window.callback = capture(wrapperCapturingSlot)
        }

        wrapperCapturingSlot.captured.dispatchTouchEvent(
            motionEvent
        )

        val events = openTelemetryRule.logRecords
        assertThat(events).hasSize(2)

        var event = events[0] as ExtendedLogRecordData
        assertThat(event)
            .hasEventName(appScreenClickEventName)
            .hasAttributesSatisfyingExactly(
                equalTo(xCoordinateAttr, motionEvent.x.toDouble()),
                equalTo(yCoordinateAttr, motionEvent.y.toDouble()),
            )

        event = events[1] as ExtendedLogRecordData
        assertThat(event)
            .hasEventName(viewClickEventName)
            .hasAttributesSatisfyingExactly(
                equalTo(xCoordinateAttr, mockView.x.toDouble()),
                equalTo(yCoordinateAttr, mockView.y.toDouble()),
                equalTo(viewIdAttr, mockView.id),
                equalTo(viewNameAttr, EventBuilderCreator.viewToName(mockView)),
            )
    }

    @Test
    fun not_captured_view_click_in_viewGroup() {
        val installationContext =
            InstallationContext(
                application,
                openTelemetryRule.openTelemetry,
                mockk<SessionManager>()
            )

        val callbackCapturingSlot = slot<ViewClickActivityCallback>()
        every { window.callback } returns callback
        every { callback.dispatchTouchEvent(any()) } returns false

        every { activity.window } returns window
        every { application.registerActivityLifecycleCallbacks(any()) } returns Unit

        ViewClickInstrumentation().install(installationContext)
        verify {
            application.registerActivityLifecycleCallbacks(capture(callbackCapturingSlot))
        }

        val viewClickActivityCallback = callbackCapturingSlot.captured
        val wrapperCapturingSlot = slot<WindowCallbackWrapper>()
        every { window.callback = any() } returns Unit

        val motionEvent =
            MotionEvent.obtain(0L, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 250f, 50f, 0)
        val mockView = mockView<View>(10012, motionEvent, hitOffset = intArrayOf(50, 30))
        val mockViewGroup =
            mockView<ViewGroup>(10013, motionEvent, clickable = false) {
                every { it.childCount } returns 1
                every { it.getChildAt(any()) } returns mockView
            }

        every { window.decorView } returns mockViewGroup

        viewClickActivityCallback.onActivityResumed(activity)
        verify {
            window.callback = capture(wrapperCapturingSlot)
        }

        wrapperCapturingSlot.captured.dispatchTouchEvent(
            motionEvent
        )

        val events = openTelemetryRule.logRecords
        assertThat(events).hasSize(1)

        val event = events[0] as ExtendedLogRecordData
        assertThat(event)
            .hasEventName(appScreenClickEventName)
            .hasAttributesSatisfyingExactly(
                equalTo(xCoordinateAttr, motionEvent.x.toDouble()),
                equalTo(yCoordinateAttr, motionEvent.y.toDouble()),
            )
    }

    @Test
    fun not_captured_view_click_for_down_event() {
        val installationContext =
            InstallationContext(
                application,
                openTelemetryRule.openTelemetry,
                mockk<SessionManager>()
            )

        val callbackCapturingSlot = slot<ViewClickActivityCallback>()
        every { window.callback } returns callback
        every { callback.dispatchTouchEvent(any()) } returns false

        every { activity.window } returns window
        every { application.registerActivityLifecycleCallbacks(any()) } returns Unit

        ViewClickInstrumentation().install(installationContext)
        verify {
            application.registerActivityLifecycleCallbacks(capture(callbackCapturingSlot))
        }

        val viewClickActivityCallback = callbackCapturingSlot.captured
        val wrapperCapturingSlot = slot<WindowCallbackWrapper>()
        every { window.callback = any() } returns Unit

        val motionEvent =
            MotionEvent.obtain(0L, SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 250f, 50f, 0)

        viewClickActivityCallback.onActivityResumed(activity)
        verify {
            window.callback = capture(wrapperCapturingSlot)
        }

        wrapperCapturingSlot.captured.dispatchTouchEvent(
            motionEvent
        )

        val events = openTelemetryRule.logRecords
        assertThat(events).hasSize(0)
    }

    private inline fun <reified T : View> mockView(
        id: Int,
        motionEvent: MotionEvent,
        hitOffset: IntArray = intArrayOf(0, 0),
        clickable: Boolean = true,
        visibility: Int = View.VISIBLE,
        applyOthers: (T) -> Unit = {}
    ): T {

        val mockView = mockkClass(T::class)
        every { mockView.visibility } returns visibility
        every { mockView.isClickable } returns clickable

        every { mockView.id } returns id
        val location = IntArray(2)

        location[0] = (motionEvent.x + hitOffset[0]).toInt()
        location[1] = (motionEvent.y + hitOffset[1]).toInt()

        val arrayCapturingSlot = slot<IntArray>()
        every { mockView.getLocationInWindow(capture(arrayCapturingSlot)) } answers {
            arrayCapturingSlot.captured[0] = location[0]
            arrayCapturingSlot.captured[1] = location[1]
        }

        every { mockView.x } returns location[0].toFloat()
        every { mockView.y } returns location[1].toFloat()

        every { mockView.width } returns (location[0] + hitOffset[0])
        every { mockView.height } returns (location[1] + hitOffset[1])
        applyOthers.invoke(mockView)

        return mockView
    }
}