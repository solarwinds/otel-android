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

package com.solarwinds.devthoughts.ui.thought

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.solarwinds.devthoughts.data.Thought

@Composable
fun HomePlaceholder() {
  Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.Center,
  ) {
    Card(modifier = Modifier.fillMaxWidth().height(150.dp)) {
      Box(modifier = Modifier.padding(8.dp).fillMaxSize()) {
        Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.fillMaxSize(),
        ) {
          Text("You haven't registered any thoughts, sadly!")
          Text("No thoughts yet?")
        }
      }
    }
  }
}

@Composable
fun ThoughtView(thought: Thought) {
  var show by remember { mutableStateOf(false) }
  var scenario by remember { mutableStateOf("") }

  if (show) {
    InputDialog(scenario, { show = false }) {
      when (it.lowercase()) {
        "a crash" -> throw RuntimeException("App crashing")
        "an anr" -> Thread.sleep(6000)
      }
      show = false
    }
  } else {
    Card(
      modifier =
        Modifier.fillMaxWidth().height(50.dp).pointerInput(Unit) {
          detectTapGestures(
            onDoubleTap = {
              show = true
              scenario = "a crash"
            },
            onLongPress = {
              show = true
              scenario = "an anr"
            },
          )
        }
    ) {
      Box(Modifier.fillMaxSize().padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxSize()) {
          Text(thought.body)
        }
      }
    }
  }
}

@Composable
fun DevThoughtsView(thoughts: List<Thought>) {
  Column(
    verticalArrangement = Arrangement.Top,
    modifier = Modifier.fillMaxSize().padding(8.dp).verticalScroll(rememberScrollState()),
  ) {
    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text("Dev thoughts", style = MaterialTheme.typography.titleLarge)
    }
    thoughts.map {
      Spacer(Modifier.height(5.dp))
      ThoughtView(it)
    }
  }
}

@Composable
fun InputDialog(scenario: String, onDismiss: () -> Unit, onSubmit: (String) -> Unit) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Alert") },
    text = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
      ) {
        Text("You're about to cause $scenario")
      }
    },
    confirmButton = { TextButton(onClick = { onSubmit(scenario) }) { Text("Proceed") } },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
  )
}
