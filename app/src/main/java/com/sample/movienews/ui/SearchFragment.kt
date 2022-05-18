package com.sample.movienews.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sample.movienews.app.BaseApp
import com.sample.movienews.databinding.FragmentSearchBinding
import com.sample.movienews.models.Movie
import com.sample.movienews.ui.adapters.MoviesAdapter
import com.sample.movienews.utils.Utils
import com.sample.movienews.viewModel.MainViewModel
import com.sample.movienews.viewModel.MainViewModelFactory

class SearchFragment : Fragment(), MoviesAdapter.OnMovieClickedListener {

    private val viewModel: MainViewModel by activityViewModels {
        MainViewModelFactory(
            (requireActivity().application as BaseApp).database.getMovieDao()
        )
    }

    private lateinit var binding: FragmentSearchBinding

    private lateinit var listener: SearchFragmentListener

    private var totalPages = 1
    private var currentPage = 1
    private var isLastItemVisible = false
    private var query = ""

    interface SearchFragmentListener {
        fun onSearchedMovieSelected(movie: Movie)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData
        // with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the MainViewModel
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            if (context is SearchFragmentListener) {
                listener = context
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun init() {
        try {

            // Initialize adapters
            binding.rvSearchedMovies.adapter = MoviesAdapter(this)

            // Request the list of movie based on search query
            viewModel.searchMovie(
                Utils.getDefaultLanguageCode(),
                "",
                1
            )

        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    private fun initListener() {
        try {

            binding.svMovieName.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(queryText: String?): Boolean {

                    query = "${queryText?.trim()}"

                    if (query.isNotEmpty()) {
                        binding.tvInfo.visibility = View.GONE
                    } else {
                        binding.tvInfo.visibility = View.VISIBLE
                    }

                    // Request the list of movie based on search query
                    viewModel.searchMovie(
                        Utils.getDefaultLanguageCode(),
                        query,
                        1
                    )

                    return false
                }
            })

            binding.rvSearchedMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

                            viewModel.searchMovie(
                                Utils.getDefaultLanguageCode(),
                                query,
                                currentPage + 1
                            )
                        }
                    }
                }
            })

            viewModel.searchedMoviesCurrentPage.observe(viewLifecycleOwner) {
                currentPage = it
            }

            viewModel.searchedMoviesTotalPages.observe(viewLifecycleOwner) {
                totalPages = it
                isLastItemVisible = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "${e.message}")
        }
    }

    companion object {
        private val TAG: String = SearchFragment::class.java.simpleName
    }

    override fun onMovieClicked(movie: Movie) {
        listener.onSearchedMovieSelected(movie)
    }
}