/*
 * Generated from SeverityLevel.bond (https://github.com/Microsoft/bond)
*/
package com.architect.androidjavaruntime.contracts;
/**
 * Enum SeverityLevel.
 */
public enum SeverityLevel
{
    VERBOSE(0), INFORMATION(1), WARNING(2), ERROR(3), CRITICAL(4);
    private final int value;

    private SeverityLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
