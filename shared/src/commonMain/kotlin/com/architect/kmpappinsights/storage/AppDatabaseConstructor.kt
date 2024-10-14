package com.architect.kmpappinsights.storage

import androidx.room.RoomDatabaseConstructor

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<RoomStorageDbContext> {
    override fun initialize(): RoomStorageDbContext
}