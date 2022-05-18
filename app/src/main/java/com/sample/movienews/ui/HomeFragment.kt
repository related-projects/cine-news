package com.sample.movienews.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentHomeBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant.POPULAR_MOVIE
import com.sample.movienews.utils.Constant.TOP_RATED_MOVIE
import com.sample.movienews.utils.Constant.UP_COMING_MOVIE
import com.sample.movienews.utils.Utils.getDefaultLanguageCode
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class HomeFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var binding: FragmentHomeBinding

    private lateinit var listener: HomeFragmentListener

    interface HomeFragmentListener {
        fun onMoreButtonPressed(type: String)
        fun onMovieSelected(movie: Movie)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData
        // with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the MainViewModel
        binding.viewModel = viewModel

        // Initialize adapters
        binding.upComingMovies.adapter = MoviesAdapter(this)
        binding.popularMovies.adapter = MoviesAdapter(this)
        binding.topRatedMovies.adapter = MoviesAdapter(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Call getUpComingMovies(), getPopularMovies()
        //and getTopRatedMovies() on init so we can
        //display status immediately.
        try {
            initListener()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            val defaultLanguageCode = getDefaultLanguageCode()

            viewModel.fetchUpComingMovies(
                UP_COMING_MOVIE,
                defaultLanguageCode
            )

            viewModel.fetchPopularMovies(
                POPULAR_MOVIE,
                defaultLanguageCode
            )

            viewModel.fetchTopRatedMovies(
                TOP_RATED_MOVIE,
                defaultLanguageCode
            )
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is HomeFragmentListener) {
                listener = context
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        listener.onMovieSelected(movie)
    }

    private fun initListener() {
        try {
            binding.tvMoreUpComingMovies.setOnClickListener {
                // Display the list of up coming movies
                listener.onMoreButtonPressed(UP_COMING_MOVIE)
            }

            binding.tvMorePopularMovies.setOnClickListener {
                // Display the list of popular movies
                listener.onMoreButtonPressed(POPULAR_MOVIE)
            }

            binding.tvMoreTopRatedMovies.setOnClickListener {
                // Display the list of top rated movies
                listener.onMoreButtonPressed(TOP_RATED_MOVIE)
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }
}