package com.example.apod

import retrofit2.http.GET
import retrofit2.http.Query

interface ApodApi {
    @GET("planetary/apod")
    suspend fun getTodayImage(@Query("api_key") apiKey: String): ApodResponse
}

data class ApodResponse(
    val url: String,
    val hdurl: String?,
    val title: String,
    val explanation: String,
    val media_type: String
)