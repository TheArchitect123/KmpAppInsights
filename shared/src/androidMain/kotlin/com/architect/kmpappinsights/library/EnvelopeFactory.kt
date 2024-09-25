package com.architect.kmpappinsights.library

import com.architect.kmpappinsights.contracts.CrashData
import com.architect.kmpappinsights.contracts.CrashDataHeaders
import com.architect.kmpappinsights.contracts.CrashDataThread
import com.architect.kmpappinsights.contracts.CrashDataThreadFrame
import com.architect.kmpappinsights.contracts.DataPoint
import com.architect.kmpappinsights.contracts.DataPointType
import com.architect.kmpappinsights.contracts.EventData
import com.architect.kmpappinsights.contracts.ExceptionData
import com.architect.kmpappinsights.contracts.ExceptionDetails
import com.architect.kmpappinsights.contracts.MessageData
import com.architect.kmpappinsights.contracts.MetricData
import com.architect.kmpappinsights.contracts.PageViewData
import com.architect.kmpappinsights.contracts.SessionState
import com.architect.kmpappinsights.contracts.SessionStateData
import com.architect.kmpappinsights.contracts.StackFrame
import com.architect.kmpappinsights.contracts.TelemetryData
import com.architect.kmpappinsights.logging.InternalLogging
import com.microsoft.telemetry.Data
import com.microsoft.telemetry.Domain
import com.microsoft.telemetry.cs2.Envelope
import java.util.Date
import java.util.UUID
import java.util.regex.Pattern
import kotlin.concurrent.Volatile

internal class EnvelopeFactory protected constructor(
    telemetryContext: TelemetryContext?,
    commonProperties: Map<String, String>?
) {
    /**
     * Flag which determines, if the EnvelopeManager has been configured, yet
     */
    private val configured: Boolean

    /**
     * The context for this recorder
     */
    private val context: TelemetryContext? = telemetryContext

    /**
     * Map of properties, which should be set for each envelope
     */
    private var commonProperties: Map<String, String>?

    /**
     * Create an instance of EnvelopeFactory
     *
     * @param telemetryContext the telemetry context
     * @param commonProperties a map of common properties which should be set for all envelopes
     */
    init {
        this.commonProperties = commonProperties
        this.configured = true
    }

    /**
     * Create an envelope template
     *
     * @return the envelope used for telemetry
     */
    protected fun createEnvelope(): Envelope {
        val envelope = Envelope()
        context!!.updateScreenResolution(ApplicationInsights.INSTANCE.context)
        envelope.appId = context.packageName
        envelope.appVer = context.appVersion
        envelope.time = Util.dateToISO8601(Date())
        envelope.iKey = context.instrumentationKey
        envelope.userId = context.userId
        envelope.deviceId = context.deviceId
        envelope.osVer = context.osVersion
        envelope.os = context.osName

        val tags: Map<String?, String?> = context.contextTags
        if (tags != null) {
            envelope.tags = tags
        }
        return envelope
    }

    /**
     * Create an envelope with the given object as its base data
     *
     * @param data The telemetry we want to wrap inside an Enevelope and send to the server
     * @return the envelope that includes the telemetry data
     */
    fun createEnvelope(data: Data<Domain?>): Envelope {
        val envelope = createEnvelope()
        envelope.data = data
        val baseData = data.baseData
        if (baseData is TelemetryData) {
            val envelopeName = baseData.envelopeName
            envelope.name = envelopeName
        }

        // todo: read sample rate from settings store and set sampleRate(percentThrottled)
        // todo: set flags from settings store and set flags(persistence, latency)
        //envelope.setSeq(this.channelId + ":" + this.seqCounter.incrementAndGet());
        return envelope
    }

    /**
     * Create an envelope with the given object as its base data
     *
     * @param telemetryData The telemetry we want to wrap inside an Enevelope and send to the server
     * @return the envelope that includes the telemetry data
     */
    fun createData(telemetryData: TelemetryData?): Data<Domain?> {
        addCommonProperties(telemetryData)

        val data = Data<Domain?>()
        data.baseData = telemetryData
        data.baseType = telemetryData!!.baseType
        data.QualifiedName = telemetryData.envelopeName

        return data
    }

    /**
     * Creates information about an event for Application Insights. This method gets called by a
     * CreateTelemetryDataTask in order to create and forward data on a background thread.
     *
     * @param eventName    The name of the event
     * @param properties   Custom properties associated with the event
     * @param measurements Custom measurements associated with the event
     * @return an Envelope object, which contains an event
     */
    fun createEventData(
        eventName: String?,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = EventData()
            telemetry.name = ensureNotNull(eventName)

            if (properties != null) {
                telemetry.properties = properties
            }
            if (measurements != null) {
                telemetry.setMeasurements(measurements)
            }

            data = createData(telemetry)
        }
        return data
    }

    /**
     * Creates tracing information for Application Insights. This method gets called by a
     * CreateTelemetryDataTask in order to create and forward data on a background thread.
     *
     * @param message    The message associated with this trace
     * @param properties Custom properties associated with the event
     * @return an Envelope object, which contains a trace
     */
    fun createTraceData(message: String?, properties: Map<String, String>?): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = MessageData()
            telemetry.message = this.ensureNotNull(message)
            if (properties != null) {
                telemetry.properties = properties
            }

            data = createData(telemetry)
        }
        return data
    }

    /**
     * Creates information about an aggregated metric for Application Insights. This method gets
     * called by a CreateTelemetryDataTask in order to create and forward data on a background thread.
     *
     * @param name          The name of the metric
     * @param value         The value of the metric
     * @param properties    Custom properties associated with the event
     * @return an Envelope object, which contains a metric
     */
    fun createMetricData(
        name: String?,
        value: Double,
        properties: Map<String, String>?
    ): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = MetricData()
            val dataPoint = DataPoint()
            dataPoint.count = 1
            dataPoint.setKind(DataPointType.MEASUREMENT)
            dataPoint.max = value
            dataPoint.max = value
            dataPoint.name = ensureNotNull(name)
            dataPoint.value = value
            val metricsList: MutableList<DataPoint> = ArrayList()
            metricsList.add(dataPoint)
            telemetry.setMetrics(metricsList)
            if (properties != null) {
                telemetry.properties = properties
            }

            data = createData(telemetry)
        }
        return data
    }

    /**
     * Creates information about an handled or unhandled exception to Application Insights. This
     * method gets called by a CreateTelemetryDataTask in order to create and forward data on a
     * background thread.
     *
     * @param exception     The exception to track
     * @param properties    Custom properties associated with the event
     * @param measurements  Custom measurements associated with the event
     * @return an Envelope object, which contains a handled or unhandled exception
     */
    fun createExceptionData(
        exception: Throwable?,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = this.getCrashData(exception, properties, measurements)
            data = createData(telemetry)
        }
        return data
    }

    /**
     * Creates information about an handled or unhandled exception to Application Insights.
     *
     * @param type       the exception type
     * @param message    the exception message
     * @param stacktrace the stacktrace for the exception
     * @return an Envelope object, which contains a handled or unhandled exception
     */
    fun createExceptionData(
        type: String?,
        message: String?,
        stacktrace: String?,
        handled: Boolean
    ): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = this.getExceptionData(type, message, stacktrace, handled)

            data = createData(telemetry)
        }
        return data
    }

    /**
     * Creates information about a page view for Application Insights. This method gets called by a
     * CreateTelemetryDataTask in order to create and forward data on a background thread.
     *
     * @param pageName     The name of the page
     * @param properties   Custom properties associated with the event
     * @param measurements Custom measurements associated with the event
     * @return an Envelope object, which contains a page view
     */
    fun createPageViewData(
        pageName: String?,
        duration: Long,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = PageViewData()
            if (duration > 0) {
                telemetry.duration = duration.toString()
            }
            telemetry.name = ensureNotNull(pageName)
            telemetry.url = null

            if (properties != null) {
                telemetry.properties = properties
            }
            if (measurements != null) {
                telemetry.setMeasurements(measurements)
            }

            data = createData(telemetry)
        }
        return data
    }

    /**
     * Creates information about a new session view for Application Insights. This method gets
     * called by a CreateTelemetryDataTask in order to create and forward data on a background thread.
     *
     * @return an Envelope object, which contains a session
     */
    fun createNewSessionData(): Data<Domain?>? {
        var data: Data<Domain?>? = null
        if (isConfigured()) {
            val telemetry = SessionStateData()
            telemetry.setState(SessionState.START)
            data = createData(telemetry)
        }
        return data
    }

    /**
     * Adds common properties to the given telemetry data.
     *
     * @param telemetry The telemetry data
     */
    protected fun addCommonProperties(telemetry: TelemetryData?) {
        telemetry!!.setVer(CONTRACT_VERSION)
        if (this.commonProperties != null) {
            val map = telemetry.properties
            if (map != null) {
                map.putAll(commonProperties!!)
                telemetry.properties = map
            }
        }
    }

    /**
     * Ensures required string values are non-null
     */
    private fun ensureNotNull(input: String?): String {
        return input ?: ""
    }

    /**
     * Set properties, which should be set for each envelope.
     *
     * @param commonProperties a map with properties, which should be set for each envelope
     */
    protected fun setCommonProperties(commonProperties: Map<String, String>?) {
        this.commonProperties = commonProperties
    }

    /**
     * Parse an exception and it's stack trace and create the CrashData object
     *
     * @param exception     The throwable object we want to create a crashdata from
     * @param properties    Properties used foor the CrashData
     * @param measurements  Key value par for custom metrics
     * @return a CrashData object that contains the stacktrace and context info
     */
    private fun getCrashData(
        exception: Throwable?,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ): CrashData {
        var localException = exception
        if (localException == null) {
            localException = Exception()
        }

        // TODO: set handledAt - Is of relevance in future releases, not at the moment
        // read stack frames
        val stackFrames: MutableList<CrashDataThreadFrame> = ArrayList()
        val stack = localException.stackTrace
        for (i in 0 until stack.size - 1) {
            val rawFrame = stack[i]
            val frame = CrashDataThreadFrame()
            frame.symbol = rawFrame.toString()
            stackFrames.add(frame)
            frame.address = ""
        }

        val crashDataThread = CrashDataThread()
        crashDataThread.setFrames(stackFrames)
        val threads: MutableList<CrashDataThread> = ArrayList(1)
        threads.add(crashDataThread)

        val crashDataHeaders = CrashDataHeaders()
        crashDataHeaders.id = UUID.randomUUID().toString()

        val message = localException.message
        crashDataHeaders.exceptionReason = ensureNotNull(message)
        crashDataHeaders.exceptionType = localException.javaClass.name
        crashDataHeaders.applicationIdentifier = context!!.packageName

        val crashData = CrashData()
        crashData.setThreads(threads)
        crashData.setHeaders(crashDataHeaders)

        // TODO: Add properties and measurements (not supported for CrashData in backend so far)
        return crashData
    }

    /**
     * Create the ExceptionData object.
     *
     * @param type       The name of the exception type
     * @param message    The exception message
     * @param stacktrace The stacktrace for the exception
     * @return a ExceptionData object that contains the stacktrace and context info
     */
    protected fun getExceptionData(
        type: String?,
        message: String?,
        stacktrace: String?,
        handled: Boolean
    ): ExceptionData {
        val exceptions = ArrayList<ExceptionDetails>()

        if (stacktrace != null) {
            // Split raw stacktrace in case it contains managed and unmanaged exception info

            val subStackTraces =
                stacktrace.split("\\n\\s*--- End of managed exception stack trace ---\\s*\\n".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            for (i in subStackTraces.indices) {
                val details = ExceptionDetails()

                // Exception info
                var exceptionSource: String
                val managed = (i == 0)

                if (managed) {
                    exceptionSource = "Managed exception: "
                    details.id = 1
                } else {
                    exceptionSource = "Unmanaged exception: "
                    details.outerId = 1
                }

                details.message = exceptionSource + message
                details.typeName = type
                details.stack = subStackTraces[i]

                // Parse stacktrace
                val stackFrames = getStackframes(subStackTraces[i], managed)
                if (stackFrames!!.size > 0) {
                    details.setParsedStack(stackFrames)
                    details.hasFullStack = true
                }
                exceptions.add(details)
            }
        }

        val data = ExceptionData()
        data.handledAt = if (handled) "HANDLED" else "UNHANDLED"
        data.setExceptions(exceptions)

        return data
    }

    protected fun getStackframes(stacktrace: String?, managed: Boolean): List<StackFrame>? {
        var frameList: MutableList<StackFrame>? = null

        if (stacktrace != null) {
            frameList = ArrayList()
            val lines =
                stacktrace.split("\\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (frameInfo in lines) {
                val frame = getStackframe(frameInfo, managed)
                if (frame != null) {
                    frameList.add(frame)
                }
            }

            var level = frameList.size - 1
            for (frame in frameList) {
                frame.level = level
                level--
            }
        }
        return frameList
    }

    protected fun getStackframe(line: String?, managed: Boolean): StackFrame? {
        var frame: StackFrame? = null
        if (line != null) {
            val methodPattern =
                if (managed) Pattern.compile("^\\s*at\\s*(.*\\(.*\\)).*") else Pattern.compile("^[\\s\\t]*at\\s*(.*)\\(.*")
            val methodMatcher = methodPattern.matcher(line)

            if (methodMatcher.find() && methodMatcher.groupCount() > 0) {
                frame = StackFrame()
                frame.method = methodMatcher.group(1)

                val filePattern =
                    if ((managed)) Pattern.compile("in\\s(.*):([0-9]+)\\s*") else Pattern.compile(".*\\((.*):([0-9]+)\\)\\s*")
                val fileMatcher = filePattern.matcher(line)

                if (fileMatcher.find() && fileMatcher.groupCount() > 1) {
                    frame.fileName = fileMatcher.group(1)
                    val lineNumber = parseInt(fileMatcher.group(2))
                    frame.line = lineNumber
                }
            }
        }
        return frame
    }

    protected fun parseInt(text: String): Int {
        var number = 0

        try {
            number = text.toInt()
        } catch (nfe: NumberFormatException) {
            InternalLogging.warn(TAG, "Couldn't parse the line number for crash report")
        }

        return number
    }

    protected fun isConfigured(): Boolean {
        if (!configured) {
            InternalLogging.warn(
                TAG,
                "Could not create telemetry data. You have to setup & start ApplicationInsights first."
            )
        }
        return configured
    }

    /**
     * Get Context
     *
     * @return The telemetry context associated with this envelope factory
     */
    fun getContext(): TelemetryContext? {
        return this.context
    }

    companion object {
        /**
         * The schema version
         */
        protected const val CONTRACT_VERSION: Int = 2

        /**
         * The tag for logging
         */
        private const val TAG = "EnvelopeManager"

        /**
         * Volatile boolean for double checked synchronize block
         */
        @Volatile
        private var isLoaded = false

        /**
         * Synchronization LOCK for setting static context
         */
        private val LOCK = Any()

        /**
         * The singleton INSTANCE of this class
         */
        private var instance: EnvelopeFactory? = null

        /**
         * @return the INSTANCE of EnvelopeFactory or null if not yet initialized
         */
        fun getInstance(): EnvelopeFactory? {
            if (instance == null) {
                InternalLogging.error(TAG, "getSharedInstance was called before initialization")
            }

            return instance
        }

        /**
         * Configures the shared instance with a telemetry context, which is needed to create envelops.
         * Warning: Method should be called before creating envelops.
         *
         * @param context          the telemetry context, which is used to create envelops with proper context information.
         * @param commonProperties Map of properties
         */
        fun initialize(context: TelemetryContext?, commonProperties: Map<String, String>?) {
            // note: isPersistenceLoaded must be volatile for the double-checked LOCK to work
            if (!isLoaded) {
                synchronized(LOCK) {
                    if (!isLoaded) {
                        isLoaded = true
                        instance = EnvelopeFactory(context, commonProperties)
                    }
                }
            }
        }
    }
}
