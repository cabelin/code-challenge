package com.arctouch.codechallenge.ui.home

import com.arctouch.codechallenge.model.Movie
import io.reactivex.Observable

interface HomeContract {

    interface View {
        fun displayMovies(movies: List<Movie>)
        fun clearAndDisplayMovies(movies: List<Movie>)
    }

    interface Presenter {
        fun loadMovies(page: Int = 1)
        fun destroyView()
        fun loadObservableMoviesByName(searchText: String): Observable<List<Movie>>
    }

}