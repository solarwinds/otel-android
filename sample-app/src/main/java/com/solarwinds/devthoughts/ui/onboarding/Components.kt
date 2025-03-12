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

package com.solarwinds.devthoughts.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.solarwinds.devthoughts.data.OnBoardingViewModel
import kotlinx.serialization.Serializable


@Serializable
object UsernameRoute

@Serializable
object LangRoute

@Serializable
object IdeRoute

@Serializable
object SessionIdRoute

val onBoardingPreferenceKey = booleanPreferencesKey("onboarded")

val sessionIdPreferenceKey = stringPreferencesKey("session-id")


@Composable
fun Username(viewModel: OnBoardingViewModel, navigation: (route: Any) -> Unit) {
    val dev by viewModel.dev.collectAsState()

    var usernameValid by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Please enter your GitHub username")
                    OutlinedTextField(dev.username ?: "", onValueChange = {
                        viewModel.updateUsername(it)
                        usernameValid = it.isNotEmpty()
                    }, label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        TextButton(
            enabled = usernameValid,
            onClick = {
                navigation(LangRoute)
            },
        ) {
            Text("Next")
        }
    }
}

@Composable
fun FavoriteLang(viewModel: OnBoardingViewModel, navigation: (route: Any) -> Unit) {
    val dev by viewModel.dev.collectAsState()
    var langValid by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Please enter your favorite programming language")
                    OutlinedTextField(dev.favoriteLang ?: "", onValueChange = {
                        viewModel.updateLang(it)
                        langValid = it.isNotEmpty()
                    }, label = { Text("Language") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    navigation(UsernameRoute)
                },
            ) {
                Text("Previous")
            }
            TextButton(
                enabled = langValid,
                onClick = {
                    navigation(IdeRoute)
                },
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun FavoriteIde(
    viewModel: OnBoardingViewModel,
    navigation: (route: Any) -> Unit
) {
    val dev by viewModel.dev.collectAsState()
    var ideValid by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Please enter your favorite IDE")
                    OutlinedTextField(dev.favoriteIde ?: "", onValueChange = {
                        viewModel.updateIde(it)
                        ideValid = it.isNotEmpty()
                    }, label = { Text("IDE") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    navigation(LangRoute)
                },
            ) {
                Text("Previous")
            }
            TextButton(
                enabled = ideValid,
                onClick = { navigation(SessionIdRoute) },
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun SessionId(
    viewModel: OnBoardingViewModel,
    navigation: (route: Any) -> Unit,
    finishOnboarding: () -> Unit
) {
    val sessionId by viewModel.sessionId.collectAsState()
    var idValid by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Please enter a session id")
                    OutlinedTextField(sessionId, onValueChange = {
                        viewModel.updateSessionId(it)
                        idValid = it.isNotEmpty()
                    }, label = { Text("Session id") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(
                onClick = {
                    navigation(LangRoute)
                },
            ) {
                Text("Previous")
            }
            TextButton(
                enabled = idValid,
                onClick = finishOnboarding,
            ) {
                Text("Finish")
            }
        }
    }
}

@Composable
fun Loading() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}
