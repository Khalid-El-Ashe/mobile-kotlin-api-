package com.example.kotlinrestapi.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.example.kotlinrestapi.util.Constance.Companion.BASE_URL
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitInstance {
    // i need to make insialize for this lib retrofit, this object to see request and what the response are
    private val retrofit by lazy {
        // i need to called logging
        val logging = HttpLoggingInterceptor()
        // to see the bode of our response
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        // i need to make connection with internet
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        // i need to make retrofit instance
        Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
            .client(client).build()
    }

    // then i need to get our API instance
    val api by lazy {
        retrofit.create(NewsApi::class.java)
    }
}