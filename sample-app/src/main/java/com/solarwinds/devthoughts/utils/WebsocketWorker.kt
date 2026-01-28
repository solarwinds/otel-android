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

package com.solarwinds.devthoughts.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WebsocketWorker(appContext: Context, workerParams: WorkerParameters) :
  CoroutineWorker(appContext, workerParams) {
  override suspend fun doWork(): Result =
    try {
      val webSocketClient = WebSocketClient()
      webSocketClient.connect()
      webSocketClient.hasConnected()

      webSocketClient.sendHello()
      webSocketClient.close()
      Result.success()
    } catch (error: Throwable) {
      Result.failure()
    }
}
