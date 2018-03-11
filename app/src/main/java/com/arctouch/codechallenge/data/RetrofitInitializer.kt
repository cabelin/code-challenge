package com.arctouch.codechallenge.data

import com.arctouch.codechallenge.data.api.TmdbApi
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitInitializer {

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(TmdbApi.URL)
            .client(OkHttpClient.Builder().addInterceptor(this::tmdbApiKeyAndLanguageAndRegionInterceptor).build())
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    private fun tmdbApiKeyAndLanguageAndRegionInterceptor(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        TmdbApi.URL.contains(request.url().host()).let {
            val httpUrl: HttpUrl = request.url().newBuilder()
                    .addQueryParameter("api_key", TmdbApi.API_KEY)
                    .addQueryParameter("language", TmdbApi.DEFAULT_LANGUAGE)
                    .addQueryParameter("region", TmdbApi.DEFAULT_REGION)
                    .build()
            request = request.newBuilder().url(httpUrl).build()
        }
        return chain.proceed(request)
    }

    val tmdbApi: TmdbApi = retrofit.create(TmdbApi::class.java)

}