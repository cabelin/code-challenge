package com.arctouch.codechallenge.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.ui.BaseActivity
import com.arctouch.codechallenge.util.InfiniteScroll
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity: BaseActivity(), HomeContract.View {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var searchView: SearchView

    private lateinit var presenter: HomeContract.Presenter

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        configureRecyclerView()
        presenter = HomePresenter(this)
        presenter.loadMovies()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)

        val search: MenuItem? = menu?.findItem(R.id.action_search)
        searchView = search?.actionView as SearchView
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        val disposable = setupSearchListener(searchView,
            presenter::loadObservableMoviesByName,
            this::clearAndDisplayMovies)
        compositeDisposable.add(disposable)

        return true
    }

    private fun configureRecyclerView() {
        movieAdapter = MovieAdapter(movies = mutableListOf(), itemClick = this::navigateToMovieDetails)
        recyclerView.adapter = movieAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        resetRecyclerViewScroll()
    }

    private fun resetRecyclerViewScroll() {
        recyclerView.clearOnScrollListeners()
        recyclerView.addOnScrollListener(InfiniteScroll(recyclerView.layoutManager as LinearLayoutManager, {
            presenter.loadMovies(page = it)
        }))
    }

    override fun displayMovies(movies: List<Movie>) {
        movieAdapter.addAllMovies(movies)
        progressBar.visibility = View.GONE
    }

    override fun clearAndDisplayMovies(movies: List<Movie>) {
        movieAdapter.removeAllMovies()
        resetRecyclerViewScroll()
        movieAdapter.addAllMovies(movies)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        presenter.destroyView()
    }
}
