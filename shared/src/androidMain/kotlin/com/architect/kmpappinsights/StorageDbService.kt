package com.architect.kmpappinsights.services

import androidx.room.Room
import androidx.room.RoomDatabase
import com.architect.kmpappinsights.storage.RoomConstants
import com.architect.kmpappinsights.storage.RoomStorageDbContext
import com.architect.kmpessentials.KmpAndroid

actual class StorageDbService {
    actual companion object {
        actual fun getDatabaseContext(): RoomDatabase.Builder<RoomStorageDbContext> {
            val appContext = KmpAndroid.getCurrentApplicationContext()
            val dbFile = appContext.getDatabasePath(RoomConstants.storageDbName)
            return Room.databaseBuilder<RoomStorageDbContext>(
                context = appContext,
                name = dbFile.absolutePath
            )
        }
    }
}

