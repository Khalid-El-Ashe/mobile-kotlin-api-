package com.example.kotlinrestapi.util

import com.example.kotlinrestapi.models.NewsResponse

// this class to tell me whether that answer if the request is success or error or loading
// the sealed is the kind of an abstract class and to make define which different classes
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}