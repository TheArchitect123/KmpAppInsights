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
 * Data contract class User.
 */
class User

    : IJsonSerializable, Serializable {
    /**
     * Gets the AccountAcquisitionDate property.
     */
    /**
     * Sets the AccountAcquisitionDate property.
     */
    /**
     * Backing field for property AccountAcquisitionDate.
     */
    @JvmField
    var accountAcquisitionDate: String? = null

    /**
     * Gets the AccountId property.
     */
    /**
     * Sets the AccountId property.
     */
    /**
     * Backing field for property AccountId.
     */
    @JvmField
    var accountId: String? = null

    /**
     * Gets the UserAgent property.
     */
    /**
     * Sets the UserAgent property.
     */
    /**
     * Backing field for property UserAgent.
     */
    var userAgent: String? = null

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
     * Gets the StoreRegion property.
     */
    /**
     * Sets the StoreRegion property.
     */
    /**
     * Backing field for property StoreRegion.
     */
    var storeRegion: String? = null

    /**
     * Gets the AuthUserId property.
     */
    /**
     * Sets the AuthUserId property.
     */
    /**
     * Backing field for property AuthUserId.
     */
    @JvmField
    var authUserId: String? = null

    /**
     * Gets the AnonUserAcquisitionDate property.
     */
    /**
     * Sets the AnonUserAcquisitionDate property.
     */
    /**
     * Backing field for property AnonUserAcquisitionDate.
     */
    @JvmField
    var anonUserAcquisitionDate: String? = null

    /**
     * Gets the AuthUserAcquisitionDate property.
     */
    /**
     * Sets the AuthUserAcquisitionDate property.
     */
    /**
     * Backing field for property AuthUserAcquisitionDate.
     */
    @JvmField
    var authUserAcquisitionDate: String? = null

    /**
     * Initializes a new instance of the User class.
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
        if (accountAcquisitionDate != null) {
            map["ai.user.accountAcquisitionDate"] = accountAcquisitionDate
        }
        if (accountId != null) {
            map["ai.user.accountId"] = accountId
        }
        if (userAgent != null) {
            map["ai.user.userAgent"] = userAgent
        }
        if (id != null) {
            map["ai.user.id"] = id
        }
        if (storeRegion != null) {
            map["ai.user.storeRegion"] = storeRegion
        }
        if (authUserId != null) {
            map["ai.user.authUserId"] = authUserId
        }
        if (anonUserAcquisitionDate != null) {
            map["ai.user.anonUserAcquisitionDate"] = anonUserAcquisitionDate
        }
        if (authUserAcquisitionDate != null) {
            map["ai.user.authUserAcquisitionDate"] = authUserAcquisitionDate
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
        if (accountAcquisitionDate != null) {
            writer.write("$prefix\"ai.user.accountAcquisitionDate\":")
            writer.write(JsonHelper.convert(this.accountAcquisitionDate))
            prefix = ","
        }

        if (accountId != null) {
            writer.write("$prefix\"ai.user.accountId\":")
            writer.write(JsonHelper.convert(this.accountId))
            prefix = ","
        }

        if (userAgent != null) {
            writer.write("$prefix\"ai.user.userAgent\":")
            writer.write(JsonHelper.convert(this.userAgent))
            prefix = ","
        }

        if (id != null) {
            writer.write("$prefix\"ai.user.id\":")
            writer.write(JsonHelper.convert(this.id))
            prefix = ","
        }

        if (storeRegion != null) {
            writer.write("$prefix\"ai.user.storeRegion\":")
            writer.write(JsonHelper.convert(this.storeRegion))
            prefix = ","
        }

        if (authUserId != null) {
            writer.write("$prefix\"ai.user.authUserId\":")
            writer.write(JsonHelper.convert(this.authUserId))
            prefix = ","
        }

        if (anonUserAcquisitionDate != null) {
            writer.write("$prefix\"ai.user.anonUserAcquisitionDate\":")
            writer.write(JsonHelper.convert(this.anonUserAcquisitionDate))
            prefix = ","
        }

        if (authUserAcquisitionDate != null) {
            writer.write("$prefix\"ai.user.authUserAcquisitionDate\":")
            writer.write(JsonHelper.convert(this.authUserAcquisitionDate))
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
