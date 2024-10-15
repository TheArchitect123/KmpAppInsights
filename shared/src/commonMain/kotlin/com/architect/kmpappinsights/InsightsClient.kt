package com.architect.kmpappinsights

import com.architect.kmpappinsights.container.InsightsContainer
import com.architect.kmpappinsights.contracts.BaseEventCustomData
import com.architect.kmpappinsights.contracts.BaseEventExceptionCustomData
import com.architect.kmpappinsights.contracts.BaseEventTraceCustomData
import com.architect.kmpappinsights.contracts.BasePageEventCustomData
import com.architect.kmpappinsights.contracts.BasePageEventSubCustomData
import com.architect.kmpappinsights.contracts.BaseRequestCustomData
import com.architect.kmpappinsights.contracts.CustomBaseData
import com.architect.kmpappinsights.contracts.CustomEvent
import com.architect.kmpappinsights.contracts.CustomTraceBaseData
import com.architect.kmpappinsights.contracts.ExceptionDetailsInfo
import com.architect.kmpappinsights.contracts.ExceptionEvent
import com.architect.kmpappinsights.contracts.ExceptionInfo
import com.architect.kmpappinsights.contracts.ExceptionStackTraceDetailsInfo
import com.architect.kmpappinsights.contracts.PageEvent
import com.architect.kmpappinsights.contracts.RequestCustomBaseData
import com.architect.kmpappinsights.contracts.RequestEvent
import com.architect.kmpappinsights.contracts.RequestStorageData
import com.architect.kmpappinsights.contracts.TraceEvent
import com.architect.kmpappinsights.contracts.TraceSeverityLevel
import com.architect.kmpappinsights.services.CrashReporting
import com.architect.kmpappinsights.services.ExceptionFormatter
import com.architect.kmpappinsights.storage.RoomStorageAccess
import com.architect.kmpappinsights.storage.models.InsightsDataType
import com.architect.kmpappinsights.storage.models.LogEntries
import com.architect.kmpessentials.aliases.DefaultAction
import com.architect.kmpessentials.backgrounding.BackgroundOptions
import com.architect.kmpessentials.backgrounding.KmpBackgrounding
import com.architect.kmpessentials.deviceInfo.DevicePlatform
import com.architect.kmpessentials.deviceInfo.KmpDeviceInfo
import com.architect.kmpessentials.launcher.KmpLauncher
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.log.RealmLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

typealias EventTypeMap = Map<String, String>

object InsightsClient {

    private val storageInMemory = mutableListOf<LogEntries>()

    private var timerUploadInSeconds: Int? = null
    private suspend fun processLogEntries() {
        try {
            val realmLink = RoomStorageAccess.roomDbContext
            val allLogs = realmLink.query(LogEntries::class).find()
            if (allLogs.isNotEmpty()) {
                val jsonEntries = allLogs.joinToString("\n") { it.jsonPayload }
                val response =
                    InsightsContainer.appInsightsHttpService.postBatchJson(jsonEntries)
                if (response.errors.isEmpty()) {
                    // clear all the logs currently running on storage
                    realmLink.write {
                        val liveLogs = allLogs.mapNotNull { logEntry ->
                            findLatest(logEntry) // Converts frozen objects to live objects
                        }

                        liveLogs.forEach { logEntry ->
                            delete(logEntry)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            writeException(
                Exception("Failed to upload logs to AppInsights. Will try again in the next $timerUploadInSeconds seconds \n ${ex.stackTraceToString()}"),
                mapOf()
            )
        }
    }

    private suspend fun processLogEntriesForStorage() {
        try {
            var realmLink = RoomStorageAccess.roomDbContext
            if (realmLink.isClosed()) {
                // reopen the realm and generate a new instance
                realmLink = RoomStorageAccess.generateNewRealmInstance()
            }

            val storeItems = storageInMemory.toList()
            if (storeItems.isNotEmpty()) {
                realmLink.write {
                    storeItems.forEach {
                        copyToRealm(it)
                    }
                }
            }

            storageInMemory.removeAll(storeItems)
        } catch (ex: Exception) {
            writeException(
                Exception("Failed to store logs on internal storage. Will try again soon. \n ${ex.stackTraceToString()}"),
                mapOf()
            )
        }
    }

    fun configureInsightsClient(
        instrumentationKey: String,
        timerUploadInSeconds: Int = 30,
        allowBackgrounding: Boolean = false,
        allowCrashReporting: Boolean = false,
        isDeveloperMode: Boolean = false,
    ): InsightsClient {
        if (timerUploadInSeconds < 15) {
            throw Exception("Timer for AppInsights can't be less than 15 seconds. Please try any number higher than 15 seconds (Preferably, 15 to 30)")
        }

        if (isDeveloperMode) {
            RealmLog.setLevel(LogLevel.ALL)
        }

        if (allowCrashReporting) {
            CrashReporting.registerForCrashReporting()
        }

        InsightsContainer.instrumentationKey = instrumentationKey
        this.timerUploadInSeconds = timerUploadInSeconds

        val timerBoot = timerUploadInSeconds.toDouble()
        KmpLauncher.startTimerRepeating(timerBoot) {
            // run the broadcast
            if (allowBackgrounding) {
                KmpBackgrounding.createAndStartWorkerWithoutCancel(
                    BackgroundOptions(
                        requiresInternet = true,
                        requiresStorage = true
                    )
                ) {
                    processLogEntries()
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    processLogEntries()
                }
            }

            true
        }

        KmpLauncher.startTimerRepeating(5.0) {
            if (allowBackgrounding) {
                KmpBackgrounding.createAndStartWorkerWithoutCancel(
                    BackgroundOptions(
                        requiresStorage = true
                    )
                ) {
                    processLogEntriesForStorage()
                }
            } else {
                GlobalScope.launch(Dispatchers.IO) {
                    processLogEntriesForStorage()
                }
            }

            true
        }

        return this
    }

    fun forceFlushAllLogs(): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            processLogEntries()
        }

        return this
    }

    fun forceFlushAllLogs(optionalAction: DefaultAction): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            processLogEntries()
            optionalAction()
        }

        return this
    }

//    fun writeAvailability(message: EventTypeMap, eventName: String): InsightsClient {
//        return this
//    }
//
//    fun writeDependency(message: EventTypeMap, eventName: String): InsightsClient {
//        return this
//    }

    fun writeCustomEvent(message: EventTypeMap, eventName: String): InsightsClient {
        val jsonElement = Json.encodeToJsonElement(
            CustomEvent.serializer(), CustomEvent(
                name = InsightsContainer.eventBaseType,
                insightsKey = InsightsContainer.instrumentationKey,
                time = Clock.System.now().toString(),
                data = CustomBaseData(
                    type = "EventData",
                    customData = BaseEventCustomData(
                        version = "1",
                        eventName = eventName,
                        eventProperties = message
                    )
                )
            )
        )

        storageInMemory.add(LogEntries().apply {
            jsonPayload = jsonElement.toString()
            logEntryType = InsightsDataType.CustomEvent.ordinal
        })

        return this
    }

    fun writePageView(
        message: EventTypeMap,
        eventName: String,
        pageName: String,
        sessionId: String = ""
    ): InsightsClient {
        val jsonElement = Json.encodeToJsonElement(
            PageEvent.serializer(),
            PageEvent(
                name = InsightsContainer.eventBaseType,
                insightsKey = InsightsContainer.instrumentationKey,
                time = Clock.System.now().toString(),
                data = BasePageEventCustomData(
                    type = "PageViewData",
                    customData = BasePageEventSubCustomData(
                        version = "1",
                        eventProperties = message,
                        eventName = eventName,
                        url = pageName,
                        sessionId = sessionId
                    )
                )
            )
        )

        storageInMemory.add(LogEntries().apply {
            jsonPayload = jsonElement.toString()
            logEntryType = InsightsDataType.PageView.ordinal
        })

        return this
    }

    fun writeRequest(
        message: EventTypeMap,
        eventName: String,
        requestInfo: RequestStorageData
    ): InsightsClient {
        //dd.hh:mm:ss.fff
        val formedMilliseconds =
            requestInfo.durationMs.milliseconds.toDouble(DurationUnit.MILLISECONDS) / 1000
        val fc = formedMilliseconds.toString()

        var milliseconds = "00"
        var seconds = ""
        if (fc.contains(".")) {
            milliseconds =
                (fc.substring(fc.indexOf(".")).toDouble() * 1000).toLong().toString()
            seconds = fc.substring(0, fc.indexOf(".")).toLong().toString()
        } else {
            seconds = fc
        }

        val jsonElement = Json.encodeToJsonElement(
            RequestEvent.serializer(),
            RequestEvent(
                name = InsightsContainer.eventBaseType,
                insightsKey = InsightsContainer.instrumentationKey,
                time = Clock.System.now().toString(),
                data = RequestCustomBaseData(
                    type = "RequestData",
                    customData = BaseRequestCustomData(
                        id = Random.nextInt(),
                        version = 1,
                        eventName = eventName,
                        eventProperties = message,
                        url = requestInfo.requestUrl,
                        source = requestInfo.source,
                        duration = "00.00:00:${seconds}.$milliseconds",
                        responseCode = requestInfo.responseCode,
                    )
                )
            )
        )

        storageInMemory.add(LogEntries().apply {
            jsonPayload = jsonElement.toString()
            logEntryType = InsightsDataType.Request.ordinal
        })

        return this
    }

    fun writeTrace(
        message: EventTypeMap,
        eventName: String,
        level: TraceSeverityLevel
    ): InsightsClient {
        val jsonElement = Json.encodeToJsonElement(
            TraceEvent.serializer(),
            TraceEvent(
                name = InsightsContainer.eventBaseType,
                insightsKey = InsightsContainer.instrumentationKey,
                time = Clock.System.now().toString(),
                data = CustomTraceBaseData(
                    type = "MessageData",

                    customData = BaseEventTraceCustomData(
                        version = "1",
                        level = level.ordinal,
                        message = eventName,
                        eventProperties = message
                    )
                )
            )
        )

        storageInMemory.add(LogEntries().apply {
            jsonPayload = jsonElement.toString()
            logEntryType = InsightsDataType.Trace.ordinal
        })

        return this
    }

    fun writeException(
        ex: Exception,
        message: EventTypeMap,
    ): InsightsClient {
        InsightsContainer.interopLogger.logError(ex.message + "\n ${ex.stackTraceToString()}")

        val excItems = mutableMapOf("StackTrace" to "", "Message" to "")
        excItems["Message"] = ex.message ?: ""
        excItems["StackTrace"] = ex.stackTraceToString()
        if (message.isNotEmpty()) {
            excItems.putAll(message)
        }

        val exFormat =
            if (KmpDeviceInfo.getRunningPlatform() == DevicePlatform.iOS) ExceptionStackTraceDetailsInfo(
                level = 0,
                method = "Unknown, please open to view details",
                fileName = "",
                line = 0,
                assembly = ""
            )
            else ExceptionFormatter.getStackException(ex)

        val jsonElement = Json.encodeToJsonElement(
            ExceptionEvent.serializer(),
            ExceptionEvent(
                name = "Microsoft.ApplicationInsights.Event",
                insightsKey = InsightsContainer.instrumentationKey,
                time = Clock.System.now().toString(),
                data = BaseEventExceptionCustomData(
                    type = "ExceptionData",
                    excData = ExceptionInfo(
                        eventProperties = excItems,
                        version = 1,
                        exception = listOf(
                            ExceptionDetailsInfo(
                                uniqueId = Random.nextInt(),
                                eventName = ex.message ?: ex.stackTraceToString(),
                                type = ex::class.simpleName ?: "System.Exception",
                                hasStack = ex.stackTraceToString().isNotBlank(),
                                parsedStacks = listOf(
                                    exFormat
                                )
                            )
                        )
                    ),
                )
            )
        )

        storageInMemory.add(LogEntries().apply {
            jsonPayload = jsonElement.toString()
            logEntryType = InsightsDataType.Exception.ordinal
        })

        return this
    }
}