package com.architect.kmpappinsights.storage.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.architect.kmpappinsights.storage.models.LogEntries

@Dao
interface LogDao {
    @Insert
    suspend fun insertLogDato(item: LogEntries)

    @Query("SELECT * FROM LogEntries")
    suspend fun getAllLogs() : List<LogEntries>

    @Query("DELETE FROM LogEntries")
    suspend fun clearAllLogs()
}