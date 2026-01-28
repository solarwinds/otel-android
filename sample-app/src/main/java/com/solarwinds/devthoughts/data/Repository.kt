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

package com.solarwinds.devthoughts.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository(private val devDao: DevDao, private val thoughtDao: DevThoughtDao) {
  private val coroutineScope = CoroutineScope(Dispatchers.IO)

  fun writeDev(dev: Dev) {
    coroutineScope.launch { devDao.insert(dev) }
  }

  fun writeThought(thought: Thought) {
    coroutineScope.launch { thoughtDao.insert(thought) }
  }

  fun findDev(devId: Int): Flow<Dev?> = devDao.findById(devId)

  fun findAllThoughts(): Flow<List<Thought>> = thoughtDao.findAll()

  fun findAllDev(): Flow<List<Dev>> = devDao.findAll()

  companion object {
    fun create(database: DevThoughtsDatabase): Repository =
      Repository(database.devDao(), database.devThoughtDao())
  }
}
