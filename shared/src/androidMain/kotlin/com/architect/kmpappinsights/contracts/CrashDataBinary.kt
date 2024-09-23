/*
 * Generated from CrashDataBinary.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.microsoft.telemetry.IJsonSerializable
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Serializable
import java.io.Writer

/**
 * Data contract class CrashDataBinary.
 */
class CrashDataBinary

    : IJsonSerializable, Serializable {
    /**
     * Gets the StartAddress property.
     */
    /**
     * Sets the StartAddress property.
     */
    /**
     * Backing field for property StartAddress.
     */
    var startAddress: String? = null

    /**
     * Gets the EndAddress property.
     */
    /**
     * Sets the EndAddress property.
     */
    /**
     * Backing field for property EndAddress.
     */
    var endAddress: String? = null

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
     * Gets the CpuType property.
     */
    /**
     * Sets the CpuType property.
     */
    /**
     * Backing field for property CpuType.
     */
    var cpuType: Long = 0

    /**
     * Gets the CpuSubType property.
     */
    /**
     * Sets the CpuSubType property.
     */
    /**
     * Backing field for property CpuSubType.
     */
    var cpuSubType: Long = 0

    /**
     * Gets the Uuid property.
     */
    /**
     * Sets the Uuid property.
     */
    /**
     * Backing field for property Uuid.
     */
    var uuid: String? = null

    /**
     * Gets the Path property.
     */
    /**
     * Sets the Path property.
     */
    /**
     * Backing field for property Path.
     */
    var path: String? = null

    /**
     * Initializes a new instance of the CrashDataBinary class.
     */
    init {
        this.InitializeFields()
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
        if (startAddress != null) {
            writer.write("$prefix\"startAddress\":")
            writer.write(JsonHelper.convert(this.startAddress))
            prefix = ","
        }

        if (endAddress != null) {
            writer.write("$prefix\"endAddress\":")
            writer.write(JsonHelper.convert(this.endAddress))
            prefix = ","
        }

        if (name != null) {
            writer.write("$prefix\"name\":")
            writer.write(JsonHelper.convert(this.name))
            prefix = ","
        }

        if (cpuType != 0L) {
            writer.write("$prefix\"cpuType\":")
            writer.write(JsonHelper.convert(this.cpuType))
            prefix = ","
        }

        if (cpuSubType != 0L) {
            writer.write("$prefix\"cpuSubType\":")
            writer.write(JsonHelper.convert(this.cpuSubType))
            prefix = ","
        }

        if (uuid != null) {
            writer.write("$prefix\"uuid\":")
            writer.write(JsonHelper.convert(this.uuid))
            prefix = ","
        }

        if (path != null) {
            writer.write("$prefix\"path\":")
            writer.write(JsonHelper.convert(this.path))
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
