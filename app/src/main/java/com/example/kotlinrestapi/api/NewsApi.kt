package com.example.kotlinrestapi.api

import com.example.kotlinrestapi.models.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.kotlinrestapi.util.Constance.Companion.API_KEY
import retrofit2.Response

interface NewsApi {

    // in here i need to make handling to my response to get some data api

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "us",
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchBreakingNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsResponse>
}