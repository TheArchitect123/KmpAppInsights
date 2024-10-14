package com.architect.kmpappinsights.storage.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class LogEntries : RealmObject {
    @PrimaryKey
    var id: String = ""
    var jsonPayload: String = ""
    var logEntryType: Int? = null
}
