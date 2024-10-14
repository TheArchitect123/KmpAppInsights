package com.architect.kmpappinsights.services

import androidx.room.Room
import androidx.room.RoomDatabase
import com.architect.kmpappinsights.storage.RoomConstants
import com.architect.kmpappinsights.storage.RoomStorageDbContext
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent

actual class StorageDbService {
    actual companion object {
        actual fun getDatabaseContext(): RoomDatabase.Builder<RoomStorageDbContext> {
            return Room.databaseBuilder<RoomStorageDbContext>(
                name = getIosDocumentsDirectory().let {
                    it as NSString
                    it.stringByAppendingPathComponent(RoomConstants.storageDbName)
                }
            )
        }

        private fun getIosDocumentsDirectory(): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            return paths.first() as String
        }
    }
}