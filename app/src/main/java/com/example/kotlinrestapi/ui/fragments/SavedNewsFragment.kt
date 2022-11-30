package com.example.kotlinrestapi.ui.fragments

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinrestapi.R
import com.example.kotlinrestapi.adapter.NewsAdapter
import com.example.kotlinrestapi.db.ArticleDatabase
import com.example.kotlinrestapi.repository.NewsRepository
import com.example.kotlinrestapi.ui.NewsViewModel
import com.example.kotlinrestapi.ui.NewsViewModelProviderFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saved_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsRepository = NewsRepository(ArticleDatabase(requireContext()))
        val viewModelFactory = NewsViewModelProviderFactory(Application(), newsRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            // i need to click and share the data with navigation component
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment2_to_articleFragment,
                bundle
            )
        }

        // i need to make swipe ( left or right or up or down , you can to chose what you need ) to delete article saved
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // in here i need to get the position of item delete need
                val position = viewHolder.adapterPosition
                val article =
                    newsAdapter.differ.currentList[position] // i need to get the index of article
                viewModel.deleteArticle(article)

                Snackbar.make(view, "Successfully delete article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        // i need to enable the TouchHelper Deleted
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }

        // i need to get the data saved
        viewModel.getSaveNews().observe(viewLifecycleOwner, Observer { articles ->
            newsAdapter.differ.submitList(articles) // i need to add the articles list in the adapter
        })
    }

    // setup recycler view
    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}