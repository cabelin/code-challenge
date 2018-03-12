package com.arctouch.codechallenge.ui.home

import android.util.Log
import com.arctouch.codechallenge.data.ServiceProvider
import com.arctouch.codechallenge.data.services.TmdbApiService
import com.arctouch.codechallenge.model.Movie
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HomePresenter(private val view: HomeContract.View): HomeContract.Presenter {

    private val apiService: TmdbApiService = ServiceProvider.provideTmdbApiService()
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    private var searchText: String = ""
    private var filtering: Boolean = false

    override fun loadMovies(page: Int) {
        val observable = if(filtering) apiService.getMoviesByName(this.searchText, page = page.toLong())
            else apiService.getUpcommingMovies(page = page.toLong())

        val disposable = observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(view::displayMovies)
        compositeDisposable.add(disposable)
    }

    override fun loadObservableMoviesByName(searchText: String): Observable<List<Movie>> {
        this.searchText = searchText
        return if(searchText.isEmpty()) {
            filtering = false
            apiService.getUpcommingMovies(page = 1)
        } else {
            filtering = true
            apiService.getMoviesByName(searchText)
        }
    }

    override fun destroyView() {
        compositeDisposable.clear()
    }
    
}