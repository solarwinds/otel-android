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

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solarwinds.devthoughts.data.DevThoughtsDatabase
import com.solarwinds.devthoughts.data.OnBoardingViewModel
import com.solarwinds.devthoughts.data.Repository
import com.solarwinds.devthoughts.ui.onboarding.FavoriteIde
import com.solarwinds.devthoughts.ui.onboarding.FavoriteLang
import com.solarwinds.devthoughts.ui.onboarding.IdeRoute
import com.solarwinds.devthoughts.ui.onboarding.LangRoute
import com.solarwinds.devthoughts.ui.onboarding.Loading
import com.solarwinds.devthoughts.ui.onboarding.SessionId
import com.solarwinds.devthoughts.ui.onboarding.SessionIdRoute
import com.solarwinds.devthoughts.ui.onboarding.Username
import com.solarwinds.devthoughts.ui.onboarding.UsernameRoute
import com.solarwinds.devthoughts.ui.onboarding.onBoardingPreferenceKey
import com.solarwinds.devthoughts.ui.onboarding.sessionIdPreferenceKey
import com.solarwinds.devthoughts.ui.theme.AppTheme
import com.solarwinds.devthoughts.utils.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class OnboardingActivity : ComponentActivity() {
  private val viewmodel: OnBoardingViewModel by viewModels()
  private lateinit var repository: Repository

  @OptIn(ExperimentalMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    repository = Repository.create(DevThoughtsDatabase.getInstance(applicationContext))

    setContent {
      val navController = rememberNavController()
      val hasOnboarded = viewmodel.hasOnboard.collectAsState(initial = false)
      val hasOnboardedDb = viewmodel.hasOnboardDb.collectAsState(initial = false)

      var loading by remember { mutableStateOf(true) }
      AppTheme {
        if (loading) {
          LaunchedEffect(onBoardingPreferenceKey) {
            applicationContext.dataStore.data
              .map { settings -> settings[onBoardingPreferenceKey] ?: false }
              .collectLatest { loading = false }
          }
          Loading()
        } else {
          if (hasOnboarded.value) {
            Loading()
            LaunchedEffect(true) {
              applicationContext.dataStore.edit { settings ->
                settings[onBoardingPreferenceKey] = true
                settings[sessionIdPreferenceKey] = viewmodel.sessionId.value
              }

              launch(Dispatchers.IO) {
                repository.writeDev(viewmodel.dev.value)
                viewmodel.updateHasOnboardedDb(true)
              }
            }

            if (hasOnboardedDb.value) {
              startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
              finish()
            }
          } else {
            Scaffold(
              modifier = Modifier.fillMaxSize(),
              topBar = {
                TopAppBar(
                  colors =
                    TopAppBarDefaults.topAppBarColors(
                      containerColor = MaterialTheme.colorScheme.primaryContainer,
                      titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                  title = { Text(getString(R.string.app_name)) },
                )
              },
            ) { innerPadding ->
              val navigation = { route: Any -> navController.navigate(route) }

              NavHost(
                navController = navController,
                startDestination = UsernameRoute,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
              ) {
                composable<UsernameRoute> { Username(viewmodel, navigation) }

                composable<LangRoute> { FavoriteLang(viewmodel, navigation) }

                composable<IdeRoute> { FavoriteIde(viewmodel, navigation) }

                composable<SessionIdRoute> {
                  SessionId(viewmodel, navigation) { viewmodel.updateHasOnboarded(true) }
                }
              }
            }
          }
        }
      }
    }
  }
}
