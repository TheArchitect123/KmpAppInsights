package com.architect.kmpappinsights.storage

import com.architect.kmpappinsights.storage.models.LogEntries
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RoomStorageAccess {
    val roomDbContext by lazy {
        val configuration = RealmConfiguration.create(schema = setOf(LogEntries::class))
        Realm.open(configuration)
    }
}