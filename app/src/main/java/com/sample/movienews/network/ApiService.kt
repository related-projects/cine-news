package com.sample.movienews.network

import com.sample.movienews.models.Movie
import com.sample.movienews.models.Response
import com.sample.movienews.models.Video
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

// TMDB api base url
private const val BASE_URL = "https://api.themoviedb.org/3/"

/**
 * Build the Moshi object with Kotlin adapter
 * factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [Movie] method
 */
interface ApiService {
    /**
     * Returns the [List] of [Movie] and
     * this method can be called from a Coroutine.
     * The @GET annotation indicates that the "movies"
     * endpoint will be requested with the GET HTTP method
     */
    @Headers(
        "Content-Type: application/json"
    )
    @GET("movie/{type}?api_key=aaaf8e545ab209765179446cd35b920f")
    suspend fun getMovies(
        @Path("type") type: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Response<Movie>

    /**
     * Returns the details of a [Movie] and
     * this method can be called from a Coroutine.
     * The @GET annotation indicates that the "movies"
     * endpoint will be requested with the GET HTTP method
     */
    @Headers(
        "Content-Type: application/json"
    )
    @GET("movie/{movie_id}?api_key=aaaf8e545ab209765179446cd35b920f")
    suspend fun getMovieDetails(
        @Path("movie_id") movie_id: String,
        @Query("language") language: String
    ): Movie

    /**
     * Returns the details of a [Movie] and
     * this method can be called from a Coroutine.
     * The @GET annotation indicates that the "movies"
     * endpoint will be requested with the GET HTTP method
     */
    @Headers(
        "Content-Type: application/json"
    )
    @GET("search/movie?api_key=aaaf8e545ab209765179446cd35b920f")
    suspend fun search(
        @Query("language") language: String,
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("include_adult") includeAdult: Boolean,
    ): Response<Movie>

    /**
     * Return the list of recommended movies for a movie
     */
    @Headers("Content-Type: application/json")
    @GET("movie/{movie_id}/recommendations?api_key=aaaf8e545ab209765179446cd35b920f")
    suspend fun recommendations(
        @Path("movie_id") movie_id: String,
        @Query("language") language: String,
        @Query("page") page: Int,
    ): Response<Movie>

    /**
     * Return the list of recommended movies for a movie
     */
    @Headers("Content-Type: application/json")
    @GET("movie/{movie_id}/videos?api_key=aaaf8e545ab209765179446cd35b920f")
    suspend fun videos(
        @Path("movie_id") movie_id: Int,
        @Query("language") language: String,
    ): Response<Video>
}

/**
 * A public Api object that exposes
 * the lazy-initialized Retrofit service
 */
object Api {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}