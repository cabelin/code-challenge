package com.arctouch.codechallenge.ui.moviedetails

import com.arctouch.codechallenge.model.Movie

class MovieDetailsContract {

    interface View {
        fun displayMovieData(movie: Movie)
    }

    interface Presenter {
        fun loadMovie(movieId: Long)
    }

}