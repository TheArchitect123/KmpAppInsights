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
 * Data contract class Operation.
 */
class Operation

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
    var id: String? = null

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
     * Gets the ParentId property.
     */
    /**
     * Sets the ParentId property.
     */
    /**
     * Backing field for property ParentId.
     */
    var parentId: String? = null

    /**
     * Gets the RootId property.
     */
    /**
     * Sets the RootId property.
     */
    /**
     * Backing field for property RootId.
     */
    var rootId: String? = null

    /**
     * Gets the SyntheticSource property.
     */
    /**
     * Sets the SyntheticSource property.
     */
    /**
     * Backing field for property SyntheticSource.
     */
    var syntheticSource: String? = null

    /**
     * Gets the IsSynthetic property.
     */
    /**
     * Sets the IsSynthetic property.
     */
    /**
     * Backing field for property IsSynthetic.
     */
    var isSynthetic: String? = null

    /**
     * Initializes a new instance of the Operation class.
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
            map["ai.operation.id"] = id
        }
        if (name != null) {
            map["ai.operation.name"] = name
        }
        if (parentId != null) {
            map["ai.operation.parentId"] = parentId
        }
        if (rootId != null) {
            map["ai.operation.rootId"] = rootId
        }
        if (syntheticSource != null) {
            map["ai.operation.syntheticSource"] = syntheticSource
        }
        if (isSynthetic != null) {
            map["ai.operation.isSynthetic"] = isSynthetic
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
            writer.write("$prefix\"ai.operation.id\":")
            writer.write(JsonHelper.convert(this.id))
            prefix = ","
        }

        if (name != null) {
            writer.write("$prefix\"ai.operation.name\":")
            writer.write(JsonHelper.convert(this.name))
            prefix = ","
        }

        if (parentId != null) {
            writer.write("$prefix\"ai.operation.parentId\":")
            writer.write(JsonHelper.convert(this.parentId))
            prefix = ","
        }

        if (rootId != null) {
            writer.write("$prefix\"ai.operation.rootId\":")
            writer.write(JsonHelper.convert(this.rootId))
            prefix = ","
        }

        if (syntheticSource != null) {
            writer.write("$prefix\"ai.operation.syntheticSource\":")
            writer.write(JsonHelper.convert(this.syntheticSource))
            prefix = ","
        }

        if (isSynthetic != null) {
            writer.write("$prefix\"ai.operation.isSynthetic\":")
            writer.write(JsonHelper.convert(this.isSynthetic))
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
