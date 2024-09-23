/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class MetricData.
 */
class MetricData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Backing field for property Metrics.
     */
    private var metrics: List<com.architect.kmpappinsights.contracts.DataPoint>? = null

    /**
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Initializes a new instance of the MetricData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.Metric"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.MetricData"
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
     * Gets the Metrics property.
     */
    fun getMetrics(): List<com.architect.kmpappinsights.contracts.DataPoint>? {
        if (this.metrics == null) {
            this.metrics = ArrayList<com.architect.kmpappinsights.contracts.DataPoint>()
        }
        return this.metrics
    }

    /**
     * Sets the Metrics property.
     */
    fun setMetrics(value: List<com.architect.kmpappinsights.contracts.DataPoint>?) {
        this.metrics = value
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

        writer.write("$prefix\"metrics\":")
        JsonHelper.writeList<com.architect.kmpappinsights.contracts.DataPoint>(
            writer,
            this.metrics
        )
        prefix = ","

        if (properties != null) {
            writer.write("$prefix\"properties\":")
            JsonHelper.writeDictionary(writer, this.properties)
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
        QualifiedName = "com.architect.kmpappinsights.contracts.MetricData"
    }
}
