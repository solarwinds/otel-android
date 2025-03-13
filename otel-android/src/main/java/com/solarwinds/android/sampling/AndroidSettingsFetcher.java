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

import com.solarwinds.joboe.sampling.Settings;
import com.solarwinds.joboe.sampling.SettingsFetcher;
import com.solarwinds.joboe.sampling.SettingsListener;

import java.util.concurrent.CountDownLatch;

public class AndroidSettingsFetcher implements SettingsFetcher {

    private static SettingsListener listener = null;

    @Override
    public Settings getSettings() {
        Settings settings = AndroidSettingsWorker.getSettings();
        if (listener != null) {
            listener.onSettingsRetrieved(settings);
        }

        return settings;
    }

    @Override
    public void registerListener(SettingsListener settingsListener) {
        listener = settingsListener;
    }

    @Override
    public CountDownLatch isSettingsAvailableLatch() {
        return new CountDownLatch(0);
    }

    @Override
    public void close() {

    }

}
