/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class EventData.
 */
open class EventData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

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
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Backing field for property Measurements.
     */
    private var measurements: Map<String, Double>? = null

    /**
     * Initializes a new instance of the EventData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.Event"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.EventData"
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
     *
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected override fun serializeContent(writer: Writer): String {
        var prefix: String = super.serializeContent(writer)
        writer.write("$prefix\"ver\":")
        writer.write(JsonHelper.convert(this.ver))
        prefix = ","

        writer.write("$prefix\"name\":")
        writer.write(JsonHelper.convert(this.name))
        prefix = ","

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
    open fun SetupAttributes() {
    }

    /**
     * Optionally initializes fields for the current context.
     */
    protected override fun InitializeFields() {
        QualifiedName = "com.architect.kmpappinsights.contracts.EventData"
    }
}
