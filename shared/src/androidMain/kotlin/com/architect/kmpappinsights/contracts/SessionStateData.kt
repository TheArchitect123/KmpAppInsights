/*
 * Generated from SessionStateData.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.SessionState
import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class SessionStateData.
 */
class SessionStateData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Backing field for property State.
     */
    private var state: SessionState = SessionState.START

    /**
     * Initializes a new instance of the SessionStateData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.SessionState"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.SessionStateData"
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
     * Gets the State property.
     */
    fun getState(): SessionState {
        return this.state
    }

    /**
     * Sets the State property.
     */
    fun setState(value: SessionState) {
        this.state = value
    }


    /**
     * Gets the Properties property.
     */
    override fun getProperties(): Map<String, String> {
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
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected override fun serializeContent(writer: Writer): String {
        var prefix: String = super.serializeContent(writer)
        writer.write("$prefix\"ver\":")
        writer.write(JsonHelper.convert(this.ver))
        prefix = ","

        writer.write("$prefix\"state\":")
        writer.write(JsonHelper.convert(state.getValue()))
        prefix = ","

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
        QualifiedName = "com.architect.kmpappinsights.contracts.SessionStateData"
    }
}
