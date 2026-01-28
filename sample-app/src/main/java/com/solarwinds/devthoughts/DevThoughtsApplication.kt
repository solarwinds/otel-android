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

package com.solarwinds.devthoughts

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.solarwinds.android.SolarwindsRumBuilder
import com.solarwinds.devthoughts.ui.onboarding.sessionIdPreferenceKey
import com.solarwinds.devthoughts.utils.WebsocketWorker
import com.solarwinds.devthoughts.utils.dataStore
import io.opentelemetry.android.session.SessionProvider
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class DevThoughtsApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    val collectorUrl = resources.getString(R.string.collector_url)
    val apiToken = resources.getString(R.string.api_token)
    SolarwindsRumBuilder()
      .collectorUrl(collectorUrl)
      .sessionProvider(
        object : SessionProvider {
          override fun getSessionId(): String {
            lateinit var id: String
            runBlocking {
              val settings = dataStore.data.first()
              id = settings[sessionIdPreferenceKey] ?: "unset"
            }
            return id
          }
        }
      )
      .apiToken(apiToken)
      .build(this)

    val constraints =
      Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresBatteryNotLow(true)
        .build()

    val work =
      PeriodicWorkRequestBuilder<WebsocketWorker>(1, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .build()

    val workManager = WorkManager.getInstance(this)
    workManager.enqueueUniquePeriodicWork(
      WebsocketWorker::class.simpleName!!,
      ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
      work,
    )
  }
}
