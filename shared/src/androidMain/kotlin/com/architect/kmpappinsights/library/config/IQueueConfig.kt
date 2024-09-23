package com.architect.kmpappinsights.library.config

interface IQueueConfig {
    /**
     * Gets the maximum size of a batch in bytes
     * @return the max batch count until we send a bundle of data to the server
     */
    /**
     * Sets the maximum size of a batch in bytes
     * @param maxBatchCount the batchsize of data that will be queued until we send/writeToDisk it
     */
    @JvmField
    var maxBatchCount: Int

    /**
     * Gets the maximum interval allowed between calls to batchInvoke
     * @return the interval until we send/writeToDisk queued up data
     */
    /**
     * Sets the maximum interval allowed between calls to batchInvoke
     * @param maxBatchIntervalMs the amount of MS until we want to send out a batch of data
     */
    @JvmField
    var maxBatchIntervalMs: Int
}
