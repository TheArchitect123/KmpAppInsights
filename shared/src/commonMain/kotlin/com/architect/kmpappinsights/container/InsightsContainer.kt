package com.architect.kmpappinsights.container

import com.architect.kmpappinsights.AppInsightsTrackingService
import com.architect.kmpappinsights.InsightsLogger
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal object InsightsContainer {
    const val eventBaseType = "Microsoft.ApplicationInsights.Event"
    var instrumentationKey = ""
    private const val baseUrl = "dc.services.visualstudio.com"
    val interopLogger = InsightsLogger()

    private val ktorFitClient by lazy {
        val httpClient = HttpClient {
            install(Logging) {
                level = LogLevel.ALL
                logger = InsightsLogger()
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowSpecialFloatingPointValues = true
                    allowStructuredMapKeys = true
                    explicitNulls = true
                })
            }
            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                url {
                    protocol = URLProtocol.HTTPS
                    host = baseUrl
                }
            }
        }

        Ktorfit.Builder().httpClient(httpClient).build()
    }

    val appInsightsHttpService by lazy {
        ktorFitClient.create<AppInsightsTrackingService>()
    }
}
