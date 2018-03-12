package com.arctouch.codechallenge.ui.moviedetails

import android.os.Bundle
import android.view.View
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.ui.BaseActivity
import com.arctouch.codechallenge.util.MovieImageUrlBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.movie_details_activity.*

class MovieDetailsActivity : BaseActivity(), MovieDetailsContract.View {

    companion object {
        const val EXTRA_PARAM_ID = "movie:_id"
    }

    private lateinit var presenter: MovieDetailsContract.Presenter

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.movie_details_activity)

        enableDisplayHomeAsUp()

        val movieId = intent.getLongExtra(EXTRA_PARAM_ID, 0)

        presenter = MovieDetailsPresenter(this)
        presenter.loadMovie(movieId)
    }

    override fun isHomeAsUpToBack(): Boolean {
        return true
    }

    override fun displayMovieData(movie: Movie) {
        this.movie = movie

        titleTextView.text = movie.title
        genresTextView.text = movie.genres?.joinToString(separator = ", ") { it.name }
        releaseDateTextView.text = movie.releaseDate
        overviewTextView.text = movie.overview

        val movieImageUrlBuilder = MovieImageUrlBuilder()

        val requestManager: RequestManager = Glide.with(this)

        if(movie.backdropPath != null) {
            requestManager
                .load(movieImageUrlBuilder.buildBackdropUrl(movie.backdropPath))
                .into(backdropImageView)
        } else {
            backdropImageView.visibility = View.GONE
        }

        requestManager
            .load(movie.posterPath?.let { movieImageUrlBuilder.buildPosterUrl(it) })
            .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
            .into(posterImageView)

        progressBar.visibility = View.GONE
    }

}
