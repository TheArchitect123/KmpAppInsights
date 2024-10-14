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
import com.architect.kmpappinsights.services.ExceptionFormatter
import com.architect.kmpappinsights.storage.RoomStorageAccess
import com.architect.kmpappinsights.storage.models.InsightsDataType
import com.architect.kmpappinsights.storage.models.LogEntries
import com.architect.kmpessentials.backgrounding.BackgroundOptions
import com.architect.kmpessentials.backgrounding.KmpBackgrounding
import com.architect.kmpessentials.deviceInfo.DevicePlatform
import com.architect.kmpessentials.deviceInfo.KmpDeviceInfo
import com.architect.kmpessentials.launcher.KmpLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

typealias EventTypeMap = Map<String, String>

@OptIn(ExperimentalUuidApi::class)
object InsightsClient {
    private var timerUploadInSeconds: Int? = null
    private suspend fun processLogEntries() {
        try {
            RoomStorageAccess.roomDbContext.query(LogEntries::class).asFlow().collect { allLogs ->
                if (allLogs.list.isNotEmpty()) {
                    val jsonEntries = allLogs.list.joinToString("\n") { it.jsonPayload }
                    val response =
                        InsightsContainer.appInsightsHttpService.postBatchJson(jsonEntries)
                    if (response.errors.isEmpty()) {
                        // clear all the logs currently running on storage
                        RoomStorageAccess.roomDbContext.write {
                            delete(allLogs.list)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            // something went wrong during upload, will attempt again later
            InsightsLogger().logError("Failed to upload logs to AppInsights. Will try again in the next $timerUploadInSeconds seconds \n ${ex.stackTraceToString()}")
        }
    }

    fun configureInsightsClient(
        instrumentationKey: String,
        timerUploadInSeconds: Int,
        allowBackgrounding: Boolean = false,
    ): InsightsClient {
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

        return this
    }

    fun forceFlushAllLogs(): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            processLogEntries()
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
        GlobalScope.launch(Dispatchers.IO) {
            try {
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

                RoomStorageAccess.roomDbContext.write {
                    copyToRealm(LogEntries().apply {
                        jsonPayload = jsonElement.toString()
                        logEntryType = InsightsDataType.CustomEvent.ordinal
                        id = Uuid.random().toString()
                    })
                }
            } catch (ex: Exception) {
                InsightsContainer.interopLogger.logError(ex.message + "\n ${ex.stackTraceToString()}")
            }
        }

        return this
    }

    fun writePageView(
        message: EventTypeMap,
        eventName: String,
        pageName: String,
        sessionId: String = ""
    ): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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

                RoomStorageAccess.roomDbContext.write {
                    copyToRealm(LogEntries().apply {
                        jsonPayload = jsonElement.toString()
                        logEntryType = InsightsDataType.PageView.ordinal
                        id = Uuid.random().toString()
                    })
                }
            } catch (ex: Exception) {
                InsightsContainer.interopLogger.logError(ex.message + "\n ${ex.stackTraceToString()}")
            }
        }

        return this
    }

    fun writeRequest(
        message: EventTypeMap,
        eventName: String,
        requestInfo: RequestStorageData
    ): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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

                RoomStorageAccess.roomDbContext.write {
                    copyToRealm(LogEntries().apply {
                        jsonPayload = jsonElement.toString()
                        logEntryType = InsightsDataType.Request.ordinal
                        id = Uuid.random().toString()
                    })
                }
            } catch (ex: Exception) {
                InsightsContainer.interopLogger.logError(ex.message + "\n ${ex.stackTraceToString()}")
            }
        }
        return this
    }

    fun writeTrace(
        message: EventTypeMap,
        eventName: String,
        level: TraceSeverityLevel
    ): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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

                RoomStorageAccess.roomDbContext.write {
                    copyToRealm(LogEntries().apply {
                        jsonPayload = jsonElement.toString()
                        logEntryType = InsightsDataType.Trace.ordinal
                        id = Uuid.random().toString()
                    })
                }
            } catch (ex: Exception) {
                InsightsContainer.interopLogger.logError(ex.message + "\n ${ex.stackTraceToString()}")
            }
        }

        return this
    }

    fun writeException(ex: Exception, message: EventTypeMap): InsightsClient {
        GlobalScope.launch(Dispatchers.IO) {
            try {
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

                RoomStorageAccess.roomDbContext.write {
                    copyToRealm(LogEntries().apply {
                        jsonPayload = jsonElement.toString()
                        logEntryType = InsightsDataType.Exception.ordinal
                        id = Uuid.random().toString()
                    })
                }
            } catch (ex: Exception) {
                InsightsContainer.interopLogger.logError(ex.message + "\n ${ex.stackTraceToString()}")
            }
        }

        return this
    }
}