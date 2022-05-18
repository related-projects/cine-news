package com.sample.movienews.ui.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.sample.movienews.R
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.ActivityMovieDetailsBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant
import com.sample.movienews.utils.Constant.COLLAPSE_MAX_LINES
import com.sample.movienews.utils.Constant.VIMEO_VIDEO
import com.sample.movienews.utils.Constant.YOUTUBE_VIDEO
import com.sample.movienews.utils.Utils
import com.sample.movienews.utils.canBeExpanded
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory


class MovieDetailsActivity : AppCompatActivity(), MoviesAdapter.OnMovieClickedListener {

    private lateinit var binding: ActivityMovieDetailsBinding

    private lateinit var currentMovie: Movie

    private val sharedViewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            (application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var movieID: String

    private var menuItem: MenuItem? = null

    private var isOverViewCollapse = true

    private lateinit var videoName: String
    private lateinit var videoLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize binding object instance
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)

        // Define activity view content
        setContentView(binding.root)

        // Initialize activity toolbar
        setSupportActionBar(binding.topAppBar)

        init()
        initListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.movie_detail_bar_menu, menu)
        menuItem = menu?.findItem(R.id.action_add_favorite)
        menuItem?.setShowAsAction(SHOW_AS_ACTION_IF_ROOM)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_add_favorite) {

            currentMovie.favorite = !currentMovie.favorite!!

            if (currentMovie.favorite!!) {
                // Add movie to database
                sharedViewModel.saveFavoriteMovie(currentMovie)
            } else {
                // Remove movie from database
                sharedViewModel.deleteFavoriteMovie(currentMovie)
            }

            invalidateOptionsMenu()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        try {
            val item = menu?.findItem(R.id.action_add_favorite)
            // Handle favorite icon press
            handleFavoriteIconState(currentMovie.favorite!!, item!!)
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onMovieClicked(movie: Movie) {
        try {

            sharedViewModel.apply {
                fetchMovie("${movie.id}", Utils.getDefaultLanguageCode())
                // Check recommended movies
                getRecommendedMovies(
                    movie.id!!,
                    Utils.getDefaultLanguageCode()
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun init() {
        try {
            // Get intent
            movieID = intent.extras?.get(Constant.MOVIE_ID_KEY) as String

            // Initialize adapters
            binding.recommendedMovies.adapter = MoviesAdapter(this)

            binding.apply {
                // Allows Data Binding to Observe LiveData
                // with the lifecycle of this Fragment
                lifecycleOwner = this@MovieDetailsActivity

                // Giving the binding access to the MainViewModel
                viewModel = sharedViewModel
            }

            sharedViewModel.apply {

                // Initialize activity appBar title
                initActivityTitle(this@MovieDetailsActivity, "")

                fetchMovie(movieID, Utils.getDefaultLanguageCode())

                // Check recommended movies
                getRecommendedMovies(
                    movieID.toInt(),
                    Utils.getDefaultLanguageCode()
                )
            }

            applyOverViewLayoutTransition()

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {

            binding.topAppBar.setNavigationOnClickListener {
                this.onBackPressed()
            }

            binding.trailerButton.setOnClickListener {
                launchWatcherActivity()
            }

            binding.movieOverviewSeeAll.setOnClickListener {
                if (isOverViewCollapse) {
                    binding.movieOverview.maxLines = Integer.MAX_VALUE
                    binding.movieOverviewSeeAll.text = getString(R.string.read_less_text)
                } else {
                    binding.movieOverview.maxLines = COLLAPSE_MAX_LINES
                    binding.movieOverviewSeeAll.text = getString(R.string.read_more_text)
                }
                isOverViewCollapse = !isOverViewCollapse
            }

            binding.movieOverview.doOnTextChanged { _, _, _, _ ->
                if (binding.movieOverview.canBeExpanded()) {
                    binding.movieOverview.ellipsize = TextUtils.TruncateAt.END
                    binding.movieOverviewSeeAll.visibility = VISIBLE
                }
            }

            sharedViewModel.movie.observe(this) {
                if (it != null) {
                    currentMovie = it

                    // Display details
                    binding.detailsLayout.visibility = VISIBLE

                    try {
                        invalidateOptionsMenu()
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}")
                    }

                    // Get trailers
                    sharedViewModel.getMovieVideos(
                        currentMovie.id,
                        Utils.getDefaultLanguageCode()
                    )

                } else  {
                    this.finishAffinity()
                }
            }

            sharedViewModel.officialVideo.observe(this) {
                if (it != null) {
                    videoName = "${it.name}"
                    if (it.site?.lowercase().equals(YOUTUBE_VIDEO)) {
                        videoLink = getString(R.string.youtube_video_link, "${it.key}")
                    } else if (it.site?.lowercase().equals(VIMEO_VIDEO)) {
                        videoLink = getString(R.string.vimeo_link, "${it.key}")
                    }

                    binding.trailerButton.visibility = VISIBLE
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun handleFavoriteIconState(isAdded: Boolean, menuItem: MenuItem) {
        try {
            if (isAdded) {
                menuItem.icon = ContextCompat.getDrawable(
                    this@MovieDetailsActivity, R.drawable.ic_favorite_added
                )
            } else {
                menuItem.icon = ContextCompat.getDrawable(
                    this@MovieDetailsActivity, R.drawable.ic_add_favorite
                )
            }
        } catch (e: Exception) {
            Log.e("handleFavoriteIconState", "${e.message}")
        }
    }

    private fun applyOverViewLayoutTransition() {
        val transition = LayoutTransition()
        transition.setDuration(500)
        transition.enableTransitionType(LayoutTransition.CHANGING)
        binding.overviewExpandableLayout.layoutTransition = transition
    }

    private fun launchWatcherActivity() {
        val intent = Intent(this, WatcherActivity::class.java)
        intent.putExtra(NAME, videoName)
        intent.putExtra(SITE, videoLink)
        startActivityIfNeeded(intent, REQUEST_CODE)
    }

    companion object {
        const val REQUEST_CODE = 3
        const val NAME = "name"
        const val SITE = "site"
        private val TAG = MovieDetailsActivity::class.java.simpleName
    }
}