package com.architect.kmpappinsights.library.config

interface ISessionConfig {
    /**
     * Gets the interval at which sessions are renewed
     */
    /**
     * Sets the interval at which sessions are renewed
     */
    @JvmField
    var sessionIntervalMs: Long
}
