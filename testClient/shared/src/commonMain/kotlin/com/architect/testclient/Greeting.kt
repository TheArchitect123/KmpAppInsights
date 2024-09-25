package com.architect.testclient
import com.architect.kmpappinsights
class Greeting {
    private val platform: Platform = getPlatform()

    fun greet(): String {

        InsightsClient
        return "Hello, ${platform.name}!"
    }
}