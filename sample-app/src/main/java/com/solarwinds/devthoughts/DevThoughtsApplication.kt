package com.solarwinds.devthoughts

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.solarwinds.android.SolarwindsRumBuilder
import com.solarwinds.devthoughts.utils.WebsocketWorker
import io.opentelemetry.android.session.SessionProvider
import java.util.concurrent.TimeUnit

class DevThoughtsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val collectorUrl = resources.getString(R.string.collector_url)
        val apiToken = resources.getString(R.string.api_token)
        val solarwindsRum = SolarwindsRumBuilder()
            .collectorUrl(collectorUrl)
            .sessionProvider(object : SessionProvider {
                override fun getSessionId(): String {
                    return this@DevThoughtsApplication.applicationInfo.uid.toString()
                }
            })
            .apiToken(apiToken)
            .build(this)


        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val work = PeriodicWorkRequestBuilder<WebsocketWorker>(1, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(this)
        workManager.enqueueUniquePeriodicWork(
            WebsocketWorker::class.simpleName!!,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            work
        )
    }
}