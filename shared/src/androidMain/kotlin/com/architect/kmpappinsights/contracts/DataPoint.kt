/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.microsoft.telemetry.IJsonSerializable
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Serializable
import java.io.Writer

/**
 * Data contract class DataPoint.
 */
class DataPoint

    : IJsonSerializable, Serializable {
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
     * Backing field for property Kind.
     */
    private var kind: DataPointType = DataPointType.MEASUREMENT

    /**
     * Gets the Value property.
     */
    /**
     * Sets the Value property.
     */
    /**
     * Backing field for property Value.
     */
    var value: Double = 0.0

    /**
     * Gets the Count property.
     */
    /**
     * Sets the Count property.
     */
    /**
     * Backing field for property Count.
     */
    var count: Int? = null

    /**
     * Gets the Min property.
     */
    /**
     * Sets the Min property.
     */
    /**
     * Backing field for property Min.
     */
    var min: Double? = null

    /**
     * Gets the Max property.
     */
    /**
     * Sets the Max property.
     */
    /**
     * Backing field for property Max.
     */
    var max: Double? = null

    /**
     * Gets the StdDev property.
     */
    /**
     * Sets the StdDev property.
     */
    /**
     * Backing field for property StdDev.
     */
    var stdDev: Double? = null

    /**
     * Initializes a new instance of the DataPoint class.
     */
    init {
        this.InitializeFields()
    }

    /**
     * Gets the Kind property.
     */
    fun getKind(): DataPointType {
        return this.kind
    }

    /**
     * Sets the Kind property.
     */
    fun setKind(value: DataPointType) {
        this.kind = value
    }


    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    override fun serialize(writer: Writer) {
        requireNotNull(writer) { "writer" }

        writer.write('{'.code)
        this.serializeContent(writer)
        writer.write('}'.code)
    }

    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected fun serializeContent(writer: Writer): String {
        var prefix = ""
        writer.write("$prefix\"name\":")
        writer.write(JsonHelper.convert(this.name))
        prefix = ","

        if (kind !== DataPointType.MEASUREMENT) {
            writer.write("$prefix\"kind\":")
            writer.write(JsonHelper.convert(kind.value))
            prefix = ","
        }

        writer.write("$prefix\"value\":")
        writer.write(JsonHelper.convert(this.value))
        prefix = ","

        if (count != null) {
            writer.write("$prefix\"count\":")
            writer.write(JsonHelper.convert(this.count))
            prefix = ","
        }

        if (min != null) {
            writer.write("$prefix\"min\":")
            writer.write(JsonHelper.convert(this.min))
            prefix = ","
        }

        if (max != null) {
            writer.write("$prefix\"max\":")
            writer.write(JsonHelper.convert(this.max))
            prefix = ","
        }

        if (stdDev != null) {
            writer.write("$prefix\"stdDev\":")
            writer.write(JsonHelper.convert(this.stdDev))
            prefix = ","
        }

        return prefix
    }

    /**
     * Optionally initializes fields for the current context.
     */
    protected fun InitializeFields() {
    }
}
