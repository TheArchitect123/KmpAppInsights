/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.ExceptionDetails
import com.architect.kmpappinsights.contracts.SeverityLevel
import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class ExceptionData.
 */
class ExceptionData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Gets the HandledAt property.
     */
    /**
     * Sets the HandledAt property.
     */
    /**
     * Backing field for property HandledAt.
     */
    var handledAt: String? = null

    /**
     * Backing field for property Exceptions.
     */
    private var exceptions: List<ExceptionDetails>? = null

    /**
     * Backing field for property SeverityLevel.
     */
    private var severityLevel: SeverityLevel = SeverityLevel.VERBOSE

    /**
     * Gets the ProblemId property.
     */
    /**
     * Sets the ProblemId property.
     */
    /**
     * Backing field for property ProblemId.
     */
    var problemId: String? = null

    /**
     * Gets the CrashThreadId property.
     */
    /**
     * Sets the CrashThreadId property.
     */
    /**
     * Backing field for property CrashThreadId.
     */
    var crashThreadId: Int = 0

    /**
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Backing field for property Measurements.
     */
    private var measurements: Map<String, Double>? = null

    /**
     * Initializes a new instance of the ExceptionData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.Exception"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.ExceptionData"
    }

    /**
     * Gets the Ver property.
     */
    fun getVer(): Int {
        return this.ver
    }

    /**
     * Sets the Ver property.
     */
    override fun setVer(value: Int) {
        this.ver = value
    }

    /**
     * Gets the Exceptions property.
     */
    fun getExceptions(): List<ExceptionDetails>? {
        if (this.exceptions == null) {
            this.exceptions = ArrayList<ExceptionDetails>()
        }
        return this.exceptions
    }

    /**
     * Sets the Exceptions property.
     */
    fun setExceptions(value: List<ExceptionDetails>?) {
        this.exceptions = value
    }

    /**
     * Gets the SeverityLevel property.
     */
    fun getSeverityLevel(): SeverityLevel {
        return this.severityLevel
    }

    /**
     * Sets the SeverityLevel property.
     */
    fun setSeverityLevel(value: SeverityLevel) {
        this.severityLevel = value
    }

    /**
     * Gets the Properties property.
     */
    override fun getProperties(): Map<String, String> {
        if (this.properties == null) {
            this.properties = LinkedHashMap()
        }
        return properties!!
    }

    /**
     * Sets the Properties property.
     */
    override fun setProperties(value: Map<String, String>) {
        this.properties = value
    }

    /**
     * Gets the Measurements property.
     */
    fun getMeasurements(): Map<String, Double>? {
        if (this.measurements == null) {
            this.measurements = LinkedHashMap()
        }
        return this.measurements
    }

    /**
     * Sets the Measurements property.
     */
    fun setMeasurements(value: Map<String, Double>?) {
        this.measurements = value
    }


    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected override fun serializeContent(writer: Writer): String {
        var prefix: String = super.serializeContent(writer)
        writer.write("$prefix\"ver\":")
        writer.write(JsonHelper.convert(this.ver))
        prefix = ","

        writer.write("$prefix\"handledAt\":")
        writer.write(JsonHelper.convert(this.handledAt))
        prefix = ","

        writer.write("$prefix\"exceptions\":")
        JsonHelper.writeList<ExceptionDetails>(writer, this.exceptions)
        prefix = ","

        if (severityLevel != SeverityLevel.VERBOSE) {
            writer.write("$prefix\"severityLevel\":")
            writer.write(JsonHelper.convert(severityLevel.getValue()))
            prefix = ","
        }

        if (problemId != null) {
            writer.write("$prefix\"problemId\":")
            writer.write(JsonHelper.convert(this.problemId))
            prefix = ","
        }

        if (crashThreadId != 0) {
            writer.write("$prefix\"crashThreadId\":")
            writer.write(JsonHelper.convert(this.crashThreadId))
            prefix = ","
        }

        if (properties != null) {
            writer.write("$prefix\"properties\":")
            JsonHelper.writeDictionary(writer, this.properties)
            prefix = ","
        }

        if (measurements != null) {
            writer.write("$prefix\"measurements\":")
            JsonHelper.writeDictionary(writer, this.measurements)
            prefix = ","
        }

        return prefix
    }

    /**
     * Sets up the events attributes
     */
    fun SetupAttributes() {
    }

    /**
     * Optionally initializes fields for the current context.
     */
    protected override fun InitializeFields() {
        QualifiedName = "com.architect.kmpappinsights.contracts.ExceptionData"
    }
}
