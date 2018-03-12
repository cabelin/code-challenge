package com.arctouch.codechallenge.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.MenuItem
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.ui.moviedetails.MovieDetailsActivity
import com.arctouch.codechallenge.util.RxSearchObservable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity() {

    protected fun enableDisplayHomeAsUp() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected open fun isHomeAsUpToBack(): Boolean {
        return false
    }

    protected fun navigateToMovieDetails(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MovieDetailsActivity.EXTRA_PARAM_ID, movie.id)

        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(isHomeAsUpToBack()) {
            when (item?.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    return true
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun <T> setupSearchListener(searchView: SearchView,
                                        loadObservableData: (searchText: String) -> Observable<T>,
                                        successData: (data: T) -> Unit): Disposable {
        return RxSearchObservable.view(searchView = searchView)
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .switchMap(loadObservableData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(successData)
    }

}
