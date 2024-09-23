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
 * Data contract class Session.
 */
class Session

    : IJsonSerializable, Serializable {
    /**
     * Gets the Id property.
     */
    /**
     * Sets the Id property.
     */
    /**
     * Backing field for property Id.
     */
    @JvmField
    var id: String? = null

    /**
     * Gets the IsFirst property.
     */
    /**
     * Sets the IsFirst property.
     */
    /**
     * Backing field for property IsFirst.
     */
    @JvmField
    var isFirst: String? = null

    /**
     * Gets the IsNew property.
     */
    /**
     * Sets the IsNew property.
     */
    /**
     * Backing field for property IsNew.
     */
    @JvmField
    var isNew: String? = null

    /**
     * Initializes a new instance of the Session class.
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
        if (id != null) {
            map["ai.session.id"] = id
        }
        if (isFirst != null) {
            map["ai.session.isFirst"] = isFirst
        }
        if (isNew != null) {
            map["ai.session.isNew"] = isNew
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
        if (id != null) {
            writer.write("$prefix\"ai.session.id\":")
            writer.write(JsonHelper.convert(this.id))
            prefix = ","
        }

        if (isFirst != null) {
            writer.write("$prefix\"ai.session.isFirst\":")
            writer.write(JsonHelper.convert(this.isFirst))
            prefix = ","
        }

        if (isNew != null) {
            writer.write("$prefix\"ai.session.isNew\":")
            writer.write(JsonHelper.convert(this.isNew))
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
