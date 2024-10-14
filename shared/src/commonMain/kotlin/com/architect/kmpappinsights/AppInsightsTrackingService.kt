package com.architect.kmpappinsights

import com.architect.kmpappinsights.contracts.AvailabilityEvent
import com.architect.kmpappinsights.contracts.CustomEvent
import com.architect.kmpappinsights.contracts.DependencyEvent
import com.architect.kmpappinsights.contracts.ExceptionEvent
import com.architect.kmpappinsights.contracts.PageEvent
import com.architect.kmpappinsights.contracts.RequestEvent
import com.architect.kmpappinsights.contracts.TraceEvent
import com.architect.kmpappinsights.response.TrackingApiResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

internal interface AppInsightsTrackingService {
    @POST("/v2/track")
    suspend fun postCustomEvent(@Body customEvent: CustomEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postRequestEvent(@Body customEvent: RequestEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postTraceEvent(@Body customEvent: TraceEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postExceptionEvent(@Body customEvent: ExceptionEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postPageEvent(@Body customEvent: PageEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postAvailabilityEvent(@Body customEvent: AvailabilityEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postDependencyEvent(@Body customEvent: DependencyEvent): TrackingApiResponse

    @POST("/v2/track")
    suspend fun postBatchJson(@Body customJson: String) : TrackingApiResponse
}

