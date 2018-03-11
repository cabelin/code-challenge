package com.arctouch.codechallenge.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.ui.moviedetails.MovieDetailsActivity

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



}
