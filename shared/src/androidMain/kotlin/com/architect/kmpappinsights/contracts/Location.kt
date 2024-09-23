/*
 * Generated from ContextTagKeys.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.microsoft.telemetry.IJsonSerializable
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Serializable
import java.io.Writer

/**
 * Data contract class Location.
 */
class Location : IJsonSerializable, Serializable {
    /**
     * Gets the Ip property.
     */
    /**
     * Sets the Ip property.
     */
    /**
     * Backing field for property Ip.
     */
    var ip: String? = null

    /**
     * Initializes a new instance of the Location class.
     */
    init {
        this.InitializeFields()
    }


    /**
     * Adds all members of this class to a hashmap
     *
     * @param map to which the members of this class will be added.
     */
    fun addToHashMap(map: MutableMap<String?, String?>) {
        if (ip != null) {
            map["ai.location.ip"] = ip
        }
    }


    /**
     * Serializes the beginning of this object to the passed in writer.
     *
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
     *
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected fun serializeContent(writer: Writer): String {
        var prefix = ""
        if (ip != null) {
            writer.write("$prefix\"ai.location.ip\":")
            writer.write(JsonHelper.convert(this.ip))
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
