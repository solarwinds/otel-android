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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.Fragment
import androidx.fragment.compose.content
import com.solarwinds.devthoughts.data.DevThoughtsDatabase
import com.solarwinds.devthoughts.data.Repository
import com.solarwinds.devthoughts.data.Thought
import com.solarwinds.devthoughts.ui.theme.AppTheme

class HomeFragment : Fragment() {
    private lateinit var repository: Repository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View =
        content {
            repository = Repository.create(DevThoughtsDatabase.getInstance(requireContext()))
            val thoughts: List<Thought> by repository.findAllThoughts().collectAsState(listOf())

            AppTheme {
                if (thoughts.isEmpty()) {
                    HomePlaceholder()
                } else {
                    DevThoughtsView(thoughts)
                }
            }
        }
}
