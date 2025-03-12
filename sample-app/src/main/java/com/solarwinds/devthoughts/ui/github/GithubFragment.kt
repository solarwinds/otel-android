package com.solarwinds.devthoughts.ui.github

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.compose.content
import androidx.lifecycle.lifecycleScope
import com.solarwinds.devthoughts.data.AppViewModel
import com.solarwinds.devthoughts.data.GitHubEvent
import com.solarwinds.devthoughts.utils.meterProviderName
import com.solarwinds.devthoughts.utils.solarwindsRum
import com.solarwinds.devthoughts.ui.onboarding.sessionIdPreferenceKey
import com.solarwinds.devthoughts.ui.theme.AppTheme
import com.solarwinds.devthoughts.utils.dataStore
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.LongGauge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class GithubFragment : Fragment() {
    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient()

    private val urlPlaceHolder = "https://api.github.com/users/%s/events"

    private val appViewModel: AppViewModel by activityViewModels()

    private lateinit var githubActivityGauge: LongGauge

    private var sessionId: String = "unset"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val solarwindsRum = context.solarwindsRum()
        githubActivityGauge = solarwindsRum.openTelemetryRum.openTelemetry.meterProvider
            .get(context.meterProviderName)
            .gaugeBuilder("github.events")
            .ofLongs()
            .build()

        lifecycleScope.launch {
            context.dataStore.data.map { settings ->
                settings[sessionIdPreferenceKey] ?: "unset"
            }.collectLatest {
                sessionId = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = content {
        val githubActivity by fetchGitHubActivity().collectAsState(listOf(), Dispatchers.IO)
        AppTheme {
            if (githubActivity.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("You don't have any activity")
                            }
                        }
                    }
                }
            } else {
                GithubActivityView(githubActivity)
            }
        }
    }

    private fun fetchGitHubActivity() = flow {
        appViewModel.dev.collect {
            var gitHubEvents = listOf<GitHubEvent>()
            try {
                val request = Request.Builder()
                    .url(urlPlaceHolder.format(it?.username))
                    .header("Accept", "application/vnd.github.v3+json")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful && response.body != null) {
                    val payload = response.body!!.string()
                    gitHubEvents = json.decodeFromString<List<GitHubEvent>>(payload)

                }
                githubActivityGauge.set(
                    gitHubEvents.size.toLong(),
                    Attributes.of(
                        AttributeKey.stringKey("username"), it!!.username!!,
                        AttributeKey.stringKey("session.id"), sessionId,
                    )
                )
                emit(gitHubEvents)

            } catch (e: Throwable) {
                emit(gitHubEvents)
            }
        }

    }
}