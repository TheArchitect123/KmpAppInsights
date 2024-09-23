/*
 * Generated from CrashDataThreadFrame.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.microsoft.telemetry.IJsonSerializable
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Serializable
import java.io.Writer

/**
 * Data contract class CrashDataThreadFrame.
 */
class CrashDataThreadFrame

    : IJsonSerializable, Serializable {
    /**
     * Gets the Address property.
     */
    /**
     * Sets the Address property.
     */
    /**
     * Backing field for property Address.
     */
    var address: String? = null

    /**
     * Gets the Symbol property.
     */
    /**
     * Sets the Symbol property.
     */
    /**
     * Backing field for property Symbol.
     */
    var symbol: String? = null

    /**
     * Backing field for property Registers.
     */
    private var registers: Map<String, String>? = null

    /**
     * Initializes a new instance of the CrashDataThreadFrame class.
     */
    init {
        this.InitializeFields()
    }

    /**
     * Gets the Registers property.
     */
    fun getRegisters(): Map<String, String>? {
        if (this.registers == null) {
            this.registers = LinkedHashMap()
        }
        return this.registers
    }

    /**
     * Sets the Registers property.
     */
    fun setRegisters(value: Map<String, String>?) {
        this.registers = value
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
        writer.write("$prefix\"address\":")
        writer.write(JsonHelper.convert(this.address))
        prefix = ","

        if (symbol != null) {
            writer.write("$prefix\"symbol\":")
            writer.write(JsonHelper.convert(this.symbol))
            prefix = ","
        }

        if (registers != null) {
            writer.write("$prefix\"registers\":")
            JsonHelper.writeDictionary(writer, this.registers)
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
