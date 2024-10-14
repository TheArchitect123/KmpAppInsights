package com.architect.kmpappinsights.services

import androidx.room.RoomDatabase
import com.architect.kmpappinsights.storage.RoomStorageDbContext

expect class StorageDbService {
    companion object {
        fun getDatabaseContext(): RoomDatabase.Builder<RoomStorageDbContext>
    }
}