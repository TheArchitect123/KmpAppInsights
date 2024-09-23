/*
 * Generated from CrashDataHeaders.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.microsoft.telemetry.IJsonSerializable
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Serializable
import java.io.Writer

/**
 * Data contract class CrashDataHeaders.
 */
class CrashDataHeaders

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
     * Gets the Process property.
     */
    /**
     * Sets the Process property.
     */
    /**
     * Backing field for property Process.
     */
    var process: String? = null

    /**
     * Gets the ProcessId property.
     */
    /**
     * Sets the ProcessId property.
     */
    /**
     * Backing field for property ProcessId.
     */
    var processId: Int = 0

    /**
     * Gets the ParentProcess property.
     */
    /**
     * Sets the ParentProcess property.
     */
    /**
     * Backing field for property ParentProcess.
     */
    var parentProcess: String? = null

    /**
     * Gets the ParentProcessId property.
     */
    /**
     * Sets the ParentProcessId property.
     */
    /**
     * Backing field for property ParentProcessId.
     */
    var parentProcessId: Int = 0

    /**
     * Gets the CrashThread property.
     */
    /**
     * Sets the CrashThread property.
     */
    /**
     * Backing field for property CrashThread.
     */
    var crashThread: Int = 0

    /**
     * Gets the ApplicationPath property.
     */
    /**
     * Sets the ApplicationPath property.
     */
    /**
     * Backing field for property ApplicationPath.
     */
    var applicationPath: String? = null

    /**
     * Gets the ApplicationIdentifier property.
     */
    /**
     * Sets the ApplicationIdentifier property.
     */
    /**
     * Backing field for property ApplicationIdentifier.
     */
    var applicationIdentifier: String? = null

    /**
     * Gets the ApplicationBuild property.
     */
    /**
     * Sets the ApplicationBuild property.
     */
    /**
     * Backing field for property ApplicationBuild.
     */
    var applicationBuild: String? = null

    /**
     * Gets the ExceptionType property.
     */
    /**
     * Sets the ExceptionType property.
     */
    /**
     * Backing field for property ExceptionType.
     */
    var exceptionType: String? = null

    /**
     * Gets the ExceptionCode property.
     */
    /**
     * Sets the ExceptionCode property.
     */
    /**
     * Backing field for property ExceptionCode.
     */
    var exceptionCode: String? = null

    /**
     * Gets the ExceptionAddress property.
     */
    /**
     * Sets the ExceptionAddress property.
     */
    /**
     * Backing field for property ExceptionAddress.
     */
    var exceptionAddress: String? = null

    /**
     * Gets the ExceptionReason property.
     */
    /**
     * Sets the ExceptionReason property.
     */
    /**
     * Backing field for property ExceptionReason.
     */
    var exceptionReason: String? = null

    /**
     * Initializes a new instance of the CrashDataHeaders class.
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
        writer.write("$prefix\"id\":")
        writer.write(JsonHelper.convert(this.id))
        prefix = ","

        if (process != null) {
            writer.write("$prefix\"process\":")
            writer.write(JsonHelper.convert(this.process))
            prefix = ","
        }

        if (processId != 0) {
            writer.write("$prefix\"processId\":")
            writer.write(JsonHelper.convert(this.processId))
            prefix = ","
        }

        if (parentProcess != null) {
            writer.write("$prefix\"parentProcess\":")
            writer.write(JsonHelper.convert(this.parentProcess))
            prefix = ","
        }

        if (parentProcessId != 0) {
            writer.write("$prefix\"parentProcessId\":")
            writer.write(JsonHelper.convert(this.parentProcessId))
            prefix = ","
        }

        if (crashThread != 0) {
            writer.write("$prefix\"crashThread\":")
            writer.write(JsonHelper.convert(this.crashThread))
            prefix = ","
        }

        if (applicationPath != null) {
            writer.write("$prefix\"applicationPath\":")
            writer.write(JsonHelper.convert(this.applicationPath))
            prefix = ","
        }

        if (applicationIdentifier != null) {
            writer.write("$prefix\"applicationIdentifier\":")
            writer.write(JsonHelper.convert(this.applicationIdentifier))
            prefix = ","
        }

        if (applicationBuild != null) {
            writer.write("$prefix\"applicationBuild\":")
            writer.write(JsonHelper.convert(this.applicationBuild))
            prefix = ","
        }

        if (exceptionType != null) {
            writer.write("$prefix\"exceptionType\":")
            writer.write(JsonHelper.convert(this.exceptionType))
            prefix = ","
        }

        if (exceptionCode != null) {
            writer.write("$prefix\"exceptionCode\":")
            writer.write(JsonHelper.convert(this.exceptionCode))
            prefix = ","
        }

        if (exceptionAddress != null) {
            writer.write("$prefix\"exceptionAddress\":")
            writer.write(JsonHelper.convert(this.exceptionAddress))
            prefix = ","
        }

        if (exceptionReason != null) {
            writer.write("$prefix\"exceptionReason\":")
            writer.write(JsonHelper.convert(this.exceptionReason))
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
