package com.architect.kmpappinsights.response

import kotlinx.serialization.Serializable

@Serializable
data class TrackingApiResponse(val errors: List<ErrorType>)
@Serializable
data class ErrorType(val index: Int, val statusCode: Int, val message: String)