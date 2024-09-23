/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class RequestData.
 */
class RequestData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Gets the Id property.
     */
    /**
     * Sets the Id property.
     */
    /**
     * Backing field for property Id.
     */
    var id: String? = null

    /**
     * Gets the Name property.
     */
    /**
     * Sets the Name property.
     */
    /**
     * Backing field for property Name.
     */
    var name: String? = null

    /**
     * Gets the StartTime property.
     */
    /**
     * Sets the StartTime property.
     */
    /**
     * Backing field for property StartTime.
     */
    var startTime: String? = null

    /**
     * Gets the Duration property.
     */
    /**
     * Sets the Duration property.
     */
    /**
     * Backing field for property Duration.
     */
    var duration: String? = null

    /**
     * Gets the ResponseCode property.
     */
    /**
     * Sets the ResponseCode property.
     */
    /**
     * Backing field for property ResponseCode.
     */
    var responseCode: String? = null

    /**
     * Gets the Success property.
     */
    /**
     * Sets the Success property.
     */
    /**
     * Backing field for property Success.
     */
    var success: Boolean = false

    /**
     * Gets the HttpMethod property.
     */
    /**
     * Sets the HttpMethod property.
     */
    /**
     * Backing field for property HttpMethod.
     */
    var httpMethod: String? = null

    /**
     * Gets the Url property.
     */
    /**
     * Sets the Url property.
     */
    /**
     * Backing field for property Url.
     */
    var url: String? = null

    /**
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Backing field for property Measurements.
     */
    private var measurements: Map<String, Double>? = null

    /**
     * Initializes a new instance of the RequestData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.Request"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.RequestData"
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

        writer.write("$prefix\"id\":")
        writer.write(JsonHelper.convert(this.id))
        prefix = ","

        if (name != null) {
            writer.write("$prefix\"name\":")
            writer.write(JsonHelper.convert(this.name))
            prefix = ","
        }

        writer.write("$prefix\"startTime\":")
        writer.write(JsonHelper.convert(this.startTime))
        prefix = ","

        writer.write("$prefix\"duration\":")
        writer.write(JsonHelper.convert(this.duration))
        prefix = ","

        writer.write("$prefix\"responseCode\":")
        writer.write(JsonHelper.convert(this.responseCode))
        prefix = ","

        writer.write("$prefix\"success\":")
        writer.write(JsonHelper.convert(this.success))
        prefix = ","

        if (httpMethod != null) {
            writer.write("$prefix\"httpMethod\":")
            writer.write(JsonHelper.convert(this.httpMethod))
            prefix = ","
        }

        if (url != null) {
            writer.write("$prefix\"url\":")
            writer.write(JsonHelper.convert(this.url))
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
        QualifiedName = "com.architect.kmpappinsights.contracts.RequestData"
    }
}
