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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Dev::class, Thought::class], version = 1, exportSchema = false)
abstract class DevThoughtsDatabase : RoomDatabase() {
  abstract fun devDao(): DevDao

  abstract fun devThoughtDao(): DevThoughtDao

  companion object {
    private lateinit var database: DevThoughtsDatabase

    fun getInstance(context: Context): DevThoughtsDatabase {
      if (::database.isInitialized) {
        return database
      }

      synchronized(this) {
        database =
          Room.databaseBuilder(context, DevThoughtsDatabase::class.java, "dev.thoughts.db").build()
      }
      return database
    }
  }
}
