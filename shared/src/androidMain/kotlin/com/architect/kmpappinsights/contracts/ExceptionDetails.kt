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
 * Data contract class ExceptionDetails.
 */
class ExceptionDetails

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
    var id: Int = 0

    /**
     * Gets the OuterId property.
     */
    /**
     * Sets the OuterId property.
     */
    /**
     * Backing field for property OuterId.
     */
    var outerId: Int = 0

    /**
     * Gets the TypeName property.
     */
    /**
     * Sets the TypeName property.
     */
    /**
     * Backing field for property TypeName.
     */
    var typeName: String? = null

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
     * Gets the HasFullStack property.
     */
    /**
     * Sets the HasFullStack property.
     */
    /**
     * Backing field for property HasFullStack.
     */
    var hasFullStack: Boolean = true

    /**
     * Gets the Stack property.
     */
    /**
     * Sets the Stack property.
     */
    /**
     * Backing field for property Stack.
     */
    var stack: String? = null

    /**
     * Backing field for property ParsedStack.
     */
    private var parsedStack: List<com.architect.kmpappinsights.contracts.StackFrame>? = null

    /**
     * Initializes a new instance of the ExceptionDetails class.
     */
    init {
        this.InitializeFields()
    }

    /**
     * Gets the ParsedStack property.
     */
    fun getParsedStack(): List<com.architect.kmpappinsights.contracts.StackFrame>? {
        if (this.parsedStack == null) {
            this.parsedStack = ArrayList<com.architect.kmpappinsights.contracts.StackFrame>()
        }
        return this.parsedStack
    }

    /**
     * Sets the ParsedStack property.
     */
    fun setParsedStack(value: List<com.architect.kmpappinsights.contracts.StackFrame>?) {
        this.parsedStack = value
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
        if (id != 0) {
            writer.write("$prefix\"id\":")
            writer.write(JsonHelper.convert(this.id))
            prefix = ","
        }

        if (outerId != 0) {
            writer.write("$prefix\"outerId\":")
            writer.write(JsonHelper.convert(this.outerId))
            prefix = ","
        }

        writer.write("$prefix\"typeName\":")
        writer.write(JsonHelper.convert(this.typeName))
        prefix = ","

        writer.write("$prefix\"message\":")
        writer.write(JsonHelper.convert(this.message))
        prefix = ","

        if (hasFullStack != false) {
            writer.write("$prefix\"hasFullStack\":")
            writer.write(JsonHelper.convert(this.hasFullStack))
            prefix = ","
        }

        if (stack != null) {
            writer.write("$prefix\"stack\":")
            writer.write(JsonHelper.convert(this.stack))
            prefix = ","
        }

        if (parsedStack != null) {
            writer.write("$prefix\"parsedStack\":")
            JsonHelper.writeList<com.architect.kmpappinsights.contracts.StackFrame>(
                writer,
                this.parsedStack
            )
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
