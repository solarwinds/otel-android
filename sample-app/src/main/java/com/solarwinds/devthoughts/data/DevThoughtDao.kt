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

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DevThoughtDao {
    @Query("SELECT * FROM thought")
    fun findAll(): Flow<List<Thought>>

    @Query("SELECT * FROM thought WHERE devId == :devId")
    fun findByDev(devId: Int): Flow<List<Thought>>

    @Transaction
    @Query("SELECT * FROM dev  WHERE username LIKE :username")
    fun findByDevUsername(username: String): Flow<List<DevThoughts>>

    @Transaction
    @Query("SELECT * FROM dev  WHERE devId == :devId")
    fun findByDevId(devId: Int): Flow<List<DevThoughts>>

    @Insert
    suspend fun insert(vararg thought: Thought)

    @Delete
    suspend fun delete(thought: Thought)
}