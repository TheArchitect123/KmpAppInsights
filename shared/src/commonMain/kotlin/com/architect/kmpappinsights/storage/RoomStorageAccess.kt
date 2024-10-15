package com.architect.kmpappinsights.storage

import com.architect.kmpappinsights.storage.models.LogEntries
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RoomStorageAccess {
    private val config = RealmConfiguration.create(schema = setOf(LogEntries::class))
    var roomDbContext = Realm.open(config)

    fun generateNewRealmInstance(): Realm {
        roomDbContext = Realm.open(config)
        return roomDbContext
    }
}