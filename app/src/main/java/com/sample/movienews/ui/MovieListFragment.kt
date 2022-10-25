package com.sample.movienews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.R
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentMovieListBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant
import com.sample.movienews.utils.Constant.MOVIES_TYPE_KEY
import com.sample.movienews.utils.Constant.MOVIE_ID_KEY
import com.sample.movienews.utils.Constant.PARENT_FRAG
import com.sample.movienews.utils.Utils
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class MovieListFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {
    private lateinit var binding: FragmentMovieListBinding
    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }
    private var moviesType: String = ""
    private var totalPages = 1
    private var currentPage = 1
    private var isLastItemVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moviesType = requireArguments().getString(
            MOVIES_TYPE_KEY) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMovieListBinding.inflate(inflater)
        binding.apply {
            // Allows Data Binding to Observe LiveData
            // with the lifecycle of this Fragment
            lifecycleOwner = this@MovieListFragment
            // Giving the binding access to the MainViewModel
            viewModel = mainViewModel
            // Initialize recyclerView's adapter
            movies.adapter = MoviesAdapter(this@MovieListFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // Initialize activity appBar title
            mainViewModel.initActivityTitle(requireContext(), moviesType)
            // Request the list of movie based on type
            mainViewModel.fetchMovies(
                moviesType,
                Utils.getDefaultLanguageCode(),
                currentPage,
                moviesType == Constant.UP_COMING_MOVIE
            )
            initListener()
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        try {
            val args = bundleOf(
                PARENT_FRAG to TAG,
                MOVIES_TYPE_KEY to moviesType,
                MOVIE_ID_KEY to "${movie.id}"
            )
            findNavController()
                .navigate(
                    R.id.action_movieListFragment_to_movieDetailsFragment,
                    args
                )
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {
            binding.apply {
                // TopBar listener
                topAppBar.setNavigationOnClickListener {
                    findNavController().navigate(
                        R.id.action_movieListFragment_to_homeFragment
                    )
                }

                // Recycler view listener
                movies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                                    Utils.getDefaultLanguageCode(),
                                    currentPage + 1,
                                    moviesType == Constant.UP_COMING_MOVIE
                                )
                            }
                        }
                    }
                })
            }

            mainViewModel.apply {
                searchedMoviesCurrentPage.observe(viewLifecycleOwner) {
                    currentPage = it
                }

                moviesTotalPages.observe(viewLifecycleOwner) {
                    totalPages = it
                    isLastItemVisible = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    companion object {
        val TAG: String = MovieListFragment::class.java.simpleName
    }
}