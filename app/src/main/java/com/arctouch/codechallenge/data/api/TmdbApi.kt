package com.arctouch.codechallenge.data.api

import com.arctouch.codechallenge.model.GenreResponse
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.model.SearchMoviesResponse
import com.arctouch.codechallenge.model.UpcomingMoviesResponse
import io.reactivex.Observable
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    companion object {
        private const val URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "1f54bd990f1cdfb230adb312546d765d"
        private const val DEFAULT_LANGUAGE = "pt-BR"
        private const val DEFAULT_REGION = "BR"

        fun create(): TmdbApi {
            val okHttpClient: OkHttpClient = OkHttpClient.Builder()
                .addInterceptor({
                    var request: Request = it.request()
                    val httpUrl: HttpUrl = request.url().newBuilder()
                        .addQueryParameter("api_key", TmdbApi.API_KEY)
                        .addQueryParameter("language", TmdbApi.DEFAULT_LANGUAGE)
                        .addQueryParameter("region", TmdbApi.DEFAULT_REGION)
                        .build()
                    request = request.newBuilder().url(httpUrl).build()
                    it.proceed(request)
                })
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(TmdbApi.URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

            return retrofit.create(TmdbApi::class.java)
        }
    }

    @GET("genre/movie/list")
    fun genres(): Observable<GenreResponse>

    @GET("movie/upcoming")
    fun upcomingMovies(
        @Query("page") page: Long
    ): Observable<UpcomingMoviesResponse>

    @GET("search/movie")
    fun searchMovies(
            @Query("query") query: String,
            @Query("page") page: Long
    ): Observable<SearchMoviesResponse>

    @GET("movie/{id}")
    fun movie(
        @Path("id") id: Long
    ): Observable<Movie>

}
