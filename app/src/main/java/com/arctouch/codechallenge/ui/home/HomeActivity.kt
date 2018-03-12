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
import com.arctouch.codechallenge.data.ServiceProvider
import com.arctouch.codechallenge.data.services.TmdbApiService
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.ui.BaseActivity
import com.arctouch.codechallenge.util.InfiniteScroll
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*


class HomeActivity : BaseActivity() {

    private val apiService: TmdbApiService by lazy {
        ServiceProvider.provideTmdbApiService()
    }

    private val movies: MutableList<Movie> = mutableListOf()

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var searchView: SearchView

    private var searchFiltered: Boolean = false

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        configureRecyclerView()
        loadMovies()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)

        val search: MenuItem? = menu?.findItem(R.id.action_search)
        searchView = search?.actionView as SearchView
        val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        val disposable = setupSearchListener(searchView,
            this::loadObservableMoviesByName,
            this::clearAndDisplayMovies)
        compositeDisposable.add(disposable)

        return true
    }

    private fun configureRecyclerView() {
        movieAdapter = MovieAdapter(movies = this.movies, itemClick = this::navigateToMovieDetails)
        recyclerView.adapter = movieAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addOnScrollListener(InfiniteScroll(linearLayoutManager, this::loadMovies))
    }

    private fun loadObservableMoviesByName(movieName: String): Observable<List<Movie>> {
        return if(movieName.isEmpty()) {
            searchFiltered = false
            apiService.getUpcommingMovies(page = 1)
        } else {
            searchFiltered = true
            apiService.getMoviesByName(movieName)
        }
    }

    private fun loadMovies(page: Int = 1) {
        val observable = when {
            searchFiltered -> apiService.getMoviesByName(searchView.query.toString(), page = page.toLong())
            else -> apiService.getUpcommingMovies(page = page.toLong())
        }
        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::displayMovies)
        compositeDisposable.add(disposable)
    }

    private fun displayMovies(movies: List<Movie>) {
        movieAdapter.addAllMovies(movies)
        progressBar.visibility = View.GONE
    }

    private fun clearAndDisplayMovies(movies: List<Movie>) {
        movieAdapter.removeAllMovies()
        movieAdapter.addAllMovies(movies)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
