package com.sample.movienews.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.sample.movienews.R
import com.sample.movienews.database.MovieDao
import com.sample.movienews.models.Movie
import com.sample.movienews.models.Video
import com.sample.movienews.network.Api
import com.sample.movienews.utils.Constant.DATE_FORMAT
import com.sample.movienews.utils.Constant.POPULAR_MOVIE
import com.sample.movienews.utils.Constant.TOP_RATED_MOVIE
import com.sample.movienews.utils.Constant.UP_COMING_MOVIE
import com.sample.movienews.utils.Utils.fromMinutesToHHmm
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class RequestsStatus { LOADING, ERROR, DONE }

class MainViewModel(private var movieDao: MovieDao) : ViewModel() {

    private val _activityTitle = MutableLiveData<String>()
    var activityTitle: LiveData<String> = _activityTitle

    private val _moviesTotalPages = MutableLiveData<Int>()
    var moviesTotalPages: LiveData<Int> = _moviesTotalPages

    private val _movieDuration = MutableLiveData<String>()
    var movieDuration: LiveData<String> = _movieDuration

    // The internal MutableLiveData that stores
    // the status of the most recent movies request
    private val _moviesStatus = MutableLiveData<RequestsStatus>()
    // The external immutable LiveData for movies request status
    var moviesStatus: LiveData<RequestsStatus> = _moviesStatus
    // The internal MutableLiveData that stores the
    // movies[Movie] of the most recent request
    private val _movies = MutableLiveData<List<Movie>>()
    var movies: LiveData<List<Movie>> = _movies

    // The internal MutableLiveData that stores the
    // movie[Movie] of the most recent request
    private val _movie = MutableLiveData<Movie>()
    var movie: LiveData<Movie> = _movie

    // The internal MutableLiveData that stores
    // the status of the most recent up coming movies request
    private val _upComingMoviesStatus = MutableLiveData<RequestsStatus>()
    // The external immutable LiveData for up coming movies request status
    var upComingMoviesStatus: LiveData<RequestsStatus> = _upComingMoviesStatus
    // The internal MutableLiveData that stores the up
    // coming movies[Movie] of the most recent request
    private val _upComingMovies = MutableLiveData<List<Movie>>()
    var upComingMovies = _upComingMovies


    // The internal MutableLiveData that stores
    // the status of the most recent up coming movies request
    private val _popularMoviesStatus = MutableLiveData<RequestsStatus>()
    // The external immutable LiveData for up coming movies request status
    var popularMoviesStatus: LiveData<RequestsStatus> = _popularMoviesStatus
    // The internal MutableLiveData that stores the up
    // coming movies[Movie] of the most recent request
    private val _popularMovies = MutableLiveData<List<Movie>>()
    var popularMovies = _popularMovies


    // The internal MutableLiveData that stores
    // the status of the most recent up coming movies request
    private val _topRatedMoviesStatus = MutableLiveData<RequestsStatus>()
    // The external immutable LiveData for up coming movies request status
    var topRatedMoviesStatus: LiveData<RequestsStatus> = _topRatedMoviesStatus
    // The internal MutableLiveData that stores the up
    // coming movies[Movie] of the most recent request
    private val _topRatedMovies = MutableLiveData<List<Movie>>()
    var topRatedMovies = _topRatedMovies

    // Internal mutableLiveData that stores the status of searched movie
    private val _searchedMovieStatus = MutableLiveData<RequestsStatus>()
    // External immutableLiveData that stores the status
    var searchedMovieStatus: LiveData<RequestsStatus> = _searchedMovieStatus

    // Internal mutableLiveData that stores the list of movies
    private val _searchMovie = MutableLiveData<List<Movie>>()
    // External immutableLiveData that stores the list of movies
    var searchMovie = _searchMovie
    // Internal mutableLiveData that stores the total pages count of the list of movies
    private val _searchedMoviesTotalPages = MutableLiveData<Int>()
    // External mutableLiveData that stores the total pages count of the list of movies
    var searchedMoviesTotalPages = _searchedMoviesTotalPages

    // Internal mutableLiveData that stores the current pages count of the list of movies
    private val _searchedMoviesCurrentPage = MutableLiveData<Int>()
    // External mutableLiveData that stores the current pages count of the list of movies
    var searchedMoviesCurrentPage = _searchedMoviesCurrentPage

    // Internal mutableLiveData that retrieves the list of favorite movies
    lateinit var favoriteMovies: LiveData<List<Movie>>

    // The internal MutableLiveData that stores the status of favorite movies
    private val _favoriteMoviesStatus = MutableLiveData<RequestsStatus>()
    // The external immutable LiveData for favorite movies request status
    var favoriteMoviesStatus: LiveData<RequestsStatus> = _favoriteMoviesStatus

    // The internal MutableLiveData that stores the status of recommended movies
    private val _recommendedMoviesStatus = MutableLiveData<RequestsStatus>()
    // The external immutable LiveData for recommended movies request status
    var recommendedMoviesStatus: LiveData<RequestsStatus> = _recommendedMoviesStatus
    // Internal mutableLiveData that stores the list of recommended movies
    private val _recommendedMovies = MutableLiveData<List<Movie>>()
    // External immutableLiveData that stores the list of recommended movies
    var recommendedMovies = _recommendedMovies

    var officialVideo: MutableLiveData<Video> = MutableLiveData()

    fun initActivityTitle(context: Context, type: String) {
        when(type) {
            POPULAR_MOVIE -> {
                _activityTitle.value = context.getString(
                    R.string.popular_movies_section_title
                )
            }
            UP_COMING_MOVIE -> {
                _activityTitle.value = context.getString(
                    R.string.upcoming_movies_section_title
                )
            }
            TOP_RATED_MOVIE -> {
                _activityTitle.value = context.getString(
                    R.string.top_rated_movies_section_title
                )
            }
            else ->
                _activityTitle.value = type
        }
    }

    /**
     * Get a movie details from the API retrofit
     * service and updates the movie details screen
     */
    fun fetchMovie(id: String, languageCode: String) {
        _moviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                val movie = Api.retrofitService
                    .getMovieDetails(id, languageCode)

                val localMovie = movieDao.getMovie(id.toInt())

                if (localMovie != null) { // It is a favorite movie
                    movie.favorite = true
                    // Update the favorite movie
                    movieDao.update(movie)
                }

                // Display the movie
                displayMovieDetails(movie)

            } catch (e: Exception) {
                // Read the movie from local DB if its exist
                getMovieById(id.toInt())

                Log.e("fetch_movie_details", "${e.message}")
            }
        }
    }

    /**
     * Update movie details UI
     */
    private fun displayMovieDetails(movie: Movie?) {
        try {
            if (movie != null) {
                movie.let {
                    _movie.value = it
                }
                _movieDuration.value = fromMinutesToHHmm(movie.runtime!!)
                _moviesStatus.value = RequestsStatus.DONE
            } else {
                _moviesStatus.value = RequestsStatus.ERROR
            }
        } catch (e: Exception) {
            Log.e("displayMovieDetails", "${e.message}")
        }
    }

    /**
     * Gets movies information from the API Retrofit service and updates the
     * movies [List] [LiveData].
     */
    fun fetchMovies(type: String, languageCode: String, page: Int, shouldFilter: Boolean = false) {
        _moviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                val response = Api.retrofitService
                    .getMovies(type, languageCode, page)

                val list: MutableList<Movie>

                if (page == 1) {
                    list = response.results as MutableList<Movie>
                } else {
                    list = _movies.value?.toMutableList()!!
                    list.addAll(response.results ?: listOf())
                    _movies.value = list
                }

                _movies.value = if (shouldFilter) {
                    list.filter {
                        Date().before(
                            SimpleDateFormat(DATE_FORMAT, Locale.ROOT)
                                .parse(it.releaseDate ?: "")
                        )
                    }
                } else {
                    list
                }
                _searchedMoviesCurrentPage.value = response.currentPage ?: 1
                _moviesTotalPages.value = response.totalPages ?: 1
                _moviesStatus.value = RequestsStatus.DONE
            } catch (e: Exception) {
                _moviesStatus.value = RequestsStatus.ERROR
                _searchedMoviesCurrentPage.value = 1
                _moviesTotalPages.value = 1
                _movies.value = listOf()
                Log.e("Movies", "${e.cause?.message}")
            }
        }
    }

    /**
     * Gets movies information from the API Retrofit service and updates the
     * up coming movies [List] [LiveData].
     */
    fun fetchUpComingMovies(type: String, languageCode: String) {
        _upComingMoviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                _upComingMovies.value = Api.retrofitService
                    .getMovies(type, languageCode, 1)
                    .results?.filter {
                        Date().before(
                            SimpleDateFormat(DATE_FORMAT, Locale.ROOT)
                                .parse(it.releaseDate ?: "")
                        )
                    } ?: listOf()
                _upComingMoviesStatus.value = RequestsStatus.DONE
            } catch (e: Exception) {
                _upComingMoviesStatus.value = RequestsStatus.ERROR
                _upComingMovies.value = listOf()
                Log.e("getUpComingMovies", "${e.message}")
            }
        }
    }

    /**
     * Gets movies information from the API Retrofit service and updates the
     * popular movies [List] [LiveData].
     */
    fun fetchPopularMovies(type: String, languageCode: String) {
        _popularMoviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                _popularMovies.value = Api.retrofitService
                    .getMovies(type, languageCode, 1)
                    .results ?: listOf()
                _popularMoviesStatus.value = RequestsStatus.DONE
            } catch (e: Exception) {
                _popularMoviesStatus.value = RequestsStatus.ERROR
                _popularMovies.value = listOf()
                Log.e("getPopularMovies", "${e.message}")
            }
        }
    }

    /**
     * Gets movies information from the API Retrofit service and updates the
     * top rated movies [List] [LiveData].
     */
    fun fetchTopRatedMovies(type: String, languageCode: String) {
        _topRatedMoviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                _topRatedMovies.value = Api.retrofitService
                    .getMovies(type, languageCode, 1)
                    .results ?: listOf()
                _topRatedMoviesStatus.value = RequestsStatus.DONE
            } catch (e: Exception) {
                _topRatedMoviesStatus.value = RequestsStatus.ERROR
                _topRatedMovies.value = listOf()
                Log.e("getTopRatedMovies", "${e.message}")
            }
        }
    }

    /**
     * Search for a specific movie based on its name
     */
    fun searchMovie(languageCode: String, query: String,
                    page: Int, includeAdult: Boolean = false) {

        _searchedMovieStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                if (query.isNotEmpty()) {
                    val response = Api.retrofitService
                        .search(
                            languageCode,
                            query,
                            page,
                            includeAdult
                        )

                    val list: MutableList<Movie>

                    if (page == 1) {
                        _searchMovie.value = response.results ?: listOf()
                    } else {
                        list = _searchMovie.value?.toMutableList()!!
                        list.addAll(response.results ?: listOf())
                        _searchMovie.value = list
                    }

                    _searchedMoviesCurrentPage.value = response.currentPage ?: 1
                    _searchedMoviesTotalPages.value = response.totalPages ?: 1
                    _searchedMovieStatus.value = RequestsStatus.DONE

                } else {
                    _searchMovie.value = listOf()
                    _searchedMoviesCurrentPage.value = 1
                    _searchedMoviesTotalPages.value = 1
                    _searchedMovieStatus.value = RequestsStatus.DONE
                }
            } catch (e: Exception) {
                _searchedMovieStatus.value = RequestsStatus.ERROR
                _searchedMoviesCurrentPage.value = 1
                _searchedMoviesTotalPages.value = 1
                _searchMovie.value = listOf()
                Log.e("searchedMovie", "${e.message}")
            }
        }
    }

    fun saveFavoriteMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                movieDao.insert(movie)
            } catch (e: Exception) {
                Log.e("saveFavoriteMovie", "${e.message}")
            }
        }
    }

    fun deleteFavoriteMovie(movie: Movie) {
        viewModelScope.launch {
            try {
                movieDao.delete(movie)
            } catch (e: Exception) {
                Log.e("removeFavoriteMovie", "${e.message}")
            }
        }
    }

    fun getListOfFavoriteMovies() {
        _favoriteMoviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                favoriteMovies = movieDao.getMovies().asLiveData()
                _favoriteMoviesStatus.value = RequestsStatus.DONE
            } catch (e: Exception) {
                _favoriteMoviesStatus.value = RequestsStatus.ERROR
                Log.e("getFavoriteMovies", "${e.message}")
            }
        }
    }

    private fun getMovieById(mdbId: Int) {
        viewModelScope.launch {
            try {
                val movie = movieDao.getMovie(mdbId)
                displayMovieDetails(movie)
            } catch (e: Exception) {
                Log.e("getMovieById", "${e.message}")
            }
        }
    }

    fun getRecommendedMovies(id: Int, languageCode: String, page: Int = 1) {
        _recommendedMoviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                val recommendations = Api.retrofitService
                    .recommendations(
                        "$id",
                        languageCode,
                        page
                    ).results

                _recommendedMovies.value = recommendations ?: listOf()
                _recommendedMoviesStatus.value = RequestsStatus.DONE
            } catch (e: Exception) {
                recommendedMovies.value = listOf()
                _recommendedMoviesStatus.value = RequestsStatus.ERROR
                Log.e("getRecommendedMovies", "${e.message}")
            }
        }
    }

    fun getMovieVideos(id: Int?, languageCode: String) {
        _moviesStatus.value = RequestsStatus.LOADING
        viewModelScope.launch {
            try {
                val videos = Api.retrofitService
                    .videos(id!!, languageCode).results

                _moviesStatus.value = RequestsStatus.DONE

                officialVideo.postValue(videos?.find { it.official == true })

            } catch (e: Exception) {
                _moviesStatus.value = RequestsStatus.DONE
                Log.e("getMovieVideos", "${e.message}")
            }
        }
    }
}

/**
 * Create a view model factory that takes a MovieDao as a property and
 * creates a MainViewModel
 */
@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(private val movieDao: MovieDao): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(movieDao) as T
        }

        throw IllegalArgumentException("Unknown viewModel class")
    }

}