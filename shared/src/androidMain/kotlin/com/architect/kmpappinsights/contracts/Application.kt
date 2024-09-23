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
 * Data contract class Application.
 */
class Application : IJsonSerializable, Serializable {
    /**
     * Gets the Ver property.
     */
    /**
     * Sets the Ver property.
     */
    /**
     * Backing field for property Ver.
     */
    @JvmField
    var ver: String? = null

    /**
     * Gets the Build property.
     */
    /**
     * Sets the Build property.
     */
    /**
     * Backing field for property Build.
     */
    var build: String? = null

    /**
     * Gets the TypeId property.
     */
    /**
     * Sets the TypeId property.
     */
    /**
     * Backing field for property TypeId.
     */
    var typeId: String? = null

    /**
     * Initializes a new instance of the Application class.
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
        if (ver != null) {
            map["ai.application.ver"] = ver
        }
        if (build != null) {
            map["ai.application.build"] = build
        }
        if (typeId != null) {
            map["ai.application.typeId"] = typeId
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
        if (ver != null) {
            writer.write("$prefix\"ai.application.ver\":")
            writer.write(JsonHelper.convert(this.ver))
            prefix = ","
        }

        if (build != null) {
            writer.write("$prefix\"ai.application.build\":")
            writer.write(JsonHelper.convert(this.build))
            prefix = ","
        }

        if (typeId != null) {
            writer.write("$prefix\"ai.application.typeId\":")
            writer.write(JsonHelper.convert(this.typeId))
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
