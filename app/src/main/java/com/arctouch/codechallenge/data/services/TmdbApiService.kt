package com.arctouch.codechallenge.data.services

import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.data.Cache.genres
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.model.Movie
import io.reactivex.Observable

class TmdbApiService {

    private val tmdbApi: TmdbApi = TmdbApi.create()

    private var loadedGenres = false

    private fun getGenres(): Observable<List<Genre>> {
        return if(loadedGenres) {
            Observable.just(Cache.genres)
        } else {
            tmdbApi.genres()
                .map { it.genres }
                .doAfterNext { genres
                    Cache.cacheGenres(genres)
                    loadedGenres = true
                }
        }
    }

    fun getUpcommingMovies(page: Long = 1): Observable<List<Movie>> {
        return getGenres()
            .flatMap { genres ->
                tmdbApi.upcomingMovies(page)
                    .map {
                        it.results.map { movie ->
                            movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
                        }
                    }
            }
    }

    fun getMovie(movieId: Long): Observable<Movie> {
        return tmdbApi.movie(movieId)
    }

    fun getMoviesByName(movieName: String, page: Long = 1): Observable<List<Movie>> {
        return getGenres()
            .flatMap { genres ->
                tmdbApi.searchMovies(movieName, page)
                    .map {
                        it.results.map { movie ->
                            movie.copy(genres = genres.filter { movie.genreIds?.contains(it.id) == true })
                        }
                    }
            }
    }

}
