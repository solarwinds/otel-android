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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.solarwinds.joboe.sampling.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
class AndroidSettingsFetcherTest {

    @InjectMocks
    private AndroidSettingsFetcher tested;

    @Mock
    private SettingsListener settingsListenerMock;

    @Mock
    private Settings settingsMock;

    @Test
    void doesNotThrowWhenListenerIsNotSet() {
        assertDoesNotThrow(() -> tested.getSettings());
    }

    @Test
    void verifyListenerIsCalled() {
        tested.registerListener(settingsListenerMock);
        try (MockedStatic<AndroidSettingsWorker> workerMock = mockStatic(AndroidSettingsWorker.class)) {
            workerMock.when(AndroidSettingsWorker::getSettings).thenReturn(settingsMock);
            tested.getSettings();
            verify(settingsListenerMock).onSettingsRetrieved(any());
        }
    }
}
