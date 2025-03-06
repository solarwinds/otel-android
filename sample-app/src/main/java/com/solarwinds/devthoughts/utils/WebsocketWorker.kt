package com.solarwinds.devthoughts.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WebsocketWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        return try {
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

}