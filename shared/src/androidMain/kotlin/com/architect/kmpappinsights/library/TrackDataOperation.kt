package com.architect.kmpappinsights.library

import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.Data
import com.microsoft.telemetry.Domain
import com.microsoft.telemetry.IChannel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

internal class TrackDataOperation : Runnable {
    // common
    private val type: DataType
    private var name: String? = null
    private var properties: Map<String, String>? = null
    private var measurements: Map<String, Double>? = null

    // managed exceptions
    private var exceptionMessage: String? = null
    private var exceptionStacktrace: String? = null
    private var handled = false

    // metric
    private var metric = 0.0

    // unmanaged exceptions
    private var exception: Throwable? = null

    // page views
    private var duration: Long = 0

    // custom
    private var telemetry: TelemetryData? = null

    constructor(telemetry: TelemetryData?) {
        this.type = DataType.NONE
        try {
            this.telemetry = deepCopy(telemetry) as TelemetryData
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    constructor(type: DataType, name: String?) {
        this.type = type
        try {
            this.name = deepCopy(name) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    constructor(type: DataType) {
        this.type = type // no need to copy as enum is pass by value
    }

    constructor(
        type: DataType,
        metricName: String?,
        metric: Double,
        properties: Map<String, String>?
    ) : this(type, metricName, properties, null) {
        this.metric = metric // no need to copy as enum is pass by value
    }

    constructor(
        type: DataType,
        name: String?,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ) {
        this.type = type // no need to copy as enum is pass by value
        try {
            this.name = deepCopy(name) as String
            if (properties != null) {
                this.properties = HashMap(properties)
            }
            if (measurements != null) {
                this.measurements = HashMap(measurements)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected constructor(
        type: DataType,
        name: String?,
        duration: Long,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ) : this(type, name, properties, measurements) {
        this.duration = duration
    }

    protected constructor(
        type: DataType,
        exception: Throwable?,
        properties: Map<String, String>?,
        measurements: Map<String, Double>?
    ) : this(type, "", properties, measurements) {
        try {
            this.exception = deepCopy(exception) as Throwable
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    protected constructor(
        type: DataType,
        name: String?,
        message: String?,
        stacktrace: String?,
        handled: Boolean
    ) {
        this.type = type // no need to copy as enum is pass by value
        try {
            this.name = deepCopy(name) as String
            this.exceptionMessage = deepCopy(message) as String
            this.exceptionStacktrace = deepCopy(stacktrace) as String
            this.handled = handled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun run() {
        val highPrioItem = (type == DataType.MANAGED_EXCEPTION && !handled)
        if (!Persistence.getInstance()!!.isFreeSpaceAvailable(highPrioItem)) {
            return
        }

        val telemetry = getTelemetry()
        if (telemetry != null) {
            val channel = ChannelManager.getInstance()!!.getChannel()
            if (highPrioItem) {
                (com.architect.kmpappinsights.library.Channel.getInstance() as com.architect.kmpappinsights.library.Channel)!!.processException(
                    telemetry
                )
            } else {
                telemetry.baseData!!.QualifiedName = telemetry.baseType
                val tags: MutableMap<String?, String?> = EnvelopeFactory.getInstance()!!
                    .getContext()!!.contextTags
                if (this.type == DataType.NEW_SESSION) {
                    //updating IsNew tag from session context doesn't work because editing shared prefs
                    //doesn't happen timely enough so we can be sure isNew is true for all cases
                    //so we set it to true explicitly
                    tags["ai.session.isNew"] = "true"
                }
                channel?.log(telemetry, tags)
            }
        }
    }

    private fun getTelemetry(): Data<Domain?>? {
        var telemetry: Data<Domain?>? = null
        if ((this.type == DataType.MANAGED_EXCEPTION)) {
            telemetry = EnvelopeFactory.getInstance()!!.createExceptionData(
                this.name,
                this.exceptionMessage,
                this.exceptionStacktrace,
                this.handled
            )
        } else {
            when (this.type) {
                DataType.NONE -> if (this.telemetry != null) {
                    telemetry = EnvelopeFactory.getInstance()!!.createData(this.telemetry)
                }

                DataType.EVENT -> telemetry = EnvelopeFactory.getInstance()!!
                    .createEventData(this.name, this.properties, this.measurements)

                DataType.PAGE_VIEW -> telemetry = EnvelopeFactory.getInstance()!!
                    .createPageViewData(
                        this.name,
                        this.duration,
                        this.properties,
                        this.measurements
                    )

                DataType.TRACE -> telemetry = EnvelopeFactory.getInstance()!!
                    .createTraceData(this.name, this.properties)

                DataType.METRIC -> telemetry = EnvelopeFactory.getInstance()!!
                    .createMetricData(this.name, this.metric, this.properties)

                DataType.NEW_SESSION -> telemetry = EnvelopeFactory.getInstance()!!
                    .createNewSessionData()

                DataType.HANDLED_EXCEPTION -> telemetry = EnvelopeFactory.getInstance()!!
                    .createExceptionData(this.exception, this.properties, this.measurements)

                else -> {}
            }
        }
        return telemetry
    }

    @Throws(Exception::class)
    private fun deepCopy(serializableObject: Any?): Any {
        val outputStream = ByteArrayOutputStream()
        ObjectOutputStream(outputStream).writeObject(serializableObject)
        val inputStream = ByteArrayInputStream(outputStream.toByteArray())

        return ObjectInputStream(inputStream).readObject()
    }

    enum class DataType {
        NONE,
        EVENT,
        TRACE,
        METRIC,
        PAGE_VIEW,
        HANDLED_EXCEPTION,
        MANAGED_EXCEPTION,
        NEW_SESSION
    }
}
