/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.SeverityLevel
import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class MessageData.
 */
class MessageData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Gets the Message property.
     */
    /**
     * Sets the Message property.
     */
    /**
     * Backing field for property Message.
     */
    var message: String? = null

    /**
     * Backing field for property SeverityLevel.
     */
    private var severityLevel: SeverityLevel = SeverityLevel.VERBOSE

    /**
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Initializes a new instance of the MessageData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.Message"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.MessageData"
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

        writer.write("$prefix\"message\":")
        writer.write(JsonHelper.convert(this.message))
        prefix = ","

        if (severityLevel != SeverityLevel.VERBOSE) {
            writer.write("$prefix\"severityLevel\":")
            writer.write(JsonHelper.convert(severityLevel.value))
            prefix = ","
        }

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
        QualifiedName = "com.architect.kmpappinsights.contracts.MessageData"
    }
}
