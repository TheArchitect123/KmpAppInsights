/*
 * Generated from CrashDataThread.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.CrashDataThreadFrame
import com.microsoft.telemetry.IJsonSerializable
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Serializable
import java.io.Writer

/**
 * Data contract class CrashDataThread.
 */
class CrashDataThread

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
     * Backing field for property Frames.
     */
    private var frames: List<CrashDataThreadFrame>? = null

    /**
     * Initializes a new instance of the CrashDataThread class.
     */
    init {
        this.InitializeFields()
    }

    /**
     * Gets the Frames property.
     */
    fun getFrames(): List<CrashDataThreadFrame>? {
        if (this.frames == null) {
            this.frames = ArrayList<CrashDataThreadFrame>()
        }
        return this.frames
    }

    /**
     * Sets the Frames property.
     */
    fun setFrames(value: List<CrashDataThreadFrame>?) {
        this.frames = value
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
        writer.write("$prefix\"id\":")
        writer.write(JsonHelper.convert(this.id))
        prefix = ","

        if (frames != null) {
            writer.write("$prefix\"frames\":")
            JsonHelper.writeList<CrashDataThreadFrame>(writer, this.frames)
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
