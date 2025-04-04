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

package com.solarwinds.instrumentation.view.testing

import androidx.test.core.app.ApplicationProvider
import com.solarwinds.android.test.common.SolarwindsRumRule
import com.solarwinds.instrumentation.view.library.viewClassNameAttr
import com.solarwinds.instrumentation.view.library.viewIdAttr
import com.solarwinds.instrumentation.view.library.viewNameAttr
import org.junit.Rule
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

class InstrumentationTest {
    @Rule
    @JvmField
    var solarwindsRumRule: SolarwindsRumRule = SolarwindsRumRule()

    @Test
    fun test_view_click_is_captured_as_event() {
        ViewTestUtil.createViewAndSetClickListener(ApplicationProvider.getApplicationContext())
            .performClick()

        val finishedLogRecordItems = solarwindsRumRule.inMemoryLogExporter.finishedLogRecordItems
        assertThat(finishedLogRecordItems.size).isEqualTo(1)

        assertThat(finishedLogRecordItems[0].attributes.get(viewNameAttr)).isNotNull()
        assertThat(finishedLogRecordItems[0].attributes.get(viewIdAttr)).isNotNull()
        assertThat(finishedLogRecordItems[0].attributes.get(viewClassNameAttr)).isNotNull()
    }
}
