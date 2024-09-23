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
 * Data contract class Device.
 */
class Device

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
    @JvmField
    var id: String? = null

    /**
     * Gets the Ip property.
     */
    /**
     * Sets the Ip property.
     */
    /**
     * Backing field for property Ip.
     */
    var ip: String? = null

    /**
     * Gets the Language property.
     */
    /**
     * Sets the Language property.
     */
    /**
     * Backing field for property Language.
     */
    var language: String? = null

    /**
     * Gets the Locale property.
     */
    /**
     * Sets the Locale property.
     */
    /**
     * Backing field for property Locale.
     */
    @JvmField
    var locale: String? = null

    /**
     * Gets the Model property.
     */
    /**
     * Sets the Model property.
     */
    /**
     * Backing field for property Model.
     */
    @JvmField
    var model: String? = null

    /**
     * Gets the Network property.
     */
    /**
     * Sets the Network property.
     */
    /**
     * Backing field for property Network.
     */
    @JvmField
    var network: String? = null

    /**
     * Gets the NetworkName property.
     */
    /**
     * Sets the NetworkName property.
     */
    /**
     * Backing field for property NetworkName.
     */
    var networkName: String? = null

    /**
     * Gets the OemName property.
     */
    /**
     * Sets the OemName property.
     */
    /**
     * Backing field for property OemName.
     */
    @JvmField
    var oemName: String? = null

    /**
     * Gets the Os property.
     */
    /**
     * Sets the Os property.
     */
    /**
     * Backing field for property Os.
     */
    @JvmField
    var os: String? = null

    /**
     * Gets the OsVersion property.
     */
    /**
     * Sets the OsVersion property.
     */
    /**
     * Backing field for property OsVersion.
     */
    @JvmField
    var osVersion: String? = null

    /**
     * Gets the RoleInstance property.
     */
    /**
     * Sets the RoleInstance property.
     */
    /**
     * Backing field for property RoleInstance.
     */
    var roleInstance: String? = null

    /**
     * Gets the RoleName property.
     */
    /**
     * Sets the RoleName property.
     */
    /**
     * Backing field for property RoleName.
     */
    var roleName: String? = null

    /**
     * Gets the ScreenResolution property.
     */
    /**
     * Sets the ScreenResolution property.
     */
    /**
     * Backing field for property ScreenResolution.
     */
    @JvmField
    var screenResolution: String? = null

    /**
     * Gets the Type property.
     */
    /**
     * Sets the Type property.
     */
    /**
     * Backing field for property Type.
     */
    @JvmField
    var type: String? = null

    /**
     * Gets the MachineName property.
     */
    /**
     * Sets the MachineName property.
     */
    /**
     * Backing field for property MachineName.
     */
    var machineName: String? = null

    /**
     * Gets the VmName property.
     */
    /**
     * Sets the VmName property.
     */
    /**
     * Backing field for property VmName.
     */
    var vmName: String? = null

    /**
     * Initializes a new instance of the Device class.
     */
    init {
        this.InitializeFields()
    }


    /**
     * Adds all members of this class to a hashmap
     * @param map to which the members of this class will be added.
     */
    fun addToHashMap(map: MutableMap<String?, String?>) {
        if (id != null) {
            map["ai.device.id"] = id
        }
        if (ip != null) {
            map["ai.device.ip"] = ip
        }
        if (language != null) {
            map["ai.device.language"] = language
        }
        if (locale != null) {
            map["ai.device.locale"] = locale
        }
        if (model != null) {
            map["ai.device.model"] = model
        }
        if (network != null) {
            map["ai.device.network"] = network
        }
        if (networkName != null) {
            map["ai.device.networkName"] = networkName
        }
        if (oemName != null) {
            map["ai.device.oemName"] = oemName
        }
        if (os != null) {
            map["ai.device.os"] = os
        }
        if (osVersion != null) {
            map["ai.device.osVersion"] = osVersion
        }
        if (roleInstance != null) {
            map["ai.device.roleInstance"] = roleInstance
        }
        if (roleName != null) {
            map["ai.device.roleName"] = roleName
        }
        if (screenResolution != null) {
            map["ai.device.screenResolution"] = screenResolution
        }
        if (type != null) {
            map["ai.device.type"] = type
        }
        if (machineName != null) {
            map["ai.device.machineName"] = machineName
        }
        if (vmName != null) {
            map["ai.device.vmName"] = vmName
        }
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
        if (id != null) {
            writer.write("$prefix\"ai.device.id\":")
            writer.write(JsonHelper.convert(this.id))
            prefix = ","
        }

        if (ip != null) {
            writer.write("$prefix\"ai.device.ip\":")
            writer.write(JsonHelper.convert(this.ip))
            prefix = ","
        }

        if (language != null) {
            writer.write("$prefix\"ai.device.language\":")
            writer.write(JsonHelper.convert(this.language))
            prefix = ","
        }

        if (locale != null) {
            writer.write("$prefix\"ai.device.locale\":")
            writer.write(JsonHelper.convert(this.locale))
            prefix = ","
        }

        if (model != null) {
            writer.write("$prefix\"ai.device.model\":")
            writer.write(JsonHelper.convert(this.model))
            prefix = ","
        }

        if (network != null) {
            writer.write("$prefix\"ai.device.network\":")
            writer.write(JsonHelper.convert(this.network))
            prefix = ","
        }

        if (networkName != null) {
            writer.write("$prefix\"ai.device.networkName\":")
            writer.write(JsonHelper.convert(this.networkName))
            prefix = ","
        }

        if (oemName != null) {
            writer.write("$prefix\"ai.device.oemName\":")
            writer.write(JsonHelper.convert(this.oemName))
            prefix = ","
        }

        if (os != null) {
            writer.write("$prefix\"ai.device.os\":")
            writer.write(JsonHelper.convert(this.os))
            prefix = ","
        }

        if (osVersion != null) {
            writer.write("$prefix\"ai.device.osVersion\":")
            writer.write(JsonHelper.convert(this.osVersion))
            prefix = ","
        }

        if (roleInstance != null) {
            writer.write("$prefix\"ai.device.roleInstance\":")
            writer.write(JsonHelper.convert(this.roleInstance))
            prefix = ","
        }

        if (roleName != null) {
            writer.write("$prefix\"ai.device.roleName\":")
            writer.write(JsonHelper.convert(this.roleName))
            prefix = ","
        }

        if (screenResolution != null) {
            writer.write("$prefix\"ai.device.screenResolution\":")
            writer.write(JsonHelper.convert(this.screenResolution))
            prefix = ","
        }

        if (type != null) {
            writer.write("$prefix\"ai.device.type\":")
            writer.write(JsonHelper.convert(this.type))
            prefix = ","
        }

        if (machineName != null) {
            writer.write("$prefix\"ai.device.machineName\":")
            writer.write(JsonHelper.convert(this.machineName))
            prefix = ","
        }

        if (vmName != null) {
            writer.write("$prefix\"ai.device.vmName\":")
            writer.write(JsonHelper.convert(this.vmName))
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
