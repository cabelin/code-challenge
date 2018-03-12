package com.arctouch.codechallenge.data

import com.arctouch.codechallenge.data.services.TmdbApiService

object ServiceProvider {

    private val tmdbApiService: TmdbApiService by lazy {
        TmdbApiService()
    }

    fun provideTmdbApiService(): TmdbApiService {
        return tmdbApiService
    }

}