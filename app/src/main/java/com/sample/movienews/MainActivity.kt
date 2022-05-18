package com.sample.movienews

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.sample.movienews.databinding.ActivityMainBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.FavoritesFragment
import com.sample.movienews.ui.HomeFragment
import com.sample.movienews.ui.SearchFragment
import com.sample.movienews.ui.activities.MovieDetailsActivity
import com.sample.movienews.ui.activities.MoviesActivity
import com.sample.movienews.utils.Constant
import com.sample.movienews.utils.Constant.MOVIES_TYPE_KEY

class MainActivity : AppCompatActivity(), HomeFragment.HomeFragmentListener,
    SearchFragment.SearchFragmentListener, FavoritesFragment.OnFavoritesFragListener {

    // Binding object instance corresponding to the activity_main.xml layout
    // when the view hierarchy is attached to the fragment.
    private lateinit var binding: ActivityMainBinding
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the HomeFragment as the current fragment
        currentFragment = HomeFragment()

    }

    override fun onResume() {
        super.onResume()
        try {
            init()
            initListener()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun init() {
        try {
            // Load the HomeFragment by default
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, currentFragment)
                .commit()

            // Change bottomNavigationView shape
            val shapeDrawable : MaterialShapeDrawable =
                binding.bottomNavigationBar.background as MaterialShapeDrawable
            shapeDrawable.shapeAppearanceModel = shapeDrawable.shapeAppearanceModel
                .toBuilder()
                .setAllCorners(CornerFamily.ROUNDED, 65.0F)
                .build()
            shapeDrawable.elevation = 5F

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {
            binding.bottomNavigationBar.setOnItemSelectedListener { item ->

                when (item.itemId) {
                    R.id.home -> {
                        currentFragment = HomeFragment()
                    }
                    R.id.search_menu -> {
                        currentFragment = SearchFragment()
                    }
                    R.id.favorite -> {
                        currentFragment = FavoritesFragment()
                    }
                }

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame, currentFragment)
                    .commit()

                true
            }

            binding.bottomNavigationBar.setOnItemReselectedListener {  }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMoreButtonPressed(type: String) {
        launchMovieListActivity(type)
    }

    override fun onMovieSelected(movie: Movie) {
        launchMovieDetailActivity(movie)
    }

    override fun onSearchedMovieSelected(movie: Movie) {
        launchMovieDetailActivity(movie)
    }

    override fun onFavoriteMovieClicked(movie: Movie) {
        launchMovieDetailActivity(movie)
    }

    private fun launchMovieDetailActivity(movie: Movie) {
        val intent = Intent(this, MovieDetailsActivity::class.java)
        intent.putExtra(Constant.MOVIE_ID_KEY, "${movie.id}")
        startActivityIfNeeded(intent, MovieDetailsActivity.REQUEST_CODE)
    }

    private fun launchMovieListActivity(type: String) {
        val intent = Intent(
            this@MainActivity,
            MoviesActivity::class.java
        )

        intent.putExtra(MOVIES_TYPE_KEY, type)

        startActivityIfNeeded(
            intent,
            MoviesActivity.REQUEST_CODE
        )
    }

    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }
}