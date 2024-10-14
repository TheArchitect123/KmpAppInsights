package com.architect.kmpappinsights.storage

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import com.architect.kmpappinsights.storage.daos.LogDao
import com.architect.kmpappinsights.storage.models.LogEntries

@Database(entities = [LogEntries::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class RoomStorageDbContext : RoomDatabase() {
    abstract fun getDao(): LogDao
}

