package com.sample.movienews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.R
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentHomeBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Constant.MOVIES_TYPE_KEY
import com.sample.movienews.utils.Constant.MOVIE_ID_KEY
import com.sample.movienews.utils.Constant.PARENT_FRAG
import com.sample.movienews.utils.Constant.POPULAR_MOVIE
import com.sample.movienews.utils.Constant.TOP_RATED_MOVIE
import com.sample.movienews.utils.Constant.UP_COMING_MOVIE
import com.sample.movienews.utils.Utils.getDefaultLanguageCode
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class HomeFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var binding: FragmentHomeBinding
    private var totalPages = 1
    private var currentPage = 1
    private var isLastItemVisible = false
    private var query = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData
        // with the lifecycle of this Fragment
        binding.apply {
            lifecycleOwner = this@HomeFragment
            // Giving the binding access to the MainViewModel
            viewModel = mainViewModel
            // Initialize adapters
            upComingMovies.adapter = MoviesAdapter(this@HomeFragment)
            popularMovies.adapter = MoviesAdapter(this@HomeFragment)
            topRatedMovies.adapter = MoviesAdapter(this@HomeFragment)
            // Initialize adapters
            rvSearchedMovies.adapter = MoviesAdapter(this@HomeFragment)
        }

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

            mainViewModel.apply {
                fetchUpComingMovies(
                    UP_COMING_MOVIE,
                    defaultLanguageCode
                )
                fetchPopularMovies(
                    POPULAR_MOVIE,
                    defaultLanguageCode
                )
                fetchTopRatedMovies(
                    TOP_RATED_MOVIE,
                    defaultLanguageCode
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    override fun onMovieClicked(movie: Movie) {
        try {
            val args = bundleOf(
                PARENT_FRAG to TAG,
                MOVIE_ID_KEY to "${movie.id}"
            )
            findNavController().navigate(
                R.id.action_homeFragment_to_movieDetailsFragment,
                args
            )
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {
            binding.apply {
                tvMoreUpComingMovies.setOnClickListener {
                    navigateToMovieListScreen(UP_COMING_MOVIE)
                }

                tvMorePopularMovies.setOnClickListener {
                    navigateToMovieListScreen(POPULAR_MOVIE)
                }

                tvMoreTopRatedMovies.setOnClickListener {
                    navigateToMovieListScreen(TOP_RATED_MOVIE)
                }

                notificationButton.setOnClickListener {
                    findNavController().navigate(
                       R.id.action_homeFragment_to_notificationFragment
                    )
                }

                accountButton.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_homeFragment_to_profileFragment
                    )
                }

                searchMovie.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(queryText: String?): Boolean {

                        try {
                            query = "${queryText?.trim()}"

                            if (query.isNotEmpty()) {
                                // Update ui state to display search list
                                binding.nestedScroller.visibility = View.GONE
                            } else {
                                // Update ui state to display movies' list
                                binding.nestedScroller.visibility = View.VISIBLE
                            }

                            // Request the list of movie based on search query
                            mainViewModel.searchMovie(
                                getDefaultLanguageCode(),
                                query,
                                1
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "${e.message}")
                        }

                        return false
                    }
                })

                rvSearchedMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val layoutManager = binding.rvSearchedMovies.layoutManager as GridLayoutManager
                        val totalItemCount = layoutManager.itemCount
                        val lastVisible = layoutManager.findLastVisibleItemPosition()
                        val endHasBeenReached = lastVisible + 5 >= totalItemCount

                        if (totalItemCount > 0 && endHasBeenReached) {
                            // You have reached the bottom of your recycler view
                            // Request the list of movie based on type
                            if (currentPage < totalPages && !isLastItemVisible) {

                                isLastItemVisible = true

                                mainViewModel.searchMovie(
                                    getDefaultLanguageCode(),
                                    query,
                                    currentPage + 1
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

                searchedMoviesTotalPages.observe(viewLifecycleOwner) {
                    totalPages = it
                    isLastItemVisible = false
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun navigateToMovieListScreen(type: String) {
        try {
            val args = bundleOf(MOVIES_TYPE_KEY to type)
            findNavController().navigate(
                R.id.action_homeFragment_to_movieListFragment,
                args
            )
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    companion object {
        val TAG: String = HomeFragment::class.java.simpleName
    }
}