/*
 * Generated from AppInsightsTypes.bond (https://github.com/Microsoft/bond)
*/
package com.architect.androidjavaruntime.contracts;
/**
 * Enum DataPointType.
 */
public enum DataPointType
{
    MEASUREMENT(0), AGGREGATION(1);

    private final int value;
    private DataPointType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
    
}
