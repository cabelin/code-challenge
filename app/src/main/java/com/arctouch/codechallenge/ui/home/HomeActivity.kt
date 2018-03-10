package com.arctouch.codechallenge.ui.home

import android.os.Bundle
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.services.TmdbApiService
import com.arctouch.codechallenge.ui.BaseActivity
import com.arctouch.codechallenge.model.Movie
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.home_activity.*

class HomeActivity : BaseActivity() {

    private val apiService: TmdbApiService by lazy { TmdbApiService() }

    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        loadMovies()
    }

    private fun loadMovies() {
        val disposable = apiService.getUpcommingMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::displayMovies)
        compositeDisposable.add(disposable)
    }

    private fun displayMovies(movies: List<Movie>) {
        recyclerView.adapter = HomeAdapter(movies)
        progressBar.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
