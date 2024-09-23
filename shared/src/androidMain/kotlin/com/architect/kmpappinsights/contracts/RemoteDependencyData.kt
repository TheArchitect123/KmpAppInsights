/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.kmpappinsights.contracts

import com.architect.kmpappinsights.contracts.DataPointType
import com.architect.kmpappinsights.contracts.DependencySourceType
import com.architect.kmpappinsights.contracts.TelemetryData
import com.microsoft.telemetry.JsonHelper
import java.io.IOException
import java.io.Writer

/**
 * Data contract class RemoteDependencyData.
 */
class RemoteDependencyData : TelemetryData() {
    /**
     * Backing field for property Ver.
     */
    private var ver = 2

    /**
     * Gets the Name property.
     */
    /**
     * Sets the Name property.
     */
    /**
     * Backing field for property Name.
     */
    var name: String? = null

    /**
     * Backing field for property Kind.
     */
    private var kind: DataPointType = DataPointType.MEASUREMENT

    /**
     * Gets the Value property.
     */
    /**
     * Sets the Value property.
     */
    /**
     * Backing field for property Value.
     */
    var value: Double = 0.0

    /**
     * Gets the Count property.
     */
    /**
     * Sets the Count property.
     */
    /**
     * Backing field for property Count.
     */
    var count: Int? = null

    /**
     * Gets the Min property.
     */
    /**
     * Sets the Min property.
     */
    /**
     * Backing field for property Min.
     */
    var min: Double? = null

    /**
     * Gets the Max property.
     */
    /**
     * Sets the Max property.
     */
    /**
     * Backing field for property Max.
     */
    var max: Double? = null

    /**
     * Gets the StdDev property.
     */
    /**
     * Sets the StdDev property.
     */
    /**
     * Backing field for property StdDev.
     */
    var stdDev: Double? = null

    /**
     * Backing field for property DependencyKind.
     */
    private var dependencyKind: com.architect.kmpappinsights.contracts.DependencyKind? =
        com.architect.kmpappinsights.contracts.DependencyKind.OTHER

    /**
     * Gets the Success property.
     */
    /**
     * Sets the Success property.
     */
    /**
     * Backing field for property Success.
     */
    var success: Boolean? = true

    /**
     * Gets the Async property.
     */
    /**
     * Sets the Async property.
     */
    /**
     * Backing field for property Async.
     */
    var async: Boolean? = null

    /**
     * Backing field for property DependencySource.
     */
    private var dependencySource: DependencySourceType = DependencySourceType.UNDEFINED

    /**
     * Gets the CommandName property.
     */
    /**
     * Sets the CommandName property.
     */
    /**
     * Backing field for property CommandName.
     */
    var commandName: String? = null

    /**
     * Gets the DependencyTypeName property.
     */
    /**
     * Sets the DependencyTypeName property.
     */
    /**
     * Backing field for property DependencyTypeName.
     */
    var dependencyTypeName: String? = null

    /**
     * Backing field for property Properties.
     */
    private var properties: Map<String, String>? = null

    /**
     * Initializes a new instance of the RemoteDependencyData class.
     */
    init {
        this.InitializeFields()
        this.SetupAttributes()
    }

    /**
     * Envelope Name for this telemetry.
     */
    override fun getEnvelopeName(): String {
        return "Microsoft.ApplicationInsights.RemoteDependency"
    }

    /**
     * Base Type for this telemetry.
     */
    override fun getBaseType(): String {
        return "Microsoft.ApplicationInsights.RemoteDependencyData"
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
     * Gets the Kind property.
     */
    fun getKind(): DataPointType {
        return this.kind
    }

    /**
     * Sets the Kind property.
     */
    fun setKind(value: DataPointType) {
        this.kind = value
    }

    /**
     * Gets the DependencyKind property.
     */
    fun getDependencyKind(): com.architect.kmpappinsights.contracts.DependencyKind? {
        return this.dependencyKind
    }

    /**
     * Sets the DependencyKind property.
     */
    fun setDependencyKind(value: com.architect.kmpappinsights.contracts.DependencyKind?) {
        this.dependencyKind = value
    }

    /**
     * Gets the DependencySource property.
     */
    fun getDependencySource(): DependencySourceType {
        return this.dependencySource
    }

    /**
     * Sets the DependencySource property.
     */
    fun setDependencySource(value: DependencySourceType) {
        this.dependencySource = value
    }

    /**
     * Gets the Properties property.
     */
    override fun getProperties(): Map<String, String> {
        if (this.properties == null) {
            this.properties = LinkedHashMap()
        }
        return properties!!
    }

    /**
     * Sets the Properties property.
     */
    override fun setProperties(value: Map<String, String>) {
        this.properties = value
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

        writer.write("$prefix\"name\":")
        writer.write(JsonHelper.convert(this.name))
        prefix = ","

        if (kind != DataPointType.MEASUREMENT) {
            writer.write("$prefix\"kind\":")
            writer.write(JsonHelper.convert(kind.value))
            prefix = ","
        }

        writer.write("$prefix\"value\":")
        writer.write(JsonHelper.convert(this.value))
        prefix = ","

        if (count != null) {
            writer.write("$prefix\"count\":")
            writer.write(JsonHelper.convert(this.count))
            prefix = ","
        }

        if (min != null) {
            writer.write("$prefix\"min\":")
            writer.write(JsonHelper.convert(this.min))
            prefix = ","
        }

        if (max != null) {
            writer.write("$prefix\"max\":")
            writer.write(JsonHelper.convert(this.max))
            prefix = ","
        }

        if (stdDev != null) {
            writer.write("$prefix\"stdDev\":")
            writer.write(JsonHelper.convert(this.stdDev))
            prefix = ","
        }

        if (dependencyKind != null) {
            writer.write("$prefix\"dependencyKind\":")
            writer.write(JsonHelper.convert(dependencyKind!!.value))
            prefix = ","
        }

        if (success != null) {
            writer.write("$prefix\"success\":")
            writer.write(JsonHelper.convert(success!!))
            prefix = ","
        }

        if (async != null) {
            writer.write("$prefix\"async\":")
            writer.write(JsonHelper.convert(async!!))
            prefix = ","
        }

        if (dependencySource != DependencySourceType.UNDEFINED) {
            writer.write("$prefix\"dependencySource\":")
            writer.write(JsonHelper.convert(dependencySource.value))
            prefix = ","
        }

        if (commandName != null) {
            writer.write("$prefix\"commandName\":")
            writer.write(JsonHelper.convert(this.commandName))
            prefix = ","
        }

        if (dependencyTypeName != null) {
            writer.write("$prefix\"dependencyTypeName\":")
            writer.write(JsonHelper.convert(this.dependencyTypeName))
            prefix = ","
        }

        if (properties != null) {
            writer.write("$prefix\"properties\":")
            JsonHelper.writeDictionary(writer, this.properties)
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
        QualifiedName = "AI.RemoteDependencyData"
    }
}
