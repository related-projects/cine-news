package com.sample.movienews.ui

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sample.movienews.R
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentMovieDetailsBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.activities.WatcherActivity
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant
import com.sample.movienews.utils.Constant.MOVIES_TYPE_KEY
import com.sample.movienews.utils.Constant.MOVIE_ID_KEY
import com.sample.movienews.utils.Constant.NAME
import com.sample.movienews.utils.Constant.PARENT_FRAG
import com.sample.movienews.utils.Constant.REQUEST_CODE
import com.sample.movienews.utils.Constant.SITE
import com.sample.movienews.utils.Utils.getDefaultLanguageCode
import com.sample.movienews.utils.canBeExpanded
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class MovieDetailsFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {
    private lateinit var binding: FragmentMovieDetailsBinding
    private val sharedViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var movieID: String
    private lateinit var videoName: String
    private lateinit var videoKey: String
    private lateinit var currentMovie: Movie
    private lateinit var popupFrom: String
    private var isOverViewCollapse = true

    companion object {
        private val TAG: String = MovieDetailsFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieID = requireArguments().getString(MOVIE_ID_KEY) ?: ""
        popupFrom = requireArguments().getString(PARENT_FRAG) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMovieDetailsBinding.inflate(inflater)
        binding.apply {
            recommendedMovies.adapter = MoviesAdapter(this@MovieDetailsFragment)
            // Allows Data Binding to Observe LiveData
            // with the lifecycle of this Fragment
            lifecycleOwner = this@MovieDetailsFragment
            // Giving the binding access to the MainViewModel
            viewModel = sharedViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.apply {
                backButton.setOnClickListener {
                    try {
                        var args: Bundle? = null
                        val id = when (popupFrom) {
                            MovieListFragment.TAG -> {
                                args = bundleOf(
                                    MOVIES_TYPE_KEY to requireArguments().getString(MOVIES_TYPE_KEY)
                                )
                                R.id.action_movieDetailsFragment_to_movieListFragment
                            }
                            HomeFragment.TAG -> {
                                R.id.action_movieDetailsFragment_to_homeFragment
                            }
                            else -> {
                                R.id.action_movieDetailsFragment_to_favoritesFragment
                            }
                        }
                        if (args != null) {
                            findNavController().navigate(id, args)
                        } else {
                            findNavController().navigate(id)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}")
                    }
                }

                favoriteButton.setOnClickListener {
                    try {
                        currentMovie.favorite = !currentMovie.favorite!!

                        if (currentMovie.favorite!!) {
                            // Add movie to database
                            sharedViewModel.saveFavoriteMovie(currentMovie)
                        } else {
                            // Remove movie from database
                            sharedViewModel.deleteFavoriteMovie(currentMovie)
                        }
                        // Update Ui
                        updateFavoriteIconState(currentMovie.favorite ?: false)
                    } catch (e: Exception) {
                        Log.e(TAG, "${e.message}")
                    }
                }

                trailerButton.setOnClickListener {
                    launchWatcherActivity()
                    // TODO: Implement youtube SDK to watch trailers
                }

                movieOverviewSeeAll.setOnClickListener {
                    if (isOverViewCollapse) {
                        binding.movieOverview.maxLines = Integer.MAX_VALUE
                        binding.movieOverviewSeeAll.text = getString(R.string.read_less_text)
                    } else {
                        binding.movieOverview.maxLines = Constant.COLLAPSE_MAX_LINES
                        binding.movieOverviewSeeAll.text = getString(R.string.read_more_text)
                    }
                    isOverViewCollapse = !isOverViewCollapse
                }

                movieOverview.doOnTextChanged { _, _, _, _ ->
                    if (binding.movieOverview.canBeExpanded()) {
                        binding.movieOverview.ellipsize = TextUtils.TruncateAt.END
                        binding.movieOverviewSeeAll.visibility = View.VISIBLE
                    }
                }
            }

            sharedViewModel.apply {
                movie.observe(viewLifecycleOwner) {
                    if (it != null) {
                        currentMovie = it

                        // Display details
                        binding.detailsLayout.visibility = View.VISIBLE

                        try {
                            updateFavoriteIconState(currentMovie.favorite ?: false)
                        } catch (e: Exception) {
                            Log.e(TAG, "${e.message}")
                        }

                        // Get trailers
                        sharedViewModel.getMovieVideos(
                            currentMovie.id,
                            getDefaultLanguageCode()
                        )
                    } else  {
                        // Simulate back button click to navigate back to previous screen
                        binding.backButton.performClick()
                    }
                }

                officialVideo.observe(viewLifecycleOwner) {
                    if (it != null) {
                        videoName = "${it.name}"
                        if (it.site?.lowercase().equals(Constant.YOUTUBE_VIDEO)) {
                            videoKey = "${it.key}"
                            binding.trailerButton.visibility = View.VISIBLE
                        } else if (it.site?.lowercase().equals(Constant.VIMEO_VIDEO)) {
                            videoKey = getString(R.string.vimeo_link, "${it.key}")
                        }
                    }
                }

                fetchMovie(movieID, getDefaultLanguageCode())

                // Check recommended movies
                getRecommendedMovies(
                    movieID.toInt(),
                    getDefaultLanguageCode()
                )
            }

            applyOverViewLayoutTransition()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        try {

            sharedViewModel.apply {
                fetchMovie("${movie.id}", getDefaultLanguageCode())
                // Check recommended movies
                getRecommendedMovies(
                    movie.id!!,
                    getDefaultLanguageCode()
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun updateFavoriteIconState(isAdded: Boolean) {
        try {
            binding.favoriteButton.apply {
                setImageDrawable(
                    if (isAdded) {
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_favorite_added)
                    } else {
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.ic_add_favorite)
                    }
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun applyOverViewLayoutTransition() {
        try {
            val transition = LayoutTransition()
            transition.setDuration(500)
            transition.enableTransitionType(LayoutTransition.CHANGING)
            binding.overviewExpandableLayout.layoutTransition = transition
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun launchWatcherActivity() {
        val intent = Intent(requireActivity(), WatcherActivity::class.java)
        intent.putExtra(NAME, videoName)
        intent.putExtra(SITE, videoKey)
        requireActivity().startActivityIfNeeded(intent, REQUEST_CODE)
    }
}