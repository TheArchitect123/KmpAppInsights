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
 * Data contract class StackFrame.
 */
class StackFrame

    : IJsonSerializable, Serializable {
    /**
     * Gets the Level property.
     */
    /**
     * Sets the Level property.
     */
    /**
     * Backing field for property Level.
     */
    var level: Int = 0

    /**
     * Gets the Method property.
     */
    /**
     * Sets the Method property.
     */
    /**
     * Backing field for property Method.
     */
    var method: String? = null

    /**
     * Gets the Assembly property.
     */
    /**
     * Sets the Assembly property.
     */
    /**
     * Backing field for property Assembly.
     */
    var assembly: String? = null

    /**
     * Gets the FileName property.
     */
    /**
     * Sets the FileName property.
     */
    /**
     * Backing field for property FileName.
     */
    var fileName: String? = null

    /**
     * Gets the Line property.
     */
    /**
     * Sets the Line property.
     */
    /**
     * Backing field for property Line.
     */
    var line: Int = 0

    /**
     * Initializes a new instance of the StackFrame class.
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
        writer.write("$prefix\"level\":")
        writer.write(JsonHelper.convert(this.level))
        prefix = ","

        writer.write("$prefix\"method\":")
        writer.write(JsonHelper.convert(this.method))
        prefix = ","

        if (assembly != null) {
            writer.write("$prefix\"assembly\":")
            writer.write(JsonHelper.convert(this.assembly))
            prefix = ","
        }

        if (fileName != null) {
            writer.write("$prefix\"fileName\":")
            writer.write(JsonHelper.convert(this.fileName))
            prefix = ","
        }

        if (line != 0) {
            writer.write("$prefix\"line\":")
            writer.write(JsonHelper.convert(this.line))
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
