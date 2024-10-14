package com.architect.kmpappinsights.storage

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.architect.kmpappinsights.services.StorageDbService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

object RoomStorageAccess {
    val roomDbContext by lazy {
        getRoomDatabase(StorageDbService.getDatabaseContext())
    }

    private fun getRoomDatabase(
        builder: RoomDatabase.Builder<RoomStorageDbContext>
    ): RoomStorageDbContext {
        return builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    val logDaoAccess by lazy {
        RoomStorageAccess.roomDbContext.getDao()
    }
}