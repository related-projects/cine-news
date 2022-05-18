package com.sample.movienews.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.ActivityMoviesBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant.MOVIES_TYPE_KEY
import com.sample.movienews.utils.Constant.MOVIE_ID_KEY
import com.sample.movienews.utils.Constant.UP_COMING_MOVIE
import com.sample.movienews.utils.Utils.getDefaultLanguageCode
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory


class MoviesActivity : AppCompatActivity(), MoviesAdapter.OnMovieClickedListener {

    // Binding object instance corresponding to the activity_nav_movie.xml layout
    // when the view hierarchy is attached to the fragment.
    private lateinit var binding: ActivityMoviesBinding
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            (application as BaseApp).database.getMovieDao()
        )
    }
    private var moviesType: String = ""
    private var totalPages = 1
    private var currentPage = 1
    private var isLastItemVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object instance
        binding = ActivityMoviesBinding.inflate(layoutInflater)

        // Set the activity content view
        setContentView(binding.root)

        init()
        initListener()
    }

    private fun init() {
        try {
            // Get intent
            moviesType = intent.extras?.get(MOVIES_TYPE_KEY) as String

            // Allows Data Binding to Observe LiveData
            // with the lifecycle of this Fragment
            binding.lifecycleOwner = this@MoviesActivity

            // Giving the binding access to the MainViewModel
            binding.viewModel = mainViewModel

            // Initialize adapters
            binding.movies.adapter = MoviesAdapter(this)

            // Initialize activity appBar title
            mainViewModel.initActivityTitle(this, moviesType)

            // Request the list of movie based on type
            mainViewModel.fetchMovies(
                moviesType,
                getDefaultLanguageCode(),
                currentPage,
                moviesType == UP_COMING_MOVIE
            )
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {
            binding.topAppBar.setNavigationOnClickListener {
                this@MoviesActivity.onBackPressed()
            }

            binding.movies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = binding.movies.layoutManager as GridLayoutManager
                    val totalItemCount = layoutManager.itemCount
                    val lastVisible = layoutManager.findLastVisibleItemPosition()
                    val endHasBeenReached = lastVisible + 5 >= totalItemCount

                    if (totalItemCount > 0 && endHasBeenReached) {
                        // You have reached to the bottom of your recycler view
                            // Request the list of movie based on type
                        if (currentPage < totalPages && !isLastItemVisible) {

                            isLastItemVisible = true

                            mainViewModel.fetchMovies(
                                moviesType,
                                getDefaultLanguageCode(),
                                currentPage + 1,
                                moviesType == UP_COMING_MOVIE
                            )
                        }
                    }
                }
            })

            mainViewModel.searchedMoviesCurrentPage.observe(this) {
                currentPage = it
            }

            mainViewModel.moviesTotalPages.observe(this) {
                totalPages = it
                isLastItemVisible = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(MOVIE_ID_KEY, "${movie.id}")
        startActivityIfNeeded(intent, MovieDetailsActivity.REQUEST_CODE)
    }

    companion object {
        const val REQUEST_CODE = 2
        private val TAG = MoviesActivity::class.java.simpleName
    }
}