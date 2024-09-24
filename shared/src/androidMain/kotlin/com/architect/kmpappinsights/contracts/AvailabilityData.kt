/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.microsoft.telemetry.Domain
import com.microsoft.telemetry.ITelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class AvailabilityData.
 */
class AvailabilityData : Domain(), ITelemetryData {
    /**
     * Gets the Ver property.
     */
    /**
     * Sets the Ver property.
     */
    /**
     * Backing field for property Ver.
     */
    var ver: Int = 2

    /**
     * Gets the TestRunId property.
     */
    /**
     * Sets the TestRunId property.
     */
    /**
     * Backing field for property TestRunId.
     */
    var testRunId: String? = null

    /**
     * Gets the TestTimeStamp property.
     */
    /**
     * Sets the TestTimeStamp property.
     */
    /**
     * Backing field for property TestTimeStamp.
     */
    var testTimeStamp: String? = null

    /**
     * Gets the TestName property.
     */
    /**
     * Sets the TestName property.
     */
    /**
     * Backing field for property TestName.
     */
    var testName: String? = null

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
     * Backing field for property Result.
     */
    private var result: TestResult = TestResult.PASS

    /**
     * Gets the RunLocation property.
     */
    /**
     * Sets the RunLocation property.
     */
    /**
     * Backing field for property RunLocation.
     */
    var runLocation: String? = null

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
     * Gets the DataSize property.
     */
    /**
     * Sets the DataSize property.
     */
    /**
     * Backing field for property DataSize.
     */
    var dataSize: Double = 0.0

    /**
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Backing field for property Measurements.
     */
    private var measurements: Map<String, Double>? = null

    /**
     * Initializes a new instance of the AvailabilityData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Gets the Result property.
     */
    fun getResult(): com.architect.kmpappinsights.contracts.TestResult {
        return this.result
    }

    /**
     * Sets the Result property.
     */
    fun setResult(value: com.architect.kmpappinsights.contracts.TestResult) {
        this.result = value
    }

    /**
     * Gets the Properties property.
     */
    fun getProperties(): Map<String, String>? {
        if (this.properties == null) {
            this.properties = LinkedHashMap()
        }
        return this.properties
    }

    /**
     * Sets the Properties property.
     */
    fun setProperties(value: Map<String, String>?) {
        this.properties = value
    }

    /**
     * Serializes the beginning of this object to the passed in writer.
     *
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    override fun serializeContent(writer: Writer): String {
        var prefix = super.serializeContent(writer)
        writer.write("$prefix\"ver\":")
        writer.write(JsonHelper.convert(this.ver))
        prefix = ","

        writer.write("$prefix\"testRunId\":")
        writer.write(JsonHelper.convert(this.testRunId))
        prefix = ","

        writer.write("$prefix\"testTimeStamp\":")
        writer.write(JsonHelper.convert(this.testTimeStamp))
        prefix = ","

        writer.write("$prefix\"testName\":")
        writer.write(JsonHelper.convert(this.testName))
        prefix = ","

        writer.write("$prefix\"duration\":")
        writer.write(JsonHelper.convert(this.duration))
        prefix = ","

        writer.write("$prefix\"result\":")
        writer.write(JsonHelper.convert(result.value))
        prefix = ","

        if (runLocation != null) {
            writer.write("$prefix\"runLocation\":")
            writer.write(JsonHelper.convert(this.runLocation))
            prefix = ","
        }

        if (message != null) {
            writer.write("$prefix\"message\":")
            writer.write(JsonHelper.convert(this.message))
            prefix = ","
        }

        if (this.dataSize > 0.0) {
            writer.write("$prefix\"dataSize\":")
            writer.write(JsonHelper.convert(this.dataSize))
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
    override fun InitializeFields() {
        QualifiedName = "AI.AvailabilityData"
    }
}
