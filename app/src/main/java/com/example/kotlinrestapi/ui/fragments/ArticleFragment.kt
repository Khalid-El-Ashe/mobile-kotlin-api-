package com.example.kotlinrestapi.ui.fragments

import android.app.Application
import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.kotlinrestapi.R
import com.example.kotlinrestapi.db.ArticleDatabase
import com.example.kotlinrestapi.models.Article
import com.example.kotlinrestapi.repository.NewsRepository
import com.example.kotlinrestapi.ui.NewsActivity
import com.example.kotlinrestapi.ui.NewsViewModel
import com.example.kotlinrestapi.ui.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsViewModel

    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelFactory = NewsViewModelProviderFactory(Application(), newsRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)

        // i need to get my articles
        val article = args.article

        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url.toString())
        }

        fab.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }
}