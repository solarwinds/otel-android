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

package com.solarwinds.devthoughts.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnBoardingViewModel : ViewModel() {
    private val _dev = MutableStateFlow(Dev(0, null, null, null))

    private val _hasOnboarded = MutableStateFlow(false)

    private val _hasOnboardedDb = MutableStateFlow(false)

    private val _sessionId = MutableStateFlow("")

    val dev = _dev.asStateFlow()

    val hasOnboard = _hasOnboarded.asStateFlow()

    val hasOnboardDb = _hasOnboarded.asStateFlow()

    val sessionId = _sessionId.asStateFlow()

    fun updateUsername(username: String) {
        _dev.update {
            _dev.value.copy(username = username)
        }
    }

    fun updateIde(ide: String) {
        _dev.update {
            _dev.value.copy(favoriteIde = ide)
        }
    }

    fun updateLang(lang: String) {
        _dev.update {
            _dev.value.copy(favoriteLang = lang)
        }
    }

    fun updateHasOnboarded(onboard: Boolean) {
        _hasOnboarded.update {
            onboard
        }
    }

    fun updateHasOnboardedDb(onboard: Boolean) {
        _hasOnboardedDb.update {
            onboard
        }
    }

    fun updateSessionId(sessionId: String) {
        _sessionId.update {
            sessionId
        }
    }

}