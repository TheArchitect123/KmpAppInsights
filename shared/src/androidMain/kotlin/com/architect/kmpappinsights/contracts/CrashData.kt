/*
 * Generated from CrashData.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.CrashDataBinary
import com.architect.kmpappinsights.contracts.CrashDataHeaders
import com.architect.kmpappinsights.contracts.CrashDataThread
import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class CrashData.
 */
class CrashData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Backing field for property Headers.
     */
    private var headers: CrashDataHeaders? = null

    /**
     * Backing field for property Threads.
     */
    private var threads: List<CrashDataThread>? = null

    /**
     * Backing field for property Binaries.
     */
    private var binaries: List<CrashDataBinary>? = null

    /**
     * Initializes a new instance of the CrashData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Gets the Ver property.
     */
    fun getVer(): Int {
        return this.ver
    }

    /**
     * Sets the Ver property.
     */
    override fun setVer(value: Int) {
        this.ver = value
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.Crash"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.CrashData"
    }

    /**
     * Gets the Headers property.
     */
    fun getHeaders(): CrashDataHeaders? {
        return this.headers
    }

    /**
     * Sets the Headers property.
     */
    fun setHeaders(value: CrashDataHeaders?) {
        this.headers = value
    }

    /**
     * Gets the Threads property.
     */
    fun getThreads(): List<CrashDataThread>? {
        if (this.threads == null) {
            this.threads = ArrayList<CrashDataThread>()
        }
        return this.threads
    }

    /**
     * Sets the Threads property.
     */
    fun setThreads(value: List<CrashDataThread>?) {
        this.threads = value
    }

    /**
     * Gets the Binaries property.
     */
    fun getBinaries(): List<CrashDataBinary>? {
        if (this.binaries == null) {
            this.binaries = ArrayList<CrashDataBinary>()
        }
        return this.binaries
    }

    /**
     * Sets the Binaries property.
     */
    fun setBinaries(value: List<CrashDataBinary>?) {
        this.binaries = value
    }


    /**
     * Gets the Properties property.
     */
    override fun getProperties(): Map<String, String>? {
        //Do nothing - does not currently take properties
        return null
    }

    /**
     * Sets the Properties property.
     */
    override fun setProperties(value: Map<String, String>) {
        //Do nothing - does not currently take properties
    }

    /**
     * Serializes the beginning of this object to the passed in writer.
     *
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected override fun serializeContent(writer: Writer): String {
        var prefix: String = super.serializeContent(writer)
        writer.write("$prefix\"ver\":")
        writer.write(JsonHelper.convert(this.ver))
        prefix = ","

        writer.write("$prefix\"headers\":")
        JsonHelper.writeJsonSerializable(writer, this.headers)
        prefix = ","

        if (threads != null) {
            writer.write("$prefix\"threads\":")
            JsonHelper.writeList<CrashDataThread>(writer, this.threads)
            prefix = ","
        }

        if (binaries != null) {
            writer.write("$prefix\"binaries\":")
            JsonHelper.writeList<CrashDataBinary>(writer, this.binaries)
            prefix = ","
        }

        return prefix
    }

    /**
     * Sets up the events attributes
     */
    fun SetupAttributes() {
    }

    /**
     * Optionally initializes fields for the current context.
     */
    protected override fun InitializeFields() {
        QualifiedName = "com.architect.kmpappinsights.contracts.CrashData"
    }
}
