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

import android.util.Log
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient.*
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketClient {
  private val client = Builder().build()

  private var websocketConnection: WebSocket? = null

  fun connect(): WebSocketClient {
    val request = Request.Builder().url("https://echo.websocket.org/.ws").build()

    val listener =
      object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
          Log.d("WebSocketClient", "Connected to WebSocket")
          websocketConnection = webSocket
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
          Log.d("WebSocketClient", "Received: $text")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
          Log.d("WebSocketClient", "Closing: $code $reason")
          webSocket.close(1000, null)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
          Log.e("WebSocketClient", "Error: ${t.message}")
        }
      }

    client.newWebSocket(request, listener)
    return this
  }

  suspend fun hasConnected(): Boolean {
    while (websocketConnection == null) {
      delay(500)
    }
    return true
  }

  fun sendHello(): WebSocketClient {
    websocketConnection?.send("Hello, Server!")
    return this
  }

  fun close() {
    client.dispatcher.executorService.shutdown()
  }
}
