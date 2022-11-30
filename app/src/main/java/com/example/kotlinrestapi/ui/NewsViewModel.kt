package com.example.kotlinrestapi.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinrestapi.NewsApplication
import com.example.kotlinrestapi.models.Article
import com.example.kotlinrestapi.models.NewsResponse
import com.example.kotlinrestapi.repository.NewsRepository
import com.example.kotlinrestapi.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

// this is class our connection with view ( activity or fragment )
class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponse? = null

    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

    init {
        getBreakingNews("us")
//        searchNews(SearchNewsFragment::etSearch.toString())
    }

    // this function to API call from the repository
    // viewModelScope.launch -> this to stays only as long as our view model is alive
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))
//        safeBreakingNewsCall(countryCode) // i need to use function safe internet
    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())

        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
//        safeSearchNewsCall(searchQuery) // i need to use function safe internet
    }

    // i need to make handling to get response API data
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        // i need to check the response and the body response
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                breakingNewsPage++ // i need to get next pages from api
                if (breakingNewsResponse == null) {
                    breakingNewsResponse = resultResponse
                } else {
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = resultResponse.articles

                    oldArticle?.addAll(newArticle) // i need to add the new data in the old data
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // i need to make handling to get response API data
    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        // i need to check the response and the body response
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                searchNewsPage++ // i need to add more pages from api
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticle = searchNewsResponse?.articles
                    val newArticle = resultResponse.articles

                    oldArticle?.addAll(newArticle) // i need to add the new data in the old data
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // i need to make function to save some news in database
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    // i need to get all Articles or news i saved it
    fun getSaveNews() = newsRepository.getSaveNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    // this is to test the internet for breaking news activity
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            // if i have internet connection i need to do this code els not have do this
            if (hasInternetConnection()) {
                // i need to get the data when i have internet
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                // if i don't have internet do this code
                breakingNews.postValue(Resource.Error("No internet connection !"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // this is to test the internet for search news activity
    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            // if i have internet connection i need to do this code els not have do this
            if (hasInternetConnection()) {
                // i need to get search data when i have internet
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                // if i don't have internet do this code
                searchNews.postValue(Resource.Error("No internet connection !"))
            }
        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    // i need to create function check the connect internet
    private fun hasInternetConnection(): Boolean {
        // i need to check system service if the user connected to the internet
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        // i need to check the sdk version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // ?: this is do like if and meaning if that is null
            val activityNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activityNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true // in here meaning i have internet connection
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true // in here meaning i have internet connection
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true // in here meaning i have internet connection

                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}