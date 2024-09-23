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
 * Data contract class Internal.
 */
class Internal : IJsonSerializable, Serializable {
    /**
     * Gets the SdkVersion property.
     */
    /**
     * Sets the SdkVersion property.
     */
    /**
     * Backing field for property SdkVersion.
     */
    @JvmField
    var sdkVersion: String? = null

    /**
     * Gets the AgentVersion property.
     */
    /**
     * Sets the AgentVersion property.
     */
    /**
     * Backing field for property AgentVersion.
     */
    var agentVersion: String? = null

    /**
     * Gets the DataCollectorReceivedTime property.
     */
    /**
     * Sets the DataCollectorReceivedTime property.
     */
    /**
     * Backing field for property DataCollectorReceivedTime.
     */
    var dataCollectorReceivedTime: String? = null

    /**
     * Gets the ProfileId property.
     */
    /**
     * Sets the ProfileId property.
     */
    /**
     * Backing field for property ProfileId.
     */
    var profileId: String? = null

    /**
     * Gets the ProfileClassId property.
     */
    /**
     * Sets the ProfileClassId property.
     */
    /**
     * Backing field for property ProfileClassId.
     */
    var profileClassId: String? = null

    /**
     * Gets the AccountId property.
     */
    /**
     * Sets the AccountId property.
     */
    /**
     * Backing field for property AccountId.
     */
    var accountId: String? = null

    /**
     * Gets the ApplicationName property.
     */
    /**
     * Sets the ApplicationName property.
     */
    /**
     * Backing field for property ApplicationName.
     */
    var applicationName: String? = null

    /**
     * Gets the InstrumentationKey property.
     */
    /**
     * Sets the InstrumentationKey property.
     */
    /**
     * Backing field for property InstrumentationKey.
     */
    var instrumentationKey: String? = null

    /**
     * Gets the TelemetryItemId property.
     */
    /**
     * Sets the TelemetryItemId property.
     */
    /**
     * Backing field for property TelemetryItemId.
     */
    var telemetryItemId: String? = null

    /**
     * Gets the ApplicationType property.
     */
    /**
     * Sets the ApplicationType property.
     */
    /**
     * Backing field for property ApplicationType.
     */
    var applicationType: String? = null

    /**
     * Gets the RequestSource property.
     */
    /**
     * Sets the RequestSource property.
     */
    /**
     * Backing field for property RequestSource.
     */
    var requestSource: String? = null

    /**
     * Gets the FlowType property.
     */
    /**
     * Sets the FlowType property.
     */
    /**
     * Backing field for property FlowType.
     */
    var flowType: String? = null

    /**
     * Gets the IsAudit property.
     */
    /**
     * Sets the IsAudit property.
     */
    /**
     * Backing field for property IsAudit.
     */
    var isAudit: String? = null

    /**
     * Gets the TrackingSourceId property.
     */
    /**
     * Sets the TrackingSourceId property.
     */
    /**
     * Backing field for property TrackingSourceId.
     */
    var trackingSourceId: String? = null

    /**
     * Gets the TrackingType property.
     */
    /**
     * Sets the TrackingType property.
     */
    /**
     * Backing field for property TrackingType.
     */
    var trackingType: String? = null

    /**
     * Initializes a new instance of the Internal class.
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
        if (sdkVersion != null) {
            map["ai.internal.sdkVersion"] = sdkVersion
        }
        if (agentVersion != null) {
            map["ai.internal.agentVersion"] = agentVersion
        }
        if (dataCollectorReceivedTime != null) {
            map["ai.internal.dataCollectorReceivedTime"] = dataCollectorReceivedTime
        }
        if (profileId != null) {
            map["ai.internal.profileId"] = profileId
        }
        if (profileClassId != null) {
            map["ai.internal.profileClassId"] = profileClassId
        }
        if (accountId != null) {
            map["ai.internal.accountId"] = accountId
        }
        if (applicationName != null) {
            map["ai.internal.applicationName"] = applicationName
        }
        if (instrumentationKey != null) {
            map["ai.internal.instrumentationKey"] = instrumentationKey
        }
        if (telemetryItemId != null) {
            map["ai.internal.telemetryItemId"] = telemetryItemId
        }
        if (applicationType != null) {
            map["ai.internal.applicationType"] = applicationType
        }
        if (requestSource != null) {
            map["ai.internal.requestSource"] = requestSource
        }
        if (flowType != null) {
            map["ai.internal.flowType"] = flowType
        }
        if (isAudit != null) {
            map["ai.internal.isAudit"] = isAudit
        }
        if (trackingSourceId != null) {
            map["ai.internal.trackingSourceId"] = trackingSourceId
        }
        if (trackingType != null) {
            map["ai.internal.trackingType"] = trackingType
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
        if (sdkVersion != null) {
            writer.write("$prefix\"ai.internal.sdkVersion\":")
            writer.write(JsonHelper.convert(this.sdkVersion))
            prefix = ","
        }

        if (agentVersion != null) {
            writer.write("$prefix\"ai.internal.agentVersion\":")
            writer.write(JsonHelper.convert(this.agentVersion))
            prefix = ","
        }

        if (dataCollectorReceivedTime != null) {
            writer.write("$prefix\"ai.internal.dataCollectorReceivedTime\":")
            writer.write(JsonHelper.convert(this.dataCollectorReceivedTime))
            prefix = ","
        }

        if (profileId != null) {
            writer.write("$prefix\"ai.internal.profileId\":")
            writer.write(JsonHelper.convert(this.profileId))
            prefix = ","
        }

        if (profileClassId != null) {
            writer.write("$prefix\"ai.internal.profileClassId\":")
            writer.write(JsonHelper.convert(this.profileClassId))
            prefix = ","
        }

        if (accountId != null) {
            writer.write("$prefix\"ai.internal.accountId\":")
            writer.write(JsonHelper.convert(this.accountId))
            prefix = ","
        }

        if (applicationName != null) {
            writer.write("$prefix\"ai.internal.applicationName\":")
            writer.write(JsonHelper.convert(this.applicationName))
            prefix = ","
        }

        if (instrumentationKey != null) {
            writer.write("$prefix\"ai.internal.instrumentationKey\":")
            writer.write(JsonHelper.convert(this.instrumentationKey))
            prefix = ","
        }

        if (telemetryItemId != null) {
            writer.write("$prefix\"ai.internal.telemetryItemId\":")
            writer.write(JsonHelper.convert(this.telemetryItemId))
            prefix = ","
        }

        if (applicationType != null) {
            writer.write("$prefix\"ai.internal.applicationType\":")
            writer.write(JsonHelper.convert(this.applicationType))
            prefix = ","
        }

        if (requestSource != null) {
            writer.write("$prefix\"ai.internal.requestSource\":")
            writer.write(JsonHelper.convert(this.requestSource))
            prefix = ","
        }

        if (flowType != null) {
            writer.write("$prefix\"ai.internal.flowType\":")
            writer.write(JsonHelper.convert(this.flowType))
            prefix = ","
        }

        if (isAudit != null) {
            writer.write("$prefix\"ai.internal.isAudit\":")
            writer.write(JsonHelper.convert(this.isAudit))
            prefix = ","
        }

        if (trackingSourceId != null) {
            writer.write("$prefix\"ai.internal.trackingSourceId\":")
            writer.write(JsonHelper.convert(this.trackingSourceId))
            prefix = ","
        }

        if (trackingType != null) {
            writer.write("$prefix\"ai.internal.trackingType\":")
            writer.write(JsonHelper.convert(this.trackingType))
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
