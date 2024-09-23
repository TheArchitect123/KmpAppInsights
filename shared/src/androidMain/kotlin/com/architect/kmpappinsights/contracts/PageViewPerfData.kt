/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.PageViewData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class PageViewPerfData.
 */
class PageViewPerfData : PageViewData() {
    /**
     * Gets the PerfTotal property.
     */
    /**
     * Sets the PerfTotal property.
     */
    /**
     * Backing field for property PerfTotal.
     */
    var perfTotal: String? = null

    /**
     * Gets the NetworkConnect property.
     */
    /**
     * Sets the NetworkConnect property.
     */
    /**
     * Backing field for property NetworkConnect.
     */
    var networkConnect: String? = null

    /**
     * Gets the SentRequest property.
     */
    /**
     * Sets the SentRequest property.
     */
    /**
     * Backing field for property SentRequest.
     */
    var sentRequest: String? = null

    /**
     * Gets the ReceivedResponse property.
     */
    /**
     * Sets the ReceivedResponse property.
     */
    /**
     * Backing field for property ReceivedResponse.
     */
    var receivedResponse: String? = null

    /**
     * Gets the DomProcessing property.
     */
    /**
     * Sets the DomProcessing property.
     */
    /**
     * Backing field for property DomProcessing.
     */
    var domProcessing: String? = null

    /**
     * Initializes a new instance of the PageViewPerfData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.PageViewPerf"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.PageViewPerfData"
    }


    /**
     * Serializes the beginning of this object to the passed in writer.
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected override fun serializeContent(writer: Writer): String {
        var prefix: String = super.serializeContent(writer)
        if (perfTotal != null) {
            writer.write("$prefix\"perfTotal\":")
            writer.write(JsonHelper.convert(this.perfTotal))
            prefix = ","
        }

        if (networkConnect != null) {
            writer.write("$prefix\"networkConnect\":")
            writer.write(JsonHelper.convert(this.networkConnect))
            prefix = ","
        }

        if (sentRequest != null) {
            writer.write("$prefix\"sentRequest\":")
            writer.write(JsonHelper.convert(this.sentRequest))
            prefix = ","
        }

        if (receivedResponse != null) {
            writer.write("$prefix\"receivedResponse\":")
            writer.write(JsonHelper.convert(this.receivedResponse))
            prefix = ","
        }

        if (domProcessing != null) {
            writer.write("$prefix\"domProcessing\":")
            writer.write(JsonHelper.convert(this.domProcessing))
            prefix = ","
        }

        return prefix
    }

    /**
     * Sets up the events attributes
     */
    override fun SetupAttributes() {
    }

    /**
     * Optionally initializes fields for the current context.
     */
    protected override fun InitializeFields() {
        QualifiedName = "com.architect.kmpappinsights.contracts.PageViewPerfData"
    }
}
