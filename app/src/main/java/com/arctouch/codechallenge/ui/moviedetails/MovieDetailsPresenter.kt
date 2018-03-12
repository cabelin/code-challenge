package com.arctouch.codechallenge.ui.moviedetails

import com.arctouch.codechallenge.data.ServiceProvider
import com.arctouch.codechallenge.data.services.TmdbApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDetailsPresenter(private val view: MovieDetailsContract.View): MovieDetailsContract.Presenter {

    private val apiService: TmdbApiService = ServiceProvider.provideTmdbApiService()
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun loadMovie(movieId: Long) {
        val disposable = apiService.getMovie(movieId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::displayMovieData)
        compositeDisposable.add(disposable)
    }

}