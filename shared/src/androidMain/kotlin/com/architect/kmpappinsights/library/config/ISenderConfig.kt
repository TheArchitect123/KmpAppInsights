package com.architect.kmpappinsights.library.config

interface ISenderConfig {
    /**
     * Gets the url to which payloads will be sent
     *
     * @return the server's endpoint URL
     */
    /**
     * Sets the url to which payloads will be sent
     *
     * @param endpointUrl url of the server that receives our data
     */
    @JvmField
    var endpointUrl: String?

    /**
     * Gets the timeout for reading the response from the data collector endpoint
     *
     * @return configured timeout in ms for reading
     */
    /**
     * Set the timeout for reading the response from the data collector endpoint
     */
    @JvmField
    var senderReadTimeout: Int

    /**
     * Gets the timeout for connecting to the data collector endpoint
     *
     * @return configured timeout in ms for sending
     */
    /**
     * Set the timeout for connecting to the data collector endpoint
     */
    @JvmField
    var senderConnectTimeout: Int
}
