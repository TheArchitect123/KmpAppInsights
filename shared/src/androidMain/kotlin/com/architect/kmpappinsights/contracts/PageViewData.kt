/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.EventData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class PageViewData.
 */
open class PageViewData : EventData() {
    /**
     * Gets the Url property.
     */
    /**
     * Sets the Url property.
     */
    /**
     * Backing field for property Url.
     */
    var url: String? = null

    /**
     * Gets the Duration property.
     */
    /**
     * Sets the Duration property.
     */
    /**
     * Backing field for property Duration.
     */
    var duration: String? = null

    /**
     * Gets the Referrer property.
     */
    /**
     * Sets the Referrer property.
     */
    /**
     * Backing field for property Referrer.
     */
    var referrer: String? = null

    /**
     * Gets the ReferrerData property.
     */
    /**
     * Sets the ReferrerData property.
     */
    /**
     * Backing field for property ReferrerData.
     */
    var referrerData: String? = null

    /**
     * Initializes a new instance of the PageViewData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.PageView"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.PageViewData"
    }


    /**
     * Serializes the beginning of this object to the passed in writer.
     *
     * @param writer The writer to serialize this object to.
     */
    @Throws(IOException::class)
    protected override fun serializeContent(writer: Writer): String {
        var prefix: String = super.serializeContent(writer)
        if (url != null) {
            writer.write("$prefix\"url\":")
            writer.write(JsonHelper.convert(this.url))
            prefix = ","
        }

        if (duration != null) {
            writer.write("$prefix\"duration\":")
            writer.write(JsonHelper.convert(this.duration))
            prefix = ","
        }

        if (referrer != null) {
            writer.write("$prefix\"referrer\":")
            writer.write(JsonHelper.convert(this.referrer))
            prefix = ","
        }

        if (referrerData != null) {
            writer.write("$prefix\"referrerData\":")
            writer.write(JsonHelper.convert(this.referrerData))
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
        QualifiedName = "com.architect.kmpappinsights.contracts.PageViewData"
    }
}
