package com.arctouch.codechallenge.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.services.TmdbApiService
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.ui.BaseActivity
import com.arctouch.codechallenge.util.InfiniteScroll
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : BaseActivity() {

    private val apiService: TmdbApiService by lazy { TmdbApiService() }

    private val movies: MutableList<Movie> = mutableListOf()

    private lateinit var movieAdapter: MovieAdapter

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        configureRecyclerView()
        loadMovies(1)
    }

    private fun configureRecyclerView() {
        movieAdapter = MovieAdapter(movies = this.movies, itemClick = this::navigateToMovieDetails)
        recyclerView.adapter = movieAdapter

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.addOnScrollListener(InfiniteScroll(linearLayoutManager, this::loadMovies))
    }

    private fun loadMovies(page: Int) {
        val disposable = apiService.getUpcommingMovies(page = page.toLong())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::displayMovies)
        compositeDisposable.add(disposable)
    }

    private fun displayMovies(movies: List<Movie>) {
        movieAdapter.addAllMovies(movies)
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
