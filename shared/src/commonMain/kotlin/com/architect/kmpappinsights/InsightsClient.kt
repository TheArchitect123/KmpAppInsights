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
import com.architect.kmpessentials.aliases.DefaultAction
import com.architect.kmpessentials.backgrounding.BackgroundOptions
import com.architect.kmpessentials.backgrounding.KmpBackgrounding
import com.architect.kmpessentials.deviceInfo.DevicePlatform
import com.architect.kmpessentials.deviceInfo.KmpDeviceInfo
import com.architect.kmpessentials.fileSystem.KmpFileSystem
import com.architect.kmpessentials.launcher.KmpLauncher
import com.architect.kmpessentials.logging.KmpLogging
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
    private var timerUploadInSeconds: Int? = null
    private val insightsStorageName = "insights"
    private var countOfRecordsToUploadViaBatch = 10
    private var appCrashMap: EventTypeMap? = null

    private suspend fun processLogEntries() {
        try {
            val jsonFilesDirectory = KmpFileSystem.createDirectNameAtAppStorage(
                insightsStorageName
            )

            // remove any duplicate files
            val filePaths =
                KmpFileSystem.getAllFilePathsFromDirectoryPath(jsonFilesDirectory).distinct()
                    .take(countOfRecordsToUploadViaBatch)
            val jsonEntries = filePaths.mapNotNull { KmpFileSystem.readTextFromFileAt(it) }
                .joinToString("\n") { it }
            val response =
                InsightsContainer.appInsightsHttpService.postBatchJson(jsonEntries)
            if (response.errors.isEmpty()) {
                filePaths.forEach { // clear the files from storage after processing them
                    KmpFileSystem.deleteFileAt(it)
                }
            }
        } catch (ex: Exception) {

        }
    }

    fun setAppMapForCrashes(appCrashMap: EventTypeMap? = null){
        this.appCrashMap = appCrashMap
    }

    fun configureInsightsClient(
        instrumentationKey: String,
        timerUploadInSeconds: Int = 30,
        countOfRecordsToUploadViaBatch: Int = 10,
        allowBackgrounding: Boolean = false,
        allowCrashReporting: Boolean = false
    ): InsightsClient {
        if (timerUploadInSeconds < 15) {
            throw Exception("Timer for AppInsights can't be less than 15 seconds. Please try any number higher than 15 seconds (Preferably, 15 to 30)")
        }

        if (allowCrashReporting) {
            CrashReporting.registerForCrashReporting()
        }

        this.countOfRecordsToUploadViaBatch = countOfRecordsToUploadViaBatch
        InsightsContainer.instrumentationKey = instrumentationKey
        this.timerUploadInSeconds = timerUploadInSeconds

        val timerBoot = timerUploadInSeconds.toDouble()
        KmpLauncher.startTimerRepeating(timerBoot) {
            // run the broadcast
            if (allowBackgrounding) {
                KmpBackgrounding.createAndStartWorkerWithoutCancel(
                    BackgroundOptions(
                        requiresInternet = true,
                        requiresStorage = false
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

    private fun prepareJsonFileData(json: String) {
        val fileName = "random_json_file_${Random.nextInt()}_${
            Clock.System.now().toEpochMilliseconds()
        }.txt"

        val jsonFilesDirectory = KmpFileSystem.createDirectNameAtAppStorage(
            insightsStorageName
        )

        val jsonFile = KmpFileSystem.getMergedFilePathFromDirectory(jsonFilesDirectory, fileName)
        if (!jsonFile.isNullOrEmpty()) {
            KmpFileSystem.writeTextToFileAt(jsonFile, json)
        }
    }

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

        prepareJsonFileData(jsonElement.toString())

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

        prepareJsonFileData(jsonElement.toString())

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

        prepareJsonFileData(jsonElement.toString())
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

        prepareJsonFileData(jsonElement.toString())
        return this
    }

    fun writeException(
        ex: Exception,
        message: EventTypeMap,
    ): InsightsClient {
        prepareJsonFileData(getExceptionDtoJson(ex, message))

        return this
    }

   private fun getExceptionDtoJson(
        ex: Exception,
        message: EventTypeMap
    ): String {
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

        return Json.encodeToJsonElement(
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
        ).toString()
    }

    internal fun uploadAppCrashLog(crashDetails: Exception){
        KmpBackgrounding.createAndStartWorker {
            val crashLog = """
                CATASTROPHIC CRASH - \n
                Message: ${crashDetails.message}
                Cause: ${crashDetails.cause}
                Stack Trace: ${crashDetails.stackTraceToString()}
            """.trimIndent()

            // Log to the console & write to storage
            KmpLogging.writeError("CATASTROPHIC_EXCEPTION", crashLog)

            // attempt to upload logs to insights
            // if it fails after 3 attempts, then write crash to storage
            val details = appCrashMap ?: mapOf()

            try {
                val cexception = Exception(crashDetails)

                // need to set the map info, for the startup crash
                val jsonLog = getExceptionDtoJson(
                    cexception,
                    details
                )

                var attempt = 3
                while (attempt == 0) {
                    attempt--
                    val response =
                        InsightsContainer.appInsightsHttpService.postBatchJson(jsonLog)
                    if (response.errors.isEmpty()) {
                        break
                    } else {
                        if (attempt == 0) { // log the exception on storage
                            writeException(crashDetails, details)
                        }
                    }
                }
            } catch (ex: Exception) {
                writeException(ex, details)
            }
        }

    }
}