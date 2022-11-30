package com.example.kotlinrestapi.repository

import com.example.kotlinrestapi.api.RetrofitInstance
import com.example.kotlinrestapi.db.ArticleDatabase
import com.example.kotlinrestapi.models.Article

class NewsRepository(val db: ArticleDatabase) {

    // this function to get our news breaking from API
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance().api.getBreakingNews(countryCode, pageNumber)

    // i need to make function to call the api search function
    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance().api.searchBreakingNews(searchQuery, pageNumber)

    // this function to save some Articles
    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSaveNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}