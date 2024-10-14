package com.architect.kmpappinsights.storage.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LogEntries(@PrimaryKey(autoGenerate = true) val id: Long = 0, val jsonPayload: String, val logEntryType : InsightsDataType)
