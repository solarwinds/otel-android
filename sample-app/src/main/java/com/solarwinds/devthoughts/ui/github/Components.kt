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

package com.solarwinds.devthoughts.ui.github

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.solarwinds.devthoughts.data.GitHubEvent

@Composable
fun GithubActivityView(githubEvents: List<GitHubEvent>) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Your Github Activity", style = MaterialTheme.typography.titleLarge)
    }
    githubEvents.map {
      Spacer(Modifier.height(5.dp))
      GitHubEventView(it)
    }
  }
}

@Composable
fun GitHubEventView(gitHubEvent: GitHubEvent) {
  Card(modifier = Modifier.height(100.dp)) {
    Box(modifier = Modifier.padding(8.dp).fillMaxSize()) {
      Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {
        Row {
          Text("Type:", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.width(10.dp))
          Text(gitHubEvent.type)
        }
        Spacer(Modifier.height(10.dp))
        Row {
          Text("Repository:", style = MaterialTheme.typography.labelLarge)
          Spacer(Modifier.width(10.dp))
          Text(gitHubEvent.repo.name)
        }
      }
    }
  }
}
