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
import kotlinx.coroutines.flow.Flow

@Dao
interface DevDao {
  @Query("SELECT * FROM Dev") fun findAll(): Flow<List<Dev>>

  @Query("SELECT * FROM dev WHERE devId == :devId") fun findById(devId: Int): Flow<Dev>

  @Query("SELECT * FROM dev WHERE username LIKE :username")
  fun findByUsername(username: String): Flow<List<Dev>>

  @Query("SELECT * FROM dev WHERE favorite_lang LIKE :lang")
  fun findByFavoriteLang(lang: String): Flow<List<Dev>>

  @Query("SELECT * FROM dev WHERE favorite_ide LIKE :ide")
  fun findByFavoriteIde(ide: String): Flow<List<Dev>>

  @Insert suspend fun insert(vararg devs: Dev)

  @Delete suspend fun delete(dev: Dev)
}
